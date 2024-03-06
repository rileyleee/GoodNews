package com.saveurlife.goodnews.authority

import android.content.Intent
import android.content.pm.PackageManager
import android.health.connect.datatypes.units.Length
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityAuthorityBinding
import com.saveurlife.goodnews.enterinfo.EnterInfoActivity
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.main.PermissionsUtil
import com.saveurlife.goodnews.models.Member
import com.saveurlife.goodnews.sync.DataSyncWorker
import com.saveurlife.goodnews.sync.SyncService
import com.saveurlife.goodnews.tutorial.TutorialActivity
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

class AuthorityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorityBinding
    private lateinit var permissionsUtil: PermissionsUtil
    private val sharedPreferences = GoodNewsApplication.preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.authButton.setOnClickListener {
            Toast.makeText(this, "권한 설정", Toast.LENGTH_SHORT).show()

            var items = sharedPreferences.getString("name", "이름없음")

            if (items == "이름없음") {
                val i = Intent(this, EnterInfoActivity::class.java)
                startActivity(i)
                finish()
            } else {
                val i = Intent(this, MainActivity::class.java)

//                val syncService = SyncService(applicationContext)
//                syncService.backGroundSync()

                startActivity(i)
                finish()
            }
        }
        Log.d("AuthorityActivity","권한 요청 직전입니다.")

        // 위험 권한 요청
        permissionsUtil = PermissionsUtil(this)
        permissionsUtil.requestAllPermissions()

        // 백그라운드 위치 권한 요청
        if (!sharedPreferences.getBoolean(
                "isBackgroundPermissionApproved",
                false
            ) && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsUtil.permissionDialog(this)
        }
    }
}