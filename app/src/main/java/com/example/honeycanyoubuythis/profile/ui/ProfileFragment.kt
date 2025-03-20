package com.example.honeycanyoubuythis.profile.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.honeycanyoubuythis.database.AppDatabase
import com.example.honeycanyoubuythis.database.user.CurrentUser
import com.example.honeycanyoubuythis.databinding.ProfileFragmentBinding
import com.example.honeycanyoubuythis.profile.viewmodel.ProfileViewModel
import com.example.honeycanyoubuythis.profile.viewmodel.ProfileViewModelFactory
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

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
            if (profileViewModel.checkIfLoggedIn()) {
                val user = profileViewModel.getUser()
                if (user != null) {
                    binding.displayNameField.setText(user.displayName)

                }
            }
        }

        with(binding) {
            displayNameField.addTextChangedListener(displayNameTextWatcher)

            editButton.setOnClickListener {
                displayNameField.isEnabled = true
                editButton.visibility = View.GONE
                saveButton.visibility = View.VISIBLE
                cancelButton.visibility = View.VISIBLE
            }

            cancelButton.setOnClickListener {
                displayNameField.isEnabled = false
                editButton.visibility = View.VISIBLE
                saveButton.visibility = View.GONE
                cancelButton.visibility = View.GONE
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
                            displayName = newDisplayName
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
                displayNameField.isEnabled = false
                editButton.visibility = View.VISIBLE
                saveButton.visibility = View.GONE
                cancelButton.visibility = View.GONE
            }
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