package com.saveurlife.goodnews;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private static final long DOUBLE_BACK_PRESS_EXIT_INTERVAL = 2000; // 2초
    private long lastBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (SystemClock.elapsedRealtime() - lastBackPressedTime < DOUBLE_BACK_PRESS_EXIT_INTERVAL) {
            super.onBackPressed();
            return;
        }

        lastBackPressedTime = SystemClock.elapsedRealtime();
        Toast.makeText(this, "한 번 더 누르면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
    }
}
