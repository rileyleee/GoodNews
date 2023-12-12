package com.saveurlife.goodnews

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.api.MapAPI
import com.saveurlife.goodnews.api.MemberAPI
import com.saveurlife.goodnews.api.PlaceDetailInfo
import com.saveurlife.goodnews.authority.AuthorityActivity
import com.saveurlife.goodnews.databinding.ActivityLoadingBinding
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.main.PermissionsUtil
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.service.DeviceStateService
import com.saveurlife.goodnews.service.UserDeviceInfoService
import com.saveurlife.goodnews.sync.AllDataSync
import com.saveurlife.goodnews.sync.DataSyncWorker
import com.saveurlife.goodnews.tutorial.TutorialActivity
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding
    private lateinit var permissionsUtil: PermissionsUtil
    private val sharedPreferences = GoodNewsApplication.preferences

//    private val config = RealmConfiguration.create(schema = setOf(Member::class, Location::class))
//    private val realm: Realm = Realm.open(config)


    // WorkManager
    private lateinit var workManager: WorkManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        workManager = WorkManager.getInstance(applicationContext)
//        Handler().postDelayed(Runnable {
//            val i = Intent(this@LoadingActivity, TutorialActivity::class.java)
//            startActivity(i)
//            finish()
//        }, 2000)


        permissionsUtil = PermissionsUtil(this)
        val toAuthority = permissionsUtil.needMorePermissions()
        var items = sharedPreferences.getString("name", "이름없음")

        Handler().postDelayed(Runnable {

            if (items !== "이름없음") {
                // 허용하지 않은 권한이 하나라도 있다면!
                if (toAuthority || !sharedPreferences.getBoolean(
                        "isBackgroundPermissionApproved",
                        false
                    ) && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) { //권한 필요해
                    val i = Intent(this@LoadingActivity, AuthorityActivity::class.java)
                    startActivity(i)
                    finish()

                } else { //권한 필요 없어
                    val i = Intent(this@LoadingActivity, MainActivity::class.java)

                    val deviceStateService = DeviceStateService()
                    if(deviceStateService.isNetworkAvailable(applicationContext)){
                        // 동기화
                        val allDataSync = AllDataSync(applicationContext)
                        allDataSync.fetchAllData()
                    }
                    startActivity(i)
                    finish()
                }

            } else {
                val i = Intent(this@LoadingActivity, TutorialActivity::class.java)
                startActivity(i)
                finish()
            }
        }, 2000)

//            // 앱의 MainActivity로 넘어가기
//            if (items.isEmpty()) {
//                val i = Intent(this@LoadingActivity, TutorialActivity::class.java)
//                startActivity(i)
//                // 현재 액티비티 닫기
//                finish()
//            } else {
//                val i = Intent(this@LoadingActivity, MainActivity::class.java)
//
//                Log.d("test", "down")
//                // 조건 설정 - 인터넷 연결 시에만 실행
//                val constraints = Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .build()
//
//                // request 생성
//                val updateRequest = OneTimeWorkRequest.Builder(DataSyncWorker::class.java)
//                    .setConstraints(constraints)
//                    .build()
//
//                // 실행
//                workManager.enqueue(updateRequest)
//
//                startActivity(i)
//                finish()
//            }
//
//        }, 2000)


//        binding.start.setOnClickListener {
//            val intent = Intent(this, TutorialActivity::class.java)
//            startActivity(intent)
//        }
    }
}