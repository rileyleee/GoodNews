package com.saveurlife.goodnews

import android.app.Activity
import android.app.Application
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
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.models.MorseCode
import com.saveurlife.goodnews.models.OffMapFacility
import com.saveurlife.goodnews.models.WaitFamily
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

class GoodNewsApplication : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        lateinit var preferences: PreferencesUtil
        lateinit var realmConfiguration: RealmConfiguration
    }

    var isInBackground = true

    override fun onCreate() {


        // 앱 전역에서 활용하기 위해 싱글톤 패턴으로 SharedPreference 구현
        preferences = PreferencesUtil(applicationContext)

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
                OffMapFacility::class,
                WaitFamily::class
            )
        )

//        Realm.deleteRealm(realmConfiguration)

        val realm: Realm = Realm.open(realmConfiguration)




        //오프라인 지도 위 시설정보 초기 입력
        val csvReader =
//            CSVReader(InputStreamReader(resources.openRawResource(R.raw.offmapfacilitydata)))
            CSVReader(InputStreamReader(resources.openRawResource(R.raw.offmapfacility_wo_phar)))
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
            }
        } else {
            Log.d("데이터 존재 여부", "시설 정보 있어요")
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
