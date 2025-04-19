# Habit Tracker Android App

This is a Habit Tracker Android application built using Java and SQLite. It includes user authentication (login/signup), habit tracking with daily completion logging via a calendar, and displays the highest streak for each habit.

## Features
- User login and signup with SQLite authentication
- Add, view, update, and delete habits
- Calendar view for each habit to log daily completion
- Display highest streak for each habit on the home page
- User session management

## Project Structure
- LoginActivity.java
- SignupActivity.java
- MainActivity.java
- HabitDetailActivity.java
- HabitDbHelper.java (SQLite database helper)
- User.java, Habit.java, HabitCompletion.java (model classes)
- XML layouts for activities and habit list items

## How to Run
1. Open the project in Android Studio.
2. Build and run the app on an emulator or physical device.
3. Use the login/signup screens to create and access your account.
4. Add habits and log daily completions via the calendar.
