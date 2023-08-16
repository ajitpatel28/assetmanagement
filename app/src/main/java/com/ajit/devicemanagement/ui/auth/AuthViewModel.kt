package com.ajit.devicemanagement.ui.auth

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajit.devicemanagement.data.model.User
import com.ajit.devicemanagement.data.repository.AuthRepository
import com.ajit.devicemanagement.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
): ViewModel() {


    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>>
        get() = _register

    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    private val _updateUser = MutableLiveData<UiState<String>>()
    val updateUser: LiveData<UiState<String>>
        get() = _updateUser

    private val _forgotPassword = MutableLiveData<UiState<String>>()
    val forgotPassword: LiveData<UiState<String>>
        get() = _forgotPassword


    fun register(
        email: String,
        password: String,
        user: User
    ) {
        _register.value = UiState.Loading
        repository.registerUser(
            email = email,
            password = password,
            user = user
        ) { _register.value = it }
    }

    fun login(
        email: String,
        password: String
    ) {
        _login.value = UiState.Loading
        repository.loginUser(
            email,
            password
        ){
            _login.value = it
        }
    }


    fun updateUser(user: User) {
        _updateUser.value = UiState.Loading
        viewModelScope.launch {
            try {
                repository.updateUser(user) { state ->
                    when (state) {
                        is UiState.Success -> {
                            Log.e("DeviceViewModel", "updated user $user")
                            _updateUser.value = UiState.Success("User has been updated")
                        }
                        is UiState.Failure -> {
                            _updateUser.value = UiState.Failure(state.error)
                        }
                        else -> {
                            // Handle other states if needed
                        }
                    }
                }
            } catch (e: Exception) {
                _updateUser.value = UiState.Failure(e.message)
            }
        }
    }


    fun forgotPassword(email: String) {
        _forgotPassword.value = UiState.Loading
        repository.forgotPassword(email){
            _forgotPassword.value = it
        }
    }

    fun logout(result: () -> Unit){
        repository.logout(result)
    }

    fun getSession(result: (User?) -> Unit){
        repository.getSession(result)
    }

    fun updateProfilePicture(userId: String, imageUri: Uri, result: (UiState<String>) -> Unit){
        repository.uploadProfilePicture(userId, imageUri, result)
    }

}