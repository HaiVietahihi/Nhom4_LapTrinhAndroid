package com.example.expensify_modified;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ThemesActivity extends AppCompatActivity {
    private FloatingActionButton fabAdd;
    private static final String PREFS_NAME = "AppThemePrefs";
    private static final String KEY_SELECTED_THEME = "selected_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme before super.onCreate
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        // Setup buttons to change theme
        ImageButton btnTheme1 = findViewById(R.id.btnTheme1);
        ImageButton btnTheme2 = findViewById(R.id.btnTheme2);
        ImageButton btnTheme3 = findViewById(R.id.btnTheme3);
        ImageButton btnTheme4 = findViewById(R.id.btnTheme4);

        btnTheme1.setOnClickListener(v -> setThemeAndRestart("DynamicTheme1"));
        btnTheme2.setOnClickListener(v -> setThemeAndRestart("DynamicTheme2"));
        btnTheme3.setOnClickListener(v -> setThemeAndRestart("DynamicTheme3"));
        btnTheme4.setOnClickListener(v -> setThemeAndRestart("DynamicTheme4"));
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String themeName = prefs.getString(KEY_SELECTED_THEME, "Theme.Nhom4_LapTrinhAndroid");

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

    private void setThemeAndRestart(String themeName) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_SELECTED_THEME, themeName);
        editor.apply();

        // Restart activity to apply theme
        Intent intent = new Intent(this, ThemesActivity.class);
        finish();
        startActivity(intent);

    }
}
