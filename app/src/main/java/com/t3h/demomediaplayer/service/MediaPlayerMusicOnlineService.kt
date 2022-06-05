package com.t3h.demomediaplayer.service

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.t3h.demomediaplayer.MainActivity
import com.t3h.demomediaplayer.R
import com.t3h.demomediaplayer.model.MusicManager
import com.t3h.demomediaplayer.model.MusicOnline
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URL


class MediaPlayerMusicOnlineService : Service() {
    private val mp = MusicManager.getInstance()

    val viewModelItemSongs = MutableLiveData<MutableList<MusicOnline>>()

    var url = "https://chiasenhac.vn/mp3/vietnam.html?tab=bai-hat-moi"
    var urlSearch = ""
    private var dis: Disposable? = null
    private var currentPosition = -1

    private val songDatas = mutableListOf<MusicOnline>()


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(p0: Intent?): IBinder {
        return MyBinder(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if ( action != null){
            makeAction(action)
        }
        return START_NOT_STICKY
    }

    private fun makeAction(action:String){
        when(action){
            "PREVIOUS"->{
                currentPosition-=1
                if ( currentPosition <0){
                    currentPosition = 0
                }
                parseToGetMusic(currentPosition)
            }
            "PAUSE"->{
                if (mp.isPlay()){
                    mp.pause()
                }else {
                    mp.start()
                }

            }
            "NEXT"->{
                currentPosition+=1
                if ( currentPosition >= getSongDatas().size){
                    currentPosition = 0
                }
                parseToGetMusic(currentPosition)
            }
        }
    }

    class MyBinder(val service: MediaPlayerMusicOnlineService) : Binder()


    fun getDataSyn() {
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
                    viewModelItemSongs.value = it
                }
            )
    }

    fun onRefresh() {
        if ("".equals(urlSearch)) {
            getDataSyn()
        } else {
            getDataSearch(urlSearch)
        }
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
//        binding.refresh.isRefreshing = true
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
                    viewModelItemSongs.value = songDatas
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

    @SuppressLint("CheckResult")
    private fun saveMusic(musicOnline: MusicOnline, urlMusic: String) {
        Toast.makeText(this, "Start download: ${musicOnline.name}", Toast.LENGTH_SHORT).show()
        Observable.create<Boolean> {
            saveMusicAsync(musicOnline, urlMusic)
            it.onNext(true)
            it.onComplete()
        }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(
                    this,
                    "Finish download: ${musicOnline.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun getSongDatas(): MutableList<MusicOnline> {
        return songDatas
    }

    private fun saveMusicAsync(musicOnline: MusicOnline, urlMusic: String) {
        val fileName =
            Environment.getExternalStorageDirectory().path + "/" + Environment.DIRECTORY_MUSIC + "/" +
                    musicOnline.name.replace(" ", "_") + "-" +
                    musicOnline.artis!!.replace(" ", "_") + ".mp3"
        if (File(fileName).exists()) {
            return
        }
        val input = URL(urlMusic).openStream()
        val b = ByteArray(1024)
        val out = FileOutputStream(
            fileName
        )
        var le = input.read(b)
        while (le >= 0) {
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
        contentResolver.insert(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            contentValue
        )
    }

    @SuppressLint("CheckResult")
    fun parseToGetMusic(position: Int) {
        currentPosition = position
        val url = songDatas[position].htmlLink
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
//                    saveMusic(songDatas[position], it)
                    openNotificationToPlay(songDatas[position])
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("NOTIFICATION", "NOTIFICATION", importance).apply {
                description = "NOTIFICATION"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun openNotificationToPlay(item: MusicOnline) {
        // Nếu muốn click vào notification, mở Activity thì PendingIntent.getActivity
        // Nếu muốn click vào notification, Broadcast đến broad lắng nghe event thì PendingIntent.getBroadcast
        // Nếu muốn click vào notification, Mở ra service(onStartCommand)
        val intentContent = Intent()
        intentContent.setClass(this, MainActivity::class.java)
        intentContent.setAction("FROM_SERVICE_ONLINE")
        val pendingIntentContent = PendingIntent.getActivity(
            this, 1,
            intentContent, PendingIntent.FLAG_IMMUTABLE
        )


        val intentPrevious = Intent()
        intentPrevious.setClass(this, MediaPlayerMusicOnlineService::class.java)
        intentPrevious.setAction("PREVIOUS")
        val pendingIntentPrevious = PendingIntent.getService(
            this, 1,
            intentPrevious, PendingIntent.FLAG_IMMUTABLE
        )

        val intentNext = Intent()
        intentNext.setClass(this, MediaPlayerMusicOnlineService::class.java)
        intentNext.setAction("NEXT")
        val pendingIntentNext = PendingIntent.getService(
            this, 1,
            intentNext, PendingIntent.FLAG_IMMUTABLE
        )

        val intentPause = Intent()
        intentPause.setClass(this, MediaPlayerMusicOnlineService::class.java)
        intentPause.setAction("PAUSE")
        val pendingIntentPause = PendingIntent.getService(
            this, 1,
            intentPrevious, PendingIntent.FLAG_IMMUTABLE
        )

        val style = androidx.media.app.NotificationCompat.MediaStyle()
        style.setShowActionsInCompactView(1)
        val noti = NotificationCompat.Builder(this, "NOTIFICATION")
            .setContentTitle("Music Fun")
            .setContentText(item.name + "-" + item.artis)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.media))
            .setSmallIcon(R.drawable.baseline_library_music_purple_500_24dp)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //set sự kiện, khi click vào notification(Mở MainActivity)
                //khi click vào Notification thì:
                    //1: onCreate nếu activity đó chết
                    //2: onNewIntent nếu activity đó đang sống.
            .setContentIntent(pendingIntentContent)
                //action
            .addAction(R.drawable.baseline_skip_previous_white_48dp, "previous", pendingIntentPrevious) // #0
            .addAction(R.drawable.baseline_play_arrow_white_48dp, "pause", pendingIntentPause) // #0
            .addAction(R.drawable.baseline_skip_next_white_48dp, "next", pendingIntentNext) // #0
                //set style: Media
            .setStyle(style)
            .build()

//        val mrg = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        mrg.notify(2, noti)

        startForeground(2, noti)

    }

}
