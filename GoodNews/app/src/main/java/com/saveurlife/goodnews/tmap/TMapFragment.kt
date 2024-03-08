package com.saveurlife.goodnews.tmap

import android.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.BuildConfig.T_MAP_API_KEY
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.databinding.FragmentTMapBinding
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.models.OffMapFacility
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.TMapView.INVISIBLE
import com.skt.tmap.TMapView.OnClickListenerCallback
import com.skt.tmap.TMapView.OnDisableScrollWithZoomLevelCallback
import com.skt.tmap.TMapView.VISIBLE
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.poi.TMapPOIItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class TMapFragment : Fragment(), TMapGpsManager.OnLocationChangedListener {
    private lateinit var binding: FragmentTMapBinding
    private val sharedPreferences = GoodNewsApplication.preferences

    private lateinit var tMapView: TMapView
    private lateinit var gps: TMapGpsManager
    private lateinit var tMapFacilityProvider: TMapFacilityProvider
    private var markerItem = TMapMarkerItem()

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: TMapFacilityCategoryAdapter
    private var selectedCategory: TMapFacilityUiType = TMapFacilityUiType.ALL

    private val uniqueLocations = HashSet<tMapFacilityData>()

//    private lateinit var clusteringIcon: Bitmap

    private val sharedPref = GoodNewsApplication.preferences

    //위치 변경될 때마다 지도가 현재 위치로 잡히는 것 방지하기 위해 한 번만 실행하도록
    private var first: Boolean = true

    //나침반 클릭하면 바로 직전 저장했던 위치 불러오기 위함
    private var lastKnownLocation: Location? = null

    private var lastLat = sharedPref.getDouble("tMapLat", 37.566474)
    private var lastLon = sharedPref.getDouble("tMapLon", 126.985022)

    //현재 위치(20초마다 바뀜)
    private var nowLat: Double = lastLat
    private var nowLon: Double = lastLon

    private var previousLeftTop: TMapPoint? = null
    private var previousRightBottom: TMapPoint? = null

    private var num: Int = 0
    private var total: Int = 0

    private var loadedAll = GoodNewsApplication.FacilityDataManager.copiedAll
    private var loadedShelter = GoodNewsApplication.FacilityDataManager.copiedShelter
    private var loadedHospital = GoodNewsApplication.FacilityDataManager.copiedHospital
    private var loadedGrocery = GoodNewsApplication.FacilityDataManager.copiedGrocery
    private var loadedMinbangwi = GoodNewsApplication.FacilityDataManager.copiedMinbangwi
    private var loadedEarthquake = GoodNewsApplication.FacilityDataManager.copiedEarthquake

    private var selectedFacility = GoodNewsApplication.FacilityDataManager.copiedAll

    private lateinit var application: GoodNewsApplication

    private var marker = TMapMarkerItem()

    private var markerClick = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTMapBinding.inflate(inflater, container, false)

        //tMapDialog Fragment 띄우기
        if (!sharedPreferences.getBoolean("tMapDialogIgnore", false)) {
            val dialog = TMapDialogFragment()
            dialog.show(childFragmentManager, "TMapDialogFragment")
        }

        tMapFacilityProvider = TMapFacilityProvider(requireContext())

        tMapView = TMapView(requireContext())
//        tMapView.setUserScrollZoomEnable(true)

        // 티맵 키 설정
        tMapView.setSKTMapApiKey(T_MAP_API_KEY)
        binding.tmapViewContainer.addView(tMapView)

        //gps
        application = requireActivity().application as GoodNewsApplication
        gps = MainActivity.gps
//        gps = TMapGpsManager(requireContext())
        gps.minTime = 1000 //위치 인식 변경 최소 시간 - 0.5초
        gps.minDistance = 0.01f //위치 인식 변경 인식 최소 거리 - 0.01 미터
        gps.provider = TMapGpsManager.PROVIDER_GPS //위성기반의 위치탐색
        gps.openGps()
        gps.provider = TMapGpsManager.PROVIDER_NETWORK //네트워크 기반의 위치탐색
        gps.openGps()

        val point: TMapPoint? = tMapView.locationPoint
        Log.i("초기값", point.toString()) //37.566474, 126.985022 SKT 타워 - 맨 처음에는 default 값으로 초기화되는 것 같음


        tMapView.setOnMapReadyListener {
//            tMapView.enableClustering = true
//            marker.enableClustering = true
            // 클러스터링 아이콘 설정
//            clusteringIcon = BitmapFactory.decodeResource(resources, R.drawable.editbox_dropdown_dark_frame)
//            tMapView.setClusteringIcon(clusteringIcon)

            tMapView.setCenterPoint(lastLat, lastLon)

            GlobalScope.launch(Dispatchers.Main) {
                // 0.5초 지연 후 실행
                delay(500)

                val leftTop: TMapPoint = tMapView.leftTopPoint
                previousLeftTop = tMapView.leftTopPoint
                Log.i("최초 좌상단 위경도", leftTop.toString())
                val rightBottom: TMapPoint = tMapView.rightBottomPoint
                previousRightBottom = tMapView.rightBottomPoint
                Log.i("최초 우하단 위경도", rightBottom.toString())

                Log.i("displayNAme", selectedCategory.displayName)
                facilityAroundMe(selectedCategory.displayName, selectedFacility, lastLat, lastLon, leftTop, rightBottom, "first")
            }


//            val leftTop: TMapPoint = tMapView.leftTopPoint
//            previousLeftTop = tMapView.leftTopPoint
//            Log.i("최초 좌상단 위경도", leftTop.toString())
//            val rightBottom: TMapPoint = tMapView.rightBottomPoint
//            previousRightBottom = tMapView.rightBottomPoint
//            Log.i("최초 우하단 위경도", rightBottom.toString())


            binding.locationTextView.text = "위도: $lastLat, 경도: $lastLon"

            //현재 위치 마커
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.presence_online)
            markerItem.id = "myMarker"
            markerItem.icon = bitmap
            markerItem.setPosition(0.5f, 1.0f) // 마커의 중심점을 중앙, 하단으로 설정
            markerItem.setTMapPoint(lastLat, lastLon)
            markerItem.name = "내 위치" // 마커의 타이틀 지정
            tMapView.addTMapMarkerItem(markerItem)

            //길 찾기
//            val tMapPointStart = TMapPoint(36.69055, 127.4037395)
//
//            val startPoint = TMapPoint(36.69055, 127.4037395)
//            val endPoint = TMapPoint(36.6874198, 127.4056281)
//
//            //findPathDataWithType 함수를 사용하여 도보 경로를 검색하고, 람다 표현식을 통해 즉시 결과를 처리
//            val tMapData = TMapData()
//            tMapData.findPathDataWithType(
//                TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint
//            ) { polyLine -> tMapView.addTMapPolyLine(polyLine) }
//
//            //findPathData 함수를 사용하여 경로를 검색하고, 경로를 받아와서 직접 처리하는 방식
//            TMapData().findPathData(
//                startPoint, endPoint
//            ) { tMapPolyLine ->
//                tMapPolyLine.lineWidth = 2f
//                tMapPolyLine.lineColor = Color.GREEN
//                tMapPolyLine.lineAlpha = 255
//                tMapPolyLine.outLineWidth = 5f
//                tMapPolyLine.outLineColor = Color.RED
//                tMapPolyLine.outLineAlpha = 255
//                TMapData.TMapPathType.PEDESTRIAN_PATH
//                tMapView.addTMapPolyLine(tMapPolyLine)
//                val info = tMapView.getDisplayTMapInfo(tMapPolyLine.linePointList)
////                tMapView.zoomLevel = info.zoom
//                tMapView.setCenterPoint(info.point.latitude, info.point.longitude)
//            }
        }

        // 서브 카테고리 선택 처리
        binding.tMapSubCategoryWrap.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                com.saveurlife.goodnews.R.id.tMapRadioAll -> addSubFacilitiesToTMap("전체")
                com.saveurlife.goodnews.R.id.tMapRadioCivilDefense -> addSubFacilitiesToTMap("민방위")
                com.saveurlife.goodnews.R.id.tMapRadioTsunami -> addSubFacilitiesToTMap("지진해일")
            }
        }


        //카테고리
        categoryRecyclerView = binding.tMapFacilityTypeList
        //수평 방향으로 스크롤
        categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = TMapFacilityCategoryAdapter(getCategories()) { category ->
            //category에 담기는 값은 ALL/SHELTER/HOSPITAL/GROCERY
            selectedCategory = category
            handleSelectedCategory(category)
            Log.d("CategorySelected", "Selected category: ${category.displayName}")
        }
        categoryRecyclerView.adapter = categoryAdapter


        //현재 위치 버튼 보기 클릭 시
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

            handleFindMyLocationButtonClick()
        }


        //줌인, 줌아웃 할 때마다 마커 찍기
        tMapView.setOnDisableScrollWithZoomLevelListener(OnDisableScrollWithZoomLevelCallback { zoom, centerPoint ->
            Log.i("줌 변경", "$zoom $centerPoint") //14.289 //TMapPoint{latitude=36.60872876356015, longitude=127.29983995734229}

            //좌상단 위경도
            val leftTop: TMapPoint = tMapView.leftTopPoint
            Log.i("좌상단 위경도", leftTop.toString())
            //우하단 위경도
            val rightBottom: TMapPoint = tMapView.rightBottomPoint
            Log.i("우하단 위경도", rightBottom.toString())


            // 이전 좌표와 현재 좌표를 이용하여 마커 표시

            Log.i("이전 좌상단 위경도", previousLeftTop.toString())
            Log.i("이전 우하단 위경도", previousRightBottom.toString())

            if(leftTop == previousLeftTop){
                Log.i("위경도는 같아요","같음")
            }

            //그냥 지도 클릭할 때도 마커 새롭게 그려져서 좌상단, 우하단 위경도 값으로 비교해서 단순 지도 클릭인지
            //줌인아웃인지 확인
            if(leftTop.latitude != previousLeftTop?.latitude && leftTop.longitude != previousLeftTop?.longitude
                && rightBottom.latitude != previousRightBottom?.latitude && rightBottom.longitude != previousRightBottom?.latitude){
                facilityAroundMe(selectedCategory.displayName, selectedFacility, nowLat, nowLon, leftTop, rightBottom, "zoom")
            }else{
                Log.i("위경도 같아서", "마커 다시 찍히지 않음")
            }

            // 이전 좌표 저장
            previousLeftTop = leftTop
            previousRightBottom = rightBottom

        })

