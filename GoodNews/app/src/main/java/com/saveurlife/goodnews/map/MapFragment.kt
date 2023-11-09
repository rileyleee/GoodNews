package com.saveurlife.goodnews.map

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFamilyBinding
import com.saveurlife.goodnews.databinding.FragmentMapBinding
import com.saveurlife.goodnews.family.FamilyAddFragment

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        // BottomSheetBehavior 초기화 및 설정
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // BottomSheet 상태 변경에 따른 로직
            }

            override fun onSlide(bottomSheetView: View, slideOffset: Float) {
                // 슬라이드에 따른 UI 변화 처리
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emergencyAddButton.setOnClickListener {
            showEmergencyDialog()
        }

        // 테스트 코드
        binding.testTextView.setOnClickListener {
            Log.i("네", "테스트텍스트를 클릭했습니다.")
        }

        // BottomSheetBehavior 설정
        val bottomSheet = view.findViewById<View>(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // 하단 시트가 확장된 경우 mapMainContents의 자식들을 비활성화
                        binding.mapMainContents.isEnabled = false
                        disableEnableControls(false, binding.mapMainContents)
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // 하단 시트가 축소된 경우 mapMainContents의 자식들을 활성화
                        binding.mapMainContents.isEnabled = true
                        disableEnableControls(true, binding.mapMainContents)
                    }
                    // 다른 상태에 대한 처리...
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드에 따른 UI 변화 처리
            }
        })


        // 뷰의 레이아웃이 완료된 후에 높이를 계산
        bottomSheet.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 레이아웃 리스너 제거
                bottomSheet.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // 프래그먼트의 전체 높이를 얻음
                val fragmentHeight = view.height

                // 64dp를 픽셀로 변환
                val expandedOffsetPixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 64f, resources.displayMetrics
                ).toInt()

                // expandedOffset을 뺀 높이를 계산
                val expandedHeight = fragmentHeight - expandedOffsetPixels

                // BottomSheet의 높이를 설정
                val layoutParams = bottomSheet.layoutParams
                layoutParams.height = expandedHeight
                bottomSheet.layoutParams = layoutParams
            }
        })

    }

    private fun showEmergencyDialog() {
        val dialogFragment = EmergencyInfoDialogFragment()
        dialogFragment.show(childFragmentManager, "EmergencyInfoDialogFragment")
    }

    private fun disableEnableControls(enable: Boolean, vg: ViewGroup) {
        for (i in 0 until vg.childCount) {
            val child = vg.getChildAt(i)
            child.isEnabled = enable
            if (child is ViewGroup) {
                disableEnableControls(enable, child)
            }
        }
    }

}