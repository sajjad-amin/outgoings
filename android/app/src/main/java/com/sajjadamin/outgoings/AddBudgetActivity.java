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

public class AddBudgetActivity extends AppCompatActivity {

    private EditText newDate,newTitle, newDescription, newAmount;
    private Button newCancel,newAdd;
    private DatePickerDialog datePickerDialog;
    public int clickCount = 0;
    String sendDate, sendTitle, sendDescription, sendAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        newDate = findViewById(R.id.add_new_budget_date);
        newTitle = findViewById(R.id.add_new_budget_title);
        newDescription = findViewById(R.id.add_new_budget_description);
        newAmount = findViewById(R.id.add_new_budget_amount);
        newCancel = findViewById(R.id.cancel_new_budget_button);
        newAdd = findViewById(R.id.add_new_budget_button);

        newDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int date = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(AddBudgetActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                sendTitle = newTitle.getText().toString();
                sendDescription = newDescription.getText().toString();
                sendAmount = newAmount.getText().toString();
                if (new Helper(AddBudgetActivity.this).connectionCheck()){
                    if (!sendDate.equals("") && !sendTitle.equals("") && !sendDescription.equals("") && !sendAmount.equals("") && clickCount == 0){
                        clickCount += 1;
                        new sendHttpRequest().execute();
                    }else {
                        Toast.makeText(AddBudgetActivity.this,"Fields must not be empty",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    clickCount = 0;
                    Toast.makeText(AddBudgetActivity.this,"You are currently offline, please turn on internet",Toast.LENGTH_SHORT).show();
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
                        .appendQueryParameter("action", "create_budget")
                        .appendQueryParameter("user_id", new Helper(AddBudgetActivity.this).getSessionData("user_id"))
                        .appendQueryParameter("password", new Helper(AddBudgetActivity.this).getSessionData("password"))
                        .appendQueryParameter("title", sendTitle)
                        .appendQueryParameter("description", sendDescription)
                        .appendQueryParameter("date", sendDate)
                        .appendQueryParameter("amount", sendAmount);
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
                startActivity(new Intent(AddBudgetActivity.this,MainActivity.class));
            }else {
                clickCount = 0;
                Toast.makeText(AddBudgetActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
