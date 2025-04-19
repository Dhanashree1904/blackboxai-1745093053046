package com.example.habittrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class HabitDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "habittracker.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    public static final String TABLE_USER = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Habit table
    public static final String TABLE_HABIT = "habits";
    public static final String COLUMN_HABIT_ID = "id";
    public static final String COLUMN_HABIT_USER_ID = "user_id";
    public static final String COLUMN_HABIT_NAME = "name";
    public static final String COLUMN_HABIT_DESCRIPTION = "description";

    // Habit completion table
    public static final String TABLE_HABIT_COMPLETION = "habit_completions";
    public static final String COLUMN_COMPLETION_ID = "id";
    public static final String COLUMN_COMPLETION_HABIT_ID = "habit_id";
    public static final String COLUMN_COMPLETION_DATE = "date"; // stored as TEXT yyyy-MM-dd

    public HabitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT"
                + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_HABIT_TABLE = "CREATE TABLE " + TABLE_HABIT + "("
                + COLUMN_HABIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HABIT_USER_ID + " INTEGER,"
                + COLUMN_HABIT_NAME + " TEXT,"
                + COLUMN_HABIT_DESCRIPTION + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_HABIT_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_HABIT_TABLE);

        String CREATE_HABIT_COMPLETION_TABLE = "CREATE TABLE " + TABLE_HABIT_COMPLETION + "("
                + COLUMN_COMPLETION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_COMPLETION_HABIT_ID + " INTEGER,"
                + COLUMN_COMPLETION_DATE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_COMPLETION_HABIT_ID + ") REFERENCES " + TABLE_HABIT + "(" + COLUMN_HABIT_ID + ")"
                + ")";
        db.execSQL(CREATE_HABIT_COMPLETION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABIT_COMPLETION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // User CRUD operations
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_NAME + "=? AND " + COLUMN_USER_PASSWORD + "=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_NAME + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    // Habit CRUD operations
    public long addHabit(int userId, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HABIT_USER_ID, userId);
        values.put(COLUMN_HABIT_NAME, name);
        values.put(COLUMN_HABIT_DESCRIPTION, description);
        long id = db.insert(TABLE_HABIT, null, values);
        db.close();
        return id;
    }

    public List<Habit> getHabits(int userId) {
        List<Habit> habits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_HABIT_ID, COLUMN_HABIT_NAME, COLUMN_HABIT_DESCRIPTION};
        String selection = COLUMN_HABIT_USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_HABIT, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HABIT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT_DESCRIPTION));
                habits.add(new Habit(id, userId, name, description));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return habits;
    }

    // Habit completion CRUD operations
    public boolean addHabitCompletion(int habitId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETION_HABIT_ID, habitId);
        values.put(COLUMN_COMPLETION_DATE, date);
        long result = db.insert(TABLE_HABIT_COMPLETION, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkHabitCompletion(int habitId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_COMPLETION_ID};
        String selection = COLUMN_COMPLETION_HABIT_ID + "=? AND " + COLUMN_COMPLETION_DATE + "=?";
        String[] selectionArgs = {String.valueOf(habitId), date};
        Cursor cursor = db.query(TABLE_HABIT_COMPLETION, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public List<String> getHabitCompletions(int habitId) {
        List<String> dates = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_COMPLETION_DATE};
        String selection = COLUMN_COMPLETION_HABIT_ID + "=?";
        String[] selectionArgs = {String.valueOf(habitId)};
        Cursor cursor = db.query(TABLE_HABIT_COMPLETION, columns, selection, selectionArgs, null, null, COLUMN_COMPLETION_DATE + " ASC");
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETION_DATE));
                dates.add(date);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dates;
    }

    public boolean deleteHabit(int habitId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_HABIT, COLUMN_HABIT_ID + "=?", new String[]{String.valueOf(habitId)});
        db.close();
        return result > 0;
    }
}
