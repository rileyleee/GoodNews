package com.saveurlife.goodnews.mypage

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)

        //어둡게 보기 기능
        val switchDarkMode = binding.switchDarkMode

        switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // 스위치가 켜졌을 때: 다크 모드로 변경
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // 스위치가 꺼졌을 때: 라이트 모드로 변경
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //정보 수정 버튼 클릭 시
        binding.myPageUpdateButton.setOnClickListener {
            showMyPageDialog()
        }
    }

    private fun selectDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog 생성
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // 사용자가 선택한 날짜를 처리합니다. 여기서는 간단하게 Toast로 표시합니다.
                val selectedDate = "${selectedYear}-${selectedMonth + 1}-${selectedDay}"
                Toast.makeText(requireContext(), selectedDate, Toast.LENGTH_SHORT).show()
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    //정보 수정 버튼 모달 창
    private fun showMyPageDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mypage_layout, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        //생년월일 수정 시
        val calendar = dialog.findViewById<View>(R.id.calendar)
        calendar?.setOnClickListener {
            // 두 번째 다이얼로그 표시 함수 호출
            selectDate()
        }

        //성별 수정 시
        val genderMan = dialog.findViewById<View>(R.id.dialogMypageMan)
        val genderWoman = dialog.findViewById<View>(R.id.dialogMypageWoman)
        //남자 선택
        genderMan?.setOnClickListener{
            //Fragment 클래스에서 제공하는 메소드로, 현재 Fragment가 연결된 Context
            val colorValueClick = ContextCompat.getColor(requireContext(), R.color.sub)
            val colorStateListClick = ColorStateList.valueOf(colorValueClick)
            val colorValue = ContextCompat.getColor(requireContext(), R.color.white)
            val colorStateList = ColorStateList.valueOf(colorValue)
            genderMan.backgroundTintList = colorStateListClick
            genderWoman?.backgroundTintList = colorStateList
        }
        //여자 선택
        genderWoman?.setOnClickListener{
            //Fragment 클래스에서 제공하는 메소드로, 현재 Fragment가 연결된 Context
            val colorValueClick = ContextCompat.getColor(requireContext(), R.color.sub)
            val colorStateListClick = ColorStateList.valueOf(colorValueClick)
            val colorValue = ContextCompat.getColor(requireContext(), R.color.white)
            val colorStateList = ColorStateList.valueOf(colorValue)
            genderWoman.backgroundTintList = colorStateListClick
            genderMan?.backgroundTintList = colorStateList
        }

        //혈액형 변경
        val blood = dialog.findViewById<View>(R.id.updateBlood)
        blood?.setOnClickListener {
            showBloodSettingDialog(dialogView)
        }


        //수정하기 클릭 시
        val updateButton = dialog.findViewById<View>(R.id.updateButton)
        updateButton?.setOnClickListener {
           dialog.dismiss()
        }


    }

    //혈액형 변경하기
    private fun showBloodSettingDialog(dialog: View) {
        val rhs = arrayOf("모름", "Rh+", "Rh-")
        val blood = arrayOf("A형", "AB형", "B형", "O형")

        val bloodDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_blood_setting, null)

        val rhPicker = bloodDialogView.findViewById<NumberPicker>(R.id.rhPicker)
        val bloodPicker = bloodDialogView.findViewById<NumberPicker>(R.id.bloodPicker)

        var dialogMypageRhEdit = dialog.findViewById<TextView>(R.id.dialogMypageRhEdit)
        var dialogMyPageBloodEdit = dialog.findViewById<TextView>(R.id.dialogMyPageBloodEdit)

        var requestBlood = bloodDialogView.findViewById<TextView>(R.id.requestBlood)

        rhPicker?.minValue = 0
        rhPicker?.maxValue = rhs.size - 1
        rhPicker?.displayedValues = rhs
        rhPicker?.wrapSelectorWheel = false
        rhPicker?.setOnValueChangedListener { picker, oldVal, newVal ->
            // 선택된 값을 사용 시 화면 업데이트
            dialogMypageRhEdit.text = rhs[newVal]

        }

        bloodPicker?.minValue = 0
        bloodPicker?.maxValue = blood.size - 1
        bloodPicker?.displayedValues = blood
        bloodPicker?.wrapSelectorWheel = false
        bloodPicker?.setOnValueChangedListener { picker, oldVal, newVal ->
            dialogMyPageBloodEdit.text = blood[newVal]
        }

        val bloodDialog = AlertDialog.Builder(requireContext())
            .setView(bloodDialogView)
            .create()

        bloodDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bloodDialog.show()

        requestBlood.setOnClickListener{
            bloodDialog.dismiss()
        }
    }
}