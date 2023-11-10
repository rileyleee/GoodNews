package com.saveurlife.goodnews.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityChattingDetailBinding
import com.saveurlife.goodnews.databinding.ActivityMainBinding
import com.saveurlife.goodnews.databinding.FragmentOneChattingBinding

class ChattingDetailActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var binding: ActivityChattingDetailBinding
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //MainActivity-OneChattingFragment에서 받은 값 채팅창에 초기화
        val chatName = intent.getStringExtra("chatName")
        val chatOtherStatus = intent.getStringExtra("chatOtherStatus")
        binding.chatDetailNameHeader.text = chatName
        updateOtherStatus(chatOtherStatus)

        //채팅 목록으로 돌아갈 때 스와이프
        gestureDetector = GestureDetector(this, this)

        binding.chattingDetailLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        //채팅 목록으로 돌아가기
        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }


        val chatting = listOf(
            ChattingDetailData("김싸피", "갈게"),
            ChattingDetailData("김싸피", "안녕"),
            ChattingDetailData("김싸피", "어디야"),
            ChattingDetailData("김싸피", "안녕하세요"),
            ChattingDetailData("김싸피", "조심해")
        )
    }

    //상태 초기화
    private fun updateOtherStatus(chatOtherStatus: String?) {
        when(chatOtherStatus){
            "safe" -> binding.chatDetailStatus.setBackgroundResource(R.drawable.my_status_safe_circle)
            "injury" -> binding.chatDetailStatus.setBackgroundResource(R.drawable.my_status_injury_circle)
            "death" -> binding.chatDetailStatus.setBackgroundResource(R.drawable.my_status_death_circle)
            "unknown" -> binding.chatDetailStatus.setBackgroundResource(R.drawable.my_status_circle)
        }
    }

    //스와이프
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

    //사용하진 않지만 오버라이드 해줘야함
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
}