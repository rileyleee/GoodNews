package com.saveurlife.goodnews.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityChattingDetailBinding
import com.saveurlife.goodnews.databinding.ActivityMainBinding
import com.saveurlife.goodnews.databinding.FragmentOneChattingBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

        val chatting = mutableListOf(
            ChattingDetailData("김싸피", "갈게", "오전 9:27"),
            ChattingDetailData("김싸피", "안녕", "오전 9:27"),
            ChattingDetailData("김싸피", "어디야", "오전 9:27"),
            ChattingDetailData("김싸피", "안녕하세요", "오전 9:27"),
            ChattingDetailData("김싸피", "조심해", "오전 9:27"),
            ChattingDetailData("김싸피", "조심해", "오전 9:27"),
            ChattingDetailData("김싸피", "조심해", "오전 9:27"),
            ChattingDetailData("김싸피", "조심해", "오전 9:27"),
            ChattingDetailData("김싸피", "조심해조심해조심해조심해조심해조심해조심해조심해", "오전 9:27"),
            ChattingDetailData("김싸피", "조심해조심해조심해조심해조심해조심해조심해조심해조심해조심해조심해조심해조심해조심해조심해조심해", "오전 9:27")
        )

        val adapter = ChattingDetailAdapter(chatting)
        val recyclerView = binding.recyclerViewChatting
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        //제일 최근 채팅부터
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(chatting.size - 1)


        //채팅 입력
        binding.chatDetailSpend.setOnClickListener {
            val inputMessage = binding.chatDetailInput.text.toString()
            if (inputMessage.isNotEmpty()) {
                // 현재 시간을 가져오고 포맷팅
                val currentDateTime = LocalDateTime.now()
                // "오후 7:12" 형식으로 포맷팅
                val formatter = DateTimeFormatter.ofPattern("a K:mm", Locale.getDefault())
                val formattedDateTime = currentDateTime.format(formatter)
                // 새로운 메시지 객체 생성
                val newMessage = ChattingDetailData("사용자 이름", inputMessage, formattedDateTime, isUserMessage = true)

                // 채팅 목록에 추가하고 어댑터에 알림
                (recyclerView.adapter as ChattingDetailAdapter).addMessage(newMessage)
                //RecyclerView가 표시하는 목록인 chatting 리스트의 마지막 항목으로 스크롤
                recyclerView.scrollToPosition(chatting.size - 1)

                // 입력 필드 초기화
                binding.chatDetailInput.text.clear()
            }
        }

        //채팅 목록으로 돌아갈 때 스와이프
        gestureDetector = GestureDetector(this, this)

        binding.chattingDetailLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        //recyclerView에서 방해받지 않으면서 스와이프
        recyclerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false // 이벤트를 RecyclerView에게 계속 전달
        }

        //채팅 목록으로 돌아가기
        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
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