package com.example.tipcalculator2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher, SeekBar.OnSeekBarChangeListener {
    private EditText editTextBillAmount;
    private TextView textViewBillAmount;
    private TextView textViewTipAmount;
    private TextView textViewTotalAmount;
    private TextView textViewTip_Percent;
    private TextView textViewPerPersonTotal;
    private Spinner spinnerPeople;
    private RadioGroup radioGroupRoundingOptions;

    private double billAmount = 0;
    private double percent = 0.15;

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextBillAmount = findViewById(R.id.editText_BillAmount);
        textViewBillAmount = findViewById(R.id.textView_BillAmount);
        textViewTipAmount = findViewById(R.id.textView_TipAmount);
        textViewTotalAmount = findViewById(R.id.textView_TotalAmount);
        textViewTip_Percent = findViewById(R.id.textViewTip_Percent);
        textViewPerPersonTotal = findViewById(R.id.textView_perPersonTotal);
        spinnerPeople = findViewById(R.id.spinner_people);
        radioGroupRoundingOptions = findViewById(R.id.radioGroup_roundingOptions);

        editTextBillAmount.addTextChangedListener(this);

        SeekBar seekBarTipPercentage = findViewById(R.id.seekBar_TipPercentage);
        seekBarTipPercentage.setOnSeekBarChangeListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.people_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeople.setAdapter(adapter);

        radioGroupRoundingOptions.setOnCheckedChangeListener((group, checkedId) -> calculate());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        try {
            billAmount = Double.parseDouble(charSequence.toString());
        } catch (NumberFormatException e) {
            billAmount = 0;
        }

        textViewBillAmount.setText(currencyFormat.format(billAmount));
        calculate();
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        percent = progress / 100.0;
        textViewTip_Percent.setText(progress + "%");
        calculate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void calculate() {
        double tip = billAmount * percent;
        double total = billAmount + tip;

        int numPeople = Integer.parseInt(spinnerPeople.getSelectedItem().toString());
        double perPersonTotal = total / numPeople;

        int checkedRadioButtonId = radioGroupRoundingOptions.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.radioButton_roundTip) {
            tip = Math.ceil(tip);
        } else if (checkedRadioButtonId == R.id.radioButton_roundTotal) {
            total = Math.ceil(total);
            perPersonTotal = total / numPeople;
        }

        textViewTipAmount.setText(currencyFormat.format(tip));
        textViewTotalAmount.setText(currencyFormat.format(total));
        textViewPerPersonTotal.setText(currencyFormat.format(perPersonTotal));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            // Create the message string with the bill, tip, total, and per person information
            String message = "Bill Amount: " + currencyFormat.format(billAmount) + "\n" +
                    "Tip Amount: " + textViewTipAmount.getText().toString() + "\n" +
                    "Total Amount: " + textViewTotalAmount.getText().toString() + "\n" +
                    "Per Person Total: " + textViewPerPersonTotal.getText().toString();

            // Create an intent to send the message
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Share via"));

            return true;
        } else if (id == R.id.action_info) {
            // Handle the "Info" menu item click
            showInfoDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Information")
                .setMessage("The dropdown menu is used to split the total among your friends.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Do something when OK is clicked
                })
                .show();
    }
}
