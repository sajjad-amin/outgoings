package com.sajjadamin.outgoings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

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

public class AddDataActivity extends AppCompatActivity {

    private EditText newDate, newAmount, newDescription;
    private Button newCancel,newAdd;
    private DatePickerDialog datePickerDialog;
    public int clickCount = 0;
    String budget_id, budget_title, budget_date, budget_amount, budget_description, sendDate, sendAmount, sendDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        //get intent data
        budget_id = getIntent().getStringExtra("budget_id");
        budget_title = getIntent().getStringExtra("budget_title");
        budget_date = getIntent().getStringExtra("budget_date");
        budget_amount = getIntent().getStringExtra("budget_amount");
        budget_description = getIntent().getStringExtra("budget_description");

        //initialize edit text
        newDate = findViewById(R.id.add_new_data_date);
        newDescription = findViewById(R.id.add_new_data_description);
        newAmount = findViewById(R.id.add_new_data_amount);
        newCancel = findViewById(R.id.cancel_new_data_button);
        newAdd = findViewById(R.id.add_new_data_button);

        //adding listener
        newDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int date = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(AddDataActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        newDate.setText(fulldate);
                    }
                },year,month,date);
                datePickerDialog.show();
            }
        });
        newDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                int calculatedData = new Helper(AddDataActivity.this).autoAddition(editable.toString());
                if (calculatedData != 0){
                    String newAmountText = Integer.toString(calculatedData);
                    newAmount.setText(newAmountText);
                }else{
                    newAmount.setText("");
                }
            }
        });
        newCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        newAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDate = newDate.getText().toString();
                sendDescription = newDescription.getText().toString();
                sendAmount = newAmount.getText().toString();
                if (new Helper(AddDataActivity.this).connectionCheck()){
                    if (!sendDate.equals("") && !sendDescription.equals("") && !sendAmount.equals("") && clickCount == 0){
                        clickCount += 1;
                        new sendHttpRequest().execute();
                    }else {
                        Toast.makeText(AddDataActivity.this,"Fields must not be empty",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    clickCount = 0;
                    Toast.makeText(AddDataActivity.this,"You are currently offline, please turn on internet",Toast.LENGTH_SHORT).show();
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
                        .appendQueryParameter("action", "create_data")
                        .appendQueryParameter("user_id", new Helper(AddDataActivity.this).getSessionData("user_id"))
                        .appendQueryParameter("budget_id", budget_id)
                        .appendQueryParameter("password", new Helper(AddDataActivity.this).getSessionData("password"))
                        .appendQueryParameter("date", sendDate)
                        .appendQueryParameter("amount", sendAmount)
                        .appendQueryParameter("description", sendDescription.replaceAll("\n",", "));
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
                Intent i = new Intent(AddDataActivity.this,BudgetActivity.class);
                i.putExtra("id", budget_id);
                i.putExtra("title", budget_title);
                i.putExtra("date", budget_date);
                i.putExtra("amount", budget_amount);
                i.putExtra("description", budget_description);
                startActivity(i);
            }else {
                clickCount = 0;
                Toast.makeText(AddDataActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