//        tMapView.setOnLongClickListenerCallback { markerList, poiList, point ->
//            // 원하는 동작 수행
//            if(markerList.size != 0){
//                Log.i("롱클릭", markerList[0].name)
//            }
//            Log.i("롱클릭", "롱클릭했어요")
//
//        }

        //클릭 이벤트
        tMapView.setOnClickListenerCallback(object : OnClickListenerCallback {
            override fun onPressDown(markerlist: java.util.ArrayList<TMapMarkerItem>, poilist: java.util.ArrayList<TMapPOIItem>,
                point: TMapPoint, pointf: PointF) {
                if (markerlist != null) {
                    Toast.makeText(requireContext(), "onPressDown${markerlist.size}", Toast.LENGTH_LONG).show()
                }
                Log.i("클릭이벤트발생onPressDown", markerlist.toString())
                Log.i("클릭이벤트발생onPressDown", point.toString())
//                Log.i("클릭이벤트발생 첫번째 값은onPressDown", markerlist[0].name)
            }

            override fun onPressUp(markerlist: java.util.ArrayList<TMapMarkerItem>, poilist: java.util.ArrayList<TMapPOIItem>,
                point: TMapPoint, pointf: PointF) {
                if (markerlist != null) {
                    Toast.makeText(requireContext(), "onPressUp${markerlist.size}", Toast.LENGTH_LONG).show()
                }
                Log.i("클릭이벤트발생onPressUp", markerlist.toString())
                Log.i("클릭이벤트발생onPressUp", point.toString())


                if(markerlist.size != 0){
                    if (markerlist[0].id != "myMarker") {
                        binding.tMapFacilityBox.visibility = VISIBLE
                        var (name, type) = markerlist[0].name.split(",")
                        binding.tMapFacilityNameTextView.text = name
                        binding.tMapFacilityTypeTextView.text = type
                        val iconRes = when (type) {
                            "대피소" -> com.saveurlife.goodnews.R.drawable.ic_shelter
                            "병원", "약국" -> com.saveurlife.goodnews.R.drawable.ic_hospital
                            "편의점", "마트" -> com.saveurlife.goodnews.R.drawable.ic_grocery
                            else -> com.saveurlife.goodnews.R.drawable.ic_pin
                        }
                        binding.tMapFacilityIconType.setBackgroundResource(iconRes)
                    }
                }else{
                    binding.tMapFacilityBox.visibility = INVISIBLE
                }
            }
        })

        binding.tMapFacilityBox.setOnClickListener {
            binding.tMapFacilityBox.visibility = VISIBLE
        }


        return binding.root
    }

    //ui 업데이트
    override fun onResume() {
        super.onResume()
        gps.provider = TMapGpsManager.PROVIDER_GPS
        gps.minTime = 1000
        gps.minDistance = 0.01f
        gps.openGps()
        gps.provider = TMapGpsManager.PROVIDER_NETWORK
        gps.openGps()
        gps.setOnLocationChangeListener(this)
        tMapView.onResume()
    }

    //위치가 변경될 때 실행
    override fun onLocationChange(location: Location) {
        lastKnownLocation = location

        //현재 변경된 위치
        nowLat = location.latitude
        nowLon = location.longitude
        Log.i("변경되는 값", "$nowLat $nowLon")

        //처음에 1번만 내 위치로 변경
        if(first && nowLat != null && nowLon != null){
            tMapView.setCenterPoint(nowLat, nowLon)
            first = false
            val leftTop: TMapPoint = tMapView.leftTopPoint
            previousLeftTop = tMapView.leftTopPoint
            Log.i("최초 좌상단 위경도1", leftTop.toString())
            val rightBottom: TMapPoint = tMapView.rightBottomPoint
            previousRightBottom = tMapView.rightBottomPoint
            Log.i("최초 우하단 위경도1", rightBottom.toString())


            //현재 위치 마커
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.presence_online)
            markerItem.id = "myMarker"
            markerItem.icon = bitmap
            markerItem.setPosition(0.5f, 1.0f) // 마커의 중심점을 중앙, 하단으로 설정
            markerItem.setTMapPoint(nowLat, nowLon)
            markerItem.name = "내 위치" // 마커의 타이틀 지정
            tMapView.addTMapMarkerItem(markerItem)

            binding.locationTextView.text = "위도: $nowLat, 경도: $nowLon"
        }




        //좌상단 위경도 val leftTop: TMapPoint = tMapView.leftTopPoint
        //우하단 위경도 val rightBottom: TMapPoint = tMapView.rightBottomPoint
