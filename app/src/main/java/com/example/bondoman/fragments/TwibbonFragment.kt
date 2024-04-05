package com.example.bondoman.fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.lifecycleScope
import com.example.bondoman.databinding.FragmentTwibbonBinding
import com.example.bondoman.utils.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class TwibbonFragment : Fragment() {
    private var _binding: FragmentTwibbonBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var camera: LifecycleCameraController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTwibbonBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind CameraController
        PermissionUtils.requirePermissions(this, arrayOf(Manifest.permission.CAMERA)) {
            camera = LifecycleCameraController(requireContext())
            camera.bindToLifecycle(this)
            camera.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            binding.previewView.controller = camera
        }

        // Capture button callback
        binding.captureButton.setOnClickListener {
            onCaptureButtonClicked()
        }
        binding.retakeButton.setOnClickListener {
            onRetakeButtonClicked()
        }

        binding.flipButton.setOnClickListener {
            if (camera.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                camera.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                camera.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }
        }
    }

    private fun onCaptureButtonClicked() {
        binding.captureButton.visibility = View.GONE
        binding.retakeButton.visibility = View.VISIBLE
        binding.flipButton.setEnabled(false)
        val cameraExecutor = Executors.newSingleThreadExecutor()
        camera.takePicture(
            cameraExecutor,
            object: ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.previewView.controller = null
                    }
                }
            }
        )
    }

    private fun onRetakeButtonClicked() {
        binding.captureButton.visibility = View.VISIBLE
        binding.retakeButton.visibility = View.GONE
        binding.flipButton.setEnabled(true)
        camera = LifecycleCameraController(requireContext())
        camera.bindToLifecycle(this)
        camera.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        binding.previewView.controller = camera
    }
}