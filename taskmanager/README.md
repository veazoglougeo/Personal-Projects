# 📌 Task Management System - MediaLab Assistant

This project is a **Task Management System**, developed in Java using **JavaFX**. It enables users to create, organize, and manage personal tasks, with support for reminders, deadlines, priorities, and task categories. The application stores data in **JSON format**, ensuring persistence between sessions.

## 🎯 Features

### ✅ Task Management
- Add, edit, and delete tasks
- Each task has: title, description, category, priority, deadline (date only), and status
- Supported statuses: `Open`, `In Progress`, `Postponed`, `Completed`, `Delayed`
- Automatic status update to `Delayed` if a deadline is missed
- Prevent setting reminders on completed tasks

### 📂 Category Management
- Create, rename, and delete categories
- Deleting a category also deletes its tasks
- Associated reminders are also cleaned up

### 🔺 Priority Management
- Default priority level: `Default`
- Create, rename, and delete custom priority levels
- Tasks with deleted priority fallback to `Default`

### ⏰ Reminder System
- Link reminders to tasks (multiple allowed per task)
- Types:
  - One day before deadline
  - One week before deadline
  - One month before deadline
  - Specific date (set by user)
- Validation to ensure reminder timing makes sense
- All reminders deleted when task is marked `Completed`

### 🔍 Task Search
- Search by: title, category, and priority (any combination)
- Results show: title, priority, category, deadline

## 💾 Data Persistence

- All data is stored in JSON files inside the `/medialab` directory
- At startup: application loads all data from JSON
- During usage: data is kept in memory for speed
- At shutdown: all updates are saved back to JSON files

## 🖥️ GUI with JavaFX

The interface includes:
- A main window titled **"MediaLab Assistant"**
- Top section shows:
  - Total number of tasks
  - Number of completed tasks
  - Number of delayed tasks
  - Tasks due within the next 7 days
- Main section for full functionality:
  - Manage tasks by category
  - Create/edit/delete categories
  - Manage priority levels
  - Create/edit/delete reminders
  - Search functionality

