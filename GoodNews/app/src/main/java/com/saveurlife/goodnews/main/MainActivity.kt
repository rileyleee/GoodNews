package com.saveurlife.goodnews.main

import LoadingDialog
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.alarm.AlarmActivity
import com.saveurlife.goodnews.ble.service.BleService
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.ActivityMainBinding
import com.saveurlife.goodnews.models.FamilyMemInfo
import com.saveurlife.goodnews.family.FamilyFragment
import com.saveurlife.goodnews.service.LocationTrackingService
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

class MainActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding

    //    val dialog = LoadingDialog(this)
    private val dialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }
    val sharedPreferences = GoodNewsApplication.preferences

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    companion object {
        var checkFlash: Boolean = false
    }
//    private val config = RealmConfiguration.create(schema = setOf(Member::class, Location::class))
//    private val realm: Realm = Realm.open(config)


    // MediaPlayer 객체를 클래스 레벨 변수로 선언
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var familyFragment: FamilyFragment

    // flash on 여부
    private val sharedViewModel: SharedViewModel by viewModels()

    //ble
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

            // BleService의 LiveData를 관찰하고 SharedViewModel을 통해 업데이트합니다.
            // 데이터가 변경될 때마다 SharedViewModel의 bleDeviceNames 라이브 데이터를 새로운 값으로 업데이트
            if (::bleService.isInitialized) {
                bleService.getDeviceArrayListNameLiveData()
                    .observe(this@MainActivity, Observer { deviceNames ->
                        val deviceMap = mutableMapOf<String, String>()
                        deviceNames.forEach { deviceName ->
                            val parts = deviceName.split("/")

                            // parts 리스트에서 필요한 데이터를 추출합니다.
                            if (parts.size >= 2) {
                                val deviceId = parts[0]
                                val deviceName = parts[1]
                                println("아이디는 이거에요 $deviceId")
                                println("이름은 이거에요 $deviceName")

                                deviceMap[deviceId] = deviceName
//                        sharedViewModel.bleDeviceNames.value = deviceNames
                            }
                        }
                        sharedViewModel.bleDeviceMap.value = deviceMap
                    })

                bleService.getBleMeshConnectedDevicesArrayListLiveData()
                    .observe(this@MainActivity) { connectedDevicesMap ->

                        sharedViewModel.updateBleMeshConnectedDevicesMap(connectedDevicesMap)

                        connectedDevicesMap.forEach { (deviceId, connectedUsersMap) ->
                            println("BLE 장치 ID: $deviceId")

                            connectedUsersMap.forEach { (userId, user) ->
                                println("사용자 ID: $userId, 이름: ${user.userName}, 상태: ${user.healthStatus}, 업데이트시간:${user.updateTime}, 위도: ${user.lat}, 경도: ${user.lon}")
                            }

                        }
                    }
            }
        }

        //서비스 연결이 끊어졌을 때 호출
        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val realm = Realm.open(GoodNewsApplication.realmConfiguration)
        val familyItems: RealmResults<FamilyMemInfo> = realm.query<FamilyMemInfo>().find()
        familyFragment = FamilyFragment()
        //ble - 서비스 바인딩
        Intent(this, BleService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
//
//        realm.writeBlocking {
//            copyToRealm(Member().apply {
//                phone = 1012345678
//                name = "김싸피"
//                birthDate = "입력하지 않음"
//                gender = "입력하지 않음"
//                bloodType = "입력하지 않음"
//                addInfo = "입력하지 않음"
//            })
//        }

        //FamilyMemInfo 객체 데이터베이스가 비어있을 때만 가족 모달창 띄우기
        if (familyItems.isEmpty() && !sharedPreferences.getBoolean("familyAlarmIgnore", false)) {
            val dialog = FamilyAlarmFragment()
            realm.close()
            dialog.show(supportFragmentManager, "FamilyAlarmFragment")
        } else {
            realm.close()
        }

        // 다시 보지 않기 여부에 따라 다이얼로그 띄워주기
        if (!sharedPreferences.getBoolean("mapDownloadIgnore", false)) {
            val dialog = MapAlarmFragment()
            dialog.show(supportFragmentManager, "MapAlarmFragment")
        }

        // viewmodel 설정
        sharedViewModel.isOnFlash.observe(this, Observer { isOn ->
            binding.navigationView.menu.getItem(2).isEnabled = !isOn
        })

        //상단바 toolbar
        setSupportActionBar(binding.toolbar)

        //Fragment 갈아 끼우기
        binding.navigationView.background = null
        binding.navigationView.menu.getItem(2).isEnabled = false

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.mapFragment,
                R.id.familyFragment,
                R.id.myPageFragment,
                R.id.flashlightFragment,
                R.id.chattingFragment,
                R.id.chooseGroupMemberFragment
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        binding.navigationView.setupWithNavController(navController)
        binding.navigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment, R.id.familyFragment, R.id.myPageFragment -> {
                    navController.navigateSingleTop(menuItem.itemId)
                    true
                }

                R.id.mapFragment -> {
                    // 지도 Fragment로 이동할 때 Loading ProgressBar를 표시
                    showLoadingProgressBar()
                    navController.navigateSingleTop(menuItem.itemId)
                    true
                }

                else -> false
            }
        }

        // 알림창 갔다가 다시 돌아올 때 toolbar, navigationBottom 원래대로 표시
        supportFragmentManager.addOnBackStackChangedListener {
            // 프래그먼트 스택에 프래그먼트가 없을 때 Toolbar와 BottomNavigationView 표시
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.toolbar.visibility = View.VISIBLE
                binding.navigationView.visibility = View.VISIBLE
                binding.bottomAppBar.visibility = View.VISIBLE
                binding.mainCircleAddButton.visibility = View.VISIBLE
            }
        }

        binding.mainCircleAddButton.setOnClickListener {
            showDialog()
        }

        // 사용자에게 배터리 최적화 무시 요청 (단, 조건에 따라 요청)
        if (!isBatteryOptimizationIgnored(this)) {
            AlertDialog.Builder(this).apply {
                setTitle("배터리 최적화 일시 중지")
                setMessage("위급 상황에서 위치를 실시간으로 저장하기 위해 최적화를 중지합니다.")
                setPositiveButton("확인") { _, _ ->
                    val intent =
                        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:com.saveurlife.goodnews")
                        }
                    startActivity(intent)
                }
                setNegativeButton("취소", null)
                show()
            }
        }

        // 위치 정보 사용 함수 호출
        callLocationTrackingService()

        // 뒤로가기 버튼 눌렀을 경우에 위치 정보 사용 함수 종료 및 앱 종료 콜백 등록
        onBackPressedDispatcher.addCallback(this) {
            // 사용자가 뒤로 가기 버튼을 눌렀을 때 실행할 코드
            val intent = Intent(this@MainActivity, LocationTrackingService::class.java)
            stopService(intent)

            // 기본적인 뒤로 가기 동작 수행 (옵션)
            finish()
        }
    }

    // 로딩 프로그래스 바 표시 함수
    private fun showLoadingProgressBar() {

        dialog.show()
    }

    // 로딩 프로그래스 바 감추기 함수
    fun hideLoadingProgressBar() {
        Log.i("ddasfasff", "hideLoadingProgressBar: ")
        dialog.dismiss()
    }


    // 배터리 최적화 여부 확인 -> boolean 반환
    private fun isBatteryOptimizationIgnored(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)

    }

    private fun startAdvertiseAndScan() {
        if (isBound && ::bleService.isInitialized) {
            bleService.startAdvertiseAndScanAndAuto()
            Toast.makeText(this, "광고 및 스캔 시작", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "서비스가 바인딩되지 않음", Toast.LENGTH_SHORT).show()
        }
    }

    // LiveData 객체를 프래그먼트에서 관찰할 수 있도록 공개 메서드로 제공
