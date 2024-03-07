package com.saveurlife.goodnews

import android.app.Activity
import android.app.Application
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.opencsv.CSVReader
import com.saveurlife.goodnews.main.PreferencesUtil
import com.saveurlife.goodnews.models.AidKit
import com.saveurlife.goodnews.models.Alert
import com.saveurlife.goodnews.models.Chat
import com.saveurlife.goodnews.models.ChatMessage
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.models.FamilyPlace
import com.saveurlife.goodnews.models.GroupMemInfo
//import com.saveurlife.goodnews.models.GroupMemInfo
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.models.MorseCode
import com.saveurlife.goodnews.models.OffMapFacility
import com.saveurlife.goodnews.service.UserDeviceInfoService
import com.skt.tmap.TMapGpsManager
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class GoodNewsApplication : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
//        lateinit var userDeviceInfoService: UserDeviceInfoService
        lateinit var preferences: PreferencesUtil
        lateinit var realmConfiguration: RealmConfiguration
        var isInitialized = false
    }

    var isInBackground = true

    override fun onCreate() {
        var userDeviceInfoService = UserDeviceInfoService.getInstance(applicationContext)

        // 앱 전역에서 활용하기 위해 싱글톤 패턴으로 SharedPreference 구현
        preferences = PreferencesUtil(applicationContext)

        // 앱 실행 시마다 지도 프래그먼트 로드 가능 여부는 false로 초기화
        preferences.setBoolean("canLoadMapFragment", false)

        super.onCreate()

        // Google Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.GOOGLE_MAPS_API_KEY)
        }

        registerActivityLifecycleCallbacks(this)

        //Realm 초기화
        realmConfiguration = RealmConfiguration.create(
            schema = setOf(
                AidKit::class,
                Alert::class,
                Chat::class,
                ChatMessage::class,
                FamilyMemInfo::class,
                FamilyPlace::class,
                GroupMemInfo::class,
                MapInstantInfo::class,
                Member::class,
                MorseCode::class,
                OffMapFacility::class
            )
        )
//         Realm.deleteRealm(realmConfiguration)

        val realm: Realm = Realm.open(realmConfiguration)


        //오프라인 지도 위 시설정보 초기 입력
        val csvReader =
            CSVReader(InputStreamReader(resources.openRawResource(R.raw.offmapfacilitydata)))
        csvReader.readNext()  // 헤더 레코드를 읽고 무시
        val records = csvReader.readAll()

        // 데이터가 없는 경우에만 등록하도록!
        if (realm.query<OffMapFacility>().count().find()==0L) {
            Log.d("데이터 존재 여부", "시설 정보 없어요")

            // 비동기 처리를 위해 코루틴 사용
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    for (record in records) {
                        val offMapFacility = OffMapFacility().apply {
                            id = record[0].toInt()
                            type = record[1]
                            name = record[2]
                            latitude = record[4].toDouble()
                            longitude = record[3].toDouble()
                            canUse = record[5] == "1"
                            addInfo = record[6]
                        }
                        copyToRealm(offMapFacility)
                    }

                }
                // REALM 데이터 입력 확인 코드
                val testData: OffMapFacility? =
                    realm.query<OffMapFacility>("id == $0", 5).first().find()
                Log.d("TestData", testData.toString())

                testData?.let {
                    Log.d("TestData_Specific", "ID: ${it.id}, Type: ${it.type}, Name: ${it.name}")
                }

                realm.close()

                FacilityDataManager.initializeDataIfNeeded{
                    preferences.setBoolean("canLoadMapFragment", true)
                }
            }
        } else {
            Log.d("데이터 존재 여부", "시설 정보 있으므로 추가 저장 불필요")
            FacilityDataManager.initializeDataIfNeeded{
                preferences.setBoolean("canLoadMapFragment", true)
            }
        }
    }

    object FacilityDataManager{
//        private var isInitialized = false
        val copiedAll = mutableListOf<OffMapFacility>()
        val copiedShelter = mutableListOf<OffMapFacility>()
        val copiedHospital = mutableListOf<OffMapFacility>()
        val copiedGrocery = mutableListOf<OffMapFacility>()
        val copiedMinbangwi = mutableListOf<OffMapFacility>()
        val copiedEarthquake = mutableListOf<OffMapFacility>()

        fun initializeDataIfNeeded(callback: () -> Unit) {
            if (!isInitialized) {
                CoroutineScope(Dispatchers.IO).launch {
                    loadDataFromRealm()
                    isInitialized = true
                    withContext(Dispatchers.Main) {
                        Log.i("**Applcation레벨**","처음으로 시설 데이터 초기 리스트 작업 완료")
                        callback.invoke()
                    }
                }
            } else {
                Log.i("**Applcation레벨**","이미 시설 데이터 초기 리스트 작업 완료")
                callback.invoke()
            }
        }
        private suspend fun loadDataFromRealm() {
            withContext(Dispatchers.IO) {
                val realm = Realm.open(realmConfiguration)
                val facsDataAll = realm.query<OffMapFacility>().find()
                val shelter = realm.query<OffMapFacility>("type = $0", "대피소").find()
                val hospital =
                    realm.query<OffMapFacility>("type = $0 OR type = $1", "약국", "병원").find()
                val grocery =
                    realm.query<OffMapFacility>("type = $0 OR type = $1", "편의점", "마트").find()
                val minbangwi = realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "민방위").find()
                val earthquake =
                    realm.query<OffMapFacility>("addInfo CONTAINS[c] $0", "지진해일").find()

                facsDataAll.forEach { fac ->
                    copiedAll.add(copyFacsData(fac))
                }
                shelter.forEach { fac ->
                    copiedShelter.add(copyFacsData(fac))
                }
                hospital.forEach { fac ->
                    copiedHospital.add(copyFacsData(fac))
                }
                grocery.forEach { fac ->
                    copiedGrocery.add(copyFacsData(fac))
                }
                minbangwi.forEach { fac ->
                    copiedMinbangwi.add(copyFacsData(fac))
                }
                earthquake.forEach { fac ->
                    copiedEarthquake.add(copyFacsData(fac))
                }

                realm.close()

                Log.i("**Applcation레벨**","Realm에 접근하여 시설 데이터 초기 리스트 복사 완료")
            }
        }
            fun copyFacsData(data: OffMapFacility): OffMapFacility {
                return OffMapFacility().apply {
                    this.id = data.id
                    this.name = data.name
                    this.type = data.type
                    this.latitude = data.latitude
                    this.longitude = data.longitude
                    this.canUse = data.canUse
                    this.addInfo = data.addInfo
            }
        }
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        isInBackground = false
    }

    override fun onActivityPaused(activity: Activity) {
        isInBackground = true
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}
