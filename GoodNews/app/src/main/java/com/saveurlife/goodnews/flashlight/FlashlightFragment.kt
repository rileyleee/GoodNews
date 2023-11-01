package com.saveurlife.goodnews.flashlight

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.FragmentFlashlightBinding

class FlashlightFragment : Fragment() {
    private var flashEditText: EditText? = null
    private var flashStartButton: TextView? = null
    private var morseInputButton: TextView? = null
    private var languageRadioGroup: RadioGroup? = null
    private var isEng = false
    private var morseOutputTextView: TextView? = null
    private var clearButton: Button? = null
    private var morseRecordButton: TextView? = null
    private val convertedCharactersEng = StringBuilder()
    private val convertedCharactersKor = StringBuilder()
    private val morseInput = StringBuilder()
    private val morseInputHandler = Handler()
    private val morseInputRunnable = Runnable {
        convertMorseToCharacter()
        morseInput.setLength(0) // Reset the Morse input
        if (isEng) {
            morseOutputTextView!!.text = convertedCharactersEng
        } else {
            morseOutputTextView!!.text = convertedCharactersKor
        }
    }
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var isFlashOn = false

    // 문자에 대한 모스 부호 매핑
    private val morseCodeMap: MutableMap<Char, String> = HashMap()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_flashlight, container, false)
    }

    // =============================

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flashEditText = view.findViewById(R.id.flashEditText)
        flashStartButton = view.findViewById(R.id.flashStartButton)
        morseInputButton = view.findViewById(R.id.morseInputButton)
        languageRadioGroup = view.findViewById(R.id.languageRadioGroup)
        morseOutputTextView = view.findViewById(R.id.morseOutputTextView)
        clearButton = view.findViewById(R.id.clearButton)
        morseRecordButton = view.findViewById(R.id.morseRecordButton)

        cameraManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
        try {
            cameraId = cameraManager!!.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        // 모스 부호 매핑 초기화
        initializeMorseCodeMap()
        flashStartButton!!.setOnClickListener {
            val input = flashEditText!!.text.toString().trim { it <= ' ' }
            val morseCode = convertToMorse(input)
            flashMorseCode(morseCode) {
                // 여기에 record_list로 저장하는 동작을 추가해줘야됨 @@
                // 근데 이미 있는 아이템의 경우에는 추가하지 않아야됨
            }
        }
        morseInputButton!!.setOnClickListener {
            morseInput.append(".")
            if (isEng) {
                morseOutputTextView!!.text =
                    convertedCharactersEng.toString() + morseInput.toString()
            } else {
                morseOutputTextView!!.text =
                    convertedCharactersKor.toString() + morseInput.toString()
            }
            morseInputHandler.removeCallbacks(morseInputRunnable)
            morseInputHandler.postDelayed(morseInputRunnable, 1200)
        }
        morseInputButton!!.setOnLongClickListener {
            morseInput.append("-")
            if (isEng) {
                morseOutputTextView!!.text =
                    convertedCharactersEng.toString() + morseInput.toString()
            } else {
                morseOutputTextView!!.text =
                    convertedCharactersKor.toString() + morseInput.toString()
            }
            morseInputHandler.removeCallbacks(morseInputRunnable)
            morseInputHandler.postDelayed(morseInputRunnable, 1200)
            true
        }
        clearButton!!.setOnClickListener {
            morseOutputTextView!!.text = ""
            convertedCharactersEng.setLength(0)
            convertedCharactersKor.setLength(0)
        }

        // 기록 등록 버튼 눌렀을 때
        morseRecordButton!!.setOnClickListener {
            if (morseOutputTextView?.text?.length == 0) return@setOnClickListener
            // 여기에서 기록 리스트에 추가해주면 됨 @@
            Toast.makeText(activity, "${morseOutputTextView?.text} 의 내용입니다", Toast.LENGTH_SHORT)
                .show()

            morseOutputTextView!!.text = ""
            convertedCharactersEng.setLength(0)
            convertedCharactersKor.setLength(0)
        }

        languageRadioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.englishRadioButton) {
                isEng = true
                morseOutputTextView!!.text = convertedCharactersEng
            } else if (checkedId == R.id.koreanRadioButton) {
                isEng = false
                morseOutputTextView!!.text = convertedCharactersKor
            }
        }
        // RecyclerView 손전등 테스트 데이터
        var recordTestData = arrayListOf(
            FlashlightData(FlashType.SELF, "살려주세요", "... --- ..."),
            FlashlightData(FlashType.OTHER, "나의 위치", "-- .- .. -."),
            FlashlightData(FlashType.SELF, "Record 3", ".- .-. .")
        )
        var flashListManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        var flashListAdapter = FlashlightListAdapter(recordTestData)


        var flashRecordListManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        var flashRecordListAdapter = FlashlightRecordAdapter(recordTestData)

        val flashListRecyclerView = view.findViewById<RecyclerView>(R.id.flashList)
        flashListRecyclerView.apply {
            adapter = flashListAdapter
            layoutManager = flashListManager
        }

        view.findViewById<RecyclerView>(R.id.flashRecordList).apply {
            adapter = flashRecordListAdapter
            layoutManager = flashRecordListManager
        }

        // ListItem 클릭 이벤트
        flashListAdapter.setOnItemClickListener(object : FlashlightListAdapter.OnItemClickListener {
            override fun onItemClick(data: FlashlightData, position: Int) {
                if (flashListAdapter.isFlashing) return

                flashListAdapter.isFlashing = true
                // 아이템 눌린 모양으로 변경
                val viewHolder =
                    flashListRecyclerView.findViewHolderForAdapterPosition(position) as? FlashlightListAdapter.ListAdapter
                viewHolder?.flashLightTextBox?.setBackgroundResource(R.drawable.active_rounded_background_with_shadow)
                // 모스 부호 플래시 이벤트
                flashMorseCode(data.morseCode) {
                    flashListAdapter.isFlashing = false
                    viewHolder?.flashLightTextBox?.setBackgroundResource(R.drawable.rounded_background_with_shadow)
                }

                // 테스트 이벤트 @@
//                testMorseCode(data.morseCode) {
//                    flashListAdapter.isFlashing = false
//                    viewHolder?.flashLightTextBox?.setBackgroundResource(R.drawable.rounded_background_with_shadow)
//                }
            }
        })
    }


    private fun initializeMorseCodeMap() {
        // 숫자
        morseCodeMap['0'] = "-----"
        morseCodeMap['1'] = ".----"
        morseCodeMap['2'] = "..---"
        morseCodeMap['3'] = "...--"
        morseCodeMap['4'] = "....-"
        morseCodeMap['5'] = "....."
        morseCodeMap['6'] = "-...."
        morseCodeMap['7'] = "--..."
        morseCodeMap['8'] = "---.."
        morseCodeMap['9'] = "----."
        // 특수기호
        morseCodeMap['.'] = ".-.-.-"
        morseCodeMap[','] = "--..--"
        morseCodeMap['?'] = "..--.."
        morseCodeMap['\\'] = ".----."
        morseCodeMap['!'] = "-.-.--"
        morseCodeMap['/'] = "-..-."
        morseCodeMap['('] = "-.--."
        morseCodeMap[')'] = "-.--.-"
        morseCodeMap['&'] = ".-..."
        morseCodeMap[':'] = "---..."
        morseCodeMap[';'] = "-.-.-."
        morseCodeMap['='] = "-...-"
        morseCodeMap['+'] = ".-.-."
        morseCodeMap['-'] = "-....-"
        morseCodeMap['_'] = "..--.-"
        morseCodeMap['"'] = ".-..-."
        morseCodeMap['$'] = "...-..-"
        morseCodeMap['@'] = ".--.-."
        morseCodeMap['¿'] = "..-.-"
        morseCodeMap['¡'] = "--...-"

        // 영어
        morseCodeMap['A'] = ".-"
        morseCodeMap['B'] = "-..."
        morseCodeMap['C'] = "-.-."
        morseCodeMap['D'] = "-.."
        morseCodeMap['E'] = "."
        morseCodeMap['F'] = "..-."
        morseCodeMap['G'] = "--."
        morseCodeMap['H'] = "...."
        morseCodeMap['I'] = ".."
        morseCodeMap['J'] = ".---"
        morseCodeMap['K'] = "-.-"
        morseCodeMap['L'] = ".-.."
        morseCodeMap['M'] = "--"
        morseCodeMap['N'] = "-."
        morseCodeMap['O'] = "---"
        morseCodeMap['P'] = ".--."
        morseCodeMap['Q'] = "--.-"
        morseCodeMap['R'] = ".-."
        morseCodeMap['S'] = "..."
        morseCodeMap['T'] = "-"
        morseCodeMap['U'] = "..-"
        morseCodeMap['V'] = "...-"
        morseCodeMap['W'] = ".--"
        morseCodeMap['X'] = "-..-"
        morseCodeMap['Y'] = "-.--"
        morseCodeMap['Z'] = "--.."

        // 한글
        morseCodeMap['ㄱ'] = ".-.."
        morseCodeMap['ㄴ'] = "..-."
        morseCodeMap['ㄷ'] = "-..."
        morseCodeMap['ㄹ'] = "...-"
        morseCodeMap['ㅁ'] = "--"
        morseCodeMap['ㅂ'] = ".--"
        morseCodeMap['ㅅ'] = "--."
        morseCodeMap['ㅇ'] = "-.-"
        morseCodeMap['ㅈ'] = ".--."
        morseCodeMap['ㅊ'] = "-.-."
        morseCodeMap['ㅋ'] = "-..-"
        morseCodeMap['ㅌ'] = "--.."
        morseCodeMap['ㅍ'] = "---"
        morseCodeMap['ㅎ'] = ".---"
        morseCodeMap['ㅏ'] = "."
        morseCodeMap['ㅑ'] = ".."
        morseCodeMap['ㅓ'] = "-"
        morseCodeMap['ㅕ'] = "..."
        morseCodeMap['ㅗ'] = ".-"
        morseCodeMap['ㅛ'] = "-."
        morseCodeMap['ㅜ'] = "...."
        morseCodeMap['ㅠ'] = ".-."
        morseCodeMap['ㅡ'] = "-.."
        morseCodeMap['ㅣ'] = "..-"
        morseCodeMap['ㅔ'] = "-.--"
        morseCodeMap['ㅐ'] = "--.-"
        // 초성 조합 문자
        morseCodeMap['ㄲ'] = morseCodeMap['ㄱ'] + " " + morseCodeMap['ㄱ']
        morseCodeMap['ㄸ'] = morseCodeMap['ㄷ'] + " " + morseCodeMap['ㄷ']
        morseCodeMap['ㅃ'] = morseCodeMap['ㅂ'] + " " + morseCodeMap['ㅂ']
        morseCodeMap['ㅆ'] = morseCodeMap['ㅅ'] + " " + morseCodeMap['ㅅ']
        morseCodeMap['ㅉ'] = morseCodeMap['ㅈ'] + " " + morseCodeMap['ㅈ']
        // 중성 조합 문자
        morseCodeMap['ㅒ'] = morseCodeMap['ㅑ'] + " " + morseCodeMap['ㅣ']
        morseCodeMap['ㅖ'] = morseCodeMap['ㅕ'] + " " + morseCodeMap['ㅣ']
        morseCodeMap['ㅘ'] = morseCodeMap['ㅗ'] + " " + morseCodeMap['ㅏ']
        morseCodeMap['ㅙ'] = morseCodeMap['ㅗ'] + " " + morseCodeMap['ㅐ']
        morseCodeMap['ㅚ'] = morseCodeMap['ㅗ'] + " " + morseCodeMap['ㅣ']
        morseCodeMap['ㅝ'] = morseCodeMap['ㅜ'] + " " + morseCodeMap['ㅓ']
        morseCodeMap['ㅞ'] = morseCodeMap['ㅜ'] + " " + morseCodeMap['ㅔ']
        morseCodeMap['ㅟ'] = morseCodeMap['ㅜ'] + " " + morseCodeMap['ㅣ']
        morseCodeMap['ㅢ'] = morseCodeMap['ㅡ'] + " " + morseCodeMap['ㅣ']
        // 종성 조합 문자
        morseCodeMap['ㄳ'] = morseCodeMap['ㄱ'] + " " + morseCodeMap['ㅅ']
        morseCodeMap['ㄵ'] = morseCodeMap['ㄴ'] + " " + morseCodeMap['ㅈ']
        morseCodeMap['ㄶ'] = morseCodeMap['ㄴ'] + " " + morseCodeMap['ㅎ']
        morseCodeMap['ㄺ'] = morseCodeMap['ㄹ'] + " " + morseCodeMap['ㄱ']
        morseCodeMap['ㄻ'] = morseCodeMap['ㄹ'] + " " + morseCodeMap['ㅁ']
        morseCodeMap['ㄼ'] = morseCodeMap['ㄹ'] + " " + morseCodeMap['ㅂ']
        morseCodeMap['ㄽ'] = morseCodeMap['ㄹ'] + " " + morseCodeMap['ㅅ']
        morseCodeMap['ㄾ'] = morseCodeMap['ㄹ'] + " " + morseCodeMap['ㅌ']
        morseCodeMap['ㄿ'] = morseCodeMap['ㄹ'] + " " + morseCodeMap['ㅍ']
        morseCodeMap['ㅀ'] = morseCodeMap['ㄹ'] + " " + morseCodeMap['ㅎ']
        morseCodeMap['ㅄ'] = morseCodeMap['ㅂ'] + " " + morseCodeMap['ㅅ']
    }


    private fun decomposeHangul(ch: Char): String {
        var result = ""
        if (ch >= '가' && ch <= '힣') {
            val cho = charArrayOf(
                'ㄱ',
                'ㄲ',
                'ㄴ',
                'ㄷ',
                'ㄸ',
                'ㄹ',
                'ㅁ',
                'ㅂ',
                'ㅃ',
                'ㅅ',
                'ㅆ',
                'ㅇ',
                'ㅈ',
                'ㅉ',
                'ㅊ',
                'ㅋ',
                'ㅌ',
                'ㅍ',
                'ㅎ'
            )
            val jung = charArrayOf(
                'ㅏ',
                'ㅐ',
                'ㅑ',
                'ㅒ',
                'ㅓ',
                'ㅔ',
                'ㅕ',
                'ㅖ',
                'ㅗ',
                'ㅘ',
                'ㅙ',
                'ㅚ',
                'ㅛ',
                'ㅜ',
                'ㅝ',
                'ㅞ',
                'ㅟ',
                'ㅠ',
                'ㅡ',
                'ㅢ',
                'ㅣ'
            )
            val jong = charArrayOf(
                ' ',
                'ㄱ',
                'ㄲ',
                'ㄳ',
                'ㄴ',
                'ㄵ',
                'ㄶ',
                'ㄷ',
                'ㄹ',
                'ㄺ',
                'ㄻ',
                'ㄼ',
                'ㄽ',
                'ㄾ',
                'ㄿ',
                'ㅀ',
                'ㅁ',
                'ㅂ',
                'ㅄ',
                'ㅅ',
                'ㅆ',
                'ㅇ',
                'ㅈ',
                'ㅊ',
                'ㅋ',
                'ㅌ',
                'ㅍ',
                'ㅎ'
            )
            val hangulBase = 0xAC00
            val choBase = 588
            val jungBase = 28
            val choIdx: Int
            val jungIdx: Int
            val jongIdx: Int
            val unicode = ch.code - hangulBase
            choIdx = unicode / choBase
            jungIdx = (unicode - choBase * choIdx) / jungBase
            jongIdx = unicode % jungBase
            result += cho[choIdx]
            result += jung[jungIdx]
            if (jong[jongIdx] != ' ') {
                result += jong[jongIdx]
            }
        } else {
            result = Character.toString(ch)
        }
        return result
    }

    private fun convertToMorse(input: String): String {
        val morseCodeBuilder = StringBuilder()
        for (i in 0 until input.length) {
            var c = input[i]

            // 소문자인 경우 대문자로 변경
            if (Character.isLowerCase(c)) {
                c = c.uppercaseChar()
            }
            if (c >= '가' && c <= '힣') { // 한글인 경우
                val decomposed = decomposeHangul(c)
                for (j in 0 until decomposed.length) {
                    val dc = decomposed[j]
                    if (morseCodeMap.containsKey(dc)) {
                        val morseChar = morseCodeMap[dc]
                        morseCodeBuilder.append(morseChar)
                        morseCodeBuilder.append(" ")
                    }
                }
            } else {
                if (morseCodeMap.containsKey(c)) {
                    val morseChar = morseCodeMap[c]
                    morseCodeBuilder.append(morseChar)
                    morseCodeBuilder.append(" ")
                } else {
                    // 띄어쓰기
                    morseCodeBuilder.append("  ")
                }
            }
        }
        return morseCodeBuilder.toString()
    }

    private fun convertMorseToCharacter() {
        for ((key, value) in morseCodeMap) {
            if (value == morseInput.toString()) {
                if (key >= 'A' && key <= 'Z') {
                    convertedCharactersEng.append(key)
                } else if (key >= '0' && key <= '9') {
                    convertedCharactersEng.append(key)
                    convertedCharactersKor.append(key)
                } else if (key == '.' || key == ',' || key == '?' || key == '\\' || key == '!' || key == '/' || key == '(' || key == ')' || key == '&' || key == ':' || key == ';' || key == '=' || key == '+' || key == '-' || key == '_' || key == '"' || key == '$' || key == '@' || key == '¿' || key == '¡') {
                    convertedCharactersEng.append(key)
                    convertedCharactersKor.append(key)
                } else {
                    convertedCharactersKor.append(key)
                }
            }
        }
    }

    // test 임시 코드 @@
