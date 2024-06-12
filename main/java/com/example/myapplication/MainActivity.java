package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView eventsRecyclerView;
    private FloatingActionButton addEventButton;
    private DatabaseHelper dbHelper;
    private EventAdapter eventAdapter;
    private List<CalendarEvent> events;
    private TextView eventsLabel;
    private String selectedDate;

    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        calendarView = findViewById(R.id.calendarView);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        addEventButton = findViewById(R.id.addEventButton);
        eventsLabel = findViewById(R.id.eventsLabel);
        dbHelper = new DatabaseHelper(this);

        events = new ArrayList<>();
        eventAdapter = new EventAdapter(this, events, this::deleteEvent);
        eventsRecyclerView.setAdapter(eventAdapter);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addEventButton.setOnClickListener(v -> showAddEventDialog());

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(clickedDayCalendar.getTime());
            showDateOptionsDialog(selectedDate);
        });

        // Load events for today's date by default
        Calendar today = Calendar.getInstance();
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today.getTime());
        loadEventsForDate(selectedDate);
        highlightDatesWithColorsAndTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always reload events for the selected date when resuming the activity
        loadEventsForDate(selectedDate);
        highlightDatesWithColorsAndTasks();
    }

    public static void updateCalendar() {
        if (instance != null) {
            instance.highlightDatesWithColorsAndTasks();
        }
    }

    private void loadEventsForDate(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_EVENTS, null, DatabaseHelper.COLUMN_EVENT_DATE + " = ?", new String[]{date}, null, null, null);

        events.clear();
        while (cursor.moveToNext()) {
            String eventName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_NAME));
            int eventColor = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_COLOR));
            events.add(new CalendarEvent(eventName, eventColor, date));
        }
        cursor.close();
        eventAdapter.notifyDataSetChanged();
        eventsLabel.setVisibility(events.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void showDateOptionsDialog(String selectedDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите действие");
        builder.setItems(new CharSequence[]{"Добавить задачу", "Отметить цветом"}, (dialog, which) -> {
            if (which == 0) {
                navigateToTaskActivity(selectedDate);
            } else if (which == 1) {
                showColorPickerDialogForDate(selectedDate);
            }
        });
        builder.show();
    }

    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить событие");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_event, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            EditText eventNameInput = customLayout.findViewById(R.id.eventNameInput);
            String eventName = eventNameInput.getText().toString();
            if (!eventName.isEmpty()) {
                showColorPickerDialogForEvent(eventName);
            } else {
                Toast.makeText(this, "Введите название события", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showColorPickerDialogForEvent(String eventName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите цвет");

        final View colorPickerView = getLayoutInflater().inflate(R.layout.color_picker_dialog, null);
        builder.setView(colorPickerView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            GridView gridView = colorPickerView.findViewById(R.id.colorGrid);
            Integer selectedColor = (Integer) gridView.getTag();
            if (selectedColor != null) {
                if (dbHelper.isColorUsedForEvent(selectedColor)) {
                    Toast.makeText(this, "Этот цвет уже используется для другого события", Toast.LENGTH_SHORT).show();
                } else {
                    saveEvent(eventName, selectedColor);
                }
            } else {
                Toast.makeText(this, "Выберите цвет", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        GridView gridView = colorPickerView.findViewById(R.id.colorGrid);
        gridView.setAdapter(new ColorPickerAdapter(this, getResources().getIntArray(R.array.default_color_picker_colors)));
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int color = (int) view.getTag();
            gridView.setTag(color);
        });
    }

    private void showColorPickerDialogForDate(String selectedDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите цвет для даты");

        final View colorPickerView = getLayoutInflater().inflate(R.layout.color_picker_dialog, null);
        builder.setView(colorPickerView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            GridView gridView = colorPickerView.findViewById(R.id.colorGrid);
            Integer selectedColor = (Integer) gridView.getTag();
            if (selectedColor != null) {
                dbHelper.addDateColor(selectedDate, selectedColor);
                highlightDatesWithColorsAndTasks(); // Обновление календаря
            } else {
                Toast.makeText(this, "Выберите цвет", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        GridView gridView = colorPickerView.findViewById(R.id.colorGrid);
        gridView.setAdapter(new ColorPickerAdapter(this, getResources().getIntArray(R.array.default_color_picker_colors)));
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int color = (int) view.getTag();
            gridView.setTag(color);
        });
    }

    private void highlightDatesWithColorsAndTasks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<EventDay> events = new ArrayList<>();
        Map<String, Integer> dateColors = new HashMap<>();
        Map<String, Boolean> dateHasTask = new HashMap<>();

        // Fetch date colors
        Cursor colorCursor = db.query(DatabaseHelper.TABLE_DATE_COLORS, null, null, null, null, null, null);
        while (colorCursor.moveToNext()) {
            String date = colorCursor.getString(colorCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
            int color = colorCursor.getInt(colorCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COLOR));

            dateColors.put(date, color);
        }
        colorCursor.close();

        // Fetch task dates
        Cursor taskCursor = db.query(DatabaseHelper.TABLE_TASKS, new String[]{DatabaseHelper.COLUMN_TASK_DATE}, null, null, null, null, null);
        while (taskCursor.moveToNext()) {
            String date = taskCursor.getString(taskCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_DATE));

            dateHasTask.put(date, true);
        }
        taskCursor.close();

        // Combine date colors and task dates
        for (String date : dateColors.keySet()) {
            String[] parts = date.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int day = Integer.parseInt(parts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            Drawable colorDrawable = new ColorDrawable(dateColors.get(date));
            Drawable taskDrawable = dateHasTask.containsKey(date) ? ContextCompat.getDrawable(this, R.drawable.ic_task_marker) : null;

            if (taskDrawable != null) {
                Drawable combinedDrawable = combineDrawables(colorDrawable, taskDrawable);
                events.add(new EventDay(calendar, combinedDrawable));
            } else {
                events.add(new EventDay(calendar, colorDrawable));
            }
        }

        // Add task-only dates
        for (String date : dateHasTask.keySet()) {
            if (!dateColors.containsKey(date)) {
                String[] parts = date.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int day = Integer.parseInt(parts[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                Drawable taskDrawable = ContextCompat.getDrawable(this, R.drawable.ic_task_marker);
                events.add(new EventDay(calendar, taskDrawable));
            }
        }

        calendarView.setEvents(events);
    }

    private Drawable combineDrawables(Drawable background, Drawable foreground) {
        LayerDrawable combined = new LayerDrawable(new Drawable[]{background, foreground});
        combined.setLayerInset(1, 10, 10, 10, 10); // Adjust padding as needed
        return combined;
    }

    private void saveEvent(String eventName, int color) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EVENT_NAME, eventName);
        values.put(DatabaseHelper.COLUMN_EVENT_COLOR, color);
        values.put(DatabaseHelper.COLUMN_EVENT_DATE, selectedDate);

        long newRowId = db.insert(DatabaseHelper.TABLE_EVENTS, null, values);
        if (newRowId != -1) {
            loadEventsForDate(selectedDate);
            highlightDatesWithColorsAndTasks();
            Toast.makeText(this, "Событие добавлено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка при добавлении события", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToTaskActivity(String selectedDate) {
        Intent intent = new Intent(MainActivity.this, TaskActivity.class);
        intent.putExtra("selectedDate", selectedDate);
        startActivity(intent);
    }

    private void deleteEvent(String eventName) {
        if (dbHelper.deleteEvent(eventName)) {
            loadEventsForDate(selectedDate);
            highlightDatesWithColorsAndTasks();
            Toast.makeText(this, "Событие удалено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка при удалении события", Toast.LENGTH_SHORT).show();
        }
    }
}

