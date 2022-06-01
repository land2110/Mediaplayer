package com.t3h.demomediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.t3h.demomediaplayer.databinding.ItemSongBinding
import com.t3h.demomediaplayer.model.MusicOnline

class SongOnlineAdapter : RecyclerView.Adapter<SongOnlineAdapter.SongViewHolder> {
    private val inter: ISongOnline

    constructor(inter: ISongOnline) {
        this.inter = inter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.binding.data = inter.getData(position)
        holder.binding.ll.setOnClickListener {
            inter.onClickItem(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return inter.getCount()
    }

    class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    interface ISongOnline {
        fun getCount(): Int
        fun getData(position: Int): MusicOnline
        fun onClickItem(position: Int)
    }
}