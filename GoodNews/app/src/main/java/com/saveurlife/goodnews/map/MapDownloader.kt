package com.saveurlife.goodnews.map

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast

class MapDownloader {

    fun downloadMapFile(context: Context, url:String, fileName: String){
        val downloadRequest = DownloadManager.Request(Uri.parse(url))
            .setTitle("지도 다운로드")
            .setDescription("상세한 지도를 저장하는 중입니다.")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(downloadRequest)

        // 다운로드 완료 감지
        val onDownloadComplete = object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)
                if(id==downloadId){
                    Toast.makeText(context, "지도 다운로드가 완료되었습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
        context.registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }
}