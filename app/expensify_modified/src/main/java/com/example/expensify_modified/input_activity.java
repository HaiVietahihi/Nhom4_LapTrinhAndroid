package com.example.expensify_modified;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class input_activity extends AppCompatActivity {
    private Button btnExpense, btnIncome, btnSubmit;
    private EditText etDate, etNote, etAmount, etCustomCategory;
    private Spinner spinnerCategory;
    private String transactionType = "expense";
    private DatabaseHelper dbHelper;

    private BottomNavigationView bottomNavigationView;
    private String[] expenseCategories;
    private String[] incomeCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // Ánh xạ view
        btnExpense = findViewById(R.id.btnExpense);
        btnIncome = findViewById(R.id.btnIncome);
        btnSubmit = findViewById(R.id.btnSubmit);
        etDate = findViewById(R.id.etDate);
        etNote = findViewById(R.id.etNote);
        etAmount = findViewById(R.id.etAmount);
        etCustomCategory = findViewById(R.id.etCustomCategory);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        dbHelper = new DatabaseHelper(this);

        // Lấy dữ liệu đa ngôn ngữ từ strings.xml
        expenseCategories = getResources().getStringArray(R.array.expense_categories);
        incomeCategories = getResources().getStringArray(R.array.income_categories);

        // Load danh mục mặc định là "chi tiêu"
        loadCategories(expenseCategories);

        // Chọn loại giao dịch
        btnExpense.setOnClickListener(v -> {
            transactionType = "expense";
            updateTabUI();
            loadCategories(expenseCategories);
        });

        btnIncome.setOnClickListener(v -> {
            transactionType = "income";
            updateTabUI();
            loadCategories(incomeCategories);
        });

        // Chọn ngày
        etDate.setOnClickListener(v -> showDatePicker());

        // Hiển thị ô nhập danh mục nếu chọn "Khác"
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinnerCategory.getSelectedItem().toString();
                if (selected.equals(getString(R.string.other))) {
                    etCustomCategory.setVisibility(View.VISIBLE);
                } else {
                    etCustomCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // Gửi dữ liệu
        btnSubmit.setOnClickListener(v -> saveTransaction());
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_input);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_input) {
                return true;
            } else if (id == R.id.nav_calendar) {
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_report) {
                startActivity(new Intent(getApplicationContext(), Chart_Activity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_more) {
                startActivity(new Intent(getApplicationContext(), MoreActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
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
    private void updateTabUI() {
        if (transactionType.equals("expense")) {
            btnExpense.setBackground(getDrawable(R.drawable.tab_selected));
            btnExpense.setTextColor(Color.WHITE);
            btnIncome.setBackground(getDrawable(R.drawable.tab_unselected));
            btnIncome.setTextColor(Color.DKGRAY);
        } else {
            btnIncome.setBackground(getDrawable(R.drawable.tab_selected));
            btnIncome.setTextColor(Color.WHITE);
            btnExpense.setBackground(getDrawable(R.drawable.tab_unselected));
            btnExpense.setTextColor(Color.DKGRAY);
        }
    }

    private void loadCategories(String[] categories) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
        etCustomCategory.setVisibility(View.GONE);
        etCustomCategory.setText("");
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (DatePicker view, int y, int m, int d) -> {
            String selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d);
            etDate.setText(selectedDate);
        }, year, month, day).show();
    }

    private void saveTransaction() {
        String date = etDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (category.equals(getString(R.string.other))) {
            category = etCustomCategory.getText().toString().trim();
            if (category.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_custom_category), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_required_info), Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String typeMapped = transactionType.equals("income") ? "IN" : "OUT";

        dbHelper.addExpense(amount, typeMapped, category, note, date);

        Toast.makeText(this, getString(R.string.saved_success), Toast.LENGTH_SHORT).show();
        finish();
    }
}