//        facilityAroundMe(nowLat, nowLon, leftTop, rightBottom)
    }

    //현재 위치로
    private fun handleFindMyLocationButtonClick() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                // UI 업데이트 작업
                if (tMapView != null) {
//                    val tPoint: TMapPoint = tMapView.convertPointToGps(50F, 100F)
//                    val lan = tPoint.latitude
//                    val lon = tPoint.longitude
                    var lat = lastKnownLocation?.latitude ?: 37.566474
                    var lon = lastKnownLocation?.longitude ?: 126.985022
                    Log.i("나침반 클릭 시 위치", "$lat $lon")

                    tMapView.setCenterPoint(lat, lon)
                    tMapView.zoomLevel = 13

                    Log.i("setCenter", "지도 중심 좌표 재 설정")
                } else {
                    Log.e("setCenter", "tMapView is null")
                }
            }
        }
    }

    private fun facilityAroundMe(selectedCategoryName: String, selectedFacility: MutableList<OffMapFacility>, nowLat: Double, nowLon: Double, leftTop: TMapPoint, rightBottom: TMapPoint, type: String) {
//        clusteringIcon = BitmapFactory.decodeResource(resources, android.R.drawable.alert_dark_frame)
//        tMapView.setClusteringIcon(clusteringIcon)
        Log.i("사이즈 확인하기", selectedFacility.size.toString())
        tMapView.removeAllTMapMarkerItem()

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.presence_online)
        markerItem.id = "myMarker"
        markerItem.icon = bitmap
        markerItem.setPosition(0.5f, 1.0f) // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(lastLat, lastLon)
        markerItem.name = "내 위치" // 마커의 타이틀 지정
        tMapView.addTMapMarkerItem(markerItem)


        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {

                val currentLocation = Location("current_location")
                currentLocation.latitude = nowLat // 현재 위도 설정
                currentLocation.longitude = nowLon // 현재 경도 설정

                val nearbyPoints: HashSet<tMapFacilityData>
                if(type == "first"){
                    Log.i("첫번째 값", "$leftTop $rightBottom $currentLocation")
                        nearbyPoints = filterPointsInVisibleArea(currentLocation, selectedFacility, leftTop, rightBottom, 20.0)
                    Log.i("first로 들어옴", nearbyPoints.size.toString())
                }else{
                    nearbyPoints = zoomFilterPointsInVisibleArea(selectedCategoryName, currentLocation, selectedFacility, leftTop, rightBottom, 20.0)
                    Log.i("zoom", nearbyPoints.size.toString())
                }

                total += nearbyPoints.size
                Log.i("지도에 띄워진 전체 시설", total.toString())
                Log.i("전체 크기", uniqueLocations.size.toString())
                Log.i("필터링된 크기", nearbyPoints.size.toString())
                nearbyPoints.forEach {
                    Log.i("타입", it.type)
                }
                Log.i("selectedCategoryName", selectedCategoryName)

                nearbyPoints
//                    .filter { selectedCategoryName == "전체" || selectedCategoryName == it.type || (it.type == "약국" && selectedCategoryName == "병원")} // 필터링: 선택된 카테고리와 일치하는 항목만 남김 //선택된 카테고리가 "전체"이면 it.type == "전체"가 항상 참이 되므로, 모든 항목이 추가
                    .forEachIndexed { _, point ->
                        markerItem = TMapMarkerItem()
                        markerItem.id = "m${num++}"
                        markerItem.name = "${point.name},${point.type}"
                        markerItem.setTMapPoint(point.location.latitude, point.location.longitude)
//                        markerItem.calloutLeftImage = BitmapFactory.decodeResource(resources, R.drawable.editbox_dropdown_dark_frame)

                    val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.editbox_dropdown_dark_frame)
                    val resizedBitmap = Bitmap.createScaledBitmap(iconBitmap, 25, 25, false)
                        markerItem.icon = resizedBitmap

                    // 비동기적으로 마커를 지도에 추가
                    withContext(Dispatchers.Main) {
                        tMapView.addTMapMarkerItem(markerItem)
                        println("등록 마커 ${markerItem.id} ${point.location.latitude}, ${point.location.longitude}")
                    }
                }
            }
            Log.i("marker 전체 표시", "완료")
        }
    }

    //현재 표시되는 지도 안에 있는 시설 데이터 리스트에 저장하기
    private fun filterPointsInVisibleArea(currentLocation: Location, facilities: MutableList<OffMapFacility>,
        leftTop: TMapPoint, rightBottom: TMapPoint, radius: Double): HashSet<tMapFacilityData> {

        val uniqueFacilitiesList = mutableListOf<tMapFacilityData>()

        facilities.forEach { facility ->
            val facilityLocation = facility.toLocation()
            val distance = calculateDistance(currentLocation, facilityLocation)

            if(distance <= radius && boundingBox(facilityLocation, leftTop, rightBottom)){
                Log.i("두 지점간의 거리", distance.toString())

                val facilityData = tMapFacilityData(
                    location = facilityLocation,
                    type = facility.type,  // 예시로 가정한 필드명
                    name = facility.name  // 예시로 가정한 필드명
                )
                uniqueFacilitiesList.add(facilityData)
                uniqueLocations.add(facilityData)
            }

//            distance <= radius && // 원하는 반경
//            boundingBox(facilityLocation, leftTop, rightBottom)
        }
        return uniqueLocations
    }

    private fun zoomFilterPointsInVisibleArea(selectedCategoryName: String, currentLocation: Location, facilities: MutableList<OffMapFacility>,
        leftTop: TMapPoint, rightBottom: TMapPoint, radius: Double): HashSet<tMapFacilityData> {
        val uniqueLocationsList = hashSetOf<tMapFacilityData>()

        facilities.forEach { facility ->
            val facilityLocation = facility.toLocation()
            val distance = calculateDistance(currentLocation, facilityLocation)

            if (distance <= radius && boundingBox(facilityLocation, leftTop, rightBottom)) {
                val facilityData = tMapFacilityData(
                    location = facilityLocation,
                    type = facility.type,  // 예시로 가정한 필드명
                    name = facility.name  // 예시로 가정한 필드명
                )

                uniqueLocationsList.add(facilityData)
                uniqueLocations.add(facilityData)
                Log.i("두 지점간의 거리", "$distance $facilityLocation")


            }
        }
        // 중복이 제거된 목록을 반환
        return uniqueLocationsList
    }

    private fun boundingBox(facilityLocation: Location, leftTop: TMapPoint, rightBottom: TMapPoint): Boolean {
        return rightBottom.latitude <= facilityLocation.latitude && facilityLocation.latitude <= leftTop.latitude &&
                leftTop.longitude <= facilityLocation.longitude && facilityLocation.longitude <= rightBottom.longitude
    }


    //줌 out 시 새로 나타난 지도 구역에 있는 시설 데이터 리스트에 저장하기
