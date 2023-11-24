package com.saveurlife.goodnews.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
import kotlin.math.sqrt

class ConnectedUserMarkerOverlay (private val geoPoint: GeoPoint, private val onTap:(GeoPoint)->Unit) : Overlay() {

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
        canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 15f, paint)
    }

    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {

        val clickedPoint = mapView.projection.toPixels(geoPoint, null)

        // 터치 위치와 마커 위치 사이의 거리를 계산
        val dx = clickedPoint.x - e.x
        val dy = clickedPoint.y - e.y
        val distance = sqrt((dx * dx + dy * dy).toDouble())

        // 거리가 마커 반경 이내인 경우 onTap 콜백 실행
        val markerRadius = 60.0f // 마커의 반경 설정 (픽셀 단위)
        if (distance <= markerRadius) {
            onTap(geoPoint)
            Log.v("onSingleTapConfirmed", "반경 내 터치가 되었습니다.")

            return true // 이벤트가 처리되었음을 나타냄
        }
        return false // 이벤트가 처리되지 않았음을 나타냄
    }
}
