package com.example.expensify_modified;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Thông tin cơ sở dữ liệu
    private static final String DATABASE_NAME = "expenses_db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TYPE = "type";       // "IN" hoặc "OUT"
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_DATE = "date";       // yyyy-MM-dd

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Tạo bảng
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EXPENSES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_AMOUNT + " REAL, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_NOTE + " TEXT, "
                + COLUMN_DATE + " TEXT"
                + ")";
        db.execSQL(createTable);
    }

    // Nâng cấp CSDL (xóa và tạo lại nếu cần)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    // Thêm chi tiêu
    public void addExpense(double amount, String type, String category, String note, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_TYPE, type); // "IN" or "OUT"
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_DATE, date);
        db.insert(TABLE_EXPENSES, null, values);
        db.close();
    }

    // Lấy chi tiêu theo ngày
    public Cursor getExpensesByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_EXPENSES,
                null,
                COLUMN_DATE + "=?",
                new String[]{date},
                null, null,
                COLUMN_ID + " DESC"
        );
    }

    // Lấy tất cả các ngày có chi tiêu (để đánh dấu trên lịch)
    public Cursor getAllSpentDates() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT DISTINCT " + COLUMN_DATE + " FROM " + TABLE_EXPENSES, null);
    }

    // Tính tổng tiền theo loại (IN/OUT) và ngày
    public double getTotalByTypeAndDate(String type, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES +
                        " WHERE " + COLUMN_TYPE + "=? AND " + COLUMN_DATE + "=?",
                new String[]{type, date}
        );
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    // Tính tổng tiền theo loại (IN/OUT) trong một tháng cụ thể
    public double getMonthlyTotalByType(String type, String month) {
        // month format: "yyyy-MM" (vd: "2025-04")
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES +
                        " WHERE " + COLUMN_TYPE + "=? AND " + COLUMN_DATE + " LIKE ?",
                new String[]{type, month + "%"}
        );
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }
    public Cursor getExpensesByMonth(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT category, SUM(amount) FROM " + TABLE_EXPENSES +
                        " WHERE type = 'OUT' AND date LIKE ? GROUP BY category",
                new String[]{monthYear + "%"}
        );
    }

}