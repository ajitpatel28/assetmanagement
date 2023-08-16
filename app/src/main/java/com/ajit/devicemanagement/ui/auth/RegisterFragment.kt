package com.ajit.devicemanagement.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.data.model.User
import com.ajit.devicemanagement.data.provider.DataSyncManager
import com.ajit.devicemanagement.databinding.FragmentRegisterBinding
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : BaseFragment(){

    val TAG: String = "RegisterFragment"
    lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var dataSyncManager: DataSyncManager
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.toggleBtn.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.passEt.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.toggleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0)
            } else {
                binding.passEt.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.toggleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0)
            }
            binding.passEt.setSelection(binding.passEt.text.length) // Maintain cursor position
        }


        binding.loginRedirect.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.registerBtn.setOnClickListener {
            if (validation()) {
                viewModel.register(
                    email = binding.emailEt.text.toString(),
                    password = binding.passEt.text.toString(),
                    user = getUserObj()
                )
            }
        }
    }



    fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.registerBtn.setText("")
                    binding.registerProgress.show()
                }
                is UiState.Failure -> {
                    binding.registerBtn.setText("Register")
                    binding.registerProgress.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.registerBtn.text = "Register"
                    binding.registerProgress.hide()
                    toast(state.data)
                    viewModel.getSession { user ->
                        if (user != null) {
                            LoginFragment.UserSession.loggedInUser = user
                            dataSyncManager.startSync{
                                findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)
                            }
                        }
                    }

                }
            }
        }
    }

    fun getUserObj(): User {
        return User(
            id = "",
            name = binding.nameEt.text.toString(),
            employeeId = binding.employeeIdEt.text.toString(),
            mobileNumber = binding.mobileEt.text.toString(),
            email = binding.emailEt.text.toString(),

            )
    }


    fun validation(): Boolean {
        var isValid = true

        if (binding.nameEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_first_name))
        }

        if (binding.employeeIdEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_last_name))
        }
        if (binding.mobileEt.text.isNullOrEmpty() || binding.mobileEt.text.length != 10) {
            isValid = false
            toast(getString(R.string.enter_mobile))
        }

        if (binding.emailEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.emailEt.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.passEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
        } else {
            if (binding.passEt.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataSyncManager = DataSyncManager(requireContext())
    }

}




