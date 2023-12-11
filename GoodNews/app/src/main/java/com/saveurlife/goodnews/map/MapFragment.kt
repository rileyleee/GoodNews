package com.saveurlife.goodnews.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.FragmentMapBinding
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.models.FacilityUIType
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.models.FamilyPlace
import com.saveurlife.goodnews.models.OffMapFacility
import com.saveurlife.goodnews.sync.SyncService
import io.realm.kotlin.ext.isValid
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
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class MapFragment : Fragment(), LocationProvider.LocationUpdateListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var mapProvider: MapTileProviderArray
    private lateinit var locationProvider: LocationProvider
    private lateinit var facilityProvider: FacilityProvider
    private lateinit var currGeoPoint: GeoPoint
    private lateinit var screenRect: BoundingBox
    private var familyMemProvider = FamilyMemProvider()
    private var familyPlaceProvider = FamilyPlaceProvider()
    private var familyList = mutableListOf<FamilyMemInfo>()
    private var familyPlaceList = mutableListOf<FamilyPlace>()
    private var familyMarkers = mutableListOf<Marker>()
    private var familyPlaceMarkers = mutableListOf<Marker>()

    // 사용자의 위치를 표시하는 이전 마커에 대한 참조를 저장할 변수
    private var previousLocationOverlay: MyLocationMarkerOverlay? = null

    // 연결된 사용자의 위치를 표시하는 이전 마커에 대한 참조를 저장할 변수
    private var previousConnectedUsersLocationOverlay = mutableListOf<ConnectedUserMarkerOverlay>()

    // 추가 코드
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: FacilityCategoryAdapter
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var listAdapter: FacilityListAdapter
    private var selectedCategory: FacilityUIType = FacilityUIType.ALL

    private val localMapTileArchivePath = "korea_7_13.sqlite"
    private val serverMapTileArchivePath = "7_15_korea.sqlite"
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var sharedViewModel: SharedViewModel

    // 오프라인 파일 위치
    private lateinit var file: File

    // 타일 provider, 최소 줌 및 해상도 설정
    val provider: String = "Mapnik"

    // 지도 파일 변경 시 수정2 (Mapnik: OSM에서 가져온 거 또는 4uMaps: MOBAC에서 가져온 거 // => sqlite 파일의 provider 값)
    val minZoom: Int = 7
    val localMaxZoom = 15
    val serverMaxZoom = 18
    val pixel: Int = 256

    // 스크롤 가능 범위: 한국의 위경도 범위
    val max = GeoPoint(38.6111, 131.8696)
    val min = GeoPoint(33.1120, 124.6100)
    val box = BoundingBox(max.latitude, max.longitude, min.latitude, min.longitude)

    // 마지막 위치 초기 설정 => 서울 시청
    val sharedPref = GoodNewsApplication.preferences
    var lastLat = sharedPref.getDouble("lastLat", 37.566535)
    var lastLon = sharedPref.getDouble("lastLon", 126.9779692)

    // 오프라인 지도 다운로드 확인 여부
    var downloadedMap = sharedPref.getBoolean("downloadedMap", false)


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

        // 서브 카테고리 선택 처리
        binding.subCategoryWrap.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioAll -> updateFacilitiesBySubCategory("전체")
                R.id.radioCivilDefense -> updateFacilitiesBySubCategory("민방위")
                R.id.radioTsunami -> updateFacilitiesBySubCategory("지진해일")
            }
        }

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

        return binding.root
    }

    // 임시 코드
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // 로딩이 완료되었으니 MainActivity의 hideLoadingProgressBar() 호출
        val mainActivity = requireActivity() as MainActivity

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

        // 오프라인 지도 존재 여부 확인 후 sharedPref에 담기
        file =
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                serverMapTileArchivePath
            )

        if (file.exists()) {
            sharedPref.setBoolean("downloadedMap", true)
        } else {
            sharedPref.setBoolean("downloadedMap", false)
        }

        var tileSource: XYTileSource

        if (downloadedMap) { // 서버에서 다운로드 받은 파일이 있으면
            tileSource = XYTileSource(
                provider,
                minZoom, serverMaxZoom, pixel, ".png",
                arrayOf("http://127.0.0.1")
            )
        } else {
            tileSource = XYTileSource(
                provider,
                minZoom, localMaxZoom, pixel, ".png",
                arrayOf("http://127.0.0.1")
            )
        }

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
            mapView.controller.setZoom(13.0)
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

                handleSelectedCategory(selectedCategory)
                mainActivity.hideLoadingProgressBar()
            }
        })

        // 사용자가 터치할 때마다 경계 변경
        mapView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // 사용자가 화면을 터치하고 뗄 때마다 호출
                screenRect = mapView.boundingBox

                Log.v("screenRect", "$screenRect")

                handleSelectedCategory(selectedCategory)
            }
            false
        }
        // 연결된 사용자 정보 확인 위함
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        updateConnectedUsersLocation()

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
                        binding.itemMapFacilityWrap.visibility = View.GONE
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

        // 서버에서 저장한 지도 파일
        if (downloadedMap) {
            Log.d("지도 출처", "서버에서 다운로드 받은 지도요")

            // 파일이 존재하는지 확인하고 존재하지 않으면 오류 메시지를 표시합니다.
            if (!file.exists()) {
                throw IOException("지도 파일이 존재하지 않습니다: ${file.absolutePath}")

            }
            return file
        } else { // 로컬에 존재하는 지도 파일
            Log.d("지도 출처", "로컬에 있는 지도요")
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

    // 위치 변경 시 위경도 받아옴
    override fun onLocationChanged(location: Location) {
        currGeoPoint = GeoPoint(location.latitude, location.longitude)
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
                binding.itemMapFacilityWrap.visibility = View.VISIBLE
                val facility = facilities[index]
                binding.facilityNameTextView.text = facility.name
                binding.facilityTypeTextView.text = facility.type
                val iconRes = when (facility.type) {
                    "대피소" -> R.drawable.ic_shelter
                    "병원" -> R.drawable.ic_hospital
                    "편의점", "마트" -> R.drawable.ic_grocery
                    "가족" -> R.drawable.ic_family
                    "약속장소" -> R.drawable.ic_meeting_place
                    else -> R.drawable.ic_pin
                }
                binding.facilityIconType.setBackgroundResource(iconRes)

                if (facility.canUse) {
                    binding.useTrueWrap.visibility = View.VISIBLE
                    binding.useFalseWrap.visibility = View.GONE
                } else {
                    binding.useTrueWrap.visibility = View.GONE
                    binding.useFalseWrap.visibility = View.VISIBLE
                }
                val lastConnection = sharedPref.getLong("SyncTime", 0L)
                val syncService = SyncService()
                binding.facilityLastUpdateTime.text =
                    syncService.convertDateLongToString(lastConnection)

            }
        }

        return overlay
    }

    // 이전 시설 마커 관리하기 위한 리스트
    private val previousFacilityOverlayItems = mutableListOf<SimpleFastPointOverlay>()

    // 시설 위치 마커로 찍는 함수 내부에서 사용
    private fun addFacilitiesToMap(category: FacilityUIType) {

        // 마커로 찍을 시설 목록 필터링
        val facilitiesOverlayItems = facilityProvider.getFilteredFacilities(category)
            .filter { screenRect.contains(GeoPoint(it.latitude, it.longitude)) }

        // 지도 하단 시트에 표시될 리스트 갱신
        listAdapter.updateData(facilitiesOverlayItems)

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

    private fun addSubFacilitiesToMap(subCategory: String) {

        // 마커로 찍을 시설 목록 필터링
        val facilitiesOverlayItems =
            facilityProvider.getFilteredFacilitiesBySubCategory(subCategory)
                .filter { screenRect.contains(GeoPoint(it.latitude, it.longitude)) }

        // 지도 하단 시트에 표시될 리스트 갱신
        listAdapter.updateData(facilitiesOverlayItems)

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
        // 초기화를 위한 더미 데이터
        return listOf(
            OffMapFacility().apply {
                id = 1
                type = "병원"
                name = "행복한 병원"
                longitude = 127.001
                latitude = 37.564
                canUse = true
                addInfo = "24시간 운영"
            })
    }

    // 선택된 카테고리를 처리하는 메서드
    private fun handleSelectedCategory(category: FacilityUIType) {
        // TODO: 여기에서 선택된 카테고리에 따라 다른 UI 요소를 업데이트합니다.
        // 예: 하단 시트의 RecyclerView를 업데이트하거나 지도상의 마커를 업데이트하는 등

        // 대피소일 경우
        if (category == FacilityUIType.SHELTER) {
            val subCategory = binding.subCategoryWrap
            subCategory.visibility = View.VISIBLE
//            subCategory.check(R.id.radioAll) // 대피소 세부 카테고리 리셋 현상 방지
        } else {
            binding.subCategoryWrap.visibility = View.GONE
        }
        updateFacilitiesByCategory(category)
    }

    // 업데이트 코드
    private fun updateFacilitiesByCategory(category: FacilityUIType) {

        when (category) {
            FacilityUIType.FAMILY -> CoroutineScope(Dispatchers.Main).launch {
                familyList = familyMemProvider.getFamilyMemInfo()
                removeFamilyPlaceMarkers() // 가족 약속 장소 마커 지우기
                addFamilyLocation()
            }

            FacilityUIType.MEETING_PLACE -> CoroutineScope(Dispatchers.Main).launch {
                familyPlaceList = familyPlaceProvider.getFamilyPlace()
                removeFamilyMarkers() // 가족 마커 지우기
                addFamilyPlaceLocation()
            }

            else -> {// 지도 마커 및 하단 리스트
                removeFamilyMarkers() // 가족 마커 지우기
                removeFamilyPlaceMarkers() // 가족 약속 장소 마커 지우기
                addFacilitiesToMap(category)
            }
        }
    }

    // 서브 카테고리 업데이트 코드
    private fun updateFacilitiesBySubCategory(subCategory: String) {
        // 지도 마커 및 하단 리스트
        addSubFacilitiesToMap(subCategory)
    }

    private fun findLatestLocation() { // GPS 버튼 클릭하면 본인 위치로 찾아가게
        Log.i("LatestLocation", "최근 위치 찾으러 들어왔어요")

        CoroutineScope(Dispatchers.IO).launch {

            val newLastLat = sharedPref.getDouble("lastLat", 37.566535)
            val newLastLon = sharedPref.getDouble("lastLon", 126.9779692)

            withContext(Dispatchers.Main) {

                mapView.controller.setCenter(
                    GeoPoint(
                        newLastLat, newLastLon
                    )
                )
                Log.i("setCenter", "지도 중심 좌표 재 설정")
                mapView.controller.setZoom(13.0)
                mapView.invalidate()
            }
        }
    }


    private fun updateConnectedUsersLocation() {

        Log.d("updateConnectedUsersLocation", "연결된 이용자 위치 마커 그리기 함수 호출")
        previousConnectedUsersLocationOverlay.forEach { overlay ->
            mapView.overlays.remove(overlay)
        }
        previousConnectedUsersLocationOverlay.clear()
        Log.d("updateConnectedUsersLocation", "연결된 이용자의 이전 위치 마커를 삭제했습니다.")

        val userProvider = ConnectedUserProvider(sharedViewModel, viewLifecycleOwner)

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                userProvider.provideConnectedUsers { userList ->
                    Log.v("livedata 유저 수", "${userList.size}")
                    // 연결된 사용자의 위치를 지도에 마커로 표시
                    userList.forEach { user ->
                        val geoPoint = GeoPoint(user.lat, user.lon)
                        Log.v("livedata 유저 geoPoint", "$geoPoint")
                        val connectedUserMarkerOverlay = ConnectedUserMarkerOverlay(geoPoint) {
                            showOtherUserInfoDialog(user)
                        }
                        mapView.overlays.add(connectedUserMarkerOverlay)
//                        Log.v("livedata 유저 오버레이", "$connectedUserMarkerOverlay")
                        // 새 위치를 다시 이전 위치 마커 리스트에 반영
                        previousConnectedUsersLocationOverlay.add(connectedUserMarkerOverlay)
//                        Log.v("livedata 유저를 이전 리스트에 담기", "${previousConnectedUsersLocationOverlay.size}")
                    }
                    mapView.invalidate() // 지도 다시 그려서 오버레이 보이게 함
                    Log.d("livedata 유저 오버레이 반영", "실행했습니다.")
                }
            }
        }
    }

    private fun showOtherUserInfoDialog(user: BleMeshConnectedUser): BleMeshConnectedUser {
        Log.d("otherUserClicked", "다른 유저가 클릭되었습니다.")
        val dialogFragment = OtherUserInfoFragment()

        // 클릭한 연결된 사용자의 정보를 프래그 먼트로 전달
        val userInfo = Bundle()

        val distance = calculateDistance(lastLat, lastLon, user.lat, user.lon)

        userInfo.putString("userName", user.userName)
        userInfo.putString("userStatus", user.healthStatus)
        userInfo.putString("userUpdateTime", user.updateTime)
        userInfo.putDouble("distance", distance)
        userInfo.putString("userId", user.userId)

        dialogFragment.arguments = userInfo

        dialogFragment.show(childFragmentManager, "OtherUserInfoFragment")
        return user
    }

    private fun showFamilyUserInfoDialog(famUser: FamilyMemInfo) {

        var syncService = SyncService()

        Log.d("famUserClicked", "가족 유저가 클릭되었습니다.")
        val dialogFragment = FamilyUserInfoFragment()

        // 클릭한 가족 사용자의 정보를 프래그 먼트로 전달
        val famUserInfo = Bundle()

        famUserInfo.putString("userName", famUser.name)
        famUserInfo.putString("userStatus", famUser.state)
        famUserInfo.putString("userUpdateTime", syncService.realmInstantToString(famUser.lastConnection))

        dialogFragment.arguments = famUserInfo

        dialogFragment.show(childFragmentManager, "showFamilyUserInfoDialog")
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        println("두 좌표의 값은 ???? $lat1, $lon1, $lat2, $lon2")
        val earthRadius = 6371000.0 // 지구 반지름 (미터 단위)

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        println("위도 경도 차이 : $dLat , $dLon")

        val a =
            sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(
                2
            )
        println("a의 값은 ?? $a")
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        println("c의 값은 ?? $c")
        println("리턴 값은 ? ${earthRadius * c}")

        return earthRadius * c
    }

    private fun addFamilyLocation() {
        Log.d("addFamilyLocation", "가족 위치 렌더링 중이에요")

        familyList.forEach { fam ->

            Log.d("fam", "$fam")

            if (fam.isValid()) {
                Log.d("addFamilyLocation", "여기 들어왔나요?")
                val location = GeoPoint("${fam.latitude}".toDouble(), "${fam.longitude}".toDouble())

                val famMarker = Marker(mapView)
                famMarker.position = location
                famMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                famMarker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_family)
                famMarker.title = "${fam.name}"
                famMarker.snippet = "최종 연결 시각: ${fam.lastConnection}, 현재 상태: ${fam.state}"

                famMarker.setOnMarkerClickListener { famMarker, _ ->
                    // 가족 정보 다이얼로그 연결 위한 데이터 전송
                    showFamilyUserInfoDialog(fam)
//                    Toast.makeText(
//                        requireContext(),
//                        "${famMarker.title}: ${famMarker.snippet}",
//                        Toast.LENGTH_LONG
//                    ).show()
                    true
                }

                Log.v("어떤 가족인가요", "${fam.name}")
                familyMarkers.add(famMarker)
                mapView.overlays.add(famMarker)
            } else {
                Log.w("addFamilyLocation", "가족 위치 렌더링 중 오류 발생")
            }
            mapView.invalidate()


        }
    }

    private fun addFamilyPlaceLocation() {
        familyPlaceList.forEach { famPlace ->

            if (famPlace.isValid()) {
                val location =
                    GeoPoint("${famPlace.latitude}".toDouble(), "${famPlace.longitude}".toDouble())

                val famPlaceMarker = Marker(mapView)
                famPlaceMarker.position = location
                famPlaceMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                famPlaceMarker.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_meeting_place)
                famPlaceMarker.title = "${famPlace.name}"
                famPlaceMarker.snippet = "주소: ${famPlace.address}, 현재 상태: ${famPlace.canUse}"

//                famPlaceMarker.setOnMarkerClickListener { famPlaceMarker, _ ->
//                    Toast.makeText(
//                        requireContext(),
//                        "${famPlaceMarker.title}: ${famPlaceMarker.snippet}",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    true
//                }
                familyPlaceMarkers.add(famPlaceMarker)
                mapView.overlays.add(famPlaceMarker)
            } else {
                Log.w("addFamilyPlaceLocation", "가족 약속장소 위치 렌더링 중 오류 발생")
            }
            mapView.invalidate()
        }
    }

    // 가족 마커 제거 함수
    private fun removeFamilyMarkers() {
        familyMarkers.forEach { marker ->
            mapView.overlays.remove(marker)
        }
        familyMarkers.clear()
        mapView.invalidate()
    }


    // 가족 약속장소 마커 제거 함수
    private fun removeFamilyPlaceMarkers() {
        familyPlaceMarkers.forEach { marker ->
            mapView.overlays.remove(marker)
        }
        familyPlaceMarkers.clear()
        mapView.invalidate()
    }
}

