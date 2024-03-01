package com.example.camaraapp.ui.main

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import com.example.camaraapp.R
import com.example.camaraapp.commons.IMAGE_ARGUMENT_KEY
import com.example.camaraapp.databinding.ActivityMainBinding
import com.example.camaraapp.ui.image_full_fragment.ImageFullFragment
import com.example.camaraapp.ui.main.adapter.RecyclerImagesAdapter


class MainActivity : AppCompatActivity(), RecyclerImagesAdapter.OnImageItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private var image: Uri? = null
    private var imageSelected = ""
    private var listImages = mutableListOf<Uri>()
    private val adapter = RecyclerImagesAdapter(listImages, this)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerImages.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerImages.adapter = adapter

        binding.imgAddImage.setOnClickListener {
            binding.imgAddImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click_animation));
            showDialogToTakeOrSelectImage()
        }


        binding.imgRefresh.setOnClickListener {
            binding.imgRefresh.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click_animation));
            if (ImageFullFragment.imageEdit != "" && imageSelected != "") {
                listImages.remove(imageSelected.toUri())
                listImages.add(ImageFullFragment.imageEdit.toUri())
                adapter.notifyDataSetChanged()
            }
                ImageFullFragment.imageEdit = ""
                imageSelected = ""
        }

    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                openCamera()
            } else {
                Toast.makeText(
                    this,
                    "!Permiso para acceder a la cámara denegado!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val requestGalleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                openGallery()
            } else {
                Toast.makeText(
                    this,
                    "!Permiso para acceder a la galería denegado!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun showDialogToTakeOrSelectImage() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.card_view_dialog)

        val imagenOpenCamera = dialog.findViewById<ImageButton>(R.id.imgOpenCamera)
        imagenOpenCamera.setOnClickListener {
            imagenOpenCamera.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click_animation));
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
                dialog.dismiss()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
                dialog.dismiss()
            }
        }

        val imageOpenGallery = dialog.findViewById<ImageButton>(R.id.imgOpenGallery)
        imageOpenGallery.setOnClickListener {
            imageOpenGallery.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_click_animation));
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
                dialog.dismiss()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestGalleryPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    requestGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                dialog.dismiss()
            }

        }
        dialog.show()
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripción")
        image = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                listImages.add(image!!)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "!Operación cancelada!", Toast.LENGTH_LONG).show()
            }
        }

    private fun openGallery() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripción")
        image = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, image)
        } else {
            intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, image)
        }
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                listImages.add(it.data?.data!!)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "!Operación cancelada!", Toast.LENGTH_LONG).show()
            }
        }
    override fun onImageClick(image: Uri) {
        openFragment(image)
        imageSelected = image.toString()
    }

    private fun openFragment(image: Uri) {
        val bundle = Bundle()
        bundle.putString(IMAGE_ARGUMENT_KEY, image.toString())
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val fragment = ImageFullFragment()
        fragment.arguments = bundle
        transaction.replace(R.id.fragmentFullImage, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        binding.viewBlockActivity.visibility = View.VISIBLE
    }
}