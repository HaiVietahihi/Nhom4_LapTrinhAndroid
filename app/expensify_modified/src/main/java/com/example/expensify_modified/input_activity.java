package com.example.expensify_modified;
import android.app.DatePickerDialog;
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

import java.util.Calendar;

public class input_activity extends AppCompatActivity {
    private Button btnExpense, btnIncome, btnSubmit;
    private EditText etDate, etNote, etAmount, etCustomCategory;
    private Spinner spinnerCategory;
    private String transactionType = "expense";
    private DatabaseHelper dbHelper;

    private final String[] expenseCategories = {
            "Ăn uống", "Di chuyển", "Mua sắm", "Giải trí", "Học tập", "Sức khỏe", "Khác"
    };

    private final String[] incomeCategories = {
            "Lương", "Thưởng", "Bán hàng", "Đầu tư", "Khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        dbHelper = new DatabaseHelper(this);

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
                if (selected.equals("Khác")) {
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
        etCustomCategory.setVisibility(View.GONE); // Reset mỗi lần đổi loại
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

        if (category.equals("Khác")) {
            category = etCustomCategory.getText().toString().trim();
            if (category.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên danh mục!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String typeMapped = transactionType.equals("income") ? "IN" : "OUT";

        dbHelper.addExpense(amount, typeMapped, category, note, date);

        Toast.makeText(this, "Đã lưu " + (typeMapped.equals("IN") ? "thu nhập" : "chi tiêu"), Toast.LENGTH_SHORT).show();
        finish();
    }
}

