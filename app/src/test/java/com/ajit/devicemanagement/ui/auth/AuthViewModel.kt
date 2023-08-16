//package com.ajit.devicemanagement.ui.auth
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.lifecycle.Observer
//import com.ajit.devicemanagement.data.model.User
//import com.ajit.devicemanagement.data.repository.AuthRepository
//import com.ajit.devicemanagement.util.UiState
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.runBlocking
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.Mockito.verify
//import org.mockito.MockitoAnnotations
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config
//
//@RunWith(RobolectricTestRunner::class)
//@Config(sdk = [Config.OLDEST_SDK])
//@ExperimentalCoroutinesApi
//class AuthViewModelTest {
//
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @Mock
//    private lateinit var authRepository: AuthRepository
//
//    private lateinit var authViewModel: AuthViewModel
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.initMocks(this)
//        authViewModel = AuthViewModel(authRepository)
//    }
//
//    @Test
//    fun testRegister() = runBlocking {
//        // Set up test data
//        val email = "test@example.com"
//        val password = "password"
//        val user = User("John Doe")
//
//        // Set up the expected result
//        val expectedResult = UiState.Success("Registration successful")
//
//        // Set up the mock response from the repository
//        Mockito.`when`(authRepository.registerUser(email, password, user, Mockito.isNotNull()))
//            .thenAnswer { invocation ->
//                val callback: (UiState<String>) -> Unit = invocation.arguments[3] as (UiState<String>) -> Unit
//                // Simulate some delay before returning the result
//                Thread.sleep(1000)
//                callback.invoke(expectedResult)
//                null
//            }
//
//        // Create an observer to capture the emitted value
//        val observer = Mockito.mock(Observer::class.java) as Observer<UiState<String>>
//        authViewModel.register.observeForever(observer)
//
//        // Perform the registration
//        authViewModel.register(email, password, user)
//
//        // Verify that the register LiveData was updated with the expected result
//        verify(observer).onChanged(expectedResult)
//        assert(expectedResult == authViewModel.register.value)
//    }
//
//    // Similarly, you can write tests for other methods in the AuthViewModel
//
//    // ...
//}
