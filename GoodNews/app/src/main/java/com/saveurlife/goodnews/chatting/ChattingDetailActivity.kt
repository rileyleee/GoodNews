package com.saveurlife.goodnews.chatting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.ble.service.BleService
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.ActivityChattingDetailBinding
import com.saveurlife.goodnews.databinding.ActivityMainBinding
import com.saveurlife.goodnews.databinding.FragmentOneChattingBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChattingDetailActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var binding: ActivityChattingDetailBinding
    private lateinit var gestureDetector: GestureDetector

    private val sharedViewModel: SharedViewModel by viewModels()

    lateinit var bleService: BleService
    //서비스가 현재 바인드 되었는지 여부를 나타내는 변수
    private var isBound = false

    private val connection = object : ServiceConnection {
        //Service가 연결되었을 때 호출
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BleService.LocalBinder
//            bleService = binder.service
            sharedViewModel.bleService.value = binder.service
            isBound = true
        }

        //서비스 연결이 끊어졌을 때 호출
        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }


    // dp를 픽셀로 변환하는 함수
    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ble - 서비스 바인딩
        Intent(this, BleService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }


        val user = intent.getSerializableExtra("chattingUser") as BleMeshConnectedUser
        println("무슨 값이 넘어왔나요 ?????? $user")
        val userData = user.toString().split('/')
        if (userData.size >= 6) {
            val userId = userData[0]
            val userName = userData[1]
            val updateTime = userData[2]
            val healthStatus = userData[3]
            val lat = userData[4].toDouble()
            val lon = userData[5].toDouble()

            //상대방 이름
            binding.chattingToolbar.chatDetailNameHeader.text = userName
            //상태 업데이트
            updateOtherStatus(healthStatus)

        }



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

        binding.chatDetailInput.setOnClickListener{
            adapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(chatting.size - 1)
        }


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

//                bleService.sendMessageChat(user.userId, inputMessage)

                // 채팅 목록에 추가하고 어댑터에 알림
                (recyclerView.adapter as ChattingDetailAdapter).addMessage(newMessage)
                //RecyclerView가 표시하는 목록인 chatting 리스트의 마지막 항목으로 스크롤
                recyclerView.scrollToPosition(chatting.size - 1)

                // 입력 필드 초기화
                binding.chatDetailInput.text.clear()
            }
        }

//        //
//
//        binding.chattingDetailLayout.viewTreeObserver.addOnGlobalLayoutListener {
//            val r = Rect()
//            binding.chattingDetailLayout.getWindowVisibleDisplayFrame(r)
//            val screenHeight = binding.chattingDetailLayout.rootView.height
//
//            // 키보드 높이 계산
//            val keypadHeight = screenHeight - r.bottom
//
//            if (keypadHeight > screenHeight * 0.15) { // 키보드가 화면의 15% 이상을 차지하는 경우
//                recyclerView.scrollToPosition(adapter.itemCount - 1)
//            }
//        }

        var isKeyboardVisible = false

        val rootView = findViewById<View>(R.id.chattingDetailLayout) // 루트 뷰 ID
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootView.rootView.height - rootView.height
            if (heightDiff > dpToPx(this, 200)) { // 키보드가 나타난 것으로 간주
                if (!isKeyboardVisible) {
                    recyclerView.scrollToPosition(adapter.itemCount - 1) // 처음 키보드가 나타나면 RecyclerView를 맨 아래로 스크롤
                    isKeyboardVisible = true
                }
            } else {
                isKeyboardVisible = false
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
        binding.chattingToolbar.backButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    //상태 초기화
    private fun updateOtherStatus(chatOtherStatus: String?) {
        when(chatOtherStatus){
            "safe" -> binding.chattingToolbar.chatDetailStatus.setBackgroundResource(R.drawable.my_status_safe_circle)
            "injury" -> binding.chattingToolbar.chatDetailStatus.setBackgroundResource(R.drawable.my_status_injury_circle)
            "death" -> binding.chattingToolbar.chatDetailStatus.setBackgroundResource(R.drawable.my_status_death_circle)
            "unknown" -> binding.chattingToolbar.chatDetailStatus.setBackgroundResource(R.drawable.my_status_circle)
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