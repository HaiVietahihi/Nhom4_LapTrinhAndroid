package com.example.expensify_modified;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expenses_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_NOTE + " TEXT, " +
                COLUMN_DATE + " TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    public void addExpense(double amount, String type, String category, String note, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_DATE, date);
        db.insert(TABLE_EXPENSES, null, values);
        db.close();
    }

    public double getTotalByTypeAndDate(String type, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES +
                        " WHERE " + COLUMN_TYPE + "=? AND " + COLUMN_DATE + "=?",
                new String[]{type, date});
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public double getMonthlyTotalByType(String type, String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES +
                        " WHERE " + COLUMN_TYPE + "=? AND " + COLUMN_DATE + " LIKE ?",
                new String[]{type, month + "%"});
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    //  LẤY DỮ LIỆU THEO THÁNG
    public Cursor getExpensesByMonth(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT category, SUM(amount) FROM " + TABLE_EXPENSES +
                        " WHERE type = 'OUT' AND date LIKE ? GROUP BY category",
                new String[]{monthYear + "%"});
    }

    //  LẤY DỮ LIỆU THEO NGÀY
    public Cursor getExpensesByDay(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT category, SUM(amount) FROM " + TABLE_EXPENSES +
                        " WHERE type = 'OUT' AND date = ? GROUP BY category",
                new String[]{date});
    }

    // LẤY DỮ LIỆU THEO NĂM
    public Cursor getExpensesByYear(String year) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT category, SUM(amount) FROM " + TABLE_EXPENSES +
                        " WHERE type = 'OUT' AND date LIKE ? GROUP BY category",
                new String[]{year + "%"});
    }

    //  LẤY DỮ LIỆU THEO THÁNG
    public Cursor getIncomeByMonth(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT category, SUM(amount) FROM " + TABLE_EXPENSES +
                        " WHERE type = 'IN' AND date LIKE ? GROUP BY category",
                new String[]{monthYear + "%"});
    }

    //  LẤY DỮ LIỆU THEO NGÀY
    public Cursor getIncomeByDay(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT category, SUM(amount) FROM " + TABLE_EXPENSES +
                        " WHERE type = 'IN' AND date = ? GROUP BY category",
                new String[]{date});
    }

    // LẤY DỮ LIỆU THEO NĂM
    public Cursor getIncomeByYear(String year) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT category, SUM(amount) FROM " + TABLE_EXPENSES +
                        " WHERE type = 'IN' AND date LIKE ? GROUP BY category",
                new String[]{year + "%"});
    }



    // TỔNG THU/CHI TRONG NĂM
    public double getYearlyTotalByType(String type, String year) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES +
                        " WHERE " + COLUMN_TYPE + "=? AND " + COLUMN_DATE + " LIKE ?",
                new String[]{type, year + "%"});
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }
}
