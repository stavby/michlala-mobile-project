package com.example.honeycanyoubuythis.login.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.honeycanyoubuythis.databinding.LoginFragmentBinding

class LoginFragment: Fragment() {
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        with(binding){
            usernameField.addTextChangedListener(loginTextWatcher)
            passwordField.addTextChangedListener(loginTextWatcher)
        }

    }

    private val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            with(binding){
                val username = usernameField.text.toString().trim()
                val password = passwordField.text.toString().trim()

                // Enable the button if both fields are not empty
                loginButton.isEnabled = username.isNotEmpty() && password.isNotEmpty()
            }

        }

        override fun afterTextChanged(s: Editable?) {}
    }
}