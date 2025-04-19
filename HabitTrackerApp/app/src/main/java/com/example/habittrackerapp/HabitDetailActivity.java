package com.example.habittrackerapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HabitDetailActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button buttonLogCompletion;
    private TextView textViewHabitName, textViewCurrentStreak;
    private HabitDbHelper dbHelper;
    private int habitId;
    private String habitName;
    private String selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_detail);

        calendarView = findViewById(R.id.calendarView);
        buttonLogCompletion = findViewById(R.id.buttonLogCompletion);
        textViewHabitName = findViewById(R.id.textViewHabitName);
        textViewCurrentStreak = findViewById(R.id.textViewCurrentStreak);

        dbHelper = new HabitDbHelper(this);

        habitId = getIntent().getIntExtra("habitId", -1);
        habitName = getIntent().getStringExtra("habitName");

        if (habitId == -1 || habitName == null) {
            Toast.makeText(this, "Habit not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textViewHabitName.setText(habitName);

        // Set selectedDate to today's date initially
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // month is 0-based
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
        });

        buttonLogCompletion.setOnClickListener(v -> {
            boolean alreadyCompleted = dbHelper.checkHabitCompletion(habitId, selectedDate);
            if (alreadyCompleted) {
                Toast.makeText(HabitDetailActivity.this, "Habit already logged for this day", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.addHabitCompletion(habitId, selectedDate);
                if (success) {
                    Toast.makeText(HabitDetailActivity.this, "Habit logged for " + selectedDate, Toast.LENGTH_SHORT).show();
                    updateCurrentStreak();
                } else {
                    Toast.makeText(HabitDetailActivity.this, "Failed to log habit", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateCurrentStreak();
    }

    private void updateCurrentStreak() {
        List<String> completions = dbHelper.getHabitCompletions(habitId);
        int currentStreak = calculateCurrentStreak(completions);
        textViewCurrentStreak.setText("Current Streak: " + currentStreak);
    }

    private int calculateCurrentStreak(List<String> completions) {
        if (completions.isEmpty()) {
            return 0;
        }

        int streak = 1;
        for (int i = completions.size() - 1; i > 0; i--) {
            String current = completions.get(i);
            String previous = completions.get(i - 1);
            if (isPreviousDay(previous, current)) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    private boolean isPreviousDay(String prevDate, String currentDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date prev = sdf.parse(prevDate);
            Date current = sdf.parse(currentDate);
            long diff = current.getTime() - prev.getTime();
            return diff == 24 * 60 * 60 * 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