//    private fun testMorseCode(morseCode: String, onCompletion: (() -> Unit)? = null) {
//        if (!hasFlash()) {
//            Toast.makeText(activity, "No flash available on your device", Toast.LENGTH_SHORT).show()
//            return
//        }
//        Toast.makeText(activity, "플래시 임시 테스트 코드입니다.", Toast.LENGTH_SHORT).show()
//        Handler(Looper.getMainLooper()).postDelayed({
//            onCompletion?.invoke()
//        }, 3000)  // 3초 (3000밀리초)
//    }

    private fun flashMorseCode(morseCode: String, onCompletion: (() -> Unit)? = null) {
        if (!hasFlash()) {
            Toast.makeText(activity, "No flash available on your device", Toast.LENGTH_SHORT).show()
            return
        }
        for (i in 0 until morseCode.length) {
            val c = morseCode[i]
            if (c == '.') {
                flashOn()
                waitMillis(400)
                flashOff()
            } else if (c == '-') {
                flashOn()
                waitMillis(1200)
                flashOff()
            } else if (c == ' ') {
                waitMillis(1000)
            }
            waitMillis(100)
        }
        onCompletion?.invoke()
    }

    private fun hasFlash(): Boolean {
        return activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) == true
    }

    private fun flashOn() {
        if (!isFlashOn) {
            try {
                cameraManager!!.setTorchMode(cameraId!!, true)
                isFlashOn = true
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun flashOff() {
        if (isFlashOn) {
            try {
                cameraManager!!.setTorchMode(cameraId!!, false)
                isFlashOn = false
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun waitMillis(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}