package com.example.expensify_modified;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Tệp layout chứa FAB
        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mở màn hình nhập dữ liệu
                Intent intent = new Intent(MainActivity.this, input_activity.class);
                startActivity(intent);
            }

        });
        Button btnOpenThemes = findViewById(R.id.btnOpenThemes);
        btnOpenThemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ThemesActivity.class);
                startActivity(intent);
            }
        });




        // Bạn có thể thêm phần hiển thị danh sách thu/chi ở đây sau
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences("AppThemePrefs", MODE_PRIVATE);
        String themeName = prefs.getString("selected_theme", "Theme.Nhom4_LapTrinhAndroid");

        switch (themeName) {
            case "DynamicTheme1":
                setTheme(R.style.DynamicTheme1);
                break;
            case "DynamicTheme2":
                setTheme(R.style.DynamicTheme2);
                break;
            case "DynamicTheme3":
                setTheme(R.style.DynamicTheme3);
                break;
            case "DynamicTheme4":
                setTheme(R.style.DynamicTheme4);
                break;
            default:
                setTheme(R.style.Theme_Nhom4_LapTrinhAndroid);
                break;
        }
    }
}


