package com.saveurlife.goodnews.map

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.saveurlife.goodnews.GoodNewsApplication


class MapDownloader(private val context: Context) {

    private val downloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }
    private val sharedPref = GoodNewsApplication.preferences
    private var downloadId: Long = 0

    fun downloadFile(url: String, outputFilePath: String) {
        Log.d("mapDownloader", "파일 다운로드 함수 호출")
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("오프라인 지도 다운로드")
            setDescription("지도 파일을 다운로드 중입니다.")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, outputFilePath)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        downloadId = downloadManager.enqueue(request)
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) ?: return

            if (downloadId == id) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(statusIndex)
                    val message: String
                    if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            message = "다운로드를 성공적으로 완료하였습니다."
                            Log.i("DownLoadManager", "status_successful 된 경우에 출력되는 로그")
                            sharedPref.setBoolean("downloadedMap", true)
                        } else {
                            message = "다운로드가 완료되지 못했습니다."
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        unregisterReceiver()
                    }
                }
                cursor.close()
            }
        }
    }

    fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                null,
                null,
                Context.RECEIVER_EXPORTED
            )
        } else {
            context.registerReceiver(
                downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                null,
                null,
                Context.RECEIVER_VISIBLE_TO_INSTANT_APPS,
            )
        }
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(downloadReceiver)
    }
}