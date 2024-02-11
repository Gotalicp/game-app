package com.example.game_app.ui.login

import android.view.View

object AuthenticationUIMapper {
    fun map(state: AuthenticationState): AuthenticationUiModel = when (state) {
        is AuthenticationState.Failed -> AuthenticationUiModel(failed = true)
        is AuthenticationState.Default -> AuthenticationUiModel(default = true)
        is AuthenticationState.Success -> AuthenticationUiModel(success = true)
        is AuthenticationState.Loading -> AuthenticationUiModel(isLoading = View.VISIBLE)
    }
}