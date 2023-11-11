    package com.saveurlife.goodnews.main

    import android.content.Intent
    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.activityViewModels
    import androidx.recyclerview.widget.DividerItemDecoration
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.saveurlife.goodnews.R
    import com.saveurlife.goodnews.ble.BleAdvertiseAdapter
    import com.saveurlife.goodnews.ble.BleMeshAdvertiseData
    import com.saveurlife.goodnews.ble.service.BleService
    import com.saveurlife.goodnews.chatting.ChattingDetailActivity
    import com.saveurlife.goodnews.chatting.OneChattingAdapter
    import com.saveurlife.goodnews.chatting.OnechattingData
    import com.saveurlife.goodnews.common.SharedViewModel

    class MainAroundAdvertiseFragment : Fragment() {
        private var adapter: BleAdvertiseAdapter? = null
        private lateinit var recyclerView: RecyclerView
        private val sharedViewModel: SharedViewModel by activityViewModels()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.fragment_main_around_advertise, container, false)
            recyclerView = view.findViewById(R.id.recyclerViewMainAroundAdvertise)
            recyclerView.layoutManager = LinearLayoutManager(context)

            //BLE 서비스 관찰자
            //SharedViewModel의 bleService LiveData가 변경될 때마다 호출
            //observe 메서드는 LiveData 객체의 데이터가 변경될 때마다 알림을 받기 위해 사용
            //viewLifecycleOwner는 프래그먼트의 뷰 라이프사이클에 해당하는 객체
            // 이를 사용함으로써 프래그먼트의 뷰가 화면에 보여지는 동안에만 LiveData의 업데이트를 받도록 함.
            // 이는 프래그먼트가 화면에서 사라졌을 때 불필요한 작업을 방지
            sharedViewModel.bleService.observe(viewLifecycleOwner) { service ->
                if (service != null) {
                    //어댑터 초기화
                    // BleAdvertiseAdapter라는 새 어댑터 인스턴스를 생성
                    // 이때 초기 BLE 장치 목록은 비어 있는 리스트 (listOf())로 시작하고, SharedViewModel과 service 객체를 어댑터에 전달
                    adapter = BleAdvertiseAdapter(listOf(), sharedViewModel, service)
                    //RecyclerView의 어댑터로 위에서 생성한 adapter를 설정
                    //이를 통해 RecyclerView는 BLE 장치 목록을 화면에 표시할 준비 마침
                    recyclerView.adapter = adapter
                }
            }

            //BLE 장치 목록 관찰자
            //SharedViewModel의 bleDevices LiveData가 변경될 때마다 호출
            sharedViewModel.bleDevices.observe(viewLifecycleOwner) { bleDevices ->
                adapter?.updateDevices(bleDevices)
            }

            //구분선
            val dividerItemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager(context).orientation)
            recyclerView.addItemDecoration(dividerItemDecoration)

            return view
        }
    }
