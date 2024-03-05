//package com.saveurlife.goodnews.tmap
//
//import android.graphics.BitmapFactory
//import android.location.Location
//import com.google.android.gms.location.LocationListener
//import com.saveurlife.goodnews.GoodNewsApplication
//import com.saveurlife.goodnews.R
//import com.saveurlife.goodnews.models.OffMapFacility
//import com.skt.tmap.TMapView
//import com.skt.tmap.overlay.TMapMarkerItem
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//
//class TMapLocationListener: LocationListener {
//    private lateinit var tMapView: TMapView
//    private var markerItem = TMapMarkerItem()
//
//    private var loadedAll = GoodNewsApplication.FacilityDataManager.copiedAll
//    private var loadedShelter = GoodNewsApplication.FacilityDataManager.copiedShelter
//    private var loadedHospital = GoodNewsApplication.FacilityDataManager.copiedHospital
//    private var loadedGrocery = GoodNewsApplication.FacilityDataManager.copiedGrocery
//    private var loadedMinbangwi = GoodNewsApplication.FacilityDataManager.copiedMinbangwi
//    private var loadedEarthquake = GoodNewsApplication.FacilityDataManager.copiedEarthquake
//
//    override fun onLocationChanged(location: Location) {
//        // 위치가 변경되었을 때 실행되는 코드
//        val nowLat = location.latitude
//        val nowLon = location.longitude
//
//        val currentLocation = Location("current_location")
//        currentLocation.latitude = nowLat
//        currentLocation.longitude = nowLon
//
//        // 현재 위치 주변의 좌표만을 뽑아서 지도에 추가
//        val nearbyPoints = filterNearbyPoints(currentLocation, loadedAll, 3.0)
//
//        nearbyPoints.forEachIndexed { index, point ->
//            val marker = TMapMarkerItem()
//            marker.id = "marker${index}"
//            marker.setTMapPoint(point.latitude, point.longitude)
//            marker.icon = BitmapFactory.decodeResource(resources, R.drawable.ic_notification_overlay)
//
//            // 비동기적으로 마커를 지도에 추가
//            GlobalScope.launch(Dispatchers.Main) {
//                tMapView.addTMapMarkerItem(marker)
//                println("등록 마커 ${point.latitude}, ${point.longitude}")
//            }
//        }
//    }
//
//    // 현재 위치 주변의 좌표를 필터링할 함수
//    private fun OffMapFacility.toLocation(): Location {
//        val location = Location("facility_location")
//        location.latitude = this.latitude
//        location.longitude = this.longitude
//        return location
//    }
//    private fun filterNearbyPoints(currentLocation: Location, facilities: MutableList<OffMapFacility>, radius: Double): List<Location> {
//        return facilities
//            .filter { facility ->
//                val facilityLocation = facility.toLocation()
//                val distance = calculateDistance(currentLocation, facilityLocation)
//                distance <= radius
//            }
//            .map { it.toLocation() }
//
//    }
//
//    // 두 지점 간의 거리를 계산하는 함수
//    private fun calculateDistance(location1: Location, location2: Location): Double {
//        val lat1 = location1.latitude
//        val lon1 = location1.longitude
//        val lat2 = location2.latitude
//        val lon2 = location2.longitude
//
//        // Haversine formula 사용
//        val dLat = Math.toRadians(lat2 - lat1)
//        val dLon = Math.toRadians(lon2 - lon1)
//        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                Math.sin(dLon / 2) * Math.sin(dLon / 2)
//        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
//        val radiusOfEarth = 6371.0 // 지구의 반지름 (단위: km)
//        return radiusOfEarth * c
//    }
//    }
//
//}