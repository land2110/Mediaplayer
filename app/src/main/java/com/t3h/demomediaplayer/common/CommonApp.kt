package com.t3h.demomediaplayer.common

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import android.content.ContentUris
import android.net.Uri
import com.t3h.demomediaplayer.R


object CommonApp {
    @JvmStatic
    val FORMAT_TIME = SimpleDateFormat("DD/MM")
    val FORMAT_DURATION_MUSIC = SimpleDateFormat("mm:ss")

    @JvmStatic
    @BindingAdapter(value= arrayOf("loadNormalImageLink"))
    fun loadNormalImageLink(img: ImageView, link: String?) {
        if (link == null) {
            img.setImageResource(R.drawable.ao_dai)
            return
        }
        Glide.with(img.context)
            .load(link)
            .error(R.drawable.ao_dai)
            .placeholder(R.drawable.ao_dai)
            .into(img)
    }

    @JvmStatic
    @BindingAdapter(value= arrayOf("loadNormalImageResource"))
    fun loadNormalImageResource(img: ImageView, resource: Int?) {
        if (resource == null) {
            img.setImageResource(R.drawable.ao_dai)
            return
        }
        Glide.with(img.context)
            .load(resource)
            .error(R.drawable.ao_dai)
            .placeholder(R.drawable.ao_dai)
            .into(img)
    }

    @JvmStatic
    @BindingAdapter(value= arrayOf("loadTextDuration"))
    fun loadTextDuration(tv: TextView, duration: Long?) {
        if(duration == null ){
            tv.setText("")
        }else{
            tv.setText(FORMAT_DURATION_MUSIC.format(duration))
        }
    }

    @JvmStatic
    @BindingAdapter(value= arrayOf("loadImageFromAlbumId"))
    fun loadImageFromAlbumId(iv: ImageView, albumId: Long?) {
        if(albumId == null ){
            iv.setImageResource(R.drawable.ao_dai)
        }else{
            val sArtworkUri = Uri.parse("content://media/external/audio/albumart")

            val uri: Uri = ContentUris.withAppendedId(sArtworkUri, albumId)
            Glide.with(iv.context)
                .load(uri)
                .error(R.drawable.ao_dai)
                .placeholder(R.drawable.ao_dai)
                .into(iv)
        }
    }

}