//package com.saveurlife.goodnews.chatting
//
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.content.ServiceConnection
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.os.IBinder
//import androidx.activity.viewModels
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.saveurlife.goodnews.ble.adapter.BleConnectedAdapter
//import com.saveurlife.goodnews.ble.service.BleService
//import com.saveurlife.goodnews.common.SharedViewModel
//import com.saveurlife.goodnews.databinding.ActivityChooseGroupMemberBinding
//
//class ChooseGroupMemberActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityChooseGroupMemberBinding
//    private val sharedViewModel: SharedViewModel by viewModels()
//
//    lateinit var bleService: BleService
//    //서비스가 현재 바인드 되었는지 여부를 나타내는 변수
//    private var isBound = false
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var chooseGroupMemberAdapter: ChooseGroupMemberAdapter
//
//    private val connection = object : ServiceConnection {
//        //Service가 연결되었을 때 호출
//        override fun onServiceConnected(className: ComponentName, service: IBinder) {
//            val binder = service as BleService.LocalBinder
//            bleService = binder.service
//            sharedViewModel.bleService.value = binder.service
//            isBound = true
//        }
//        //서비스 연결이 끊어졌을 때 호출
//        override fun onServiceDisconnected(arg0: ComponentName) {
//            isBound = false
//        }
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityChooseGroupMemberBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        //ble - 서비스 바인딩
//        Intent(this, BleService::class.java).also { intent ->
//            bindService(intent, connection, Context.BIND_AUTO_CREATE)
//        }
//
//        recyclerView = binding.recyclerViewChooseGroup
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//
//
//        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(this, Observer { connectedDevicesMap ->
//            // connectedDevicesMap에서 필요한 데이터 추출
//            val users = connectedDevicesMap.flatMap { it.value.values }.toList()
//            val adapter = BleConnectedAdapter(users)
//            println("$users 유저는?")
//            binding.recyclerViewChooseGroup.adapter = adapter
//        })
//
//
//        //뒤로가기 클릭
//        binding.backButtonChoose.setOnClickListener {
//            finish()
//        }
//    }
//}