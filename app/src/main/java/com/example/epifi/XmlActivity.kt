package com.example.epifi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.epifi.databinding.ActivityXmlBinding
import com.example.epifi.ui.UiEvents
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class XmlActivity : ComponentActivity() {

    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityXmlBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_xml)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiEvents.collectLatest {
                        when (it) {
                            UiEvents.DismissScreen -> finish()
                            is UiEvents.ShowToast -> showToast(it.message)
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
