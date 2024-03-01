package com.example.camaraapp.ui.image_full_fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.camaraapp.R
import com.example.camaraapp.commons.IMAGE_ARGUMENT_KEY
import com.example.camaraapp.commons.SCALE_FACTOR_MAX
import com.example.camaraapp.commons.SCALE_FACTOR_MIN
import com.example.camaraapp.databinding.FragmentImageFullBinding

class ImageFullFragment : Fragment() {

    private val viewModel by viewModels<ImageFullViewModel>()
    private lateinit var binding : FragmentImageFullBinding
    private var angleCounter = 90f
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private var imageModified : Bitmap? = null

    companion object{
        var imageEdit = ""
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val image = arguments?.getString(IMAGE_ARGUMENT_KEY)
        binding = FragmentImageFullBinding.inflate(inflater, container, false)

        binding.imgFullScreen.setImageURI(image?.toUri())
        val imageBitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.parse(image))

        binding.btnBack.setOnClickListener {
            binding.btnBack.startAnimation(AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.image_click_animation));
            activity?.findViewById<View>(R.id.viewBlockActivity)?.visibility = View.GONE
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.imgRotate.setOnClickListener{
            binding.imgRotate.startAnimation(AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.image_click_animation));
            binding.imgFullScreen.setImageBitmap(viewModel.rotateImage(imageBitmap, angleCounter))
            imageEdit = viewModel.getImageUri(activity?.applicationContext!!, viewModel.rotateImage(imageBitmap, angleCounter)).toString()
            angleCounter += 90f
        }

        binding.imgCrop.setOnClickListener {
            binding.imgFullScreen.setImageBitmap(viewModel.cropBitMap(imageBitmap, 700, 700, 400, 600))
            imageEdit = viewModel.getImageUri(activity?.applicationContext!!, viewModel.cropBitMap(imageBitmap, 700, 700, 400, 600)).toString()
        }

        scaleGestureDetector = ScaleGestureDetector(requireActivity(), ScaleListener())
        binding.imgFullScreen.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }

        binding.btnSave.setOnClickListener {
            binding.btnSave.startAnimation(AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.image_click_animation));
            activity?.findViewById<View>(R.id.viewBlockActivity)?.visibility = View.GONE
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        activity?.findViewById<View>(R.id.viewBlockActivity)?.visibility = View.GONE
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(SCALE_FACTOR_MIN, SCALE_FACTOR_MAX)

            binding.imgFullScreen.scaleX = scaleFactor
            binding.imgFullScreen.scaleY = scaleFactor
            return true
        }
    }
}