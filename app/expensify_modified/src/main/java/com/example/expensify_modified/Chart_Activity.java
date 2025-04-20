package com.example.expensify_modified;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Chart_Activity extends AppCompatActivity {

    private PieChart pieChart;
    private CombinedChart combinedChart;
    private RecyclerView recyclerView;
    private TextView tvMonthYear, tvExpense, tvIncome, tvBalance;
    private Spinner spinnerViewMode;
    private ImageView btnPrev, btnNext;
    private Button btnOK;
    private DatabaseHelper dbHelper;
    private BottomNavigationView bottomNavigationView;
    private boolean isExpenseTab = true;

    private enum ViewMode { DAY, MONTH, YEAR }
    private ViewMode currentViewMode = ViewMode.DAY;
    private Calendar currentCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        dbHelper = new DatabaseHelper(this);


        pieChart = findViewById(R.id.pieChart);
        combinedChart = findViewById(R.id.combinedChart);
        recyclerView = findViewById(R.id.recyclerViewCategories);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvExpense = findViewById(R.id.tvExpense);
        tvIncome = findViewById(R.id.tvIncome);
        tvBalance = findViewById(R.id.tvBalance);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        spinnerViewMode = findViewById(R.id.spinnerViewMode);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnOK = findViewById(R.id.btnOK);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.tabExpense).setOnClickListener(v -> {
            isExpenseTab = true;
            updateTabUI();
            loadChartData();
        });

        findViewById(R.id.tabIncome).setOnClickListener(v -> {
            isExpenseTab = false;
            updateTabUI();
            loadChartData();
        });

        setupBottomNavigation();
        setupSpinner();
        setupButtons();
        updateTabUI();
        updateDateDisplay();
    }
    private int getThemeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.data;
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

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_report);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_input) {
                startActivity(new Intent(getApplicationContext(), input_activity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_calendar) {
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_report) {
                return true;
            } else if (id == R.id.nav_more) {
                startActivity(new Intent(getApplicationContext(), MoreActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.view_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerViewMode.setAdapter(adapter);

        spinnerViewMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentViewMode = ViewMode.values()[position];
                updateDateDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupButtons() {
        tvMonthYear.setOnClickListener(v -> showDatePicker());
        btnPrev.setOnClickListener(v -> {
            shiftDate(-1);
            updateDateDisplay();
        });
        btnNext.setOnClickListener(v -> {
            shiftDate(1);
            updateDateDisplay();
        });
        btnOK.setOnClickListener(v -> loadChartData());
    }

    private void showDatePicker() {
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        int day = currentCalendar.get(Calendar.DAY_OF_MONTH);

        if (currentViewMode == ViewMode.DAY) {
            new DatePickerDialog(this, (view, y, m, d) -> {
                currentCalendar.set(y, m, d);
                updateDateDisplay();
            }, year, month, day).show();
        } else if (currentViewMode == ViewMode.MONTH) {
            MonthYearPickerDialog dialog = new MonthYearPickerDialog();
            dialog.setListener((y, m) -> {
                currentCalendar.set(Calendar.YEAR, y);
                currentCalendar.set(Calendar.MONTH, m);
                updateDateDisplay();
            });
            dialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        } else if (currentViewMode == ViewMode.YEAR) {
            YearPickerDialog dialog = new YearPickerDialog();
            dialog.setListener(y -> {
                currentCalendar.set(Calendar.YEAR, y);
                updateDateDisplay();
            });
            dialog.show(getSupportFragmentManager(), "YearPickerDialog");
        }
    }

    private void shiftDate(int value) {
        switch (currentViewMode) {
            case DAY:
                currentCalendar.add(Calendar.DAY_OF_MONTH, value);
                break;
            case MONTH:
                currentCalendar.add(Calendar.MONTH, value);
                break;
            case YEAR:
                currentCalendar.add(Calendar.YEAR, value);
                break;
        }
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf;
        switch (currentViewMode) {
            case DAY:
                sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                break;
            case MONTH:
                sdf = new SimpleDateFormat("yyyy/MM", Locale.getDefault());
                break;
            case YEAR:
                sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
                break;
            default:
                sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        }
        tvMonthYear.setText(sdf.format(currentCalendar.getTime()));
    }

    private void updateTabUI() {
        TextView tabExpense = findViewById(R.id.tabExpense);
        TextView tabIncome = findViewById(R.id.tabIncome);
        int tabSelectedColor = getThemeColor(R.attr.tabSelectedColor);
        int tabUnselectedColor = getThemeColor(R.attr.tabUnselectedColor);


        if (isExpenseTab) {
            tabExpense.setBackgroundColor(tabSelectedColor);
            tabIncome.setBackgroundColor(tabUnselectedColor);

            pieChart.setVisibility(View.VISIBLE);
            combinedChart.setVisibility(View.GONE);

            // Hiện các TextView liên quan đến chi tiêu
            tvExpense.setVisibility(View.VISIBLE);
            tvBalance.setVisibility(View.VISIBLE);
            tvIncome.setVisibility(View.VISIBLE);
        } else {
            tabExpense.setBackgroundColor(tabUnselectedColor);
            tabIncome.setBackgroundColor(tabSelectedColor);

            pieChart.setVisibility(View.GONE);
            combinedChart.setVisibility(View.VISIBLE);

            // Ẩn tvExpense và tvBalance khi xem thu nhập
            tvExpense.setVisibility(View.GONE);
            tvBalance.setVisibility(View.GONE);
            tvIncome.setVisibility(View.VISIBLE);
        }
    }


    private void loadChartData() {
        String formattedDate;
        String type = isExpenseTab ? "OUT" : "IN";
        Cursor cursor = null;
        double total = 0;

        switch (currentViewMode) {
            case DAY:
                formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentCalendar.getTime());
                total = dbHelper.getTotalByTypeAndDate(type, formattedDate);
                cursor = isExpenseTab ? dbHelper.getExpensesByDay(formattedDate) : dbHelper.getIncomeByDay(formattedDate);
                break;
            case MONTH:
                formattedDate = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(currentCalendar.getTime());
                total = dbHelper.getMonthlyTotalByType(type, formattedDate);
                cursor = isExpenseTab ? dbHelper.getExpensesByMonth(formattedDate) : dbHelper.getIncomeByMonth(formattedDate);
                break;
            case YEAR:
                formattedDate = new SimpleDateFormat("yyyy", Locale.getDefault()).format(currentCalendar.getTime());
                total = dbHelper.getYearlyTotalByType(type, formattedDate);
                cursor = isExpenseTab ? dbHelper.getExpensesByYear(formattedDate) : dbHelper.getIncomeByYear(formattedDate);
                break;
        }
// Hiển thị chi tiêu
        tvExpense.setText(String.format("%s\n-%,.0f", getString(R.string.tab_expense), isExpenseTab ? total : 0));

// Hiển thị thu nhập
        tvIncome.setText(String.format("%s\n+%,.0f", getString(R.string.tab_income), isExpenseTab ? 0 : total));

// Hiển thị thu chi
        if (isExpenseTab) {
            // Nếu là tab chi tiêu, hiển thị số âm
            tvBalance.setText(String.format("%s\n-%,.0f", getString(R.string.balance), total));
        } else {
            // Nếu là tab thu nhập, hiển thị số dương
            tvBalance.setText(String.format("%s\n+%,.0f", getString(R.string.balance), total));
        }
        if (cursor != null && cursor.moveToFirst()) {
            List<CategoryExpenseAdapter.CategoryExpense> list = new ArrayList<>();
            List<PieEntry> pieEntries = new ArrayList<>();
            List<BarEntry> barEntries = new ArrayList<>();
            int index = 0;

            do {
                String category = cursor.getString(0);
                double amount = cursor.getDouble(1);
                double percent = (total > 0) ? (amount / total) * 100 : 0;

                list.add(new CategoryExpenseAdapter.CategoryExpense(category, amount, percent));

                if (isExpenseTab) {
                    pieEntries.add(new PieEntry((float) amount, category));
                } else {
                    barEntries.add(new BarEntry(index++, (float) amount));
                }
            } while (cursor.moveToNext());

            cursor.close();
            recyclerView.setAdapter(new CategoryExpenseAdapter(list));

            if (isExpenseTab) {
                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(new int[]{ R.color.red, R.color.orange, R.color.green, R.color.blue, R.color.purple }, this);
                dataSet.setValueTextSize(14f);
                dataSet.setSliceSpace(2f);
                pieChart.setData(new PieData(dataSet));
                pieChart.setUsePercentValues(false);
                pieChart.setDrawHoleEnabled(true);
                pieChart.setEntryLabelTextSize(12f);
                pieChart.getDescription().setEnabled(false);
                pieChart.getLegend().setEnabled(false);
                pieChart.invalidate();
            } else {
                BarDataSet barDataSet = new BarDataSet(barEntries, "Thu nhập theo danh mục");
                barDataSet.setColors(new int[]{ R.color.red, R.color.orange, R.color.green, R.color.blue, R.color.purple }, this);
                barDataSet.setValueTextSize(14f);
                CombinedData combinedData = new CombinedData();
                combinedData.setData(new BarData(barDataSet));
                combinedChart.setData(combinedData);
                combinedChart.getDescription().setEnabled(false);
                combinedChart.getXAxis().setEnabled(false);
                combinedChart.getAxisLeft().setAxisMinimum(0);
                combinedChart.getAxisRight().setEnabled(false);
                combinedChart.invalidate();
            }
        }
    }
}
