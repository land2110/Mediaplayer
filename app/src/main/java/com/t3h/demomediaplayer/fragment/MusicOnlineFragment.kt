package com.t3h.demomediaplayer.fragment

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.t3h.demomediaplayer.adapter.SongOnlineAdapter
import com.t3h.demomediaplayer.databinding.FragmentMusicOnlineBinding
import com.t3h.demomediaplayer.model.MusicManager
import com.t3h.demomediaplayer.model.MusicOnline
import com.t3h.demomediaplayer.model.MusicOnlineManager
import com.t3h.demomediaplayer.service.MediaPlayerMusicOnlineService

class MusicOnlineFragment : Fragment(), SongOnlineAdapter.ISongOnline,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentMusicOnlineBinding
    private lateinit var conn: ServiceConnection
    private var service: MediaPlayerMusicOnlineService? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicOnlineBinding.inflate(inflater, container, false)
        binding.rcSong.layoutManager = GridLayoutManager(context, 3)
        binding.rcSong.adapter = SongOnlineAdapter(this)
        binding.refresh.setOnRefreshListener(this)
        startServiceUnBound()
        connectServiceBound()
        return binding.root
    }

    private fun startServiceUnBound() {
        val intent = Intent()
        intent.setClass(requireContext(), MediaPlayerMusicOnlineService::class.java)
        requireContext().startService(intent)
    }

    private fun connectServiceBound() {
        conn = object : ServiceConnection {
            override fun onServiceConnected(cp: ComponentName, binder: IBinder) {
                service = (binder as MediaPlayerMusicOnlineService.MyBinder).service
                register()
                if (service?.getSongDatas()?.size == 0) {
                    binding.refresh.isRefreshing = true
                    service?.getDataSyn()
                }else {
                    binding.rcSong.adapter?.notifyDataSetChanged()
                }

            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                service = null
            }
        }
        val intent = Intent()
        intent.setClass(requireContext(), MediaPlayerMusicOnlineService::class.java)
        requireContext().bindService(intent, conn, Context.BIND_AUTO_CREATE)
    }

    private fun register() {
        service?.viewModelItemSongs?.observe(viewLifecycleOwner, {
            binding.rcSong.adapter?.notifyDataSetChanged()
            binding.refresh.isRefreshing = false
        })
    }

    override fun onDestroyView() {
        requireContext().unbindService(conn)
        super.onDestroyView()
    }

    override fun getCount(): Int {
        if (service == null) {
            return 0
        }
        return service!!.getSongDatas().size
    }

    override fun getData(position: Int): MusicOnline {
        return service!!.getSongDatas()[position]
    }

    override fun onClickItem(position: Int) {
        service?.parseToGetMusic(position)
    }

    fun getDataSyn() {
        service?.getDataSyn()
    }

    fun getDataSearch(url: String, keySearch: String) {
        service?.getDataSearch(url, keySearch)
    }

    override fun onRefresh() {
        service?.onRefresh()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


}