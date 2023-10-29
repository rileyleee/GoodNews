package com.saveurlife.goodnews.flashlight;

import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import android.os.Handler;

import com.saveurlife.goodnews.R;

public class SubActivity extends AppCompatActivity {

    private EditText inputEditText;
    private Button convertButton;
    private Button morseInputButton;
    private RadioGroup languageRadioGroup;
    private Boolean isEng=true;
    private TextView morseOutputTextView;
    private Button clearButton;
    private StringBuilder convertedCharactersEng = new StringBuilder();
    private StringBuilder convertedCharactersKor = new StringBuilder();
    private StringBuilder morseInput = new StringBuilder();
    private Handler morseInputHandler = new Handler();
    private Runnable morseInputRunnable = new Runnable() {
        @Override
        public void run() {
            convertMorseToCharacter();
            morseInput.setLength(0);  // Reset the Morse input
            if(isEng){
                morseOutputTextView.setText(convertedCharactersEng);
            }
            else{
                morseOutputTextView.setText(convertedCharactersKor);
            }
        }
    };
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashOn = false;

    // 문자에 대한 모스 부호 매핑
    private Map<Character, String> morseCodeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        inputEditText = findViewById(R.id.inputEditText);
        convertButton = findViewById(R.id.convertButton);

        morseInputButton = findViewById(R.id.morseInputButton);
        languageRadioGroup = findViewById(R.id.languageRadioGroup);
        morseOutputTextView = findViewById(R.id.morseOutputTextView);
        clearButton = findViewById(R.id.clearButton);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // 모스 부호 매핑 초기화
        initializeMorseCodeMap();

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputEditText.getText().toString().trim();
                String morseCode = convertToMorse(input);
                flashMorseCode(morseCode);
            }
        });

        morseInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morseInput.append(".");
                if(isEng){
                    morseOutputTextView.setText(convertedCharactersEng.toString()+morseInput.toString());
                }
                else{
                    morseOutputTextView.setText(convertedCharactersKor.toString()+morseInput.toString());
                }

                morseInputHandler.removeCallbacks(morseInputRunnable);
                morseInputHandler.postDelayed(morseInputRunnable, 1200);
            }
        });

        morseInputButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                morseInput.append("-");
                if(isEng){
                    morseOutputTextView.setText(convertedCharactersEng.toString()+morseInput.toString());
                }
                else{
                    morseOutputTextView.setText(convertedCharactersKor.toString()+morseInput.toString());
                }

                morseInputHandler.removeCallbacks(morseInputRunnable);
                morseInputHandler.postDelayed(morseInputRunnable, 1200);
                return true;
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morseOutputTextView.setText("");
                convertedCharactersEng.setLength(0);
                convertedCharactersKor.setLength(0);
            }
        });

        languageRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.englishRadioButton) {
                    isEng=true;
                    morseOutputTextView.setText(convertedCharactersEng);
                } else if (checkedId == R.id.koreanRadioButton) {
                    isEng=false;
                    morseOutputTextView.setText(convertedCharactersKor);
                }
            }
        });
    }

    private void initializeMorseCodeMap() {
        // 숫자
        morseCodeMap.put('0', "-----");
        morseCodeMap.put('1', ".----");
        morseCodeMap.put('2', "..---");
        morseCodeMap.put('3', "...--");
        morseCodeMap.put('4', "....-");
        morseCodeMap.put('5', ".....");
        morseCodeMap.put('6', "-....");
        morseCodeMap.put('7', "--...");
        morseCodeMap.put('8', "---..");
        morseCodeMap.put('9', "----.");
        // 특수기호
        morseCodeMap.put('.', ".-.-.-");
        morseCodeMap.put(',', "--..--");
        morseCodeMap.put('?', "..--..");
        morseCodeMap.put('\\', ".----.");
        morseCodeMap.put('!', "-.-.--");
        morseCodeMap.put('/', "-..-.");
        morseCodeMap.put('(', "-.--.");
        morseCodeMap.put(')', "-.--.-");
        morseCodeMap.put('&', ".-...");
        morseCodeMap.put(':', "---...");
        morseCodeMap.put(';', "-.-.-.");
        morseCodeMap.put('=', "-...-");
        morseCodeMap.put('+', ".-.-.");
        morseCodeMap.put('-', "-....-");
        morseCodeMap.put('_', "..--.-");
        morseCodeMap.put('"', ".-..-.");
        morseCodeMap.put('$', "...-..-");
        morseCodeMap.put('@', ".--.-.");
        morseCodeMap.put('¿', "..-.-");
        morseCodeMap.put('¡', "--...-");

        // 영어
        morseCodeMap.put('A', ".-");
        morseCodeMap.put('B', "-...");
        morseCodeMap.put('C', "-.-.");
        morseCodeMap.put('D', "-..");
        morseCodeMap.put('E', ".");
        morseCodeMap.put('F', "..-.");
        morseCodeMap.put('G', "--.");
        morseCodeMap.put('H', "....");
        morseCodeMap.put('I', "..");
        morseCodeMap.put('J', ".---");
        morseCodeMap.put('K', "-.-");
        morseCodeMap.put('L', ".-..");
        morseCodeMap.put('M', "--");
        morseCodeMap.put('N', "-.");
        morseCodeMap.put('O', "---");
        morseCodeMap.put('P', ".--.");
        morseCodeMap.put('Q', "--.-");
        morseCodeMap.put('R', ".-.");
        morseCodeMap.put('S', "...");
        morseCodeMap.put('T', "-");
        morseCodeMap.put('U', "..-");
        morseCodeMap.put('V', "...-");
        morseCodeMap.put('W', ".--");
        morseCodeMap.put('X', "-..-");
        morseCodeMap.put('Y', "-.--");
        morseCodeMap.put('Z', "--..");

        // 한글
        morseCodeMap.put('ㄱ', ".-..");
        morseCodeMap.put('ㄴ', "..-.");
        morseCodeMap.put('ㄷ', "-...");
        morseCodeMap.put('ㄹ', "...-");
        morseCodeMap.put('ㅁ', "--");
        morseCodeMap.put('ㅂ', ".--");
        morseCodeMap.put('ㅅ', "--.");
        morseCodeMap.put('ㅇ', "-.-");
        morseCodeMap.put('ㅈ', ".--.");
        morseCodeMap.put('ㅊ', "-.-.");
        morseCodeMap.put('ㅋ', "-..-");
        morseCodeMap.put('ㅌ', "--..");
        morseCodeMap.put('ㅍ', "---");
        morseCodeMap.put('ㅎ', ".---");
        morseCodeMap.put('ㅏ', ".");
        morseCodeMap.put('ㅑ', "..");
        morseCodeMap.put('ㅓ', "-");
        morseCodeMap.put('ㅕ', "...");
        morseCodeMap.put('ㅗ', ".-");
        morseCodeMap.put('ㅛ', "-.");
        morseCodeMap.put('ㅜ', "....");
        morseCodeMap.put('ㅠ', ".-.");
        morseCodeMap.put('ㅡ', "-..");
        morseCodeMap.put('ㅣ', "..-");
        morseCodeMap.put('ㅔ', "-.--");
        morseCodeMap.put('ㅐ', "--.-");
        // 초성 조합 문자
        morseCodeMap.put('ㄲ', morseCodeMap.get('ㄱ') + " " + morseCodeMap.get('ㄱ'));
        morseCodeMap.put('ㄸ', morseCodeMap.get('ㄷ') + " " + morseCodeMap.get('ㄷ'));
        morseCodeMap.put('ㅃ', morseCodeMap.get('ㅂ') + " " + morseCodeMap.get('ㅂ'));
        morseCodeMap.put('ㅆ', morseCodeMap.get('ㅅ') + " " + morseCodeMap.get('ㅅ'));
        morseCodeMap.put('ㅉ', morseCodeMap.get('ㅈ') + " " + morseCodeMap.get('ㅈ'));
        // 중성 조합 문자
        morseCodeMap.put('ㅒ', morseCodeMap.get('ㅑ') + " " + morseCodeMap.get('ㅣ'));
        morseCodeMap.put('ㅖ', morseCodeMap.get('ㅕ') + " " + morseCodeMap.get('ㅣ'));
        morseCodeMap.put('ㅘ', morseCodeMap.get('ㅗ') + " " + morseCodeMap.get('ㅏ'));
        morseCodeMap.put('ㅙ', morseCodeMap.get('ㅗ') + " " + morseCodeMap.get('ㅐ'));
        morseCodeMap.put('ㅚ', morseCodeMap.get('ㅗ') + " " + morseCodeMap.get('ㅣ'));
        morseCodeMap.put('ㅝ', morseCodeMap.get('ㅜ') + " " + morseCodeMap.get('ㅓ'));
        morseCodeMap.put('ㅞ', morseCodeMap.get('ㅜ') + " " + morseCodeMap.get('ㅔ'));
        morseCodeMap.put('ㅟ', morseCodeMap.get('ㅜ') + " " + morseCodeMap.get('ㅣ'));
        morseCodeMap.put('ㅢ', morseCodeMap.get('ㅡ') + " " + morseCodeMap.get('ㅣ'));
        // 종성 조합 문자
        morseCodeMap.put('ㄳ', morseCodeMap.get('ㄱ') + " " + morseCodeMap.get('ㅅ'));
        morseCodeMap.put('ㄵ', morseCodeMap.get('ㄴ') + " " + morseCodeMap.get('ㅈ'));
        morseCodeMap.put('ㄶ', morseCodeMap.get('ㄴ') + " " + morseCodeMap.get('ㅎ'));
        morseCodeMap.put('ㄺ', morseCodeMap.get('ㄹ') + " " + morseCodeMap.get('ㄱ'));
        morseCodeMap.put('ㄻ', morseCodeMap.get('ㄹ') + " " + morseCodeMap.get('ㅁ'));
        morseCodeMap.put('ㄼ', morseCodeMap.get('ㄹ') + " " + morseCodeMap.get('ㅂ'));
        morseCodeMap.put('ㄽ', morseCodeMap.get('ㄹ') + " " + morseCodeMap.get('ㅅ'));
        morseCodeMap.put('ㄾ', morseCodeMap.get('ㄹ') + " " + morseCodeMap.get('ㅌ'));
        morseCodeMap.put('ㄿ', morseCodeMap.get('ㄹ') + " " + morseCodeMap.get('ㅍ'));
        morseCodeMap.put('ㅀ', morseCodeMap.get('ㄹ') + " " + morseCodeMap.get('ㅎ'));
        morseCodeMap.put('ㅄ', morseCodeMap.get('ㅂ') + " " + morseCodeMap.get('ㅅ'));
    }


    private String decomposeHangul(char ch) {
        String result = "";

        if (ch >= '가' && ch <= '힣') {
            char[] cho = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
            char[] jung = {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'};
            char[] jong = {' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

            int hangulBase = 0xAC00;
            int choBase = 588;
            int jungBase = 28;

            int choIdx, jungIdx, jongIdx;

            int unicode = ch - hangulBase;

            choIdx = unicode / choBase;
            jungIdx = (unicode - (choBase * choIdx)) / jungBase;
            jongIdx = unicode % jungBase;

            result += cho[choIdx];
            result += jung[jungIdx];

            if (jong[jongIdx] != ' ') {
                result += jong[jongIdx];
            }
        } else {
            result = Character.toString(ch);
        }

        return result;
    }

    private String convertToMorse(String input) {
        StringBuilder morseCodeBuilder = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // 소문자인 경우 대문자로 변경
            if (Character.isLowerCase(c)) {
                c = Character.toUpperCase(c);
            }

            if (c >= '가' && c <= '힣') { // 한글인 경우
                String decomposed = decomposeHangul(c);
                for (int j = 0; j < decomposed.length(); j++) {
                    char dc = decomposed.charAt(j);
                    if (morseCodeMap.containsKey(dc)) {
                        String morseChar = morseCodeMap.get(dc);
                        morseCodeBuilder.append(morseChar);
                        morseCodeBuilder.append(" ");
                    }
                }
            } else {
                if (morseCodeMap.containsKey(c)) {
                    String morseChar = morseCodeMap.get(c);
                    morseCodeBuilder.append(morseChar);
                    morseCodeBuilder.append(" ");
                }
                else{
                    // 띄어쓰기
                    morseCodeBuilder.append("  ");
                }
            }
        }

        return morseCodeBuilder.toString();
    }

    private void convertMorseToCharacter() {
        for (Map.Entry<Character, String> entry : morseCodeMap.entrySet()) {
            if (entry.getValue().equals(morseInput.toString())) {
                if(entry.getKey() >= 'A' && entry.getKey() <= 'Z'){
                    convertedCharactersEng.append(entry.getKey());
                }
                else if(entry.getKey()>='0'&&entry.getKey()<='9'){
                    convertedCharactersEng.append(entry.getKey());
                    convertedCharactersKor.append(entry.getKey());
                }
                else if (entry.getKey() == '.' || entry.getKey() == ',' || entry.getKey() == '?' || entry.getKey() == '\\' || entry.getKey() == '!' ||
                        entry.getKey() == '/' || entry.getKey() == '(' || entry.getKey() == ')' || entry.getKey() == '&' || entry.getKey() == ':' ||
                        entry.getKey() == ';' || entry.getKey() == '=' || entry.getKey() == '+' || entry.getKey() == '-' || entry.getKey() == '_' ||
                        entry.getKey() == '"' || entry.getKey() == '$' || entry.getKey() == '@' || entry.getKey() == '¿' || entry.getKey() == '¡') {
                    convertedCharactersEng.append(entry.getKey());
                    convertedCharactersKor.append(entry.getKey());
                }
                else{
                    convertedCharactersKor.append(entry.getKey());
                }
            }
        }
    }

    private void flashMorseCode(String morseCode) {
        if (!hasFlash()) {
            Toast.makeText(this, "No flash available on your device", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < morseCode.length(); i++) {
            char c = morseCode.charAt(i);
            if (c == '.') {
                flashOn();
                waitMillis(400);
                flashOff();
            } else if (c == '-') {
                flashOn();
                waitMillis(1200);
                flashOff();
            } else if (c == ' ') {
                waitMillis(1000);
            }
            waitMillis(100);
        }
    }

    private boolean hasFlash() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void flashOn() {
        if (!isFlashOn) {
            try {
                cameraManager.setTorchMode(cameraId, true);
                isFlashOn = true;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void flashOff() {
        if (isFlashOn) {
            try {
                cameraManager.setTorchMode(cameraId, false);
                isFlashOn = false;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
