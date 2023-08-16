package com.ajit.devicemanagement.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.databinding.FragmentUserProfileEditBinding
import com.ajit.devicemanagement.ui.auth.AuthViewModel
import com.ajit.devicemanagement.ui.auth.LoginFragment
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserProfileEditFragment : BaseFragment() {

    lateinit var binding: FragmentUserProfileEditBinding
    private val viewModel: AuthViewModel by viewModels()
    private val currentUser = LoginFragment.UserSession.loggedInUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileEditBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateBack.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileEditFragment_to_userProfileFragment)
        }

        binding.apply {
            currentUser?.let {
                nameEt.setText(it.name)
                employeeIdEt.setText(it.employeeId)
                emailEt.setText(it.email)
                mobileEt.setText(it.mobileNumber)
            }
            emailEt.isEnabled = false
            employeeIdEt.isEnabled = false


            binding.updatePrfileBtn.setOnClickListener {
                if(validation()){
                    val newName = nameEt.text.toString()
                    val newMobileNumber = mobileEt.text.toString()

                    currentUser?.let { user ->
                        user.name = newName
                        user.mobileNumber = newMobileNumber

                        // Update the user details in Firestore
                        viewModel.updateUser(user)
                    }
                }
            }
        }


        viewModel.updateUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.updatePrfileBtn.text = ""
                    binding.updateProgress.show()
                }
                is UiState.Success -> {
                    binding.updatePrfileBtn.text = getString(R.string.update_button_label)
                    binding.updateProgress.hide()
                    toast(state.data)
                    findNavController().navigate(R.id.action_userProfileEditFragment_to_userProfileFragment)
                }
                is UiState.Failure -> {
                    binding.updatePrfileBtn.text = getString(R.string.update_button_label)
                    binding.updateProgress.hide()
                    toast(state.error)
                }
            }
        }
    }


   private fun validation(): Boolean {
        var isValid = true

        if (binding.nameEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_first_name))
        }


        if (binding.mobileEt.text.isNullOrEmpty() || binding.mobileEt.text.length != 10) {
            isValid = false
            toast(getString(R.string.enter_mobile))
        }



        return isValid
    }


}