//    private fun zoomFilterPointsInVisibleArea(currentLocation: Location, facilities: MutableList<OffMapFacility>,
//                                              leftTop: TMapPoint, rightBottom: TMapPoint, radius: Double): List<Location> {
//        return facilities
//            .filter { facility ->
//                val facilityLocation = facility.toLocation()
//                val distance = calculateDistance(currentLocation, facilityLocation)
//
//                if(distance <= radius){
//                    Log.i("두 지점간의 거리", "$distance $facilityLocation")
//                }
//
//                distance <= radius // && // 원하는 반경
////                        (previousLeftTop?.latitude!! <= facilityLocation.latitude && facilityLocation.latitude <=leftTop.latitude &&
////                                previousLeftTop?.longitude!! <= facilityLocation.longitude && facilityLocation.longitude <= leftTop.longitude) ||
////                        (previousLeftTop?.longitude!! <= facilityLocation.longitude && facilityLocation.longitude <= previousRightBottom?.longitude!! &&
////                                rightBottom.latitude <= facilityLocation.latitude && facilityLocation.latitude <= previousRightBottom?.latitude!!) ||
////                        (previousRightBottom?.longitude!! <= facilityLocation.longitude && facilityLocation.longitude <= rightBottom.longitude &&
////                                rightBottom.latitude <= facilityLocation.latitude && facilityLocation.latitude <= leftTop.latitude)||
////                        (previousLeftTop?.longitude!! <= facilityLocation.longitude && facilityLocation.longitude <= previousRightBottom?.longitude!! &&
////                                previousLeftTop?.latitude!! <= facilityLocation.latitude && facilityLocation.latitude <= leftTop.latitude )
//
//            }
//            .map { it.toLocation() }
//
//        //현재 좌상단,우하단 위경도를 이전 변수로
//        previousLeftTop = leftTop
//        previousRightBottom = rightBottom
//    }


    // 두 지점 간의 거리를 계산하는 함수(return 값의 단위는 km)
