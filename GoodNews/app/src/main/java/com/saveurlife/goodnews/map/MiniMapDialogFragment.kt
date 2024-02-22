package com.saveurlife.goodnews.map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentMinimapDialogBinding
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

class MiniMapDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentMinimapDialogBinding
    private lateinit var mapView: MapView
    private lateinit var mapProvider: MapTileProviderArray

    private val provider: String = "Mapnik"
    private val minZoom: Int = 7
    private val localMaxZoom = 15
    private val pixel: Int = 256
    private val localMapTileArchivePath = "korea_7_13.sqlite"

    // 스크롤 가능 범위: 한국의 위경도 범위
    private val max = GeoPoint(38.6111, 131.8696)
    private val min = GeoPoint(33.1120, 124.6100)
    private val box = BoundingBox(max.latitude, max.longitude, min.latitude, min.longitude)

    override fun onViewCreated(view: View, userLocation: Bundle?) {
        super.onViewCreated(view, userLocation)

        mapView = view.findViewById(R.id.map) as MapView

        val tileSource = XYTileSource(
            provider,
            minZoom, localMaxZoom, pixel, ".png",
            arrayOf("http://127.0.0.1")
        )

        val simpleReceiver = SimpleRegisterReceiver(requireContext())

        try {
            val archives =
                arrayOf<IArchiveFile>(ArchiveFileFactory.getArchiveFile(getMapsFile(requireContext())))
            val moduleProvider = MapTileFileArchiveProvider(simpleReceiver, tileSource, archives)

            mapProvider = MapTileProviderArray(tileSource, null, arrayOf(moduleProvider)).apply {
                setUseDataConnection(false)
            }
            mapView.tileProvider = mapProvider
            mapView.setUseDataConnection(false)


        } catch (ex: Exception) {
            Log.e("MapFragmentSql", ex.message ?: "에러 발생")
        }

        val tilesOverlay = TilesOverlay(mapProvider, requireContext())


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
            mapView.controller.setZoom(13.0)
            mapView.controller.setCenter(
//                GeoPoint(userLocation.lat, userLocation.lon)
                // 임시로 서울 시청을 중심좌표로 설정
                GeoPoint(37.566535, 126.9779692)
            )

            // 타일 반복 방지
            mapView.isHorizontalMapRepetitionEnabled = false
            mapView.isVerticalMapRepetitionEnabled = false

            // 줌 컨트롤러 표시/숨김 설정 (+- 버튼)
            mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            // 기기 화면 DPI에 따라 스케일 DPI 적용(기기별로 보이는 지도 크기 최대한 유사하도록)
            mapView.isTilesScaledToDpi = false

            mapView.invalidate()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMinimapDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 미니맵 닫기 버튼 눌렀을 때
        binding.minimapClose.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun getMapsFile(context: Context): File {

        val resourceInputStream =
            context.resources.openRawResource(R.raw.korea_7_13) // 지도 파일 변경 시 수정3

        // 파일 경로
        val file = File(context.filesDir, localMapTileArchivePath)

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