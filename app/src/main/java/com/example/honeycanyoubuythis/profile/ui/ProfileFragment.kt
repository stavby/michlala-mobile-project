package com.example.honeycanyoubuythis.profile.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.honeycanyoubuythis.database.AppDatabase
import com.example.honeycanyoubuythis.database.user.CurrentUser
import com.example.honeycanyoubuythis.databinding.ProfileFragmentBinding
import com.example.honeycanyoubuythis.profile.viewmodel.ProfileViewModel
import com.example.honeycanyoubuythis.profile.viewmodel.ProfileViewModelFactory
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    private var profilePictureUri: Uri? = null

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        val currentUserDao = AppDatabase.getInstance(requireContext()).currentUserDao()
        val factory = ProfileViewModelFactory(currentUserDao)
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val user = profileViewModel.getUser()
            if (user != null) {
                with(binding){
                    displayNameField.setText(user.displayName)

                    if(user.profilePicture != null){
                        val bitmap = BitmapFactory.decodeByteArray(user.profilePicture, 0, user.profilePicture!!.size)
                        profilePicture.setImageBitmap(bitmap)
                    }
                }
            }
        }

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    profilePictureUri = data?.data
                    profilePictureUri?.let {
                        binding.profilePicture.setImageURI(it)
                        lifecycleScope.launch {
                            profileViewModel.updateProfilePicture(requireContext(), it)
                        }
                    }
                }
            }

        with(binding) {
            val buttonList = listOf(editButton, saveButton, cancelButton)
            val editableFieldsList = listOf(displayNameField, profilePicture)

            displayNameField.addTextChangedListener(displayNameTextWatcher)
            profilePicture.setOnClickListener {
                openGallery()
            }

            editButton.setOnClickListener {
                switchEnabledStatus(editableFieldsList)
                switchShownButtons(buttonList)
            }

            cancelButton.setOnClickListener {
                switchEnabledStatus(editableFieldsList)
                switchShownButtons(buttonList)
                lifecycleScope.launch {
                    val user = profileViewModel.getUser()
                    if (user != null) {
                        displayNameField.setText(user.displayName)
                    }
                }
            }

            saveButton.setOnClickListener {
                val newDisplayName = displayNameField.text.toString()
                lifecycleScope.launch {
                    val user = profileViewModel.getUser()
                    if (user != null) {
                        val updatedUser = CurrentUser(
                            id = user.id,
                            email = user.email,
                            displayName = newDisplayName,
                            profilePicture = user.profilePicture
                        )

                        if (profileViewModel.updateUser(updatedUser)) {
                            Toast.makeText(
                                requireContext(),
                                "Display name updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Display name couldn't be updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                switchEnabledStatus(editableFieldsList)
                switchShownButtons(buttonList)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun switchShownButtons(buttonList: List<MaterialButton>) {
        buttonList.forEach { button ->
            if (button.isVisible) {
                button.visibility = View.GONE
            } else {
                button.visibility = View.VISIBLE
            }
        }
    }

    private fun switchEnabledStatus(viewList: List<View>) {
        viewList.forEach { view ->
            view.isEnabled = !view.isEnabled
        }
    }

    private val displayNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.saveButton.isEnabled = s.toString().isNotEmpty()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}