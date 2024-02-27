package com.saveurlife.goodnews.tmap

import android.R
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saveurlife.goodnews.BuildConfig.T_MAP_API_KEY
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.databinding.FragmentTMapBinding
import com.saveurlife.goodnews.map.FacilityProvider
import com.saveurlife.goodnews.models.OffMapFacility
import com.skt.tmap.TMapData
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint

class TMapFragment : Fragment(), TMapGpsManager.OnLocationChangedListener {
    private lateinit var binding: FragmentTMapBinding
    private lateinit var tMapView: TMapView
    private lateinit var gps: TMapGpsManager
    private lateinit var facilityProvider: FacilityProvider
    private var markerItem = TMapMarkerItem()

    //위치 변경될 때마다 지도가 현재 위치로 잡히는 것 방지하기 위해 한 번만 실행하도록
    private var first: Boolean = true

    //나침반 클릭하면 바로 직전 저장했던 위치 불러오기 위함
    private var lastKnownLocation: Location? = null

//    private var copiedAll: List<OffMapFacility> = emptyList()
//    private var copiedShelter: List<OffMapFacility> = emptyList()
//    private var copiedHospital: List<OffMapFacility> = emptyList()
//    private var copiedGrocery: List<OffMapFacility> = emptyList()
//    private var copiedMinbangwi: List<OffMapFacility> = emptyList()
//    private var copiedEarthquake: List<OffMapFacility> = emptyList()
//
//    private var loadedAll = GoodNewsApplication.FacilityDataManager.copiedAll
//    private var loadedShelter = GoodNewsApplication.FacilityDataManager.copiedShelter
//    private var loadedHospital = GoodNewsApplication.FacilityDataManager.copiedHospital
//    private var loadedGrocery = GoodNewsApplication.FacilityDataManager.copiedGrocery
//    private var loadedMinbangwi = GoodNewsApplication.FacilityDataManager.copiedMinbangwi
//    private var loadedEarthquake = GoodNewsApplication.FacilityDataManager.copiedEarthquake


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTMapBinding.inflate(inflater, container, false)

        tMapView = TMapView(requireContext())



        // 티맵 키 설정
        tMapView.setSKTMapApiKey(T_MAP_API_KEY)

        binding.tmapViewContainer.addView(tMapView)

        //gps
        gps = TMapGpsManager(requireContext())
        gps.minTime = 1000 //위치 인식 변경 최소 시간 - 1초
        gps.minDistance = 0.01f //위치 인식 변경 인식 최소 거리 - 0.01 미터
        gps.provider = TMapGpsManager.PROVIDER_GPS //위성기반의 위치탐색
        gps.openGps()
        gps.provider = TMapGpsManager.PROVIDER_NETWORK //네트워크 기반의 위치탐색
        gps.openGps()

        val point: TMapPoint? = tMapView.locationPoint
        Log.i("초기값", point.toString()) //37.566474, 126.985022 SKT 타워 - 맨 처음에는 default 값으로 초기화되는 것 같음


        //현재 위치 버튼 보기 클릭 시
        binding.findMyLocationButton.setOnClickListener {
            handleFindMyLocationButtonClick()
        }

//        setFacilityData(loadedAll, loadedShelter, loadedHospital, loadedGrocery, loadedMinbangwi, loadedEarthquake)

