package com.saveurlife.goodnews.map

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderArray
import org.osmdroid.tileprovider.modules.ArchiveFileFactory
import org.osmdroid.tileprovider.modules.IArchiveFile
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MapFragment : Fragment(), LocationProvider.LocationUpdateListener {

    private lateinit var mapView: MapView
    private lateinit var mapProvider: MapTileProviderArray
    private lateinit var locationProvider: LocationProvider
    private var currGeoPoint: GeoPoint? = null


    private val mapTileArchivePath = "daejeon_ssafy.sqlite" // 지도 파일 변경 시 수정1

    // 스크롤 가능 범위: 한국의 위경도 범위
    val max = GeoPoint(38.6111, 131.8696)
    val min = GeoPoint(33.1120, 124.6100)
    val box = BoundingBox(max.latitude, max.longitude, min.latitude, min.longitude)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationProvider = LocationProvider(requireContext())

        // 콜백 설정
        locationProvider.setLocationUpdateListener(this)

        // 사용자 위치 정보 받아오는 메서드
        locationProvider.initLocationClient()

        mapView = view.findViewById(R.id.map) as MapView

        val context = requireContext()
        Configuration.getInstance().load(context, GoodNewsApplication.preferences.preferences)

        // 현재 기기에서 보이는 지도의 중심 좌표
        val projection = mapView.projection
        val centerGeoPoint = projection.fromPixels(mapView.width / 2, mapView.height / 2)
        val centerLat = centerGeoPoint.latitude
        val centerLon = centerGeoPoint.longitude

//        // 현재 내 위치(나중에 자체 트래킹할 예정)
//        var currGeoPoint = GeoPoint(36.37610711596839, 127.3952801221493)

        val tileSource = XYTileSource(
            "4uMaps", // 지도 파일 변경 시 수정2 (Mapnik: OSM에서 가져온 거 또는 4uMaps: MOBAC에서 가져온 거 // => sqlite 파일의 provider 값)
            12, 15, 256, ".png",
            arrayOf("http://127.0.0.1")
        )
        val simpleReceiver = SimpleRegisterReceiver(context)

        try {
            val archives =
                arrayOf<IArchiveFile>(ArchiveFileFactory.getArchiveFile(getMapsFile(context)))
            val moduleProvider = MapTileFileArchiveProvider(simpleReceiver, tileSource, archives)
            mapProvider = MapTileProviderArray(tileSource, null, arrayOf(moduleProvider)).apply {
                setUseDataConnection(false)
            }
            mapView.tileProvider = mapProvider
            mapView.setUseDataConnection(false)


        } catch (ex: Exception) {
            Log.e("MapFragmentSql", ex.message ?: "에러 발생")
        }

        val tilesOverlay = TilesOverlay(mapProvider, context)


        mapView.apply {

            mapView.overlays.add(tilesOverlay)

            mapView.zoomController.setZoomInEnabled(true)
            mapView.zoomController.setZoomOutEnabled(true)

            // 멀티 터치 설정 (터치로 줌)
            mapView.setMultiTouchControls(true)

            // 스크롤 가능한 범위 제한 (대한민국으로 한정)
            mapView.setScrollableAreaLimitDouble(box)

            // 로딩 시, 타일 색상 지정
            mapView.overlayManager.tilesOverlay.loadingBackgroundColor = Color.GRAY
            mapView.overlayManager.tilesOverlay.loadingLineColor = Color.BLACK

            // 중심좌표 및 배율 설정
            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(GeoPoint(36.37497534353303, 127.3914186217678))

            // 타일 반복 방지
            mapView.isHorizontalMapRepetitionEnabled = false
            mapView.isVerticalMapRepetitionEnabled = false

            // 줌 컨트롤러 표시/숨김 설정 (+- 버튼)
            mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            // 기기 화면 DPI에 따라 스케일 DPI 적용(기기별로 보이는 지도 크기 최대한 유사하도록)
            mapView.isTilesScaledToDpi = false

        }

    }

    @Throws(IOException::class)
    private fun getMapsFile(context: Context): File {
        val resourceInputStream =
            context.resources.openRawResource(R.raw.daejeon_ssafy) // 지도 파일 변경 시 수정3

        // 파일 경로
        val file = File(context.filesDir, mapTileArchivePath)

        // 파일이 이미 존재하지 않는 경우에만 복사 진행
        if (!file.exists()) {
            resourceInputStream.use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (input.read(buffer).also { length = it } != -1) {
                        output.write(buffer, 0, length)
                    }
                }
            }
        }
        return file
    }

    fun updateCurrentLocation(geoPoint: GeoPoint) {
        currGeoPoint?.let {
            val markerOverlay = MarkerOverlay(it)
            mapView.overlays.add(markerOverlay)
            mapView.invalidate() // 지도 다시 그려서 오버레이 보이게 함
        }
    }

    // 위치 변경 시 위경도 받아옴
    override fun onLocationChanged(location: Location) {
        currGeoPoint = GeoPoint(location.latitude, location.longitude)
        Toast.makeText(
            context,
            "현재 위경도가 바뀌었습니다: 위도: ${location.latitude} 경도: ${location.longitude}",
            Toast.LENGTH_SHORT
        ).show()
        currGeoPoint?.let {
            updateCurrentLocation(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

}