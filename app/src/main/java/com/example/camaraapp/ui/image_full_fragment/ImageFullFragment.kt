package com.example.camaraapp.ui.image_full_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.camaraapp.R
import com.example.camaraapp.commons.IMAGE_ARGUMENT_KEY
import com.example.camaraapp.databinding.FragmentImageFullBinding

class ImageFullFragment : Fragment() {

    private lateinit var binding : FragmentImageFullBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val image = arguments?.getString(IMAGE_ARGUMENT_KEY)
        binding = FragmentImageFullBinding.inflate(inflater, container, false)

        binding.imgFullScreen.setImageURI(image?.toUri())

        binding.btnBack.setOnClickListener {
            activity?.findViewById<View>(R.id.viewBlockActivity)?.visibility = View.GONE
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        activity?.findViewById<View>(R.id.viewBlockActivity)?.visibility = View.GONE
    }
}