package com.umko.pengeluaranku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pengeluaranku.db";
    private static final int DB_VERSION = 1;

    public static class Expense {
        public final long id;
        public final String title;
        public final long amount;
        public final String date;

        public Expense(long id, String title, long amount, String date) {
            this.id = id;
            this.title = title;
            this.amount = amount;
            this.date = date;
        }
    }

    public ExpenseDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "amount INTEGER NOT NULL, " +
                "date TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expenses");
        onCreate(db);
    }

    public void addExpense(String title, long amount, String date) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("amount", amount);
        cv.put("date", date);
        getWritableDatabase().insert("expenses", null, cv);
    }

    public void deleteExpense(long id) {
        getWritableDatabase().delete("expenses", "id = ?", new String[]{String.valueOf(id)});
    }

    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, title, amount, date FROM expenses ORDER BY id DESC", null);
        while (c.moveToNext()) {
            list.add(new Expense(c.getLong(0), c.getString(1), c.getLong(2), c.getString(3)));
        }
        c.close();
        return list;
    }

    public long getTotal() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COALESCE(SUM(amount), 0) FROM expenses", null);
        long total = 0;
        if (c.moveToFirst()) {
            total = c.getLong(0);
        }
        c.close();
        return total;
    }
}