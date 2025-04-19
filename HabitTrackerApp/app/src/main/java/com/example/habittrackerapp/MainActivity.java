package com.example.habittrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements HabitAdapter.OnHabitClickListener {

    private RecyclerView recyclerViewHabits;
    private HabitAdapter habitAdapter;
    private HabitDbHelper dbHelper;
    private int userId;
    private TextView textViewWelcome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWelcome = findViewById(R.id.textViewWelcome);
        recyclerViewHabits = findViewById(R.id.recyclerViewHabits);
        recyclerViewHabits.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new HabitDbHelper(this);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = dbHelper.getUserId(username);
        textViewWelcome.setText("Welcome, " + username);

        loadHabits();
    }

    private void loadHabits() {
        List<Habit> habits = dbHelper.getHabits(userId);
        habitAdapter = new HabitAdapter(habits, this, dbHelper);
        recyclerViewHabits.setAdapter(habitAdapter);
    }

    @Override
    public void onHabitClick(Habit habit) {
        Intent intent = new Intent(MainActivity.this, HabitDetailActivity.class);
        intent.putExtra("habitId", habit.getId());
        intent.putExtra("habitName", habit.getName());
        startActivity(intent);
    }
}
