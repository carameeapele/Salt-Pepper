package com.example.saltpepper.data.session

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val KEY_ALIAS = "com.example.saltpepper.session"
private const val RECORD_VERSION = 1
private const val IV_SIZE = 12
private const val TAG_BITS = 128
private const val MAX_CIPHERTEXT_SIZE = 1024 * 1024
private val storageLock = Any()
private var applicationContext: Context? = null

fun initializeSessionStorage(context: Context) {
    synchronized(storageLock) {
        applicationContext = context.applicationContext
    }
}

actual fun createSecureSessionStore(): SecureSessionStore = synchronized(storageLock) {
    AndroidSecureSessionStore(
        checkNotNull(applicationContext) { "Session storage has not been initialized" }
    )
}

private class AndroidSecureSessionStore(context: Context) : SecureSessionStore {
    private val file = AtomicFile(File(context.noBackupFilesDir, "refresh-session.enc"))

    override fun read(): String? = synchronized(storageLock) {
        val input = try {
            file.openRead()
        } catch (exception: FileNotFoundException) {
            if (file.baseFile.exists()) throw exception
            return@synchronized null
        }
        DataInputStream(input).use { stream ->
            check(stream.readInt() == RECORD_VERSION) { "Invalid session storage version" }
            val ivLength = stream.readInt()
            check(ivLength == IV_SIZE) { "Invalid session storage IV" }
            val iv = ByteArray(ivLength)
            stream.readFully(iv)
            val ciphertextLength = stream.readInt()
            check(ciphertextLength in (TAG_BITS / 8)..MAX_CIPHERTEXT_SIZE) {
                "Invalid session storage ciphertext length"
            }
            val ciphertext = ByteArray(ciphertextLength)
            stream.readFully(ciphertext)
            check(stream.read() == -1) { "Invalid session storage record" }
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, readKey(), GCMParameterSpec(TAG_BITS, iv))
            cipher.doFinal(ciphertext).toString(Charsets.UTF_8)
        }
    }

    override fun write(value: String) {
        synchronized(storageLock) {
            val plaintext = value.toByteArray(Charsets.UTF_8)
            require(plaintext.size <= MAX_CIPHERTEXT_SIZE - TAG_BITS / 8) {
                "Session value is too large"
            }
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, readOrCreateKey())
            val ciphertext = cipher.doFinal(plaintext)
            check(cipher.iv.size == IV_SIZE) { "Invalid encryption IV" }
            val output = file.startWrite()
            try {
                val stream = DataOutputStream(output)
                stream.writeInt(RECORD_VERSION)
                stream.writeInt(cipher.iv.size)
                stream.write(cipher.iv)
                stream.writeInt(ciphertext.size)
                stream.write(ciphertext)
                stream.flush()
                file.finishWrite(output)
            } catch (exception: Throwable) {
                file.failWrite(output)
                throw exception
            }
        }
    }

    override fun clear() {
        synchronized(storageLock) {
            file.delete()
            if (file.baseFile.exists() || File(file.baseFile.path + ".bak").exists() ||
                File(file.baseFile.path + ".new").exists()
            ) {
                throw IOException("Unable to clear session storage")
            }
        }
    }

    private fun keyStore(): KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun readKey(): SecretKey = checkNotNull(keyStore().getKey(KEY_ALIAS, null) as? SecretKey) {
        "Session encryption key is unavailable"
    }

    private fun readOrCreateKey(): SecretKey {
        val existing = keyStore().getKey(KEY_ALIAS, null)
        if (existing != null) return existing as SecretKey
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }
}