//    fun getDeviceArrayListNameLiveData(): LiveData<List<String>>? {
//        return if (isBound) bleService.deviceArrayListNameLiveData else null
//    }


    //Dialog fragment 모달창
    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.setContentView(R.layout.dialog_layout) // dialog_layout는 표시하고자 하는 다이얼로그의 레이아웃 이름입니다.


        // 필요한 경우 다이얼로그의 버튼 또는 다른 뷰에 대한 이벤트 리스너를 여기에 추가합니다.
        val view = dialog.window?.decorView

        // 투명도 애니메이션 설정
        val alphaAnimation = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        alphaAnimation.duration = 300

        // 크기 애니메이션 설정
        val scaleXAnimation = ObjectAnimator.ofFloat(view, "scaleX", 0.0f, 1.0f)
        scaleXAnimation.duration = 300
        val scaleYAnimation = ObjectAnimator.ofFloat(view, "scaleY", 0.0f, 1.0f)
        scaleYAnimation.duration = 300


        // 애니메이션 세트를 생성하고 시작
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnimation, scaleXAnimation, scaleYAnimation)
        animatorSet.start()

        //경보음
        val soundLayer = dialog.findViewById<View>(R.id.soundLayer)
        soundLayer.setOnClickListener {
            // 두 번째 다이얼로그 표시 함수 호출
            showSecondDialog()
            dialog.dismiss()
        }

        //채팅
        val chattingLayer = dialog.findViewById<View>(R.id.chattingLayer)
        chattingLayer.setOnClickListener {
            // 두 번째 다이얼로그 표시 함수 호출
            showChattingDialog()
//            binding.navigationView.menu.getItem(2).isChecked = true
//            navController.navigate(R.id.chattingFragment)
            dialog.dismiss()
        }

        //ble
        val centerWifi = dialog.findViewById<ImageView>(R.id.centerWifi)
        //광고, 스캔하기 버튼
        centerWifi.setOnClickListener {
            sharedViewModel.isMainAroundVisible.value = false
            startAdvertiseAndScan()
            dialog.dismiss()
        }


        val navController = findNavController(R.id.nav_host_fragment)

        val flashLayer = dialog.findViewById<Layer>(R.id.flashLayer)
        flashLayer?.setOnClickListener {
            binding.navigationView.menu.getItem(2).isChecked = true
            navController.navigate(R.id.flashlightFragment)
            dialog.dismiss()
        }

        dialog.show()
    }

    //채팅 fragment 실행
    private fun showChattingDialog() {
        switchToChattingFragment(0)
    }

    //toolbar 보여주기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, AlarmActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // return navController.navigateUp() || super.onSupportNavigateUp()
        return NavigationUI.navigateUp(navController, null)
    }

    private fun showSecondDialog() {
        val secondDialog = Dialog(this)
        secondDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        secondDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        secondDialog.setCancelable(true)
        secondDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        secondDialog.window?.setGravity(Gravity.CENTER)
        secondDialog.setContentView(R.layout.dialog_siren_layout)

        // 필요한 경우 두 번째 다이얼로그의 버튼 또는 다른 뷰에 대한 이벤트 리스너를 여기에 추가합니다.
        val sirenStartButton = secondDialog.findViewById<Button>(R.id.sirenStartButton)
        val sirenStartTextView = secondDialog.findViewById<TextView>(R.id.sirenStartTextView)
        val sirenStopButton = secondDialog.findViewById<Button>(R.id.sirenStopButton)
        val sirenStopTextView = secondDialog.findViewById<TextView>(R.id.sirenStopTextView)

        sirenStartButton.setOnClickListener {
            sirenStartButton.visibility = View.GONE
            sirenStartTextView.visibility = View.GONE
            sirenStopButton.visibility = View.VISIBLE
            sirenStopTextView.visibility = View.VISIBLE
            playSound(
                R.raw.siren_sound,
                sirenStartButton,
                sirenStartTextView,
                sirenStopButton,
                sirenStopTextView
            )
        }

        sirenStopButton.setOnClickListener {
            stopSound()
            sirenStartButton.visibility = View.VISIBLE
            sirenStartTextView.visibility = View.VISIBLE
            sirenStopButton.visibility = View.GONE
            sirenStopTextView.visibility = View.GONE
        }

        secondDialog.setOnDismissListener {
            stopSound()
        }

        secondDialog.show()
    }

    //경보음 멈추기
    private fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    //경보음 재생하기
    private fun playSound(
        sirenSound: Int,
        sirenStartButton: Button,
        sirenStartTextView: TextView,
        sirenStopButton: Button,
        sirenStopTextView: TextView
    ) {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(this, sirenSound)
        mediaPlayer?.start()

        // 소리 재생이 끝나면 자원 해제
        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
            sirenStartButton.visibility = View.VISIBLE
            sirenStartTextView.visibility = View.VISIBLE
            sirenStopButton.visibility = View.GONE
            sirenStopTextView.visibility = View.GONE
        }
    }

    fun switchToChattingFragment(selectedTab: Int) {
        println("$selectedTab 뭘 받아올까요 ??")
        binding.navigationView.menu.getItem(2).isChecked = true

//        val transaction = supportFragmentManager.beginTransaction()
//        val chattingFragment = ChattingFragment()
//
//        // 인덱스 값을 Bundle을 사용하여 Fragment로 전달
        val bundle = Bundle()
        bundle.putInt("selectedTab", selectedTab)
        navController.navigate(R.id.chattingFragment, bundle)
//        chattingFragment.arguments = bundle
//
//        transaction.replace(R.id.nav_host_fragment, chattingFragment) // 'fragment_container'는 해당 fragment를 호스팅하는 layout의 ID입니다.
//        transaction.addToBackStack(null) // (옵션) back 버튼을 눌렀을 때 이전 Fragment로 돌아가게 만듭니다.
//        transaction.commit()
    }

    // 위치 정보 사용
    fun callLocationTrackingService() {
        val intent = Intent(this, LocationTrackingService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        //위치 정보 저장 중지
        val serviceIntent = Intent(this, LocationTrackingService::class.java)
        stopService(serviceIntent)

        if (isBound) {
            unbindService(connection)
        }
    }
    private fun NavController.navigateSingleTop(id: Int) {
//        if(id == R.id.familyFragment){
//            familyFragment.addList()
//            .addList()
////    //        .addList()
//            var workManager = WorkManager.getInstance(context)
//
//            // 조건 설정 - 인터넷 연결 시에만 실행
//            val constraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build()
//
//            // request 생성
//            val updateRequest = OneTimeWorkRequest.Builder(FamilySyncWorker::class.java)
//                .setConstraints(constraints)
//                .build()
//
//             실행
//            workManager.enqueue(updateRequest)
//            FamilyFragment.familyListAdapter = FamilyListAdapter(context)
//            FamilyFragment.familyListAdapter.addList()
//        }

        if (currentDestination?.id != id) {


            navigate(id)
            val startDestination = this.graph.startDestinationId
            val builder = NavOptions.Builder()
            builder.setLaunchSingleTop(true)  // 이미 back stack의 top에 해당 fragment가 있으면 재사용
            builder.setPopUpTo(startDestination, false)  // 시작 destination까지 back stack을 clear하지 않음
            val options = builder.build()
            navigate(id, null, options)
        }

    }
}


