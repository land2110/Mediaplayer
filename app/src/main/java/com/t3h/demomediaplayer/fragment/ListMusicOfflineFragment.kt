package com.t3h.demomediaplayer.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.t3h.demomediaplayer.CommonApp
import com.t3h.demomediaplayer.R
import com.t3h.demomediaplayer.adapter.MusicOfllineAdapter
import com.t3h.demomediaplayer.databinding.FragmentListMusicOfllineBinding
import com.t3h.demomediaplayer.model.MusicManager
import com.t3h.demomediaplayer.model.MusicOffline
import com.t3h.demomediaplayer.permission.PermissionUtils
import java.util.concurrent.Executors

class ListMusicOfflineFragment : Fragment(), MusicOfllineAdapter.IMusicOffline,
    SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private lateinit var binding: FragmentListMusicOfllineBinding
    private val mpManager = MusicManager.getInstance()
    private val musicOffline = mutableListOf<MusicOffline>()
    private var currentPosition = -1
    private var currentAsyn: AsyncTask<Void, Int, Int>? = null
    private var isStartTracking = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListMusicOfllineBinding.inflate(inflater, container, false)

        binding.rcMusic.layoutManager = LinearLayoutManager(requireContext())
        binding.rcMusic.adapter = MusicOfllineAdapter(this)

        binding.seekBarMusic.setOnSeekBarChangeListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.btnPrevious.setOnClickListener(this)

        val pers = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_MEDIA_LOCATION,
        )
        if (PermissionUtils.checkPermissionAndShowPoup(this, pers)) {
            initData()
        } else {
            //kiem tra xem co hien thi yeu cau quyen duoc hay khong
            if (!PermissionUtils.checkShouldShow(this, pers)) {
                openDialogShowSetting()
            }
        }
        return binding.root
    }

    private fun openDialogShowSetting() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Confirm")
            .setMessage("Do you want to confirm open setting app?")
            .setPositiveButton("Confirm") { dialog, with ->
                run {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts(
                        "package", requireContext().packageName,
                        null
                    )
                    intent.setData(uri)
                    startActivityForResult(intent, 100)
                }
            }
            .setNegativeButton("No") { dialog, with ->
                {

                }
            }.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    private fun initData() {
        //lay tat ca cac bai nhac trong dt
        val cursor: Cursor = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null, null, null, null
        )!!

        cursor.moveToFirst()
        val indexId = cursor.getColumnIndex("_id")
        val indexPath = cursor.getColumnIndex("_data")
        val indexDuration = cursor.getColumnIndex("duration")
        val indexAlbum = cursor.getColumnIndex("album_id")
        val indexArtis = cursor.getColumnIndex("artist")
        val indexTitle = cursor.getColumnIndex("title")
        while (!cursor.isAfterLast) {
            val id = cursor.getInt(indexId)
            val path = cursor.getString(indexPath)
            val duration = cursor.getLong(indexDuration)
            var artist = cursor.getString(indexArtis)
            if (artist == null) {
                artist = ""
            }
            val album = cursor.getLong(indexAlbum)
            val title = cursor.getString(indexTitle)
//            val id:Int, val path: String, val duration:Long, val artist:String, val albumId:Long
            musicOffline.add(MusicOffline(id, path, title, duration, artist, album))

            cursor.moveToNext()
        }
        cursor.close()


    }

    override fun getSize(): Int {
        return musicOffline.size
    }

    override fun getData(position: Int): MusicOffline {
        return musicOffline[position]
    }

    override fun onClickItem(position: Int) {
        currentPosition = position
        binding.seekBarMusic.max = musicOffline[position].duration.toInt()
        mpManager.setUrl(musicOffline[position].path)
        mpManager.start()
        if (musicOffline[position].duration == 0L) {
            musicOffline[position].duration = mpManager.getDuration()
            binding.rcMusic.adapter?.notifyItemChanged(position)
            onClickItem(position)
            return
        }
        this.updateUIBottom(position)

        createAsynUpdateSeekBar()
    }

    private fun createAsynUpdateSeekBar() {
        if (currentAsyn != null) {
            currentAsyn!!.cancel(true)
        }
        currentAsyn = object : AsyncTask<Void, Int, Int>() {
            override fun doInBackground(vararg p0: Void?): Int {
                while (!isCancelled) {
                    publishProgress(mpManager.getCurrentPosition())
                    SystemClock.sleep(500)
                }

                return 0
            }

            override fun onProgressUpdate(vararg values: Int?) {
                if (!isStartTracking) {
                    binding.seekBarMusic.progress = values[0]!!
                    CommonApp.loadTextDuration(binding.tvCurrentDuration, values[0]!!.toLong())
                }

            }
        }
        currentAsyn?.executeOnExecutor(Executors.newFixedThreadPool(1))
    }

    private fun updateUIBottom(position: Int) {
        binding.tvName.setText(musicOffline[position].title)
        CommonApp.loadTextDuration(binding.tvDuration, musicOffline[position].duration)


    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        isStartTracking = true
    }

    override fun onStopTrackingTouch(seek: SeekBar) {
        if (!mpManager.isEmptyMp()) {
            mpManager.seek(seek.progress)
        }
        isStartTracking = false
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> {
                currentPosition = (currentPosition + 1) % musicOffline.size
                onClickItem(currentPosition)
            }
            R.id.btn_previous -> {
                currentPosition = (currentPosition - 1)
                if (currentPosition < 0) {
                    currentPosition = musicOffline.size - 1
                }
                onClickItem(currentPosition)
            }
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 50) {
            var isOkAll = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isOkAll = false
                }
            }
            if (isOkAll) {
                initData()
                binding.rcMusic.adapter?.notifyDataSetChanged()
            }
        }

    }


}