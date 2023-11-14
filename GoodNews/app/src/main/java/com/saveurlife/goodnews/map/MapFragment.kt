package com.saveurlife.goodnews.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentMapBinding
import com.saveurlife.goodnews.models.FacilityUIType
import com.saveurlife.goodnews.models.OffMapFacility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MapFragment : Fragment(), LocationProvider.LocationUpdateListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var mapProvider: MapTileProviderArray
    private lateinit var locationProvider: LocationProvider
    private lateinit var facilityProvider: FacilityProvider
    private lateinit var currGeoPoint: GeoPoint
    private lateinit var screenRect: BoundingBox

    // 이전 마커에 대한 참조를 저장할 변수
    private var previousLocationOverlay: MyLocationMarkerOverlay? = null

    // 추가 코드
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: FacilityCategoryAdapter
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var listAdapter: FacilityListAdapter
    private var selectedCategory: FacilityUIType = FacilityUIType.ALL

    private val mapTileArchivePath = "korea_7_13.sqlite" // 지도 파일 변경 시 수정1
    // private val mapTileArchivePath = "7_15_korea-001.sqlite"
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    // 타일 provider, 최소 줌 및 해상도 설정
    val provider: String =
        "Mapnik" // 지도 파일 변경 시 수정2 (Mapnik: OSM에서 가져온 거 또는 4uMaps: MOBAC에서 가져온 거 // => sqlite 파일의 provider 값)
    val minZoom: Int = 7
    val maxZoom: Int = 13
