package com.example.bondoman.fragments

import TokenManager
import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.AudioManager
import android.media.MediaActionSound
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bondoman.R
import com.example.bondoman.adapter.BillAdapter
import com.example.bondoman.databinding.FragmentScanBinding
import com.example.bondoman.models.BillList
import com.example.bondoman.models.BillReq
import com.example.bondoman.models.BillRes
import com.example.bondoman.repositories.TransactionRepository
import com.example.bondoman.room.database.TransactionDatabase
import com.example.bondoman.room.models.Transaction
import com.example.bondoman.services.RetrofitInstance
import com.example.bondoman.utils.PermissionUtils
import com.example.bondoman.viewmodels.TransactionViewModel
import com.example.bondoman.viewmodels.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
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
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var billList: BillList? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        resetLayout()
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

        // Initialize pickMedia
        pickMedia = registerForActivityResult(PickVisualMedia()) {
                uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                binding.previewView.visibility = View.INVISIBLE
                binding.imageView.setImageURI(uri)
                binding.imageView.visibility = View.VISIBLE
                enableLoadingAnimation()
                disableAllButtons()
//                sendImageToBackend(bitmap)
                lifecycleScope.launch(Dispatchers.IO) {
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    val cache = requireContext().cacheDir
                    val imgFile = File(cache, "image.jpg")

                    imgFile.outputStream().use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, it)
                    }
                    sendImageToBackend(imgFile)
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        // Capture button callback
        binding.captureButton.setOnClickListener {
            onCaptureButtonClicked()
        }
        binding.galleryButton.setOnClickListener {
            onGalleryButtonClicked()
        }
        binding.doneButton.setOnClickListener {
            onDoneButtonClicked()
        }
        binding.retakeButton.setOnClickListener {
            onRetakeButtonClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun enableLoadingAnimation() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.loadingPanel.visibility = View.VISIBLE
        }
    }

    private fun disableLoadingAnimation() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.loadingPanel.visibility = View.GONE
        }
    }

    private fun disableAllButtons() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.captureButton.isEnabled = false
            binding.galleryButton.isEnabled = false
        }
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
                    disableAllButtons()
                    enableLoadingAnimation()
                    val bitmap = image.toBitmap().rotate(image.imageInfo.rotationDegrees.toFloat())

                    val cache = requireContext().cacheDir
                    val imgFile = File(cache, "image.jpg")

                    imgFile.outputStream().use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, it)
                    }
                    sendImageToBackend(imgFile)
                }
            }
        )
    }
    private fun Bitmap.rotate(degrees: Float): Bitmap =
        Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)


    private fun sendImageToBackend(file: File) {
        lifecycleScope.launch(Dispatchers.IO) {
            val apiService = RetrofitInstance.bill
            val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody("image/jpeg".toMediaType()))

            // No Permission TODO choose redirect to login / do nothing
            if (TokenManager.getRemainingTime() <= 0) {
                return@launch;
            }

            val token = ("Bearer " + TokenManager.getToken()!!)


            apiService.upload(token = token, file = part).enqueue(object : Callback<BillRes> {
                override fun onResponse(
                    call: Call<BillRes>,
                    response: Response<BillRes>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("scan", response.body()!!.items.items.toString())
                        billList = response.body()!!.items
                        val billAdapter = BillAdapter(requireContext(), billList!!)
                        binding.itemsView.adapter = billAdapter
                        binding.itemsView.layoutManager = LinearLayoutManager(requireContext())
                        showItems()
                    }
                    else {
                        Toast.makeText(requireContext(), "Upload failed, try again", Toast.LENGTH_SHORT).show()
                        resetLayout()
                    }

                }

                override fun onFailure(call: Call<BillRes>, t: Throwable) {
                    t.printStackTrace()
                }
            })

        }
    }

    private fun resetLayout() {
        binding.previewView.visibility = View.VISIBLE
        if (::camera.isInitialized) {
            binding.previewView.controller = camera
        }
        binding.imageView.visibility = View.INVISIBLE
        binding.itemsView.visibility = View.INVISIBLE

        binding.captureButton.visibility = View.VISIBLE
        binding.galleryButton.visibility = View.VISIBLE
        binding.captureButton.isEnabled = true
        binding.galleryButton.isEnabled = true

        binding.retakeButton.visibility = View.GONE
        binding.doneButton.visibility = View.GONE

        binding.loadingPanel.visibility = View.GONE
    }

    private fun showItems() {
        binding.previewView.visibility = View.INVISIBLE
        binding.imageView.visibility = View.INVISIBLE
        binding.itemsView.visibility = View.VISIBLE

        binding.captureButton.visibility = View.GONE
        binding.galleryButton.visibility = View.GONE

        binding.retakeButton.visibility = View.VISIBLE
        binding.doneButton.visibility = View.VISIBLE

        binding.loadingPanel.visibility = View.GONE
    }

    private fun onGalleryButtonClicked() {
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    private fun onDoneButtonClicked() {
        // guard
        if (billList == null) {
            throw Exception("No Bill Available")
        }

        // calculate total price
        lifecycleScope.launch(Dispatchers.Default) {
            val totalPrice: Float = billList!!.items
                .map { it -> it.price * it.qty }
                .reduce { acc, price -> acc + price }

            // insert new transaction
            val format = DecimalFormat.getNumberInstance(Locale("id", "ID"))
            format.apply {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
                isGroupingUsed = true
            }

            val newTransaction = Transaction(price = "IDR ${format.format(totalPrice)}", category = "Pembelian")
            val viewModel = ViewModelProvider(this@ScanFragment, ViewModelFactory(
                TransactionRepository(
                    TransactionDatabase.getDatabaseInstance(requireContext()))
            )
            ).get(TransactionViewModel::class.java)

            viewModel.addTransaction(newTransaction)
        }

        // notify and change fragment(?)
        Toast.makeText(requireContext(), "Transaction added", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_navigation_scan_to_navigation_transaction)
    }

    private fun onRetakeButtonClicked() {
        resetLayout()
    }

    companion object {

    }
}