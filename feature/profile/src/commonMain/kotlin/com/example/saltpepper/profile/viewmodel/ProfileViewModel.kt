package com.example.saltpepper.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.saltpepper.core.ui.viewmodel.BaseViewModel
import com.example.saltpepper.data.profile.repository.ProfileRepository
import com.example.saltpepper.data.recipe.model.Recipe
import com.example.saltpepper.data.recipe.repository.RecipeRepository
import com.example.saltpepper.profile.model.ProfileUiModel
import com.example.saltpepper.profile.model.toUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val recipeRepository: RecipeRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<ProfileScreenUiState>(ProfileScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var refreshJob: Job? = null

    fun refresh() {
        if (refreshJob?.isActive == true) return
        _uiState.value = ProfileScreenUiState.Loading
        refreshJob = viewModelScope.launch {
            val profileRequest = async { profileRepository.getProfile() }
            val recipesRequest = async { recipeRepository.getMyRecipes() }
            val profile = profileRequest.await()
            val recipes = recipesRequest.await()
            val errors = listOfNotNull(
                profile.exceptionOrNull()?.let { it.message ?: "Could not load profile" },
                recipes.exceptionOrNull()?.let { it.message ?: "Could not load recipes" }
            )
            _uiState.value = if (errors.isEmpty()) {
                ProfileScreenUiState.Success(
                    profile.getOrThrow().toUiModel(), recipes.getOrThrow()
                )
            } else {
                ProfileScreenUiState.Error(errors.distinct().joinToString("\n"))
            }
        }
    }
}

sealed interface ProfileScreenUiState {
    data object Loading : ProfileScreenUiState
    data class Success(
        val profile: ProfileUiModel,
        val myRecipes: List<Recipe>
    ) : ProfileScreenUiState
    data class Error(val message: String) : ProfileScreenUiState
}
