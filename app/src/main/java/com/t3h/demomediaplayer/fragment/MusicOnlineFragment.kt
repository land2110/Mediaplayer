package com.t3h.demomediaplayer.fragment

import android.annotation.SuppressLint
import android.content.ContentValues
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.t3h.demomediaplayer.adapter.SongOnlineAdapter
import com.t3h.demomediaplayer.databinding.FragmentMusicOnlineBinding
import com.t3h.demomediaplayer.model.MusicManager
import com.t3h.demomediaplayer.model.MusicOnline
import com.t3h.demomediaplayer.model.MusicOnlineManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URL

class MusicOnlineFragment : Fragment(), SongOnlineAdapter.ISongOnline,
    SwipeRefreshLayout.OnRefreshListener {
    var url = "https://chiasenhac.vn/mp3/vietnam.html?tab=bai-hat-moi"
    var urlSearch = ""
    private val songDatas = mutableListOf<MusicOnline>()
    private lateinit var binding: FragmentMusicOnlineBinding
    private val mp = MusicManager.getInstance()
    private var dis: Disposable? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicOnlineBinding.inflate(inflater, container, false)
        songDatas.clear()
        binding.rcSong.layoutManager = GridLayoutManager(context, 3)
        binding.rcSong.adapter = SongOnlineAdapter(this)
        binding.refresh.setOnRefreshListener(this)
        getDataSyn()
        return binding.root
    }

    fun getDataSyn() {
        binding.refresh.isRefreshing = true
        this.urlSearch = ""
        //huy RX
        dis?.dispose()
        dis = Observable.create<MutableList<MusicOnline>> {
            //tren thead khac
            val songs = mutableListOf<MusicOnline>()
            for (i in (1..27)) {
                songs.addAll(getDataAsyn("${url}&page=${i}"))
            }
            it.onNext(songs)
            it.onComplete()
        }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    //mainthread
                    songDatas.clear()
                    songDatas.addAll(it)
//                    load lai recycleview
                    binding.rcSong.adapter?.notifyDataSetChanged()
                    binding.refresh.isRefreshing = false
                }
            )
    }

    override fun getCount(): Int {
        return songDatas.size
    }

    override fun getData(position: Int): MusicOnline {
        return songDatas[position]
    }

    override fun onClickItem(position: Int) {
        parseToGetMusic(position, songDatas[position].htmlLink)
    }

    @SuppressLint("CheckResult")
    private fun parseToGetMusic(position:Int, url: String) {
        Observable.create<String> {
            //tren thead khac
            it.onNext(parseToGetMusicAsyn(url))
            it.onComplete()
        }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
//                    it is mp3 link
                    mp.setUrl(it)

                    saveMusic(songDatas[position], it)
                }
            )
    }

    private fun parseToGetMusicAsyn(url: String): String {
        val doc = Jsoup.connect(url).get()
        val container = doc.select("div.container")
        for (col in container.get(4).select("a.download_item")) {
            val urlMp3 = col.attr("href")
            if (urlMp3.endsWith(".mp3")) {
                Log.d("MusicOnlineActivity", "urlMp3: " + urlMp3)
                return urlMp3
            }
        }
        Log.d("MusicOnlineActivity", "getData....")
        return ""
    }

    private fun getDataAsyn(url: String): MutableList<MusicOnline> {
        val songs = mutableListOf<MusicOnline>()
//        Jsoup: phan tich web html
        try {
            val doc = Jsoup.connect(url).get()
            val container = doc.select("div.container").get(4)
            val cols = container.select("li.media")
            for (col in cols!!) {
                try {
                    var imageLink = col.select("img").attr("src")
                    val htmlLink = "https://chiasenhac.vn" + col.select("a").attr("href")
                    val name = col.select("a").attr("title")
                    val singer = col.select("a").get(2).text()
                    songs.add(MusicOnline(htmlLink, name, singer, htmlLink, imageLink))
                    Log.d(
                        "MusicOnlineActivity", "name: " + name +
                                "\nlink: " + htmlLink +
                                "\nsinger: " + singer +
                                "\nimageLink: " + imageLink
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } catch (e: Exception) {

        }

        return songs
    }

    fun getDataSearch(url: String) {
        binding.refresh.isRefreshing = true
        this.urlSearch = url
        dis?.dispose()
        dis = Observable.create<MutableList<MusicOnline>> {
            //tren thead khac
            val songs = mutableListOf<MusicOnline>()
            for (i in (1..3)) {
                songs.addAll(
                    getDataSearchAsyn("${url}&page_music==${i}")
                )
            }
            it.onNext(songs)
            it.onComplete()
        }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
//                    it is mp3 link
                    songDatas.clear()
                    songDatas.addAll(it)
                    binding.rcSong.adapter?.notifyDataSetChanged()
                    binding.refresh.isRefreshing = false
                }, {}
            )
    }

    private fun getDataSearchAsyn(url: String): MutableList<MusicOnline> {
        val songs = mutableListOf<MusicOnline>()
        try {
            val doc = Jsoup.connect(url).get()
            val container = doc.select("div.container").get(4)
            for (itemLi in container.select("ul.list-unstyled").get(1).select("li")) {
                try {
                    val name = itemLi.select("a").attr("title")
                    val htmlLink = itemLi.select("a").attr("href")
                    val imageLink = itemLi.select("img").attr("src")
                    val author = itemLi.select("div.author").text()
                    if ("".equals(author)) {
                        continue
                    }
                    songs.add(MusicOnline(htmlLink, name, author, htmlLink, imageLink))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {

        }
        return songs
    }

    override fun onRefresh() {
        if ("".equals(urlSearch)) {
            getDataSyn()
        } else {
            getDataSearch(urlSearch)
        }
    }

    @SuppressLint("CheckResult")
    private fun saveMusic(musicOnline:MusicOnline, urlMusic: String){
        Toast.makeText(requireContext(), "Start download: ${musicOnline.name}", Toast.LENGTH_SHORT).show()
        Observable.create<Boolean> {
            saveMusicAsync(musicOnline, urlMusic)
            it.onNext(true)
            it.onComplete()
        }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(
                    requireContext(),
                    "Finish download: ${musicOnline.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveMusicAsync(musicOnline:MusicOnline, urlMusic: String){
        val fileName = Environment.getExternalStorageDirectory().path+"/"+Environment.DIRECTORY_MUSIC+"/" +
                musicOnline.name.replace(" ", "_") + "-"+
                musicOnline.artis!!.replace(" ", "_")+".mp3"
        if (File(fileName).exists()){
            return
        }
        val input = URL(urlMusic).openStream()
        val b = ByteArray(1024)
        val out = FileOutputStream(
            fileName
        )
        var le = input.read(b)
        while (le >=0){
            out.write(b, 0, le)
            le = input.read(b)
        }
        input.close()
        out.close()
        val contentValue = ContentValues()
        contentValue.put("_data", fileName)
        val mp = MediaPlayer()
        mp.setDataSource(fileName)
        mp.prepare()
        val duration = mp.duration
        mp.release()
        contentValue.put("duration", duration)
        contentValue.put("artist", musicOnline.artis)
        contentValue.put("title", musicOnline.name)
        requireContext().contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValue)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


}