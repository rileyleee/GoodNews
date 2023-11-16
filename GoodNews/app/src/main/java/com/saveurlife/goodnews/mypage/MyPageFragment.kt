package com.saveurlife.goodnews.mypage

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.DialogBloodSettingBinding
import com.saveurlife.goodnews.databinding.DialogCalendarSettingBinding
import com.saveurlife.goodnews.databinding.DialogMypageLayoutBinding
import com.saveurlife.goodnews.databinding.FragmentMyPageBinding
import com.saveurlife.goodnews.main.PreferencesUtil
import com.saveurlife.goodnews.map.MapDownloader
import com.saveurlife.goodnews.models.Member
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import java.util.Calendar

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private lateinit var preferencesUtil: PreferencesUtil
    private lateinit var mapDownloader: MapDownloader
    private var selectedYear: String? = null
    private var selectedMonth: String? = null
    private var selectedDay: String? = null
    private var selectedRh: String? = null
    private var selectedBlood: String? = null
    private var myAge: Int? = null

//    private val config = RealmConfiguration.create(schema = setOf(Member::class, Location::class))
//    private val realm: Realm = Realm.open(config)

    val realm = Realm.open(GoodNewsApplication.realmConfiguration)
    private val items: RealmResults<Member> = realm.query<Member>().find()


    //Realm에서 정보 가져오기
    private var realmName: String? = null
    private var realmPhone: String? = null
    private var realmBirth: String? = null
    private var realmGender: String? = null
    private var realmBloodType: String? = null
    private var realmaddInfo: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        preferencesUtil = PreferencesUtil(requireContext())

        //Realm에서 내 정보 가져오기
        items.forEach { member ->
            realmName = member.name
            realmPhone = member.phone
            realmBirth = member.birthDate
            realmGender = member.gender
            realmBloodType = member.bloodType
            realmaddInfo = member.addInfo
        }


        //어둡게 보기 기능 - 현재 다크 모드 상태에 따라 스위치 상태 설정
        val isDarkMode = preferencesUtil.getBoolean("darkMode", false)
        binding.switchDarkMode.isChecked = isDarkMode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 스위치가 켜졌을 때: 다크 모드로 변경
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // 스위치가 꺼졌을 때: 라이트 모드로 변경
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            preferencesUtil.setBoolean("darkMode", isChecked)
        }

        // 지도 다운로드 버튼 클릭 했을 때
        binding.mapDownloadButton.setOnClickListener {
            Log.d("com.saveurlife.goodnews.map.MapDownloader","지도 다운로드 하러가야지")
            startMapFileDownload()
        }

        //객체 만들기
//        realm.writeBlocking {
//            copyToRealm(Member().apply {
//                memberId = 0
//                phone = 1012345678
//                name = "김싸피"
//                birthDate = "입력하지 않음"
//                gender = "입력하지 않음"
//                bloodType = "입력하지 않음"
//                addInfo = "입력하지 않음"
//            })
//        }

        //객체 가져오기
//        val items: RealmResults<Member> = realm.query<Member>().find()
//        println("$items 뭐가 나올까나")

        //특정 조건으로 객체 가져오기
//        val incompleteItems: RealmResults<Member> =
//            realm.query<Member>("memberId == 0")
//                .find()
//        incompleteItems.forEach { member ->
//            println("${member.memberId}")
//            println("${member.name}")
//            println("${member.phone}")
//        }
//        println(incompleteItems)

        //변경
//        realm.writeBlocking {
//            findLatest(incompleteItems[0])?.phone = 1111111111111
//        }

        //삭제
