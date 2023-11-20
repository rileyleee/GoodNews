    package com.saveurlife.goodnews.main

    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.activityViewModels
    import androidx.recyclerview.widget.DividerItemDecoration
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.saveurlife.goodnews.ble.adapter.BleAdvertiseAdapter
    import com.saveurlife.goodnews.common.SharedViewModel
    import com.saveurlife.goodnews.databinding.FragmentMainAroundAdvertiseBinding

    class MainAroundAdvertiseFragment : Fragment() {
        private lateinit var binding: FragmentMainAroundAdvertiseBinding
        private var adapter: BleAdvertiseAdapter? = null
        private val sharedViewModel: SharedViewModel by activityViewModels()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//            val view = inflater.inflate(R.layout.fragment_main_around_advertise, container, false)
//            recyclerView = view.findViewById(R.id.recyclerViewMainAroundAdvertise)
//            recyclerView.layoutManager = LinearLayoutManager(context)

            binding = FragmentMainAroundAdvertiseBinding.inflate(inflater, container, false)
            binding.recyclerViewMainAroundAdvertise.layoutManager = LinearLayoutManager(context)

            //연결, 미연결 ui
            sharedViewModel.isMainAroundVisible.observe(viewLifecycleOwner) { isVisible ->
                setViewsVisibility(isVisible)
            }


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
                    binding.recyclerViewMainAroundAdvertise.adapter = adapter
                }
            }

            // MainActivity에서 sharedViewModel의 isMainAroundVisible 값 확인
            sharedViewModel.isMainAroundVisible.observe(viewLifecycleOwner) { isVisible ->
                if (isVisible == false) {
                    // isMainAroundVisible이 false일 때 수행할 동작
                    binding.mainAroundInfo.visibility = View.GONE
                    binding.mainAroundInfoConnect.visibility = View.VISIBLE
                    binding.lottieBle.visibility = View.VISIBLE
                }
            }

            //BLE 장치 목록 관찰자
            //SharedViewModel의 bleDevices LiveData가 변경될 때마다 호출
            sharedViewModel.bleDevices.observe(viewLifecycleOwner) { bleDevices ->
                adapter?.updateDevices(bleDevices)

                if (bleDevices.isNullOrEmpty()) {
                    // 데이터가 비어 있을 때 수행할 동작
                    binding.mainAroundInfo.visibility = View.GONE
                    binding.mainAroundInfoConnect.visibility = View.VISIBLE
                    binding.lottieBle.visibility = View.VISIBLE
                }else{
                    binding.mainAroundInfoConnect.visibility = View.GONE
                    binding.lottieBle.visibility = View.GONE
                }
            }

            //구분선
            val dividerItemDecoration = DividerItemDecoration(binding.recyclerViewMainAroundAdvertise.context, LinearLayoutManager(context).orientation)
            binding.recyclerViewMainAroundAdvertise.addItemDecoration(dividerItemDecoration)

            return binding.root
        }

        private fun setViewsVisibility(isVisible: Boolean) {
            binding.mainAroundInfo.visibility = if (isVisible) View.VISIBLE else View.GONE
            binding.mainAroundImage.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
