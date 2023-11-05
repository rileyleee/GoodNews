package com.saveurlife.goodnews.map

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
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

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var mapProvider: MapTileProviderArray
    private val REQUEST_PERMISSION_REQDUSET_CODE = 1
    private val mapTileArchivePath = "offline_daejeon2.sqlite" // 지도 파일 변경 시 수정1
    val max = GeoPoint(38.6111, 131.8696)
    val min = GeoPoint(33.1120, 124.6100)
    val box = BoundingBox(max.latitude, max.longitude, min.latitude, min.longitude)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.map) as MapView

        val context = requireContext()
        Configuration.getInstance().load(context, GoodNewsApplication.preferences.preferences)

        val tileSource = XYTileSource(
            "4uMaps", // 지도 파일 변경 시 수정2 (Mapnik: OSM에서 가져온 거 또는 4uMaps: MOBAC에서 가져온 거 => sqlite 파일의 provider 값)
            12, 15, 256, ".png",
            arrayOf("http://127.0.0.1")
        )
        val simpleReceiver = SimpleRegisterReceiver(context)

        try {
            val archives = arrayOf<IArchiveFile>(ArchiveFileFactory.getArchiveFile(getMapsFile(context)))
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
            mapView.controller.setZoom(15.0)
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

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
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

    @Throws(IOException::class)
    private fun getMapsFile(context: Context): File {
        val resourceInputStream = context.resources.openRawResource(R.raw.offline_daejeon2) // 지도 파일 변경 시 수정3

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
}