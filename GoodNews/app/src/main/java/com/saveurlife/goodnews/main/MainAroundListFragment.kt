package com.saveurlife.goodnews.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.ble.BleConnectedAdapter
import com.saveurlife.goodnews.ble.BleMeshConnectedUser
import com.saveurlife.goodnews.chatting.ChattingDetailActivity
import com.saveurlife.goodnews.common.SharedViewModel
import com.saveurlife.goodnews.databinding.FragmentFamilyAlarmBinding
import com.saveurlife.goodnews.databinding.FragmentMainAroundListBinding


class MainAroundListFragment : Fragment() {
//    private val bleMeshConnectedUser = BleMeshConnectedUser("1","이름", "2023", "1", 38.000,127.00);
    private lateinit var binding: FragmentMainAroundListBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainAroundListBinding.inflate(inflater, container, false)

        // sharedViewModel의 isMainAroundVisible 관찰
//        sharedViewModel.isMainAroundVisible.observe(viewLifecycleOwner, Observer { isVisible ->
//            if (isVisible) {
//                binding.mainAroundInfoConnect.visibility = View.VISIBLE
//                binding.mainAroundImageConnect.visibility = View.VISIBLE
//            } else {
//                binding.mainAroundInfoConnect.visibility = View.GONE
//                binding.mainAroundImageConnect.visibility = View.GONE
//
//                // sharedViewModel의 bleMeshConnectedDevicesMapLiveData 확인
//                val isDevicesListEmpty = sharedViewModel.bleMeshConnectedDevicesMapLiveData.value.isNullOrEmpty()
//
//                //리스트가 아무것도 없을 때
//                if (isDevicesListEmpty) {
//                    binding.lottieBleList.visibility = View.VISIBLE
//                    binding.mainAroundInfoConnectList.visibility = View.VISIBLE
//                    binding.aroundSosAll.visibility = View.GONE
//                    binding.recyclerViewMainAroundList.visibility = View.GONE
//                }else{
//                    //리스트가 있을 때
//                    binding.aroundSosAll.visibility = View.VISIBLE
//                    binding.recyclerViewMainAroundList.visibility = View.VISIBLE
//                    binding.lottieBleList.visibility = View.GONE
//                    binding.mainAroundInfoConnectList.visibility = View.GONE
//                }
//            }
//        })

        // sharedViewModel의 isMainAroundVisible 관찰
        sharedViewModel.isMainAroundVisible.observe(viewLifecycleOwner, Observer { isVisible ->
            updateUI(isVisible, sharedViewModel.bleMeshConnectedDevicesMapLiveData.value.isNullOrEmpty())
        })

        // sharedViewModel의 bleMeshConnectedDevicesMapLiveData 관찰
        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(viewLifecycleOwner, Observer { devicesMap ->
            updateUI(sharedViewModel.isMainAroundVisible.value ?: true, devicesMap.isNullOrEmpty())
        })


        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(viewLifecycleOwner, Observer { connectedDevicesMap ->
            // connectedDevicesMap에서 필요한 데이터 추출
            val users = connectedDevicesMap.flatMap { it.value.values }.toList()
            val adapter = BleConnectedAdapter(users)
            binding.recyclerViewMainAroundList.adapter = adapter
        })

        //데이터 추가
        val bleMeshConnectedUser = listOf(
            BleMeshConnectedUser("1", "이름1", "2023", "1", 38.000, 127.00),
            // ... 추가 객체들
        )

        val recyclerView = binding.recyclerViewMainAroundList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = BleConnectedAdapter(bleMeshConnectedUser)


        //채팅으로 넘어가기
        sharedViewModel.bleMeshConnectedDevicesMapLiveData.observe(viewLifecycleOwner, Observer { connectedDevicesMap ->
            val users = connectedDevicesMap.flatMap { it.value.values }.toList()
            // 채팅 버튼 클릭 리스너 설정
            val adapter = BleConnectedAdapter(users).apply {
                onChattingButtonClickListener = object : BleConnectedAdapter.OnChattingButtonClickListener {
                    override fun onChattingButtonClick(user: BleMeshConnectedUser) {
                        // Intent 생성 및 BleMeshConnectedUser 객체 추가
                        println("user가 뭔지 아닝 ????? $user")
                        val intent = Intent(context, ChattingDetailActivity::class.java).apply {
                            putExtra("chattingUser", user)
                        }
                        // ChattingDetailActivity 시작
                        startActivity(intent)
                    }
                }
            }
            binding.recyclerViewMainAroundList.adapter = adapter
        })

        //구분선 넣기
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager(context).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        return binding.root
    }

    // UI 업데이트를 위한 함수
    private fun updateUI(isMainAroundVisible: Boolean, isDevicesListEmpty: Boolean) {
        if (isMainAroundVisible) {
            binding.mainAroundInfoConnect.visibility = View.VISIBLE
            binding.mainAroundImageConnect.visibility = View.VISIBLE
        } else {
            binding.mainAroundInfoConnect.visibility = View.GONE
            binding.mainAroundImageConnect.visibility = View.GONE

            if (isDevicesListEmpty) {
                // 리스트가 비어있을 때
                binding.lottieBleList.visibility = View.VISIBLE
                binding.mainAroundInfoConnectList.visibility = View.VISIBLE
                binding.aroundSosAll.visibility = View.GONE
                binding.recyclerViewMainAroundList.visibility = View.GONE
            } else {
                // 리스트가 있을 때
                binding.lottieBleList.visibility = View.GONE
                binding.mainAroundInfoConnectList.visibility = View.GONE
                binding.aroundSosAll.visibility = View.VISIBLE
                binding.recyclerViewMainAroundList.visibility = View.VISIBLE
            }
        }
    }
}