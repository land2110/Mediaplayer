package com.t3h.demomediaplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.t3h.demomediaplayer.databinding.ItemMucisOfflineBinding
import com.t3h.demomediaplayer.model.MusicOffline

class MusicOfllineAdapter : RecyclerView.Adapter<MusicOfllineAdapter.MusicOfflineHolder> {
    private val inter: IMusicOffline
    constructor(inter: IMusicOffline){
        this.inter = inter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicOfflineHolder {
        return MusicOfflineHolder(
            ItemMucisOfflineBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MusicOfflineHolder, position: Int) {
        holder.binding.data = this.inter.getData(position)

        holder.binding.root.setOnClickListener {
            this.inter.onClickItem(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return this.inter.getSize()
    }

    interface IMusicOffline {
        fun getSize(): Int
        fun getData(position: Int): MusicOffline
        fun onClickItem(position:Int)
    }

    class MusicOfflineHolder(val binding: ItemMucisOfflineBinding) :
        RecyclerView.ViewHolder(binding.root)
}