//    private fun calculateDistance(location1: Location, location2: Location): Double {
//        val lat1 = location1.latitude
//        val lon1 = location1.longitude
//        val lat2 = location2.latitude
//        val lon2 = location2.longitude
//
//        // Haversine formula 사용
//        val dLat = Math.toRadians(lat2 - lat1)
//        val dLon = Math.toRadians(lon2 - lon1)
//        val a = sin(dLat / 2) * sin(dLat / 2) +
//                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
//                sin(dLon / 2) * sin(dLon / 2)
//        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
//        val radiusOfEarth = 6371.0 // 지구의 반지름 (단위: km)
//        return radiusOfEarth * c
//    }

    private fun calculateDistance(location1: Location, location2: Location): Double {
        val x1 = location1.latitude
        val y1 = location1.longitude
        val x2 = location2.latitude
        val y2 = location2.longitude

        val distance: Double
        val radius = 6371.0 // 지구 반지름(km)
        val toRadian = Math.PI / 180
        val deltaLatitude = abs(x1 - x2) * toRadian
        val deltaLongitude = abs(y1 - y2) * toRadian
        val sinDeltaLat = sin(deltaLatitude / 2)
        val sinDeltaLng = sin(deltaLongitude / 2)
        val squareRoot = sqrt(
            sinDeltaLat * sinDeltaLat +
                    cos(x1 * toRadian) * cos(x2 * toRadian) * sinDeltaLng * sinDeltaLng
        )
        distance = 2 * radius * asin(squareRoot)
        return distance
    }

    // 현재 위치 주변의 좌표를 필터링할 함수
    private fun OffMapFacility.toLocation(): Location {
        val location = Location("facility_location")
        location.latitude = this.latitude
        location.longitude = this.longitude
        return location
    }
    private fun filterNearbyPoints(currentLocation: Location, facilities: MutableList<OffMapFacility>, radius: Double): List<Location> {
        return facilities
            .filter { facility ->
                val facilityLocation = facility.toLocation()
                val distance = calculateDistance(currentLocation, facilityLocation)
                distance <= radius
            }
            .map { it.toLocation() }

    }

    //카테고리
    private fun handleSelectedCategory(category: TMapFacilityUiType) {
        // 대피소일 경우
        if (category == TMapFacilityUiType.SHELTER) {
            binding.tMapSubCategoryWrap.visibility = View.VISIBLE
            // subCategory.check(R.id.radioAll) // 대피소 세부 카테고리 리셋 현상 방지
        } else {
            binding.tMapSubCategoryWrap.visibility = View.GONE
        }
        addFacilitiesToTMap(category)
    }

    private fun addFacilitiesToTMap(category: TMapFacilityUiType) {
        selectedFacility = tMapFacilityProvider.getFilteredFacilities(category)

        val leftTop: TMapPoint = tMapView.leftTopPoint
        Log.i("좌상단 위경도", leftTop.toString())
        val rightBottom: TMapPoint = tMapView.rightBottomPoint
        Log.i("우하단 위경도", rightBottom.toString())

        Log.i("현재 선택된 카테고리", category.displayName)

        // 이전 좌표와 현재 좌표를 이용하여 마커 표시
        facilityAroundMe(category.displayName, selectedFacility, nowLat, nowLon, leftTop, rightBottom, "zoom")
    }

    private fun updateFacilitiesBySubCategory(subCategory: String) {
        // 지도 마커 및 하단 리스트
        addSubFacilitiesToTMap(subCategory)
    }

    private fun addSubFacilitiesToTMap(subCategory: String) {
        // 마커로 찍을 시설 목록 필터링
        selectedFacility = tMapFacilityProvider.getFilteredFacilitiesBySubCategory(subCategory)

        val leftTop: TMapPoint = tMapView.leftTopPoint
        Log.i("좌상단 위경도", leftTop.toString())
        val rightBottom: TMapPoint = tMapView.rightBottomPoint
        Log.i("우하단 위경도", rightBottom.toString())

        Log.i("현재 선택된 카테고리", subCategory)

        // 이전 좌표와 현재 좌표를 이용하여 마커 표시
        facilityAroundMe(subCategory, selectedFacility, nowLat, nowLon, leftTop, rightBottom, "zoom")
    }

    //TMapFacilityUiType에 있는 값들을 리스트로 반환
    private fun getCategories(): List<TMapFacilityUiType> {
        return TMapFacilityUiType.values().toList()
    }
}