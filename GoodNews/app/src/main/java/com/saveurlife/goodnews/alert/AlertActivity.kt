package com.saveurlife.goodnews.alert

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityAlertBinding
import com.saveurlife.goodnews.models.Alert
import com.saveurlife.goodnews.service.LocationTrackingService
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort

class AlertActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var binding: ActivityAlertBinding
    private lateinit var gestureDetector: GestureDetector

    private lateinit var recyclerView: RecyclerView
    private lateinit var alertAdapter: AlertAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerViewAlert
        recyclerView.layoutManager = LinearLayoutManager(this)


        //왼쪽 슬라이드해서 알림 Activity 종료하기
        gestureDetector = GestureDetector(this, this)

        val mainLayout: ConstraintLayout = findViewById(R.id.mainLayout) // 레이아웃 ID는 적절히 변경해주세요
        mainLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        //recyclerView에서 방해받지 않으면서 스와이프
        recyclerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false // 이벤트를 RecyclerView에게 계속 전달
        }

        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // 뒤로가기 버튼 눌렀을 경우에 위치 정보 사용 함수 종료 및 앱 종료 콜백 등록
        onBackPressedDispatcher.addCallback(this) {
            // 사용자가 뒤로 가기 버튼을 눌렀을 때 실행할 코드
            val intent = Intent(this@AlertActivity, LocationTrackingService::class.java)
            stopService(intent)

            // 기본적인 뒤로 가기 동작 수행 (옵션)
            finish()
        }


        //realm에서 알림 불러오기
        val config = RealmConfiguration.Builder(schema = setOf(Alert::class)).build()
        val realm = Realm.open(config)



        val items: RealmResults<Alert> = realm.query<Alert>().sort("time", Sort.ASCENDING).find()
        // 예를 들어 RealmResults를 가져온다고 가정
        for (item in items) {
            Log.d("AlertRealmData", "Item: ${item.name}")
        }

        //역순으로 바꾸기
        val reversedList = items.toList().reversed()

        alertAdapter = AlertAdapter(reversedList)
        recyclerView.adapter = alertAdapter

//        realm.close()

    }

    //onFling - 사용자가 빠르게 화면을 스와이프했을 때 호출
    override fun onFling(
        e1: MotionEvent?, //스와이프의 시작 지점에 대한 이벤트 정보
        e2: MotionEvent, //스와이프의 종료 지점에 대한 이벤트 정보
        velocityX: Float,
        velocityY: Float //스와이프의 x축과 y축 방향으로의 속도
    ): Boolean {
        val swipeThreshold = 200 //스와이프 동작을 감지하기 위한 최소 거리를 정의
        val swipeDistanceX = e2.x - (e1?.x ?: 0f) // x축 방향 거리
        val swipeDistanceY = e2.y - (e1?.y ?: 0f) // y축 방향 거리

        //값이 양수이면 오른쪽으로, 음수이면 왼쪽으로 스와이프

        //스와이프 거리가 설정한 임계값보다 크다면, 사용자가 충분히 화면을 오른쪽으로 스와이프 했다고 판단하고 조건문 내의 코드를 실행
        if (kotlin.math.abs(swipeDistanceX) > kotlin.math.abs(swipeDistanceY) && swipeDistanceX > swipeThreshold) {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            return true
        }
        //스와이프 동작이 임계값보다 작으면 false를 반환하여 해당 이벤트가 처리되지 않았음을 나타냄
        return false
    }


    //사용하진 않고 오버라이드만 해둔것
//    GestureDetector.OnGestureListener 인터페이스는 다양한 제스처 이벤트를 처리하기 위한 여러 메서드를 포함
//    이 인터페이스를 구현할 때는 해당 인터페이스에 정의된 모든 메서드를 구현해야함.
//    그렇기 때문에 사용하지 않는 메서드들도 선언해야함.
//    GestureDetector.OnGestureListener 제스처 이벤트를 처리 메서드
//    onDown(): 화면에 손가락을 터치했을 때 호출
//    onShowPress(): 화면에 손가락을 누르고 있을 때 호출
//    onSingleTapUp(): 화면을 한 번 터치하고 떼었을 때 호출
//    onScroll(): 화면을 드래그할 때 호출
//    onLongPress(): 화면에 손가락을 길게 누르고 있을 때 호출
    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onDestroy() {
        super.onDestroy()

        //위치 정보 저장 중지
        val serviceIntent = Intent(this, LocationTrackingService::class.java)
        stopService(serviceIntent)
    }
}