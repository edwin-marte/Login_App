package com.example.login_app.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.login_app.R
import com.example.login_app.databinding.FragmentPasswordResetBinding
import com.example.login_app.presentation.MainViewModel
import com.example.login_app.core.Resource

class PasswordResetFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentPasswordResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_password_reset, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.sendEmailButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            if (email.isEmpty() || email.isBlank()) return@setOnClickListener

            viewModel.passwordReset(email).observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Resource.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "The email has been sent.",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()
                    }
                    is Resource.Failure -> {
                        showMsj(
                            "Error occurred while resetting the password",
                            result.exception
                        )
                    }
                }
            })
        }
    }

    private fun showMsj(msj: String, e: Exception?) {
        Toast.makeText(requireContext(), "$msj ${e ?: ""}", Toast.LENGTH_SHORT).show()
    }
}