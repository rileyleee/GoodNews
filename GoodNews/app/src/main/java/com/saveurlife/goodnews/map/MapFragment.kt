package com.saveurlife.goodnews.map

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentMapBinding
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
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme


class MapFragment : Fragment(), LocationProvider.LocationUpdateListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var mapProvider: MapTileProviderArray
    private lateinit var locationProvider: LocationProvider
    private lateinit var facilityProvider: FacilityProvider
    private lateinit var currGeoPoint: GeoPoint

    private val mapTileArchivePath = "korea_7_13.sqlite" // 지도 파일 변경 시 수정1
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    // 타일 provider, 최소 줌 및 해상도 설정
    val provider: String =
        "Mapnik" // 지도 파일 변경 시 수정2 (Mapnik: OSM에서 가져온 거 또는 4uMaps: MOBAC에서 가져온 거 // => sqlite 파일의 provider 값)
    val minZoom: Int = 7
    val maxZoom: Int = 13
    val pixel: Int = 256

    // 스크롤 가능 범위: 한국의 위경도 범위
    val max = GeoPoint(38.6111, 131.8696)
    val min = GeoPoint(33.1120, 124.6100)
    val box = BoundingBox(max.latitude, max.longitude, min.latitude, min.longitude)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)

        // BottomSheetBehavior 초기화 및 설정
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // BottomSheet 상태 변경에 따른 로직
            }

            override fun onSlide(bottomSheetView: View, slideOffset: Float) {
                // 슬라이드에 따른 UI 변화 처리
            }
        })

        return binding.root
    }

    // 임시 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        mapView = view.findViewById(R.id.map) as MapView

        // 현재 내 위치 정보 제공자
        locationProvider = LocationProvider(requireContext())

        // 오프라인 시설 정보 제공자
        facilityProvider = FacilityProvider(requireContext())
        locationProvider.initLocationClient()

        // 콜백 설정
        locationProvider.setLocationUpdateListener(this)


        val context = requireContext()
        Configuration.getInstance().load(context, GoodNewsApplication.preferences.preferences)

        // 현재 기기에서 보이는 지도의 중심 좌표
        val projection = mapView.projection
        val centerGeoPoint = projection.fromPixels(mapView.width / 2, mapView.height / 2)
        val centerLat = centerGeoPoint.latitude
        val centerLon = centerGeoPoint.longitude

        val tileSource = XYTileSource(
            provider,
            minZoom, maxZoom, pixel, ".png",
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

        addFacilitiesToMap()

        // 정보 공유 버튼 클릭했을 때
        binding.emergencyAddButton.setOnClickListener {
            showEmergencyDialog()
        }

        // BottomSheetBehavior 설정
        val bottomSheet = view.findViewById<View>(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // 하단 시트가 확장된 경우 mapMainContents의 자식들을 비활성화
                        binding.mapMainContents.isEnabled = false
                        disableEnableControls(false, binding.mapMainContents)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // 하단 시트가 축소된 경우 mapMainContents의 자식들을 활성화
                        binding.mapMainContents.isEnabled = true
                        disableEnableControls(true, binding.mapMainContents)
                    }
                    // 다른 상태에 대한 처리...
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드에 따른 UI 변화 처리
            }
        })


        // 뷰의 레이아웃이 완료된 후에 높이를 계산
        bottomSheet.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 레이아웃 리스너 제거
                bottomSheet.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // 프래그먼트의 전체 높이를 얻음
                val fragmentHeight = view.height

                // 64dp를 픽셀로 변환
                val expandedOffsetPixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 64f, resources.displayMetrics
                ).toInt()

                // expandedOffset을 뺀 높이를 계산
                val expandedHeight = fragmentHeight - expandedOffsetPixels

                // BottomSheet의 높이를 설정
                val layoutParams = bottomSheet.layoutParams
                layoutParams.height = expandedHeight
                bottomSheet.layoutParams = layoutParams
            }
        })

    }

    @Throws(IOException::class)
    private fun getMapsFile(context: Context): File {
        val resourceInputStream =
            context.resources.openRawResource(R.raw.korea_7_13) // 지도 파일 변경 시 수정3

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

    // 위치 변경 시 위경도 받아옴
    override fun onLocationChanged(location: Location) {
        currGeoPoint = GeoPoint(location.latitude, location.longitude)

        currGeoPoint?.let {
            updateCurrentLocation(it)
        }
    }

    // 현재 위치 마커로 찍기
    fun updateCurrentLocation(geoPoint: GeoPoint) {
        Log.v("현재 위치", "$geoPoint")
        val myLocationMarkerOverlay = MyLocationMarkerOverlay(geoPoint)
        mapView.overlays.add(myLocationMarkerOverlay)
        mapView.invalidate() // 지도 다시 그려서 오버레이 보이게 함
    }

    // 시설 FastSimplyOverlay 설정
    private fun createOverlayWithOptions(pointTheme: SimplePointTheme): SimpleFastPointOverlay {
        // 오버레이 옵션 설정
        val textStyle = Paint().apply {
            style = Paint.Style.FILL
            color = Color.BLUE
//            textAlign = Paint.Align.CENTER
//            textSize = 24f
            isAntiAlias = true

        }

        Log.d("Overlay 설정", "오버레이 글씨 스타일")

        val opt = SimpleFastPointOverlayOptions.getDefaultStyle().apply {
            setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
            setSymbol(SimpleFastPointOverlayOptions.Shape.CIRCLE)
            setRadius(10.0F)
            setIsClickable(true)
            setCellSize(15)
//            setTextStyle(textStyle)
        }

        Log.d("Overlay 설정", "오버레이 크기 스타일 설정")

        val overlay = SimpleFastPointOverlay(pointTheme, opt)
        overlay.setOnClickListener(SimpleFastPointOverlay.OnClickListener { points, point ->
            Log.d("시설정보 로그찍기", "${points.get(point)}")
            Toast.makeText(context, "시설정보: ${points.get(point)}", Toast.LENGTH_SHORT).show()
        })

        Log.d("Overlay 설정", "오버레이 최종 선언")

        return overlay
    }


    // 시설 위치 마커로 찍는 함수
    private fun addFacilitiesToMap() {
        Log.d("FacilityMarker", "시설 위치 마커로 그리는 거 호출 완료")
        val facilitiesOverlayItems = facilityProvider.getFacilityData()
        val pointTheme = SimplePointTheme(facilitiesOverlayItems, true)

        val facMarkerOverlay = createOverlayWithOptions(pointTheme)
        mapView.overlays.add(facMarkerOverlay)
        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        locationProvider.stopLocationUpdates() // 위치 정보 업데이트 중지
    }


    private fun showEmergencyDialog() {
        val dialogFragment = EmergencyInfoDialogFragment()
        dialogFragment.show(childFragmentManager, "EmergencyInfoDialogFragment")
    }

    private fun disableEnableControls(enable: Boolean, vg: ViewGroup) {
        for (i in 0 until vg.childCount) {
            val child = vg.getChildAt(i)
            child.isEnabled = enable
            if (child is ViewGroup) {
                disableEnableControls(enable, child)
            }
        }
    }
}