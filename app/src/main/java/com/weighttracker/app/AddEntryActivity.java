package com.weighttracker.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for adding a new weight entry.
 * Allows the user to pick a date, enter a weight, and save it.
 */
public class AddEntryActivity extends AppCompatActivity {

    private TextView tvDate;
    private EditText etWeight;
    private TextView tvWeightUnit;
    private TextView tvError;
    private androidx.cardview.widget.CardView cardTip;

    private DataStore dataStore;
    private Date selectedDate;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    private static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        dataStore = DataStore.getInstance();

        // Bind views
        tvDate = findViewById(R.id.tv_date);
        etWeight = findViewById(R.id.et_weight);
        tvWeightUnit = findViewById(R.id.tv_weight_unit);
        tvError = findViewById(R.id.tv_error);
        cardTip = findViewById(R.id.card_tip);

        // Set default date to today
        selectedDate = new Date();
        tvDate.setText(DATE_FORMAT.format(selectedDate));

        // Set unit label
        tvWeightUnit.setText(dataStore.isMetric() ? "kg" : "lbs");

        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Cancel button
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());

        // Date picker click
        findViewById(R.id.tv_date).setOnClickListener(v -> openDatePicker());
        findViewById(R.id.iv_calendar_icon).setOnClickListener(v -> openDatePicker());

        // Save button
        findViewById(R.id.btn_save).setOnClickListener(v -> saveEntry());

        // Tip card dismiss
        findViewById(R.id.btn_got_it).setOnClickListener(v ->
                cardTip.setVisibility(View.GONE));

        // Progress photo button (placeholder for Week 8)
        findViewById(R.id.btn_add_photo).setOnClickListener(v -> {
            // Camera functionality will be implemented in Week 8
        });
    }

    /**
     * Opens an Android DatePickerDialog for the user to select a date.
     */
    private void openDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    selectedDate = selected.getTime();
                    tvDate.setText(DATE_FORMAT.format(selectedDate));
                    // Clear any date error
                    tvError.setVisibility(View.GONE);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    /**
     * Validates input and saves the weight entry to the DataStore.
     */
    private void saveEntry() {
        String weightStr = etWeight.getText().toString().trim();

        // Validate weight
        if (weightStr.isEmpty()) {
            showError("Weight cannot be empty.");
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightStr);
            if (weight <= 0) {
                showError("Please enter a valid weight value.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid weight value.");
            return;
        }

        // Validate date
        if (selectedDate == null) {
            showError("Please select a valid date.");
            return;
        }

        // Create and save entry
        WeightEntry entry = new WeightEntry(selectedDate, weight, dataStore.isMetric());
        dataStore.addEntry(entry);

        // Navigate back to main screen
        finish();
    }

    /**
     * Displays a validation error message.
     */
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
