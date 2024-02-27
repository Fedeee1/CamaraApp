package com.example.camaraapp.ui.main.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.camaraapp.R

class RecyclerImagesAdapter(private val listImages: List<Uri> ,private val listener: OnImageItemClickListener) :
    RecyclerView.Adapter<RecyclerImagesAdapter.ViewHolder>() {

    interface OnImageItemClickListener {
        fun onImageClick(image: Uri)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgSelected: ImageView
        var cardImage: CardView

        init {
            imgSelected = itemView.findViewById(R.id.imgImageAdded)
            cardImage = itemView.findViewById(R.id.cardImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_view_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.imgSelected.setImageURI(listImages[position])

        viewHolder.cardImage.setOnClickListener {
            listener.onImageClick(listImages[position])
        }
    }

    override fun getItemCount(): Int {
        return listImages.size
    }
}