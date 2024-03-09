package com.saveurlife.goodnews.tmap

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.saveurlife.goodnews.R

class CustomToast(context: Context, message: String) {

    private val applicationContext: Context? = context?.applicationContext

    init {
        val inflater = LayoutInflater.from(applicationContext)
        val layout = inflater.inflate(R.layout.custom_toast_t_map_find_route, null)


        // 커스텀 레이아웃의 파라미터 설정
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.layoutParams = layoutParams


        layout.findViewById<TextView>(R.id.tMapToastTextView).text = message



        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.setView(layout)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}

