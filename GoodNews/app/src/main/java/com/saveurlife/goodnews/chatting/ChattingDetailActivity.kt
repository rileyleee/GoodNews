package com.saveurlife.goodnews.chatting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.ble.ChatRepository
import com.saveurlife.goodnews.ble.CurrentActivityEvent
import com.saveurlife.goodnews.ble.EventBusManager
import com.saveurlife.goodnews.ble.message.ChatDatabaseManager
import com.saveurlife.goodnews.ble.service.BleService
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.ActivityChattingDetailBinding
import com.saveurlife.goodnews.map.MiniMapDialogFragment


class ChattingDetailActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var binding: ActivityChattingDetailBinding
    private lateinit var gestureDetector: GestureDetector
    private val sharedViewModel: SharedViewModel by viewModels()
    //private var users: List<BleMeshConnectedUser> = emptyList() //옵저빙위한 변수

    lateinit var bleService: BleService

    //서비스가 현재 바인드 되었는지 여부를 나타내는 변수
    private var isBound = false
    private val connection = object : ServiceConnection {
        //Service가 연결되었을 때 호출
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BleService.LocalBinder
            bleService = binder.service
            sharedViewModel.bleService.value = binder.service
            isBound = true

            observeChatRoomMessages()
            getConnectedUsers()
        }

        //서비스 연결이 끊어졌을 때 호출
        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            // 상대방 위치 및 채팅 입력 창 감추기
            binding.chattingToolbar.chatDetailUserLocation.visibility = View.GONE
            binding.chatDetailInput.visibility = View.GONE
            binding.chatDetailSpend.visibility = View.GONE
        }
    }

    //채팅 저장소
    private lateinit var chatDatabaseManager: ChatDatabaseManager
    private lateinit var chatRepository: ChatRepository

    //상대방 Id
    private lateinit var userId: String
    private lateinit var userName: String
    private var userLat: Double = 0.0
    private var userLon: Double = 0.0

    private lateinit var recyclerView: RecyclerView
    private lateinit var chattingDetailAdapter: ChattingDetailAdapter

    private var page = 0
    private val miniMapFragment = MiniMapDialogFragment()


    private fun observeChatRoomMessages() {
        if (::userId.isInitialized) {
            bleService.getReadUpdatedChatRoomMessages(userId).observe(this, Observer { messages ->
                chattingDetailAdapter.updateMessages(messages)
                recyclerView.scrollToPosition(chattingDetailAdapter.itemCount - 1)
            })
        } else {
            // userId가 초기화되지 않았을 경우의 처리 로직
            Log.e("ChattingDetailActivity", "userId is not initialized.")
        }
    }

    private fun getConnectedUsers() {

        if(page==2) {
            Log.v("값 넣기 전 lat:  ", userLat.toString())
            Log.v("값 넣기 전 lon: ", userLon.toString())
            // 초기값 설정
            Log.v("**채팅 뷰모델: ", "이거 호출되었나요")

            if (bleService.getBleMeshConnectedUser(userId) == null) {
                Log.v("연결되지 않은 사용자: ", "null입니다.")

            } else {
                // 연결되어 있는 경우에만 위경도 값 갱신
                userLat = bleService.getBleMeshConnectedUser(userId).lat
                userLon = bleService.getBleMeshConnectedUser(userId).lon
                Log.v("연결된 사용자: ", userLat.toString())
                Log.v("연결된 사용자: ", userLon.toString())
            }

            // 상대방 위치 및 채팅 입력 창 감추기
            if (userLat == 0.0 && userLon == 0.0) {
                Log.v("연결되지 않은 사용자: ", "미니맵/채팅 불가")
                binding.chattingToolbar.chatDetailUserLocation.visibility = View.GONE
                binding.chatDetailInput.visibility = View.GONE
                binding.chatDetailSpend.visibility = View.GONE
            }

            // 상대방 위치 보기(미니맵)
            binding.chattingToolbar.chatDetailUserLocation.setOnClickListener {

                val otherUserLocation = Bundle()
                otherUserLocation.putDouble("latitude", userLat)
                otherUserLocation.putDouble("longitude", userLon)

                miniMapFragment.arguments = otherUserLocation
                miniMapFragment.show(supportFragmentManager, "MiniMapDialogFragment")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChattingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        page = extras?.getInt("page", 0) ?: 0

        recyclerView = binding.recyclerViewChatting
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 지도 상 옵저버 수정 이후 추후 반영 예정
//        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(
//            this,
//            Observer{ connectedDevicesMap ->
//                Log.v("**ChattingDetailActivity","옵저버 가능")
//                Log.v("**개별 채팅화면 연결된 기기 수: ",connectedDevicesMap.size.toString())
//            })


        //MainAroundListFragment(BLE 연결된 사용자 리스트 화면에서 연결)
        if (page == 1) {
            val user = intent.getSerializableExtra("chattingUser") as BleMeshConnectedUser
            val userData = user.toString().split('/')
            if (userData.size >= 6) {
                println("$userData 이거 모게요?")
                userId = userData[0]
                println("$userId 이거 id 모게요?")
                userName = userData[1]
                val updateTime = userData[2]
                val healthStatus = userData[3]
                val lat = userData[4].toDouble()
                val lon = userData[5].toDouble()

                //상대방 이름
                binding.chattingToolbar.chatDetailNameHeader.text = userName
                //상대방 상태 업데이트
                updateOtherStatus(healthStatus)
                //상대방 위치 보기 (미니맵)
                binding.chattingToolbar.chatDetailUserLocation.setOnClickListener {
                    val otherUserLocation = Bundle()
                    otherUserLocation.putDouble("latitude", lat)
                    otherUserLocation.putDouble("longitude", lon)

                    miniMapFragment.arguments = otherUserLocation
                    miniMapFragment.show(supportFragmentManager, "MiniMapDialogFragment")
                }
            }
        }
        //OneChattingFragment(채팅 목록에서 연결)
        else if (page == 2) {
            Log.v("다른 건 업데이트 된건가: ", "else if 직후")
            userId = intent.getStringExtra("chatRoomId")
                ?: throw IllegalStateException("userId not found in Intent")
            if (extras != null) {
                userName = extras.getString("chatName", "")
                val chatOtherStatus = extras.getString("chatOtherStatus", "")

                binding.chattingToolbar.chatDetailNameHeader.text = userName
                updateOtherStatus(chatOtherStatus)
            }
        }

        //ble - 서비스 바인딩
        Intent(this, BleService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        chattingDetailAdapter = ChattingDetailAdapter(emptyList(), userId)
        recyclerView.adapter = chattingDetailAdapter

        // ChatDatabaseManager 및 ChatRepository 인스턴스 생성
        chatDatabaseManager = ChatDatabaseManager()
        chatRepository = ChatRepository(chatDatabaseManager)


        //채팅 입력
        binding.chatDetailSpend.setOnClickListener {
            val inputMessage = binding.chatDetailInput.text.toString()
            if (inputMessage.isNotEmpty()) {
                //상대방의 id, 입력 메세지를 넘기기
                bleService.sendMessageChat(userId, userName, inputMessage)

                // 입력 필드 초기화
                binding.chatDetailInput.text.clear()
            }
        }

        var isKeyboardVisible = false

        val rootView = findViewById<View>(R.id.chattingDetailLayout) // 루트 뷰 ID
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootView.rootView.height - rootView.height
            if (heightDiff > dpToPx(this, 200)) { // 키보드가 나타난 것으로 간주
                if (!isKeyboardVisible) {
                    recyclerView.scrollToPosition(chattingDetailAdapter.itemCount - 1) // 처음 키보드가 나타나면 RecyclerView를 맨 아래로 스크롤
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
        when (chatOtherStatus) {
            "safe" -> binding.chattingToolbar.chatDetailStatus.setBackgroundResource(R.drawable.my_status_safe_circle)
            "injury" -> binding.chattingToolbar.chatDetailStatus.setBackgroundResource(R.drawable.my_status_injury_circle)
            "death" -> binding.chattingToolbar.chatDetailStatus.setBackgroundResource(R.drawable.my_status_death_circle)
            "unknown" -> binding.chattingToolbar.chatDetailStatus.setBackgroundResource(R.drawable.my_status_circle)
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

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        EventBusManager.post(CurrentActivityEvent(userId))
    }

    override fun onPause() {
        super.onPause()
        // 다른 액티비티로 전환될 때에도 이벤트를 보낼 수 있습니다.
        EventBusManager.post(CurrentActivityEvent("none"))
    }
}