//    val maxZoom: Int = 15
    val pixel: Int = 256

    // 스크롤 가능 범위: 한국의 위경도 범위
    val max = GeoPoint(38.6111, 131.8696)
    val min = GeoPoint(33.1120, 124.6100)
    val box = BoundingBox(max.latitude, max.longitude, min.latitude, min.longitude)

    val sharedPref = GoodNewsApplication.preferences
    var lastLat = sharedPref.getDouble("lastLat", 37.566535)
    var lastLon = sharedPref.getDouble("lastLon", 126.9779692)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val preferencesUtil = GoodNewsApplication.preferences

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

        // 추가 코드 (recyclerView / divider)
        categoryRecyclerView = binding.facilityTypeList
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = FacilityCategoryAdapter(getCategories()) { category ->
            selectedCategory = category
            // 선택된 카테고리 처리 로직, 예를 들어 다른 RecyclerView를 업데이트하거나 지도에 마커를 표시하는 등
            handleSelectedCategory(category)
            Log.d("CategorySelected", "Selected category: ${category.displayName}")
            Log.d("test", "바뀌면 안됨" + categoryAdapter.toString())


        }
        categoryRecyclerView.adapter = categoryAdapter


        listRecyclerView = binding.facilityListWrap
        listRecyclerView.layoutManager = LinearLayoutManager(context)
        val facilities = getFacilityListData()
        listAdapter = FacilityListAdapter(facilities, preferencesUtil)
        listRecyclerView.adapter = listAdapter

        val dividerItemDecoration =
            DividerItemDecoration(listRecyclerView.context, LinearLayoutManager.VERTICAL)
        listRecyclerView.addItemDecoration(dividerItemDecoration)


        // 처음에 "전체" 카테고리가 선택되도록 합니다.
        handleSelectedCategory(FacilityUIType.ALL)


        return binding.root
    }

    // 임시 코드
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.map) as MapView

        // 현재 내 위치 정보 제공자
        locationProvider = LocationProvider(requireContext())
        locationProvider.initLocationClient()

        // 오프라인 시설 정보 제공자
        facilityProvider = FacilityProvider(requireContext())

        // 콜백 설정
        locationProvider.setLocationUpdateListener(this)


        val context = requireContext()
        Configuration.getInstance().load(context, GoodNewsApplication.preferences.preferences)

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
            mapView.controller.setCenter(
                GeoPoint(lastLat, lastLon)
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

        // 지도 초기화 후 화면 경계 초기 값 설정
        mapView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mapView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                screenRect = mapView.boundingBox // 초기화

                Log.v("screenRect", "$screenRect")

                // 지도 위에 시설 정보 그리기
                addFacilitiesToMap()

            }
        })

        // 사용자가 터치할 때마다 경계 변경
        mapView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // 사용자가 화면을 터치하고 뗄 때마다 호출
                screenRect = mapView.boundingBox

                Log.v("screenRect", "$screenRect")

                // 지도 위에 시설 정보 그리기
                addFacilitiesToMap()
            }
            false
        }


        // 내 위치로 이동 버튼 클릭했을 때
        binding.findMyLocationButton.setOnClickListener {

            it.isSelected = true
            val drawable = binding.findMyLocationButton.drawable

            // 아이콘에 색상 필터 적용 (예: 흰색으로 변경)
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

            // 첫 번째 깜빡임 후 원래 상태로 되돌림
            it.postDelayed({
                it.isSelected = false
                drawable.clearColorFilter()

                // 두 번째 깜빡임 시작
                it.postDelayed({
                    it.isSelected = true
                    drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

                    // 두 번째 깜빡임 후 원래 상태로 되돌림
                    it.postDelayed({
                        it.isSelected = false
                        drawable.clearColorFilter()
                    }, 150)
                }, 150)
            }, 150)

            findLatestLocation()
        }

        // 정보 공유 버튼 클릭했을 때
        binding.emergencyAddButton.setOnClickListener {
            showEmergencyDialog(currGeoPoint)
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
                        bottomSheet.setOnTouchListener { _, _ -> true }
                        disableEnableControls(false, binding.mapMainContents)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // 하단 시트가 축소된 경우 mapMainContents의 자식들을 활성화
                        binding.mapMainContents.isEnabled = true
                        bottomSheet.setOnTouchListener(null)
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


        // 서버에서 저장한 파일 경로
//        val file =
//            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), mapTileArchivePath)
//
//        // 파일이 존재하는지 확인하고 존재하지 않으면 오류 메시지를 표시합니다.
//        if (!file.exists()) {
//            throw IOException("지도 파일이 존재하지 않습니다: ${file.absolutePath}")
////            val resourceInputStream =
////                context.resources.openRawResource(R.raw.korea_7_13) // 지도 파일 변경 시 수정3
////              //파일 경로
////            val file = File(context.filesDir, mapTileArchivePath)
////
////            // 파일이 이미 존재하지 않는 경우에만 복사 진행
////            if (!file.exists()) {
////                resourceInputStream.use { input ->
////                    FileOutputStream(file).use { output ->
////                        val buffer = ByteArray(1024)
////                        var length: Int
////                        while (input.read(buffer).also { length = it } != -1) {
////                            output.write(buffer, 0, length)
////                        }
////                    }
////                }
////            }
//        }
//        return file
    }

    // 위치 변경 시 위경도 받아옴
    override fun onLocationChanged(location: Location) {
        currGeoPoint = GeoPoint(location.latitude, location.longitude)

        // sharedPreference에 저장
        GoodNewsApplication.preferences.setDouble("lastLat", location.latitude)
        GoodNewsApplication.preferences.setDouble("lastLon", location.longitude)
        Log.d("마지막 위치 저장완료", "위도: $lastLat, 경도: $lastLon")

        currGeoPoint?.let {
            updateCurrentLocation(it)
        }
    }

    // 현재 위치 마커로 찍기
    fun updateCurrentLocation(geoPoint: GeoPoint) {
        // 이전 위치 오버레이가 있으면 지도에서 제거
        previousLocationOverlay?.let {
            Log.d("updateCurrentLocation", "이전 내 위치 마커를 삭제했습니다.")
            mapView.overlays.remove(it)
        }
        Log.v("현재 위치", "$geoPoint")
        val myLocationMarkerOverlay = MyLocationMarkerOverlay(geoPoint)
        mapView.overlays.add(myLocationMarkerOverlay)
        mapView.invalidate() // 지도 다시 그려서 오버레이 보이게 함

        // 새 위치를 다시 이전 위치 마커에 반영
        previousLocationOverlay = myLocationMarkerOverlay
    }


    // 데이터를 가진 SimpleFastOverlays

    private fun createOverlayWithOptions(facilities: List<OffMapFacility>): SimpleFastPointOverlay {
        // 시설 목록을 LabelledGeoPoint 목록으로 변환
        val points = facilities.map { facility ->
            LabelledGeoPoint(facility.latitude, facility.longitude, facility.name)
        }
        val pointTheme = SimplePointTheme(points, true)

        // 오버레이 옵션 설정
        val opt = SimpleFastPointOverlayOptions.getDefaultStyle().apply {
            algorithm = SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION
            symbol = SimpleFastPointOverlayOptions.Shape.SQUARE
            setRadius(5.0f)
            setIsClickable(true)
            cellSize = 15
            // 텍스트 스타일 설정을 제거하거나 투명하게 설정
            textStyle = Paint().apply {
                color = Color.TRANSPARENT
                textSize = 0f
            }
            pointStyle = Paint().apply {
                color = Color.YELLOW
                style = Paint.Style.FILL
                isAntiAlias = true
            }
        }

        // 오버레이 생성 및 클릭 리스너 설정
        val overlay = SimpleFastPointOverlay(pointTheme, opt).apply {
            setOnClickListener { _, index ->
                val facility = facilities[index]
                Toast.makeText(
                    context,
                    "시설이름: ${facility.name} 시설타입: ${facility.type}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return overlay
    }

    // 이전 시설 마커 관리하기 위한 리스트
    private val previousFacilityOverlayItems = mutableListOf<SimpleFastPointOverlay>()

    // 시설 위치 마커로 찍는 함수 내부에서 사용
    private fun addFacilitiesToMap() {

        // 마커로 찍을 시설 목록 필터링
        val facilitiesOverlayItems = facilityProvider.getFacilityData()
            .filter { screenRect.contains(GeoPoint(it.latitude, it.longitude)) }

        // 기존에 표시된 마커 제거
        previousFacilityOverlayItems.forEach { previousOverlay ->
            mapView.overlays.remove(
                previousOverlay
            )
        }
        // 리스트 초기화
        previousFacilityOverlayItems.clear()

        // 새 오버레이 생성
        val overlay = createOverlayWithOptions(facilitiesOverlayItems)

        // 오버레이를 지도에 추가
        mapView.overlays.add(overlay)
        mapView.invalidate()

        // 현재 보이는 범위에 있는 시설 정보를 이전 마커로 새로 등록
        previousFacilityOverlayItems.add(overlay)
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


    private fun showEmergencyDialog(currGeoPoint: GeoPoint) {
        val dialogFragment = EmergencyInfoDialogFragment()

        // 현재 위치를 정보 공유 fragment로 전달
        val location = Bundle()
        location.putDouble("latitude", currGeoPoint.latitude)
        location.putDouble("longitude", currGeoPoint.longitude)

        dialogFragment.arguments = location

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

    // 첫 번째 RecyclerView의 데이터를 가져오는 메서드 (추가)
    private fun getCategories(): List<FacilityUIType> {
        return FacilityUIType.values().toList()
    }

    private fun getFacilityListData(): List<OffMapFacility> {
        // 실제 데이터를 반환 (임시 데이터...)
        return listOf(
            OffMapFacility().apply {
                id = 1
                type = "병원"
                name = "행복한 병원"
                longitude = 127.001
                latitude = 37.564
                canUse = true
                addInfo = "24시간 운영"
            },
            OffMapFacility().apply {
                id = 2
                type = "소방서"
                name = "안전한 소방서"
                longitude = 127.002
                latitude = 37.565
                canUse = true
                addInfo = "긴급 구조 전문"
            },
            OffMapFacility().apply {
                id = 3
                type = "경찰서"
                name = "믿음직한 경찰서"
                longitude = 127.003
                latitude = 37.566
                canUse = false
                addInfo = "24시간 순찰"
            },
            OffMapFacility().apply {
                id = 4
                type = "경찰서"
                name = "믿음직한 경찰서"
                longitude = 127.003
                latitude = 37.566
                canUse = false
                addInfo = "24시간 순찰"
            },
            OffMapFacility().apply {
                id = 5
                type = "경찰서"
                name = "믿음직한 경찰서"
                longitude = 127.003
                latitude = 37.566
                canUse = false
                addInfo = "24시간 순찰"
            },
            OffMapFacility().apply {
                id = 6
                type = "경찰서"
                name = "믿음직한 경찰서"
                longitude = 127.003
                latitude = 37.566
                canUse = false
                addInfo = "24시간 순찰"
            })
    }

    // 선택된 카테고리를 처리하는 메서드
    private fun handleSelectedCategory(category: FacilityUIType) {
        // TODO: 여기에서 선택된 카테고리에 따라 다른 UI 요소를 업데이트합니다.
        // 예: 하단 시트의 RecyclerView를 업데이트하거나 지도상의 마커를 업데이트하는 등
        // 선택된 카테고리에 해당하는 시설 데이터를 가져옴 (리사이클러뷰에 표시될 것)
        /* 임시 코드
        * val filteredFacilities = facilityProvider.getFilteredFacilities(category)
        */

        // 새로운 데이터를 가진 어댑터 생성 -> 새로운 카테고리 데이터로 교체
        // listAdapter = FacilityListAdapter(filteredFacilities)

        // 리사이클러뷰에 새 어댑터 설정 -> 새로운 데이터로 화면 갱신
        // listRecyclerView.adapter = listAdapter

    }

    private fun findLatestLocation() { // GPS 버튼 클릭하면 본인 위치로 찾아가게
        Log.i("LatestLocation", "최근 위치 찾으러 들어왔어요")

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {

                mapView.controller.setCenter(
                    GeoPoint(
                        lastLat, lastLon
                    )
                )
                mapView.invalidate()
            }
        }
    }
}
