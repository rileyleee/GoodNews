package com.saveurlife.goodnews.chatting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.FragmentChooseGroupMemberBinding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.saveurlife.goodnews.ble.service.BleService

class ChooseGroupMemberFragment : Fragment() {
    private lateinit var binding: FragmentChooseGroupMemberBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    lateinit var adapter: ChooseGroupMemberAdapter

    lateinit var bleService: BleService
    private var isBound = false
    private val connection = object : ServiceConnection {
        //Service가 연결되었을 때 호출
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BleService.LocalBinder
            bleService = binder.service
            sharedViewModel.bleService.value = binder.service
            isBound = true
        }
        //서비스 연결이 끊어졌을 때 호출
        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        // 서비스를 시작하고 바인드하는 코드
        Intent(context, BleService::class.java).also { intent ->
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseGroupMemberBinding.inflate(inflater, container, false)

        adapter = ChooseGroupMemberAdapter(emptyList())

        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(viewLifecycleOwner, Observer { connectedDevicesMap ->
            // connectedDevicesMap에서 필요한 데이터 추출
            val users = connectedDevicesMap.flatMap { it.value.values }.toList()
            println("$users 여기는 어떨까아요??")
//            adapter = ChooseGroupMemberAdapter(users)
            adapter.updateUsers(users)
            binding.recyclerViewChooseGroup.adapter = adapter
        })

        val recyclerView = binding.recyclerViewChooseGroup
        recyclerView.layoutManager = LinearLayoutManager(context)

        //확인
        binding.createGroupChattingButton.setOnClickListener {
            if (isBound && ::bleService.isInitialized) {
                bleService.addMembersToGroup("그룹테스트", adapter.getSelectedUserIds())
//                Log.i("선택사용자", adapter.getSelectedUserIds().toString())
            } else {
                // 오류 메시지 표시 또는 다른 처리
                Toast.makeText(context, "BLE 서비스가 초기화되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        //뒤로가기 클릭
        binding.backButtonChoose.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}