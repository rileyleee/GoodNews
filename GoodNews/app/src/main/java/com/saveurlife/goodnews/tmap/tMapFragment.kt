package com.saveurlife.goodnews.tmap

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saveurlife.goodnews.databinding.FragmentTMapBinding
import com.skt.tmap.TMapData
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem

class TMapFragment : Fragment() {
    private lateinit var binding: FragmentTMapBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTMapBinding.inflate(inflater, container, false)

        val tMapView = TMapView(requireContext())
        binding.tmapViewContainer.addView(tMapView)

        tMapView.setSKTMapApiKey("Fx1ES52HYL4iTsJF748Mxa8E9UlbyX7s46tkycR7")
//        tMapView.isCompassMode = true // 나침반 모드 설정
//        tMapView.setIconVisibility(true) // 현재 위치 아이콘 표시 설정
//        tMapView.isTrackingMode = true // 트래킹 모드 설정



        tMapView.setOnMapReadyListener(object : TMapView.OnMapReadyListener {
            override fun onMapReady() {
                // 맵 로딩 완료 후 구현
                // 내가 원하는 위치로
                tMapView.setCenterPoint(37.494473, 127.120742); //중심점 변경
                tMapView.zoomLevel = 10; //줌 레벨 설정


                val marker = TMapMarkerItem()
                marker.id = "marker1"
                marker.setTMapPoint(37.494473, 127.120742)
                marker.icon = BitmapFactory.decodeResource(resources, android.R.drawable.ic_dialog_map)
                tMapView.addTMapMarkerItem(marker)

//                val pointList = ArrayList<TMapPoint>()
//                pointList.add(TMapPoint(37.472678, 126.920928))
//                pointList.add(TMapPoint(37.494473, 127.120742))
//                pointList.add(TMapPoint(37.405619, 127.091903))
//
//                val line = TMapPolyLine("line1", pointList)
//                tMapView.addTMapPolyLine(line)

                val tMapPointStart = TMapPoint(37.570841, 126.985302) // SKT타워(출발지)

                val startPoint = TMapPoint(37.570841, 126.985302)
                val endPoint = TMapPoint(37.551135, 126.988205)

                TMapData().findPathData(
                    startPoint, endPoint
                ) { tMapPolyLine ->
                    tMapPolyLine.lineWidth = 3f
                    tMapPolyLine.lineColor = Color.BLUE
                    tMapPolyLine.lineAlpha = 255
                    tMapPolyLine.outLineWidth = 5f
                    tMapPolyLine.outLineColor = Color.RED
                    tMapPolyLine.outLineAlpha = 255
                    tMapView.addTMapPolyLine(tMapPolyLine)
                    val info = tMapView.getDisplayTMapInfo(tMapPolyLine.linePointList)
                    tMapView.zoomLevel = info.zoom
                    tMapView.setCenterPoint(info.point.latitude, info.point.longitude)
                }

            }
        })


        return binding.root
    }
}