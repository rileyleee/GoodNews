package com.saveurlife.goodnews.enterinfo

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.saveurlife.goodnews.GoodNewsApplication
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.api.MemberAPI
import com.saveurlife.goodnews.main.MainActivity
import com.saveurlife.goodnews.databinding.ActivityEnterInfoBinding
import com.saveurlife.goodnews.main.PreferencesUtil
import com.saveurlife.goodnews.models.Member
import io.realm.kotlin.Realm
import com.saveurlife.goodnews.service.UserDeviceInfoService;
import com.saveurlife.goodnews.main.PermissionsUtil
import com.saveurlife.goodnews.service.DeviceStateService
import com.saveurlife.goodnews.service.LocationService
import com.saveurlife.goodnews.sync.AllDataSync
import com.saveurlife.goodnews.sync.DataSyncWorker
import com.saveurlife.goodnews.sync.SyncService
import java.util.Calendar


class EnterInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterInfoBinding
    private lateinit var realm: Realm
    private lateinit var permissionsUtil: PermissionsUtil
    private lateinit var memberAPI: MemberAPI
    private lateinit var syncService: SyncService
    private lateinit var preferencesUtil: PreferencesUtil
    val userDeviceInfoService = UserDeviceInfoService(this);

    private lateinit var setPhone: String

    // WorkManager
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.open(GoodNewsApplication.realmConfiguration)
        setPhone = userDeviceInfoService.phoneNumber.toString()
        binding = ActivityEnterInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        memberAPI = MemberAPI()
        syncService = SyncService()
        preferencesUtil = PreferencesUtil(applicationContext)
        workManager = WorkManager.getInstance(applicationContext)

        // EditText 비활성화
        with(binding) {
            phoneEditText1.hint = setPhone.substring(0, 3)
            phoneEditText2.hint = setPhone.substring(3, 7)
            phoneEditText3.hint = setPhone.substring(7)


            phoneEditText1.isEnabled = false
            phoneEditText2.isEnabled = false
            phoneEditText3.isEnabled = false
        }

        initGenderSelection() // 성별 선택

        // 생년월일 박스 클릭 했을 때
        binding.birthGroup.setOnClickListener {
            val birthDialogView =
                LayoutInflater.from(this).inflate(R.layout.dialog_calendar_setting, null)
            showBirthSettingDialog(birthDialogView)
        }

        // 혈액형 박스 클릭 했을 때
        binding.bloodGroup.setOnClickListener {
            val bloodDialogView =
                LayoutInflater.from(this).inflate(R.layout.dialog_blood_setting, null)
            showBloodSettingDialog(bloodDialogView)
        }

        // 정보 등록 버튼 눌렀을 때, 이벤트
        binding.submitInfo.setOnClickListener {
            submitUserInfo()
//             val intent = Intent(this, MainActivity::class.java)
//             startActivity(intent)
        }
    }

    // 성별 변경
    private fun initGenderSelection() {
        // 남자 클릭 했을 때
        binding.genderMale.setOnClickListener {
            it.isSelected = true // 남자를 선택된 상태로 설정
            binding.genderFemale.isSelected = false // 여자는 선택되지 않은 상태로 설정

            it.setBackgroundResource(com.saveurlife.goodnews.R.drawable.input_stroke_selected)
            binding.genderFemale.setBackgroundResource(com.saveurlife.goodnews.R.drawable.input_stroke)
        }

        // 여자 클릭 했을 때
        binding.genderFemale.setOnClickListener {
            it.isSelected = true // 여자를 선택된 상태로 설정
            binding.genderMale.isSelected = false // 남자는 선택되지 않은 상태로 설정

            it.setBackgroundResource(com.saveurlife.goodnews.R.drawable.input_stroke_selected)
            binding.genderMale.setBackgroundResource(com.saveurlife.goodnews.R.drawable.input_stroke)
        }
    }

    //생년월일 변경하기
    private fun showBirthSettingDialog(dialog: View) {
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
        ).reversedArray()
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
        val birthDialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_calendar_setting, null)
        val yearPicker = birthDialogView.findViewById<NumberPicker>(R.id.yearPicker) //년도 picker
        val monthPicker = birthDialogView.findViewById<NumberPicker>(R.id.monthPicker) //월 picker
        val dayPicker = birthDialogView.findViewById<NumberPicker>(R.id.dayPicker) //일 picker

        var dialogYearEdit = binding.dialogEnterYear //년도 변경 text
        var dialogMonthEdit = binding.dialogEnterMonth //월 변경 text
        var dialogDayEdit = binding.dialogEnterDay //일 변경 text

        var requestBirth = birthDialogView.findViewById<TextView>(R.id.requestBirth) //수정 버튼

        yearPicker?.minValue = 0 //최소값
        yearPicker?.maxValue = year.size - 1 //최대값
        yearPicker?.displayedValues = year //년도 배열에 대한 값
        yearPicker?.wrapSelectorWheel = false
        yearPicker?.setOnValueChangedListener { picker, oldVal, newVal ->
            dialogYearEdit.text = "${year[newVal]}년" //선택한 값을 변경 text에 넣기
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val myAge = currentYear - year[newVal].toInt()
            preferencesUtil.setInt("age", myAge)
        }

        monthPicker?.minValue = 0
        monthPicker?.maxValue = month.size - 1
        monthPicker?.displayedValues = month
        monthPicker?.wrapSelectorWheel = false
        monthPicker?.setOnValueChangedListener { picker, oldVal, newVal ->
            dialogMonthEdit.text = "${month[newVal]}월"
        }

        dayPicker?.minValue = 0
        dayPicker?.maxValue = day.size - 1
        dayPicker?.displayedValues = day
        dayPicker?.wrapSelectorWheel = false
        dayPicker?.setOnValueChangedListener { picker, oldVal, newVal ->
            dialogDayEdit.text = "${day[newVal]}일"
        }

        //Dialog 띄우기
        val birthDialog = AlertDialog.Builder(this)
            .setView(birthDialogView)
            .create()

        //배경 투명하게 만들기
        birthDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //birthDialog 보여주기
        birthDialog.show()
        //완료버튼 누르면 birthDialog 창 닫기
        requestBirth.setOnClickListener {
            // 기본값으로 설정된 첫 번째 항목을 가져와서 TextView에 설정합니다.
            val selectedYear = year[yearPicker.value]
            val selectedMonth = month[monthPicker.value]
            val selectedDay = day[dayPicker.value]

            // 가져온 값으로 TextView를 업데이트 합니다.
            dialogYearEdit.text = "${selectedYear}년"
            dialogMonthEdit.text = "${selectedMonth}월"
            dialogDayEdit.text = "${selectedDay}일"
            birthDialog.dismiss()
        }

    }

    //혈액형 변경하기
    private fun showBloodSettingDialog(dialog: View) {
        val rhs = arrayOf("모름", "Rh+", "Rh-")
        val blood = arrayOf("A형", "AB형", "B형", "O형")

        val bloodDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_blood_setting, null)

        val rhPicker = bloodDialogView.findViewById<NumberPicker>(R.id.rhPicker)
        val bloodPicker = bloodDialogView.findViewById<NumberPicker>(R.id.bloodPicker)

        var dialogMypageRhEdit = binding.dialogRhText
        var dialogMyPageBloodEdit = binding.dialogBloodText

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

        val bloodDialog = AlertDialog.Builder(this)
            .setView(bloodDialogView)
            .create()

        bloodDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bloodDialog.show()

        requestBlood.setOnClickListener {
            val selectedRh = rhs[rhPicker.value]
            val selectedBloodType = blood[bloodPicker.value]

            // 가져온 값으로 TextView를 업데이트 합니다.
            dialogMypageRhEdit.text = selectedRh
            dialogMyPageBloodEdit.text = selectedBloodType

            bloodDialog.dismiss()
        }
    }


    // 정보 등록 함수
    private fun submitUserInfo() {
        // PreferencesUtil 인스턴스 생성
        val preferencesUtil = PreferencesUtil(this)

        // 사용자 입력 값 추출
        val setName = binding.nameEditText.text.toString()

        val setMemberId = userDeviceInfoService.deviceId.toString()


        val birthYear = binding.dialogEnterYear.text.toString()
        val birthMonth = binding.dialogEnterMonth.text.toString()
        val birthDay = binding.dialogEnterDay.text.toString()
        val calendar = Calendar.getInstance()
        val setBirthDate = if (birthYear == "YYYY년" || birthMonth == "MM월" || birthDay == "DD일") {
            "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월 ${calendar.get(Calendar.DAY_OF_MONTH)}일"
        } else {
            "$birthYear $birthMonth $birthDay"
        }

        val setGender = when {
            binding.genderMale.isSelected -> "남"
            binding.genderFemale.isSelected -> "여"
            else -> "-"
        }

        val rhText = binding.dialogRhText.text.toString()
        val bloodText = binding.dialogBloodText.text.toString()

        val setBloodType = if (rhText == "Rh" && bloodText == "--형") {
            "Rh --형"
        } else {
            "$rhText $bloodText"
        }

        val setAddInfo = binding.warningEditText.text.toString().ifEmpty { "-" }

        // 입력 값 검증 (필수 입력 값 안 들어왔을 때)
        if (setName.isBlank()) {
            Toast.makeText(this, "필수 정보 이름을 입력해 주세요.", Toast.LENGTH_LONG).show()
            return
        } else {
            Log.i(
                "저장 정보",
                setBirthDate + " " + setName + " " + setGender + " " + setBloodType + " " + setAddInfo
            )

            // Realm에 저장
            val locationService = LocationService(applicationContext)
            val loc = locationService.lastKnownLocation.split("/")
            realm.writeBlocking {
                copyToRealm(Member().apply {
                    memberId = setMemberId
                    birthDate = setBirthDate.toString()
                    phone = setPhone
                    name = setName
                    gender = setGender.toString()
                    bloodType = setBloodType.toString()
                    addInfo = setAddInfo.toString()
                    latitude = loc[0].toDouble()
                    longitude = loc[1].toDouble()
                })
            }

            realm.close()
            // Shared에 저장
            preferencesUtil.setString("name", setName)
            preferencesUtil.setString("id",setMemberId)

            memberAPI.registMemberInfo(setMemberId,
                setPhone,
                setName,
                setBirthDate?.let { syncService.convertDateStringToNumStr(it) },
                setGender,
                setBloodType,
                setAddInfo,
            )
            memberAPI.updateMember(setMemberId, loc[0].toDouble(), loc[1].toDouble())

            Log.i("저장", "저장완료")
            // 메인으로 이동
            val intent = Intent(this, MainActivity::class.java)

            val deviceStateService = DeviceStateService()
            if(deviceStateService.isNetworkAvailable(applicationContext)){
                val allDataSync = AllDataSync(applicationContext)
                allDataSync.fetchAllData()
            }

            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        permissionsUtil.dismissDialog()
        super.onDestroy()
    }
}