package com.saveurlife.goodnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.saveurlife.goodnews.api.FamilyAPI
import com.saveurlife.goodnews.api.MapAPI
import com.saveurlife.goodnews.api.MemberAPI
import com.saveurlife.goodnews.databinding.ActivityLoadingBinding
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.service.DeviceStateService
import com.saveurlife.goodnews.service.UserDeviceInfoService
import com.saveurlife.goodnews.sync.DataSyncWorker
import com.saveurlife.goodnews.tutorial.TutorialActivity
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoadingBinding

//    private val config = RealmConfiguration.create(schema = setOf(Member::class, Location::class))
//    private val realm: Realm = Realm.open(config)

    val realm = Realm.open(GoodNewsApplication.realmConfiguration)
    private val items: RealmResults<Member> = realm.query<Member>().find()
    // WorkManager
    private lateinit var workManager:WorkManager
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



        Handler().postDelayed(Runnable {
            // 앱의 MainActivity로 넘어가기
            if(items.isEmpty()){
                val i = Intent(this@LoadingActivity, TutorialActivity::class.java)
                startActivity(i)
                // 현재 액티비티 닫기
                finish()
            }else{
                val i = Intent(this@LoadingActivity, MainActivity::class.java)

                Log.d("test","down")
                // 조건 설정 - 인터넷 연결 시에만 실행
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                // request 생성
                val updateRequest = OneTimeWorkRequest.Builder(DataSyncWorker::class.java)
                    .setConstraints(constraints)
                    .build()

                // 실행
                workManager.enqueue(updateRequest)

                startActivity(i)
                finish()
            }
        }, 2000)




//        binding.start.setOnClickListener {
//            val intent = Intent(this, TutorialActivity::class.java)
//            startActivity(intent)
//        }
    }
}