package com.sajjadamin.outgoings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Objects;

public class EditBudgetActivity extends AppCompatActivity {

    private String budget_id, budget_title, budget_description, budget_date, budget_amount;
    private EditText updateDateEt, updateTitleEt, updateDescriptionEt, updateAmountEt;
    private Button updateCancel, update_btn;
    private DatePickerDialog datePickerDialog;
    private Toolbar toolbar;
    String updateDate, updateTitle, updateDescription, updateAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        //get budget data
        budget_id = getIntent().getStringExtra("id");
        budget_title = getIntent().getStringExtra("title");
        budget_description = getIntent().getStringExtra("description");
        budget_date = getIntent().getStringExtra("date");
        budget_amount = getIntent().getStringExtra("amount");

        //helper class
        final Helper helper = new Helper(EditBudgetActivity.this);

        //setup toolbar
        toolbar = findViewById(R.id.edit_budget_activity_toolbar);
        toolbar.setTitle("Edit "+budget_title);
        setSupportActionBar(toolbar);

        //initialize variables
        updateDateEt = findViewById(R.id.edit_budget_date);
        updateTitleEt = findViewById(R.id.edit_budget_title);
        updateDescriptionEt = findViewById(R.id.edit_budget_description);
        updateAmountEt = findViewById(R.id.edit_budget_amount);
        updateCancel = findViewById(R.id.cancel_edit_budget_button);
        update_btn = findViewById(R.id.update_budget_button);

        //set data into edit text
        updateDateEt.setText(budget_date);
        updateTitleEt.setText(budget_title);
        updateDescriptionEt.setText(budget_description);
        updateAmountEt.setText(budget_amount);

        //adding listeners
        updateDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int date = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(EditBudgetActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        String stringYear,stringMonth,stringDate,fulldate;
                        stringYear = Integer.toString(i);
                        if (i1 < 9){
                            stringMonth = Integer.toString(i1+1);
                            stringMonth = "0"+stringMonth;
                        }else {
                            stringMonth = Integer.toString(i1+1);
                        }
                        if (i2 < 9){
                            stringDate = Integer.toString(i2);
                            stringDate = "0"+stringDate;
                        }else {
                            stringDate = Integer.toString(i2);
                        }
                        fulldate = stringYear+"-"+stringMonth+"-"+stringDate;
                        updateDateEt.setText(fulldate);
                    }
                },year,month,date);
                datePickerDialog.show();
            }
        });
        updateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDate = updateDateEt.getText().toString();
                updateTitle = updateTitleEt.getText().toString();
                updateDescription = updateDescriptionEt.getText().toString();
                updateAmount = updateAmountEt.getText().toString();
                if (helper.connectionCheck()){
                    if (!updateDate.equals("") && !updateTitle.equals("") && !updateDescription.equals("") && !updateAmount.equals("")){
                        new sendHttpRequest().execute();
                    }else {
                        Toast.makeText(EditBudgetActivity.this,"Fields must not be empty",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(EditBudgetActivity.this,"You are currently offline, please turn on internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class sendHttpRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://outgoings.sajjadamin.com/api.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("action", "update_budget")
                        .appendQueryParameter("user_id", new Helper(EditBudgetActivity.this).getSessionData("user_id"))
                        .appendQueryParameter("budget_id", budget_id)
                        .appendQueryParameter("password", new Helper(EditBudgetActivity.this).getSessionData("password"))
                        .appendQueryParameter("title", updateTitle)
                        .appendQueryParameter("description", updateDescription)
                        .appendQueryParameter("date", updateDate)
                        .appendQueryParameter("amount", updateAmount);
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                bufferedWriter.write(Objects.requireNonNull(builder.build().getEncodedQuery()));
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success")){
                finish();
                startActivity(new Intent(EditBudgetActivity.this,MainActivity.class));
            }else {
                Toast.makeText(EditBudgetActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
