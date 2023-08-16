package com.ajit.devicemanagement.ui.user

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.ui.device.DeviceViewModel
import com.ajit.devicemanagement.databinding.FragmentUserProfileBinding
import com.ajit.devicemanagement.ui.auth.AuthViewModel
import com.ajit.devicemanagement.ui.auth.LoginFragment
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserProfileFragment : BaseFragment() {


    lateinit var binding: FragmentUserProfileBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val deviceViewModel: DeviceViewModel by viewModels()
    private val PICK_IMAGE_REQUEST = 1


    private val currentUser = LoginFragment.UserSession.loggedInUser



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.profileDetailBack.setOnClickListener {
            activity?.onBackPressed()
        }



        binding.userProfileEditBtn.setOnClickListener {
            openGallery()
        }



        binding.apply {
            currentUser?.let {
                titleName.text = it.name
                profileName.text = it.name
                titleEmployeeId.text = it.employeeId
                detailEmployeeIdValue.text = it.employeeId
                profileEmail.text = it.email
                profileMobile.text = it.mobileNumber
                ImageUtils.loadImageLocally(requireContext(), it.id, userProfile)


            }
        }

        binding.profileDetailEditBtn.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_userProfileEditFragment)
        }



        binding.logout.setOnClickListener {
            authViewModel.logout {
                deviceViewModel.deleteAllDevices()
                findNavController().navigate(R.id.action_userProfileFragment_to_loginFragment)
            }
        }
    }

    private fun openGallery() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }




    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImage)
            binding.userProfile.setImageBitmap(bitmap)


            val userId = currentUser?.id ?: ""

            authViewModel.updateProfilePicture(userId, selectedImage!!) { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.progressBar.hide()
                        ImageUtils.saveImageLocally(requireContext(), bitmap, userId)
                        toast("Profile picture updated successfully.")
                    }
                    is UiState.Failure -> {
                        binding.progressBar.hide()
                        toast("Failed to update profile picture.")
                    }
                    is UiState.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }
    }

}
