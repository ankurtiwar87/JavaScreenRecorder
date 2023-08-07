package com.ankur.javascreenrecorder

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class VideoListActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var videoListView: ListView
    private lateinit var surfaceView: SurfaceView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var videoList: ArrayList<String>
    private var currentVideoIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        videoListView = findViewById(R.id.videoListView)
        surfaceView = findViewById(R.id.surfaceView)
        surfaceView.holder.addCallback(this)

        videoList = getVideoList()
        val videoListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, videoList)
        videoListView.adapter = videoListAdapter

        // Set item click listener to play the selected video
        videoListView.setOnItemClickListener { _, _, position, _ ->
            currentVideoIndex = position
            playCurrentVideo()
        }

        // Start playing the first video
        playCurrentVideo()
    }

    private fun getVideoList(): ArrayList<String> {
        val videoList = ArrayList<String>()
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val selection = "${MediaStore.Video.Media.DATA} like ?"
        val selectionArgs = arrayOf("%/Movies/%")
        val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            while (it.moveToNext()) {
                val videoPath = it.getString(columnIndex)
                videoList.add(videoPath)
            }
        }

        return videoList
    }


    private fun playCurrentVideo() {
        if (currentVideoIndex >= 0 && currentVideoIndex < videoList.size) {
            val videoPath = videoList[currentVideoIndex]
            playVideo(videoPath)
        }
    }





    private fun playVideo(videoPath: String) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(videoPath)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mediaPlayer.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDisplay(holder)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
