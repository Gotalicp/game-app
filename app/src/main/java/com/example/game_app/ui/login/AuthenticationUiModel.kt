package com.example.game_app.ui.login

import android.view.View

data class AuthenticationUiModel(
    val isLoading: Int = View.GONE,
    val default: Boolean = false,
    val failed: Boolean = false,
    val success: Boolean = false,
)

sealed interface AuthenticationState {
    data class Loading(val showLoading: Boolean) : AuthenticationState
    data class Failed(val showError: Boolean) : AuthenticationState
    data class Success(val nextActivity: Boolean) : AuthenticationState
    data class Default(val e: Boolean) : AuthenticationState
}
