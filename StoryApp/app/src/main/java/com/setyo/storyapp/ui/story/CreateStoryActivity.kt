package com.setyo.storyapp.ui.story

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.setyo.storyapp.R
import com.setyo.storyapp.databinding.ActivityCreateStoryBinding
import com.setyo.storyapp.helper.ViewModelFactory
import com.setyo.storyapp.helper.uriToFile
import com.setyo.storyapp.helper.createTempFile
import com.setyo.storyapp.helper.reduceImageSize
import com.setyo.storyapp.helper.rotateImageIfRequired
import com.setyo.storyapp.ui.login.LoginActivity
import com.setyo.storyapp.ui.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var photoPath: String
    private var getFile: File? = null
    private val createStoryViewModel: CreateStoryViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setUpView()

        binding.apply {
            buttonPhoto.setOnClickListener { startTakePhoto() }
            buttonGallery.setOnClickListener { openGallery() }
            buttonUpload.setOnClickListener { uploadImage()}
        }
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun setUpView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val localFile = uriToFile(selectedImage, this)

            getFile = localFile
            binding.imageViewPost.setImageURI(selectedImage)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooseImage = Intent.createChooser(intent, "Pilih Gambar")
        launchGallery.launch(chooseImage)
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val localFile = File(photoPath)
            getFile = localFile

            val photoResult = BitmapFactory.decodeFile(getFile?.path)
            val rotatedPhotoResult = rotateImageIfRequired(photoResult, getFile?.path)
            binding.imageViewPost.setImageBitmap(rotatedPhotoResult)
        }
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@CreateStoryActivity, "com.setyo.storyapp", it
            )
            photoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launchCamera.launch(intent)
        }
    }

    private fun uploadImage() {
        showLoading()
        createStoryViewModel.getUser().observe(this@CreateStoryActivity) {
            if (getFile != null) {
                val file = reduceImageSize(getFile as File)
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                uploadResponse(
                    it.token,
                    imageMultipart,
                    binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
                )
            } else {
                Toast.makeText(this@CreateStoryActivity, getString(R.string.description_null), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadResponse(token: String, file: MultipartBody.Part, description: RequestBody) {
        createStoryViewModel.uploadStory(token, file, description)
        createStoryViewModel.uploadResponse.observe(this@CreateStoryActivity) {
            if (!it.error) {
                moveActivity()
            }
        }
        showToast()
    }

    private fun moveActivity() {
        val intent = Intent(this@CreateStoryActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(this,"Tidak mendapatkan permission.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast() {
        createStoryViewModel.textToast.observe(this@CreateStoryActivity) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        createStoryViewModel.isLoading.observe(this@CreateStoryActivity) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}