package com.t3h.demomediaplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.t3h.demomediaplayer.R
import com.t3h.demomediaplayer.databinding.ItemSongBinding
import com.t3h.demomediaplayer.model.MusicOnline

class SongOnlineAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    companion object{
        val NORMAL_TYPE = 0
        val LOAD_MORE_TYPE = 1
    }
    private val inter: ISongOnline
    var isLoading = false
    constructor(inter: ISongOnline) {
        this.inter = inter
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {
        if (viewType == NORMAL_TYPE){
            return SongViewHolder(
                ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }else {
            return LoadMoreHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_load_more, parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SongViewHolder){
            holder.binding.data = inter.getData(position)
            holder.binding.ll.setOnClickListener {
                inter.onClickItem(holder.adapterPosition)
            }
        }

        if (position == inter.getCount()-1 && !isLoading){
            isLoading = true
            inter.continueLoad()
        }
    }

    override fun getItemCount(): Int {
        return inter.getCount()
    }

    override fun getItemViewType(position: Int): Int {
        if (position < inter.getCount()-1){
            return NORMAL_TYPE
        }else {
            return LOAD_MORE_TYPE
        }
    }

    class SongViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
    }
    class LoadMoreHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }

    interface ISongOnline {
        fun getCount(): Int
        fun getData(position: Int): MusicOnline
        fun onClickItem(position: Int)
        fun continueLoad()
    }
}