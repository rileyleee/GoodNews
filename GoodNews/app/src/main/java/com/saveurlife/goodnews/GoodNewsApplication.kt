package com.saveurlife.goodnews

import android.app.Application
import android.util.Log
import com.opencsv.CSVReader
import com.saveurlife.goodnews.main.PreferencesUtil
import com.saveurlife.goodnews.models.AidKit
import com.saveurlife.goodnews.models.Alert
import com.saveurlife.goodnews.models.Chat
import com.saveurlife.goodnews.models.ChatMessage
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.models.FamilyPlace
import com.saveurlife.goodnews.models.GroupMemInfo
import com.saveurlife.goodnews.models.Location
import com.saveurlife.goodnews.models.MapInstantInfo
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.models.MorseCode
import com.saveurlife.goodnews.models.OffMapFacility
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.zip.ZipInputStream

class GoodNewsApplication : Application() {

    companion object {
        lateinit var preferences: PreferencesUtil
    }

    override fun onCreate() {

        // 앱 전역에서 활용하기 위해 싱글톤 패턴으로 SharedPreference 구현
        preferences = PreferencesUtil(applicationContext)

        super.onCreate()


        // 앱의 첫 실행 확인 및 타일 데이터 복사
        val isFirstRun = preferences.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            copyDBFromAssets()
            preferences.setBoolean("isFirstRun", false)
        }

        //Realm 초기화
        val config = RealmConfiguration.create(
            schema = setOf(
                AidKit::class,
                Alert::class,
                Chat::class,
                ChatMessage::class,
                FamilyMemInfo::class,
                FamilyPlace::class,
                GroupMemInfo::class,
                Location::class,
                MapInstantInfo::class,
                Member::class,
                MorseCode::class,
                OffMapFacility::class
            )
        )

        val realm: Realm = Realm.open(config)

        //오프라인 지도 위 시설정보 초기 입력
        val csvReader =
            CSVReader(InputStreamReader(resources.openRawResource(R.raw.offmapfacilitydata)))
        csvReader.readNext()  // 헤더 레코드를 읽고 무시
        val records = csvReader.readAll()

        // 데이터가 없는 경우에만 등록하도록!
        if (realm.query<OffMapFacility>().count().equals(0L)) {

            // 비동기 처리를 위해 코루틴 사용
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    for (record in records) {
                        // Assuming the CSV columns are id, type, name, latitude, longitude, canUse, addInfo
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
        }
    }


    // 타일을 내부 저장소로 복사
    private fun copyDBFromAssets() {
        val assetManager = assets
        val zipFileName = "testm.zip"

        val `in` = assetManager.open(zipFileName)

        val osmDir = File(filesDir, "osmdroid")
        if (!osmDir.exists()) {
            if (osmDir.mkdirs()) {
                Log.d("DirectoryStatus", "osmdroid 폴더 생성 성공!")
            } else {
                Log.e("DirectoryStatus", "osmdroid 폴더 생성 실패!")
                return
            }
        } else {
            Log.d("DirectoryStatus", "osmdroid 폴더가 이미 있어!")
        }

        ZipInputStream(`in`).use { zipInputStream ->
            var zipEntry = zipInputStream.nextEntry
            while (zipEntry != null) {
                val newFile = File(osmDir, zipEntry.name)
                if (zipEntry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    val fOStream = FileOutputStream(newFile)
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (zipInputStream.read(buffer).also { length = it } != -1) {
                        fOStream.write(buffer, 0, length)
                    }
                    fOStream.close()
                    Log.d("FileCopyStatus", "${zipEntry.name} 파일 복사 완료!")
                }
                zipEntry = zipInputStream.nextEntry
            }
            Log.d("UnzipStatus", "압축 해제 완료!")  // 압축 해제 완료 로그 추가
        }
    }
}


//        val outFile = File(osmDir, zipFileName)
//        val out = FileOutputStream(outFile)

//        // 압축 풀기
//        ZipInputStream(assetManager.open(zipFileName)).use { zipInputStream ->
//            val zipEntry = zipInputStream.nextEntry
//
//            if (zipEntry != null && zipEntry.name == dbName) {
//                val buffer = ByteArray(1024)
//                var read: Int
//                while (zipInputStream.read(buffer).also { read = it } != -1) {
//                    out.write(buffer, 0, read)
//                }
//            }
//        }
//        out.flush()
//        out.close()
//    }
//}