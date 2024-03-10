package com.example.bondoman.fragments

import android.Manifest
import android.content.Context
import android.media.AudioManager
import android.media.MediaActionSound
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.lifecycleScope
import com.example.bondoman.R
import com.example.bondoman.databinding.FragmentScanBinding
import com.example.bondoman.utils.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScanFragment : Fragment() {
    private var _binding: FragmentScanBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var camera: LifecycleCameraController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind CameraController
        PermissionUtils.requirePermissions(this, arrayOf(Manifest.permission.CAMERA)) {
            camera = LifecycleCameraController(requireContext())
            camera.bindToLifecycle(this)
            camera.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            binding.previewView.controller = camera
        }

        // Capture button callback
        binding.captureButton.setOnClickListener {
            onCaptureButtonClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun playShutterSound() {
        val audio: AudioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (audio.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                val sound = MediaActionSound()
                sound.play(MediaActionSound.SHUTTER_CLICK)
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun onCaptureButtonClicked() {
        val cameraExecutor = Executors.newSingleThreadExecutor()
        camera.takePicture(
            cameraExecutor,
            object: ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    playShutterSound()
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.previewView.controller = null
                    }
                    // TODO call backend and add transaction
                }
            }
        )
    }

    companion object {

    }
}