//        realm.writeBlocking {
//            val writeTransactionItems = query<Member>().find()
//            delete(writeTransactionItems.first())
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapDownloader = MapDownloader(requireContext())

        //데이터 불러오기
        initData()

        //정보 수정 버튼 클릭 시(내 정보 수정할 수 있는 모달창)
        binding.myPageUpdateButton.setOnClickListener {
            showMyPageDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        mapDownloader.registerReceiver()
    }

    //myPageFragment에 정보 불러오기
    private fun initData() {
        binding.name.text = realmName
        binding.phoneNumber.text = realmPhone.toString()

        if (realmBirth == "입력하지 않음") {
            binding.birthday.text = "생년월일 미입력"
        } else {
            binding.birthday.text = realmBirth
        }

        if (realmBloodType == "입력하지 않음") {
            binding.rh.text = "혈액형"
            binding.blood.text = "미입력"
        } else {
            val parts = realmBloodType!!.split(" ")
            binding.rh.text = parts[0]
            binding.blood.text = parts[1]
        }

        if (realmaddInfo == "입력하지 않음") {
            binding.significant.isVisible = false
        } else {
            binding.significant.isVisible = true
            binding.significant.text = realmaddInfo
        }
        if (realmBirth == "입력하지 않음") {
            binding.age.isVisible = false
        } else {
            binding.age.text = preferencesUtil.getString("age", "0")
            binding.age.isVisible = true
        }
        binding.switchDarkMode.isChecked = preferencesUtil.getBoolean("darkMode", false)
    }

    //dialog 모달창에 정보 불러오기
    private fun initDataDialog(dialogBinding: DialogMypageLayoutBinding) {
        dialogBinding.dialogMypageNameEdit.text = realmName
        dialogBinding.dialogMypagePhoneEdit.text = realmPhone.toString()
        dialogBinding.dialogMypagebirthday.text = realmBirth
        dialogBinding.dialogMypageBloodEdit.text = realmBloodType
        if (realmGender == "입력하지 않음") {
            noGenderSelection(dialogBinding)
        }
        if (realmaddInfo != "입력하지 않음") {
            dialogBinding.textInputEditText.text =
                Editable.Factory.getInstance().newEditable(realmaddInfo)
        }

    }

    //정보 수정 버튼 모달 창
    private fun showMyPageDialog() {
        val binding = DialogMypageLayoutBinding.inflate(LayoutInflater.from(context))
        //데이터 불러오기
        initDataDialog(binding)

        var gender = realmGender
        if (gender == "남자") {
            updateGenderSelection("남자", binding)
        } else if (gender == "여자") {
            updateGenderSelection("여자", binding)
        } else {
            noGenderSelection(binding)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        //생년월일 수정
        binding.calendar.setOnClickListener {
            // 두 번째 다이얼로그 표시 함수 호출
            showBirthSettingDialog(binding)
        }

        //성별 수정 - 남자 선택
        binding.dialogMypageMan.setOnClickListener {
            updateGenderSelection("남자", binding)
        }
        //성별 수정 - 여자 선택
        binding.dialogMypageWoman.setOnClickListener {
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
                realm.writeBlocking {
                    findLatest(items[0])?.addInfo = textInputEditText
                    realmaddInfo = textInputEditText
                }
                initData()
                dialog.dismiss()
            } else {
                // 50자를 초과하는 경우 경고 메시지 표시 또는 다른 처리 수행
                Toast.makeText(context, "특이사항은 50자 이내로 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //성별 미입력
    private fun noGenderSelection(binding: DialogMypageLayoutBinding) {
        val colorValue = ContextCompat.getColor(requireContext(), R.color.white)
        val colorStateList = ColorStateList.valueOf(colorValue)

        binding.dialogMypageWoman.backgroundTintList = colorStateList
        binding.dialogMypageMan.backgroundTintList = colorStateList
    }

    // 성별 선택
    private fun updateGenderSelection(selectedGender: String, binding: DialogMypageLayoutBinding) {
        //Fragment 클래스에서 제공하는 메소드로, 현재 Fragment가 연결된 Context
        val colorValueClick = ContextCompat.getColor(requireContext(), R.color.gender)
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
        realm.writeBlocking {
            findLatest(items[0])?.gender = selectedGender
            realmGender = selectedGender
        }
    }

    //생년월일 변경하기
    private fun showBirthSettingDialog(dialog: DialogMypageLayoutBinding) {
        val year = arrayOf(
            "1920", "1921", "1922", "1923", "1924", "1925", "1926", "1927",
            "1928", "1929", "1930", "1931", "1932", "1933", "1934", "1935", "1936", "1937",
            "1938", "1939", "1940", "1941", "1942", "1943", "1944", "1945", "1946", "1947",
            "1948", "1949", "1950", "1951", "1952", "1953", "1954", "1955", "1956", "1957",
            "1958", "1959", "1960", "1961", "1962", "1963", "1964", "1965", "1966", "1967",
            "1968", "1969", "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977",
            "1978", "1979", "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987",
            "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997",
            "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007",
            "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017",
            "2018", "2019", "2020", "2021", "2022", "2023"
        )
        val month = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val day = arrayOf(
            "01",
            "02",
            "03",
            "04",
            "05",
            "06",
            "07",
            "08",
            "09",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "30",
            "31"
        )

        //dialog로 띄울 xml파일
        val birthDialogBinding = DialogCalendarSettingBinding.inflate(LayoutInflater.from(context))

        val yearPicker = birthDialogBinding.yearPicker //년도 picker
        val monthPicker = birthDialogBinding.monthPicker //월 picker
        val dayPicker = birthDialogBinding.dayPicker //일 picker
        val requestBirth = birthDialogBinding.requestBirth //수정 버튼

        if (realmBirth == "입력하지 않음") {
            selectedYear = "2000"
            selectedMonth = "01"
            selectedDay = "01"
        } else {
            val (savedYear, savedMonth, savedDay) = realmBirth!!.split("년 ", "월 ", "일")
                .map { it.trim() }
            //기존 생년월일 불러오기
            selectedYear = savedYear
            selectedMonth = savedMonth
            selectedDay = savedDay
        }

        //년도 선택
        yearPicker?.apply {
            minValue = 0 //최소값
            maxValue = year.size - 1 //최대값
            displayedValues = year //년도 배열에 대한 값
            wrapSelectorWheel = false
            value = year.indexOf(selectedYear)
            setOnValueChangedListener { picker, oldVal, newVal ->
                selectedYear = year[newVal]
                //만 나이 계산
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                myAge = currentYear - (selectedYear?.toInt() ?: 0)
                preferencesUtil.setString("age", "만 ${myAge}세")
            }
        }

        //월 선택
        monthPicker?.apply {
            minValue = 0
            maxValue = month.size - 1
            displayedValues = month
            wrapSelectorWheel = false
            value = month.indexOf(selectedMonth)
            setOnValueChangedListener { picker, oldVal, newVal ->
                selectedMonth = month[newVal]
            }
        }

        //일 선택
        dayPicker?.apply {
            minValue = 0
            maxValue = day.size - 1
            displayedValues = day
            wrapSelectorWheel = false
            value = day.indexOf(selectedDay)
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
        birthDialog.setCancelable(false)
        birthDialog.show()
        //완료버튼 누르면 birthDialog 창 닫기
        requestBirth.setOnClickListener {
            val newBirthday = "${selectedYear}년 ${selectedMonth}월 ${selectedDay}일"
            dialog.dialogMypagebirthday.text = newBirthday
            realm.writeBlocking {
                findLatest(items[0])?.birthDate = newBirthday
                realmBirth = newBirthday
            }
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

        if (realmBloodType == "입력하지 않음") {
            selectedRh = "모름"
            selectedBlood = "A형"
        } else {
            val parts = realmBloodType!!.split(" ")
            val savedRh = parts[0] // "Rh+"
            val savedBlood = parts[1] // "0형"

            selectedRh = savedRh
            selectedBlood = savedBlood
        }

        rhPicker?.apply {
            minValue = 0
            maxValue = rhs.size - 1
            displayedValues = rhs
            wrapSelectorWheel = false
            value = rhs.indexOf(selectedRh) //인덱스 값 저장
            setOnValueChangedListener { picker, oldVal, newVal ->
                // 선택된 값을 사용 시 화면 업데이트
                selectedRh = if (rhs[newVal] == "모름") "Rh불명" else rhs[newVal]
            }
        }

        bloodPicker?.apply {
            minValue = 0
            maxValue = blood.size - 1
            displayedValues = blood
            wrapSelectorWheel = false
            value = blood.indexOf(selectedBlood)
            setOnValueChangedListener { picker, oldVal, newVal ->
                selectedBlood = blood[newVal]
            }
        }


        val bloodDialog = AlertDialog.Builder(requireContext())
            .setView(bloodDialogBinding.root)
            .create()

        bloodDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bloodDialog.setCancelable(false)
        bloodDialog.show()

        requestBlood.setOnClickListener {
            binding.rh.text = selectedRh
            binding.blood.text = selectedBlood

//            dialog.dialogMypageRhEdit.text = selectedRh
            dialog.dialogMypageBloodEdit.text = "$selectedRh $selectedBlood"

            realm.writeBlocking {
                findLatest(items[0])?.bloodType = "$selectedRh $selectedBlood"
                realmBloodType = "$selectedRh $selectedBlood"
            }
            bloodDialog.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        mapDownloader.unregisterReceiver()
    }

    private fun startMapFileDownload() {
        Log.v("mypagefragment","지도 다운로드 함수 호출")
        val url = "https://saveurlife.kr/images/7_15_korea-001.sqlite"
        val fileName = "7_15_korea-001.sqlite"

        mapDownloader.downloadFile(url, fileName)
    }

}