        tMapView.setOnMapReadyListener {
            val pointList = ArrayList<TMapPoint>()
            pointList.add(TMapPoint(36.69055, 127.4037395))
            pointList.add(TMapPoint(36.6874198, 127.4056281))
            pointList.add(TMapPoint(36.6774198, 127.409))


            for ((index, point) in pointList.withIndex()) {
                val marker = TMapMarkerItem()
                marker.id = "marker${index}"
                marker.setTMapPoint(point.latitude, point.longitude)
                marker.icon = BitmapFactory.decodeResource(resources, R.drawable.ic_notification_overlay) // 마커 아이콘 설정

                // 마커를 지도에 추가
                tMapView.addTMapMarkerItem(marker)
                println("등록 마커 ${point.latitude}, ${point.longitude}")
            }







            val tMapPointStart = TMapPoint(36.69055, 127.4037395)

            val startPoint = TMapPoint(36.69055, 127.4037395)
            val endPoint = TMapPoint(36.6874198, 127.4056281)

            //findPathDataWithType 함수를 사용하여 도보 경로를 검색하고, 람다 표현식을 통해 즉시 결과를 처리
            val tMapData = TMapData()
            tMapData.findPathDataWithType(
                TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint
            ) { polyLine -> tMapView.addTMapPolyLine(polyLine) }

            //findPathData 함수를 사용하여 경로를 검색하고, 경로를 받아와서 직접 처리하는 방식
            TMapData().findPathData(
                startPoint, endPoint
            ) { tMapPolyLine ->
                tMapPolyLine.lineWidth = 2f
                tMapPolyLine.lineColor = Color.GREEN
                tMapPolyLine.lineAlpha = 255
                tMapPolyLine.outLineWidth = 5f
                tMapPolyLine.outLineColor = Color.RED
                tMapPolyLine.outLineAlpha = 255
                TMapData.TMapPathType.PEDESTRIAN_PATH
                tMapView.addTMapPolyLine(tMapPolyLine)
                val info = tMapView.getDisplayTMapInfo(tMapPolyLine.linePointList)
//                tMapView.zoomLevel = info.zoom
                tMapView.setCenterPoint(info.point.latitude, info.point.longitude)
            }
        }
        return binding.root
    }

//    private fun setFacilityData(
//        all: List<OffMapFacility>,
//        shelter: List<OffMapFacility>,
//        hospital: List<OffMapFacility>,
//        grocery: List<OffMapFacility>,
//        minbangwi: List<OffMapFacility>,
//        earthquake: List<OffMapFacility>
//    ) {
//        this.copiedAll = all
//        this.copiedShelter = shelter
//        this.copiedHospital = hospital
//        this.copiedGrocery = grocery
//        this.copiedMinbangwi = minbangwi
//        this.copiedEarthquake = earthquake
//
//        // tMapView가 null이 아닌 경우에만 마커 표시 메서드 호출
//        if (tMapView != null) {
//            showMarkers()
//        }
//    }

//    private fun showMarkers() {
//        // copiedAll 데이터에 대한 마커 표시
//        for ((index, facility) in loadedEarthquake.withIndex()) {
//
//            val marker = TMapMarkerItem()
//            marker.id = "marker${index}"
//            marker.setTMapPoint(facility.latitude, facility.longitude)
//            marker.icon = BitmapFactory.decodeResource(resources, R.drawable.ic_notification_overlay) // 마커 아이콘 설정
//            // 마커 설정 등의 코드 작성
//            tMapView.addTMapMarkerItem(marker)
//        }
//
//        // copiedShelter, copiedHospital 등에 대해서도 마커 표시 작업을 반복
//
//        // 필요에 따라 마커의 아이콘, 색상, 정보 표시 등의 설정 수행
//    }

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

                    Log.i("setCenter", "지도 중심 좌표 재 설정")
                } else {
                    Log.e("setCenter", "tMapView is null")
                }
            }
        }
    }

    //ui 업데이트 해줘야함
    override fun onResume() {
        super.onResume()
        gps.provider = TMapGpsManager.PROVIDER_GPS
        gps.minTime = 1000
        gps.minDistance = 0.01f
        gps.openGps()
        gps.setOnLocationChangeListener(this)
    }

    override fun onLocationChange(location: Location) {
        lastKnownLocation = location

        val lat: Double = location.latitude
        val lon: Double = location.longitude
        Log.i("변경되는 값", "$lat $lon")
//        val point: TMapPoint = gps.getLocation()
//        tMapView.zoomLevel = 20
        //처음에 1번만 내 위치로 변경
        if(first){
            tMapView.setCenterPoint(lat, lon)
            first = false
        }


        binding.locationTextView.text = "위도: $lat, 경도: $lon"
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.presence_online)

        markerItem.id = "myMarker"
        markerItem.icon = bitmap
        markerItem.setPosition(0.5f, 1.0f) // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(lat, lon)
        markerItem.name = "내 위치" // 마커의 타이틀 지정
        tMapView.addTMapMarkerItem(markerItem)
    }

}