@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.cinterop.BetaInteropApi::class
)

package com.example.saltpepper.data.session

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.set
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.posix.memcpy

actual fun createSecureSessionStore(): SecureSessionStore = KeychainSecureSessionStore()

private class KeychainSecureSessionStore : SecureSessionStore {
    override fun read(): String? = withSessionQuery(
        listOf(kSecReturnData to kCFBooleanTrue, kSecMatchLimit to kSecMatchLimitOne)
    ) { query ->
        memScoped {
            val result = alloc<CFTypeRefVar>()
            result.value = null
            val status = SecItemCopyMatching(query, result.ptr)
            val data = result.value?.let { CFBridgingRelease(it) as NSData }
            when (status) {
                errSecItemNotFound -> null
                errSecSuccess -> {
                    checkNotNull(data) { "Keychain returned no session data" }
                    check(data.length <= Int.MAX_VALUE.toULong()) { "Session data is too large" }
                    val bytes = ByteArray(data.length.toInt())
                    if (bytes.isNotEmpty()) {
                        bytes.usePinned { pinned ->
                            memcpy(pinned.addressOf(0), data.bytes, data.length)
                        }
                    }
                    bytes.decodeToString(throwOnInvalidSequence = true)
                }
                else -> error("Unable to read session from Keychain: $status")
            }
        }
    }

    override fun write(value: String) {
        val bytes = value.encodeToByteArray()
        val data = if (bytes.isEmpty()) {
            NSData.create(bytes = null, length = 0u)
        } else {
            bytes.usePinned { pinned ->
                NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
            }
        }
        val dataRef = checkNotNull(CFBridgingRetain(data))
        try {
            val attributes = listOf(
                kSecValueData to dataRef,
                kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
            )
            withSessionQuery { query ->
                withDictionary(attributes) { updates ->
                    val status = SecItemUpdate(query, updates)
                    if (status == errSecItemNotFound) {
                        withSessionQuery(attributes) { newItem ->
                            val addStatus = SecItemAdd(newItem, null)
                            check(addStatus == errSecSuccess) {
                                "Unable to store session in Keychain: $addStatus"
                            }
                        }
                    } else {
                        check(status == errSecSuccess) { "Unable to update session in Keychain: $status" }
                    }
                }
            }
        } finally {
            CFRelease(dataRef)
        }
    }

    override fun clear() {
        withSessionQuery { query ->
            val status = SecItemDelete(query)
            check(status == errSecSuccess || status == errSecItemNotFound) {
                "Unable to clear session from Keychain: $status"
            }
        }
    }
}

private fun <Result> withSessionQuery(
    attributes: List<Pair<CFTypeRef?, CFTypeRef?>> = emptyList(),
    block: (CFDictionaryRef) -> Result
): Result {
    val service = checkNotNull(
        CFStringCreateWithCString(null, "com.example.saltpepper.session", kCFStringEncodingUTF8)
    )
    try {
        val account = checkNotNull(
            CFStringCreateWithCString(null, "refresh-session", kCFStringEncodingUTF8)
        )
        try {
            return withDictionary(
                listOf(
                    kSecClass to kSecClassGenericPassword,
                    kSecAttrService to service,
                    kSecAttrAccount to account
                ) + attributes,
                block
            )
        } finally {
            CFRelease(account)
        }
    } finally {
        CFRelease(service)
    }
}

private fun <Result> withDictionary(
    entries: List<Pair<CFTypeRef?, CFTypeRef?>>,
    block: (CFDictionaryRef) -> Result
): Result = memScoped {
    val keys = allocArray<CFTypeRefVar>(entries.size)
    val values = allocArray<CFTypeRefVar>(entries.size)
    entries.forEachIndexed { index, entry ->
        keys[index] = entry.first
        values[index] = entry.second
    }
    val dictionary = checkNotNull(
        CFDictionaryCreate(
            null,
            keys.reinterpret(),
            values.reinterpret(),
            entries.size.toLong(),
            kCFTypeDictionaryKeyCallBacks.ptr,
            kCFTypeDictionaryValueCallBacks.ptr
        )
    )
    try {
        block(dictionary)
    } finally {
        CFRelease(dictionary)
    }
}