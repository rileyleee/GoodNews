package com.saveurlife.goodnews.mypage

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.DialogBloodSettingBinding
import com.saveurlife.goodnews.databinding.DialogCalendarSettingBinding
import com.saveurlife.goodnews.databinding.DialogMypageLayoutBinding
import com.saveurlife.goodnews.databinding.FragmentMyPageBinding
import com.saveurlife.goodnews.main.PreferencesUtil
import java.util.Calendar

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding

    private lateinit var preferencesUtil: PreferencesUtil

    private var selectedYear: String? = null
    private var selectedMonth: String? = null
    private var selectedDay: String? = null
    private var selectedRh: String? = null
    private var selectedBlood: String? = null
    private var myAge: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)

        preferencesUtil = PreferencesUtil(requireContext())

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

        //데이터 불러오기
        initData()

        //정보 수정 버튼 클릭 시(내 정보 수정할 수 있는 모달창)
        binding.myPageUpdateButton.setOnClickListener {
            showMyPageDialog()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    //myPageFragment에 정보 불러오기
    private fun initData() {
        binding.name.text = preferencesUtil.getString("name", "0")
        binding.phoneNumber.text = preferencesUtil.getString("phoneNumber", "0")
        binding.birthday.text = preferencesUtil.getString("birthday", "0")
        binding.rh.text = preferencesUtil.getString("rh", "0")
        binding.blood.text = preferencesUtil.getString("blood", "0")
        binding.significant.text = preferencesUtil.getString("significant", "0")
        binding.age.text = preferencesUtil.getString("age","0")
        binding.significant.text = preferencesUtil.getString("significant", "0")
    }

    //dialog 모달창에 정보 불러오기
    private fun initDataDialog(dialogBinding: DialogMypageLayoutBinding) {
        dialogBinding.dialogMypageNameEdit.text = preferencesUtil.getString("name", "0")
        dialogBinding.dialogMypagePhoneEdit.text = preferencesUtil.getString("phoneNumber", "0")
        dialogBinding.dialogMypagebirthday.text = preferencesUtil.getString("birthday", "0")
        dialogBinding.dialogMypageRhEdit.text = preferencesUtil.getString("rh", "0")
        dialogBinding.dialogMypageBloodEdit.text = preferencesUtil.getString("blood", "0")
        dialogBinding.textInputEditText.text = Editable.Factory.getInstance().newEditable(preferencesUtil.getString("significant", "특이사항 없음"))
    }

    //정보 수정 버튼 모달 창
    private fun showMyPageDialog() {
        val binding = DialogMypageLayoutBinding.inflate(LayoutInflater.from(context))
        //데이터 불러오기
        initDataDialog(binding)

        var gender = preferencesUtil.getString("gender","0")
        if(gender == "남자"){
            updateGenderSelection("남자", binding)
        }else{
            updateGenderSelection("여자", binding)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        //생년월일 수정
        binding.calendar.setOnClickListener {
            // 두 번째 다이얼로그 표시 함수 호출
            showBirthSettingDialog(binding)
        }

        //성별 수정 - 남자 선택
        binding.dialogMypageMan.setOnClickListener{
            updateGenderSelection("남자", binding)
        }
        //성별 수정 - 여자 선택
        binding.dialogMypageWoman.setOnClickListener{
            updateGenderSelection("여자", binding)
        }

        //혈액형 수정
        binding.updateBlood.setOnClickListener {
            showBloodSettingDialog(binding)
        }


        //수정하기 클릭 시
        binding.updateButton.setOnClickListener {
            //특이사항 수정
            var textInputEditText = binding.textInputEditText.text.toString()
            if (textInputEditText.length <= 50) {
                preferencesUtil.setString("significant", textInputEditText)
//                preferencesUtil.setString("significant", "50글자를 넣으려면 얼만큼이어야할까 50글자를 넣으려면 얼만큼이어야할까 얼만큼이어야할까 얼")
                initData()
                dialog.dismiss()
            } else {
                // 50자를 초과하는 경우 경고 메시지 표시 또는 다른 처리 수행
                Toast.makeText(context, "특이사항은 50자 이내로 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateGenderSelection(selectedGender: String, binding: DialogMypageLayoutBinding) {
        //Fragment 클래스에서 제공하는 메소드로, 현재 Fragment가 연결된 Context
        val colorValueClick = ContextCompat.getColor(requireContext(), R.color.sub)
        val colorStateListClick = ColorStateList.valueOf(colorValueClick)
        val colorValue = ContextCompat.getColor(requireContext(), R.color.white)
        val colorStateList = ColorStateList.valueOf(colorValue)

        if (selectedGender == "여자") {
            binding.dialogMypageWoman.backgroundTintList = colorStateListClick
            binding.dialogMypageMan.backgroundTintList = colorStateList
        } else {
            binding.dialogMypageWoman.backgroundTintList = colorStateList
            binding.dialogMypageMan.backgroundTintList = colorStateListClick
        }
        preferencesUtil.setString("gender", selectedGender)
    }

    //생년월일 변경하기
    private fun showBirthSettingDialog(dialog: DialogMypageLayoutBinding) {
        val year = arrayOf("1920", "1921", "1922", "1923", "1924", "1925", "1926", "1927",
            "1928", "1929", "1930", "1931", "1932", "1933", "1934", "1935", "1936", "1937",
            "1938", "1939", "1940", "1941", "1942", "1943", "1944", "1945", "1946", "1947",
            "1948", "1949", "1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957",
            "1958", "1959", "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967",
            "1968", "1969", "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977",
            "1978", "1979", "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987",
            "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997",
            "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007",
            "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017",
            "2018", "2019", "2020", "2021", "2022", "2023")
        val month = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val day = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12","13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31")

        //dialog로 띄울 xml파일
        val birthDialogBinding = DialogCalendarSettingBinding.inflate(LayoutInflater.from(context))

        val yearPicker = birthDialogBinding.yearPicker //년도 picker
        val monthPicker = birthDialogBinding.monthPicker //월 picker
        val dayPicker = birthDialogBinding.dayPicker //일 picker
        val requestBirth = birthDialogBinding.requestBirth //수정 버튼

        val savedBirthday = preferencesUtil.getString("birthday", "2000년 01월 01일")
        val (savedYear, savedMonth, savedDay) = savedBirthday.split("년 ", "월 ", "일").map { it.trim() }

        //기존 생년월일 불러오기
        selectedYear = savedYear
        selectedMonth = savedMonth
        selectedDay = savedDay

        //년도 선택
        yearPicker?.apply{
            minValue = 0 //최소값
            maxValue = year.size - 1 //최대값
            displayedValues = year //년도 배열에 대한 값
            wrapSelectorWheel = false
            value = year.indexOf(savedYear)
            setOnValueChangedListener { picker, oldVal, newVal ->
                selectedYear = year[newVal]
                //만 나이 계산
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                myAge = currentYear - (selectedYear?.toInt() ?: 0)
                preferencesUtil.setString("age", "만 ${myAge}세")
            }
        }

        //월 선택
        monthPicker?.apply{
            minValue = 0
            maxValue = month.size - 1
            displayedValues = month
            wrapSelectorWheel = false
            value = month.indexOf(savedMonth)
            setOnValueChangedListener { picker, oldVal, newVal ->
                selectedMonth = month[newVal]
            }
        }

        //일 선택
        dayPicker?.apply{
            minValue = 0
            maxValue = day.size - 1
            displayedValues = day
            wrapSelectorWheel = false
            value = day.indexOf(savedDay)
            setOnValueChangedListener { picker, oldVal, newVal ->
                selectedDay = day[newVal]
            }
        }

        //Dialog 띄우기
        val birthDialog = AlertDialog.Builder(requireContext())
            .setView(birthDialogBinding.root)
            .create()

        //배경 투명하게 만들기
        birthDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //birthDialog 보여주기
        birthDialog.show()
        //완료버튼 누르면 birthDialog 창 닫기
        requestBirth.setOnClickListener{
            val newBirthday = "${selectedYear}년 ${selectedMonth}월 ${selectedDay}일"
            dialog.dialogMypagebirthday.text = newBirthday
            preferencesUtil.setString("birthday", newBirthday)
            birthDialog.dismiss()
        }
    }

    //혈액형 변경하기
    private fun showBloodSettingDialog(dialog: DialogMypageLayoutBinding) {
        val rhs = arrayOf("모름", "Rh+", "Rh-")
        val blood = arrayOf("A형", "AB형", "B형", "O형")

        val bloodDialogBinding = DialogBloodSettingBinding.inflate(LayoutInflater.from(context))

        val rhPicker = bloodDialogBinding.rhPicker
        val bloodPicker = bloodDialogBinding.bloodPicker
        val requestBlood = bloodDialogBinding.requestBlood

        val savedRh = preferencesUtil.getString("rh", "0")
        val savedBlood = preferencesUtil.getString("blood", "0")

        selectedRh = savedRh
        selectedBlood = savedBlood

        rhPicker?.apply{
            minValue = 0
            maxValue = rhs.size - 1
            displayedValues = rhs
            wrapSelectorWheel = false
            value = rhs.indexOf(savedRh) //인덱스 값 저장
            setOnValueChangedListener { picker, oldVal, newVal ->
                // 선택된 값을 사용 시 화면 업데이트
                selectedRh = if (rhs[newVal] == "모름") "rh 불명" else rhs[newVal]
            }
        }

        bloodPicker?.apply{
            minValue = 0
            maxValue = blood.size - 1
            displayedValues = blood
            wrapSelectorWheel = false
            value = blood.indexOf(savedBlood)
            setOnValueChangedListener { picker, oldVal, newVal ->
                selectedBlood = blood[newVal]
            }
        }

        val bloodDialog = AlertDialog.Builder(requireContext())
            .setView(bloodDialogBinding.root)
            .create()

        bloodDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bloodDialog.show()

        requestBlood.setOnClickListener{
            binding.rh.text = "$selectedRh"
            binding.blood.text = "$selectedBlood"

            dialog.dialogMypageRhEdit.text = selectedRh
            dialog.dialogMypageBloodEdit.text = selectedBlood
            preferencesUtil.setString("rh", selectedRh ?: "0")
            preferencesUtil.setString("blood", selectedBlood ?: "0")
            bloodDialog.dismiss()
        }
    }
}