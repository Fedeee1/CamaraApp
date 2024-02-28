package com.example.camaraapp.ui.image_full_fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.camaraapp.R
import com.example.camaraapp.commons.IMAGE_ARGUMENT_KEY
import com.example.camaraapp.databinding.FragmentImageFullBinding

class ImageFullFragment : Fragment() {

    private lateinit var binding : FragmentImageFullBinding
    private var angleCounter = 90f
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

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
            activity?.findViewById<View>(R.id.viewBlockActivity)?.visibility = View.GONE
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.imgRotate.setOnClickListener{
            binding.imgFullScreen.setImageBitmap(rotateImage(imageBitmap, angleCounter))
            angleCounter += 90f
        }

        binding.imgCrop.setOnClickListener {
            binding.imgFullScreen.setImageBitmap(cropBitMap(imageBitmap, 400, 400, 200, 200))
        }

        scaleGestureDetector = ScaleGestureDetector(requireActivity(), ScaleListener())
        binding.imgFullScreen.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        activity?.findViewById<View>(R.id.viewBlockActivity)?.visibility = View.GONE
    }

    private fun rotateImage(image: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            image, 0, 0, image.width, image.height,
            matrix, true
        )
    }

    private fun cropBitMap(image: Bitmap, startX: Int, startY: Int, width: Int, height: Int ) : Bitmap {
        return Bitmap.createBitmap(image, startX, startY, width, height)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.1f, 10.0f)

            binding.imgFullScreen.scaleX = scaleFactor
            binding.imgFullScreen.scaleY = scaleFactor
            return true
        }
    }
}