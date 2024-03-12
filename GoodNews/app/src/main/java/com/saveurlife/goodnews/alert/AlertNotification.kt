package com.saveurlife.goodnews.alert

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.saveurlife.goodnews.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlertNotification(private val context: Context) {
    //가족 장소 등록
    open fun foresendFamilyPlaceRegistNotification(seq: Int, name: String) {
        Handler(Looper.getMainLooper()).post {
            val inflater = LayoutInflater.from(context)
            val layout: View = inflater.inflate(R.layout.custom_toast_family_place, null)

            // 커스텀 레이아웃의 파라미터 설정
            val layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layout.layoutParams = layoutParams

            // 커스텀 레이아웃의 뷰에 접근하여 설정
            val content = layout.findViewById<TextView>(R.id.toast_family_place)
            val time = layout.findViewById<TextView>(R.id.toast_family_place_time)

            var seqKor = ""
            if(seq == 1){
                seqKor = "첫번째"
            }else if(seq == 2){
                seqKor = "두번째"
            }else if(seq == 3){
                seqKor == "세번째"
            }

            content.text = "${name}님이 $seqKor 모임 장소를 등록했습니다."

            val currentTime = SimpleDateFormat("a hh:mm", Locale.KOREA)
                .format(Calendar.getInstance().time)
            time.text = currentTime

            // 시스템 알림 사운드 재생
            try {
                val mediaPlayer: MediaPlayer =
                    MediaPlayer.create(context, R.raw.toast_alarm)
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_SHORT

            toast.setView(layout)

            toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.show()
        }
    }

    fun foresendFamilyPlaceUpdateNotification(seq: String, name: String) {
        Handler(Looper.getMainLooper()).post {
            val inflater = LayoutInflater.from(context)
            val layout: View = inflater.inflate(R.layout.custom_toast_family_place, null)

            // 커스텀 레이아웃의 파라미터 설정
            val layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layout.layoutParams = layoutParams

            // 커스텀 레이아웃의 뷰에 접근하여 설정
            val content = layout.findViewById<TextView>(R.id.toast_family_place)
            val time = layout.findViewById<TextView>(R.id.toast_family_place_time)


            var temp = seq.split("/")

            var changeSeq: String = ""
            var changeAddress: String = ""
            var changeSafe: String = ""

            if(temp[0] == "1"){
                changeSeq = "첫번째"
            }else if(temp[0] == "2"){
                changeSeq = "두번째"
            }else if(temp[0] == "3"){
                changeSeq = "세번째"
            }

            if(temp[1] == "주소"){
                changeAddress = "주소"
            }

            if(temp[2] == "안전"){
                changeSafe = "안전"
            }else if(temp[2] == "위험"){
                changeSafe = "위험"
            }

            if(changeAddress == "주소" && changeSafe.isEmpty()){
                content.text = "${name}님이 $changeSeq 모임 장소를 변경했습니다."
            }else if(changeAddress.isEmpty() && changeSafe.isNotEmpty()){
                content.text = "${name}님이 $changeSeq 모임 장소의 상태를 ${changeSafe}으로 변경했습니다."
            }else if(changeAddress.isNotEmpty() && changeSafe.isNotEmpty()){
                content.text = "${name}님이 $changeSeq 모임 장소 변경 및 상태를 ${changeSafe}으로 변경했습니다."
            }

            val currentTime = SimpleDateFormat("a hh:mm", Locale.KOREA)
                .format(Calendar.getInstance().time)
            time.text = currentTime

            // 시스템 알림 사운드 재생
            try {
                val mediaPlayer: MediaPlayer =
                    MediaPlayer.create(context, R.raw.toast_alarm)
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_SHORT

            toast.setView(layout)

            toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.show()
        }
    }
}