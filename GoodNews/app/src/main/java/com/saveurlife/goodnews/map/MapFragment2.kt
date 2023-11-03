package com.saveurlife.goodnews.map

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

class MapFragment2() : Fragment() {
    // 네트워크 연결 시 작동

    private val REQUEST_PERMISSION_REQDUSET_CODE = 1
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_map2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // OSMDROID 초기화
        Configuration.getInstance()
            .load(context, GoodNewsApplication.preferences.preferences)

        map = view.findViewById<MapView>(R.id.mapView)

        // 중심좌표 및 배율 설정
        map.controller.setZoom(15.0)
        map.controller.setCenter(GeoPoint(36.3504119, 127.3845475))

        // 타일 소스 설정
        map.setTileSource(TileSourceFactory.MAPNIK)

        // 타일 반복 방지
        map.isHorizontalMapRepetitionEnabled = false
        map.isVerticalMapRepetitionEnabled = false

        // 로딩 시, 타일 색상 지정
        map.overlayManager.tilesOverlay.loadingBackgroundColor = Color.GRAY
        map.overlayManager.tilesOverlay.loadingLineColor = Color.BLACK

        // 스크롤 가능한 범위 제한 (대한민국으로 한정)
        val max = GeoPoint(38.6111, 131.8696)
        val min = GeoPoint(33.1120, 124.6100)
        val box = BoundingBox(max.latitude, max.longitude, min.latitude, min.longitude)
        map.setScrollableAreaLimitDouble(box)


        // 줌 컨트롤러 표시/숨김 설정 (+- 버튼)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        // 멀티 터치 설정 (터치로 줌)
        map.setMultiTouchControls(true)


        // 기기 화면 DPI에 따라 스케일 DPI 적용(기기별로 보이는 지도 크기 최대한 유사하도록)
        map.isTilesScaledToDpi = false
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSION_REQDUSET_CODE
            )
        }
    }
}