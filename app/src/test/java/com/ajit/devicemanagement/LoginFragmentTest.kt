package com.ajit.devicemanagement

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.ajit.devicemanagement.data.model.User
import com.ajit.devicemanagement.data.provider.DataSyncManager
import com.ajit.devicemanagement.ui.auth.AuthViewModel
import com.ajit.devicemanagement.ui.auth.LoginFragment
import com.ajit.devicemanagement.util.UiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginFragmentTest {

    @Mock
    private lateinit var viewModel: AuthViewModel

    @Mock
    private lateinit var dataSyncManager: DataSyncManager

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.app_navigation)

        val scenario = launchFragmentInContainer<LoginFragment>(themeResId = R.style.AppTheme)
        scenario.onFragment { fragment ->
            fragment.viewModel = viewModel
            fragment.dataSyncManager = dataSyncManager
            fragment.findNavController().setGraph(R.navigation.app_navigation)
        }
    }

    @Test
    fun testLogin() {
        // Mock the necessary dependencies and responses
        val loggedInUser = User(/* Provide necessary user details */)
        val successResult = UiState.Success(loggedInUser)
        `when`(viewModel.login).thenReturn(successResult)

        // Perform actions on the fragment using Espresso
        onView(withId(R.id.email_et)).perform(replaceText("test@example.com"))
        onView(withId(R.id.pass_et)).perform(replaceText("password"))
        onView(withId(R.id.login_btn)).perform(click())

        // Verify the expected behavior using Mockito
        verify(viewModel).login("test@example.com", "password")
        // ...
    }
}
