package com.saveurlife.goodnews.alarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityAlarmBinding
import com.saveurlife.goodnews.service.LocationTrackingService

class AlarmActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var binding: ActivityAlarmBinding
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gestureDetector = GestureDetector(this, this)

        val mainLayout: ConstraintLayout = findViewById(R.id.mainLayout) // 레이아웃 ID는 적절히 변경해주세요
        mainLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // 뒤로가기 버튼 눌렀을 경우에 위치 정보 사용 함수 종료 및 앱 종료 콜백 등록
        onBackPressedDispatcher.addCallback(this) {
            // 사용자가 뒤로 가기 버튼을 눌렀을 때 실행할 코드
            val intent = Intent(this@AlarmActivity, LocationTrackingService::class.java)
            stopService(intent)

            // 기본적인 뒤로 가기 동작 수행 (옵션)
            finish()
        }

    }

    //onFling - 사용자가 빠르게 화면을 스와이프했을 때 호출
    override fun onFling(
        e1: MotionEvent?, //스와이프의 시작 지점에 대한 이벤트 정보
        e2: MotionEvent, //스와이프의 종료 지점에 대한 이벤트 정보
        velocityX: Float,
        velocityY: Float //스와이프의 x축과 y축 방향으로의 속도
    ): Boolean {
        val swipeThreshold = 100 //스와이프 동작을 감지하기 위한 최소 거리를 정의
        val swipeDistance = e2?.x?.minus(e1?.x ?: 0f) ?: 0f // 스와이프의 시작 지점에서 종료 지점까지의 x축 방향 거리를 계산
        //값이 양수이면 오른쪽으로, 음수이면 왼쪽으로 스와이프

        //스와이프 거리가 설정한 임계값보다 크다면, 사용자가 충분히 화면을 오른쪽으로 스와이프 했다고 판단하고 조건문 내의 코드를 실행
        if (swipeDistance > swipeThreshold) {
            finish()
            //액티비티 전환 시 사용할 애니메이션을 지정
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