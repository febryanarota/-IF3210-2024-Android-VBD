package com.example.bondoman.fragments

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.lifecycleScope
import com.example.bondoman.R
import com.example.bondoman.databinding.FragmentTwibbonBinding
import com.example.bondoman.utils.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
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
//        binding.retakeButton.setOnClickListener {
//            onRetakeButtonClicked()
//        }
    }

    private fun onCaptureButtonClicked() {
        val cameraExecutor = Executors.newSingleThreadExecutor()
        camera.takePicture(
            cameraExecutor,
            object: ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
//                    playShutterSound()
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.previewView.controller = null
                    }
                }
            }
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TwibbonFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TwibbonFragment().apply {
            }
    }
}