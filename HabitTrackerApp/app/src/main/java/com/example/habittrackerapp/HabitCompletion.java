package com.example.habittrackerapp;

public class HabitCompletion {
    private int id;
    private int habitId;
    private String date; // yyyy-MM-dd

    public HabitCompletion() {}

    public HabitCompletion(int id, int habitId, String date) {
        this.id = id;
        this.habitId = habitId;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
