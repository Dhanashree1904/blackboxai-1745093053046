package com.example.habittrackerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList;
    private OnHabitClickListener listener;
    private HabitDbHelper dbHelper;

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
    }

    public HabitAdapter(List<Habit> habitList, OnHabitClickListener listener, HabitDbHelper dbHelper) {
        this.habitList = habitList;
        this.listener = listener;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.textViewHabitName.setText(habit.getName());

        // Calculate highest streak for the habit
        int highestStreak = calculateHighestStreak(habit.getId());
        holder.textViewHighestStreak.setText("Highest Streak: " + highestStreak);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitClick(habit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHabitName, textViewHighestStreak;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHabitName = itemView.findViewById(R.id.textViewHabitName);
            textViewHighestStreak = itemView.findViewById(R.id.textViewHighestStreak);
        }
    }

    private int calculateHighestStreak(int habitId) {
        // Get all completion dates for the habit
        List<String> completionDates = dbHelper.getHabitCompletions(habitId);
        if (completionDates.isEmpty()) {
            return 0;
        }

        int highestStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < completionDates.size(); i++) {
            String prevDate = completionDates.get(i - 1);
            String currentDate = completionDates.get(i);

            // Check if currentDate is exactly one day after prevDate
            if (isNextDay(prevDate, currentDate)) {
                currentStreak++;
            } else {
                if (currentStreak > highestStreak) {
                    highestStreak = currentStreak;
                }
                currentStreak = 1;
            }
        }
        if (currentStreak > highestStreak) {
            highestStreak = currentStreak;
        }
        return highestStreak;
    }

    private boolean isNextDay(String prevDate, String currentDate) {
        // Dates are in yyyy-MM-dd format
        // Simple date comparison to check if currentDate is one day after prevDate
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date prev = sdf.parse(prevDate);
            java.util.Date current = sdf.parse(currentDate);
            long diff = current.getTime() - prev.getTime();
            return diff == 24 * 60 * 60 * 1000;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
