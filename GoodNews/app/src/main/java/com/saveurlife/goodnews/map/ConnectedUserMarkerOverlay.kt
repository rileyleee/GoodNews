package com.saveurlife.goodnews.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

class ConnectedUserMarkerOverlay (private val geoPoint: GeoPoint) : Overlay() {

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        super.draw(canvas, mapView, shadow)

        // 마커의 스타일 설정
        val paint = Paint().apply {
            color = Color.BLUE
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        // 지리적 좌표를 화면 좌표로 변환
        val point = mapView.projection.toPixels(geoPoint, null)

        // 화면 좌표에 원(마커)을 그림
        canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 10f, paint)
    }
}