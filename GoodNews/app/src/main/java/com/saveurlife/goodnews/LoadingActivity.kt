package com.saveurlife.goodnews

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.saveurlife.goodnews.databinding.ActivityLoadingBinding
import com.saveurlife.goodnews.main.FamilyAlarmFragment
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.models.Member
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

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