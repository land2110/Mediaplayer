package com.t3h.demomediaplayer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.t3h.demomediaplayer.databinding.ActivityMainBinding
import com.t3h.demomediaplayer.fragment.ListMusicOfflineFragment
import com.t3h.demomediaplayer.fragment.MusicOnlineFragment

class MainActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(
                R.id.content,
                ListMusicOfflineFragment(),
                ListMusicOfflineFragment::class.java.name
            )
            .commit()

        binding.btnMenu.setOnClickListener {
            binding.drawer.openDrawer(Gravity.LEFT)
        }
        binding.tvMusicOffline.setOnClickListener(this)
        binding.tvMusicOnline.setOnClickListener(this)
        binding.btnSearch.setOnClickListener(this)
        binding.edtSearch.addTextChangedListener(this)
    }

    override fun onBackPressed() {
        finish()
    }

    fun getCurrentFragment() : Fragment?{
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null && fragment.isVisible){
                return fragment;
            }
        }
        return null
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.tv_music_offline->{
                binding.drawer.closeDrawer(Gravity.LEFT)
                val fg = getCurrentFragment()
                if (fg!= null && fg is ListMusicOfflineFragment ){
                    return
                }
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.content,
                        ListMusicOfflineFragment(),
                        ListMusicOfflineFragment::class.java.name
                    )
                    .commit()
            }
            R.id.tv_music_online->{
                binding.drawer.closeDrawer(Gravity.LEFT)
                val fg = getCurrentFragment()
                if (fg!= null && fg is MusicOnlineFragment ){
                    return
                }
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.content,
                        MusicOnlineFragment(),
                        MusicOnlineFragment::class.java.name
                    )
                    .commit()
            }
            R.id.btn_search->{
                val fg = getCurrentFragment()
                if (fg!= null && fg is MusicOnlineFragment ){
                    var contentSearch = binding.edtSearch.text.toString().trim()
                    if ("".equals(contentSearch)){
                        fg.getDataSyn()
                    }else {
                        fg.getDataSearch(
                            "https://chiasenhac.vn/tim-kiem?q=${contentSearch.replace(" ","%20")}"
                        )
                    }
                }
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(ed: Editable) {
        val fg = getCurrentFragment()
        if (fg!= null && fg is MusicOnlineFragment ){
            var contentSearch = binding.edtSearch.text.toString().trim()
            if ("".equals(contentSearch)){
                fg.getDataSyn()
            }else {
                fg.getDataSearch(
                    "https://chiasenhac.vn/tim-kiem?q=${contentSearch.replace(" ","%20")}"
                )
            }
        }
    }
}