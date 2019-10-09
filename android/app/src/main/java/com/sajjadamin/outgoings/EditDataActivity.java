package com.sajjadamin.outgoings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class EditDataActivity extends AppCompatActivity {

    private EditText updateDateEt, updateAmountEt, updateDescriptionEt;
    private Button updateCancel, update_btn;
    private DatePickerDialog datePickerDialog;
    private Toolbar toolbar;
    private String id, date, amount, desciption;
    String updateDate, updateAmount, updateDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        //get budget data
        id = getIntent().getStringExtra("id");
        date = getIntent().getStringExtra("date");
        amount = getIntent().getStringExtra("amount");
        desciption = getIntent().getStringExtra("description");

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.edit_data_activity_toolbar);
        toolbar.setTitle("Edit "+date);
        setSupportActionBar(toolbar);

        //initialize variables
        updateDateEt = findViewById(R.id.edit_data_date);
        updateAmountEt = findViewById(R.id.edit_data_amount);
        updateDescriptionEt = findViewById(R.id.edit_data_description);
        updateCancel = findViewById(R.id.cancel_edit_data_button);
        update_btn = findViewById(R.id.update_data_button);

        //set data into edit text
        updateDateEt.setText(date);
        updateAmountEt.setText(amount);
        updateDescriptionEt.setText(desciption);

        //set listener
        updateDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int date = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(EditDataActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        updateDescriptionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                int calculatedData = new Helper(EditDataActivity.this).autoAddition(editable.toString());
                if (calculatedData != 0){
                    String newAmountText = Integer.toString(calculatedData);
                    updateAmountEt.setText(newAmountText);
                }else{
                    updateAmountEt.setText("");
                }
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
                updateDescription = updateDescriptionEt.getText().toString();
                updateAmount = updateAmountEt.getText().toString();
                if (new Helper(EditDataActivity.this).connectionCheck()){
                    if (!updateDate.equals("") && !updateDescription.equals("") && !updateAmount.equals("")){
                        new sendHttpRequest().execute();
                    }else {
                        Toast.makeText(EditDataActivity.this,"Fields must not be empty",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(EditDataActivity.this,"You are currently offline, please turn on internet",Toast.LENGTH_SHORT).show();
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
                        .appendQueryParameter("action", "update_data")
                        .appendQueryParameter("user_id", new Helper(EditDataActivity.this).getSessionData("user_id"))
                        .appendQueryParameter("data_id", id)
                        .appendQueryParameter("password", new Helper(EditDataActivity.this).getSessionData("password"))
                        .appendQueryParameter("date", updateDate)
                        .appendQueryParameter("amount", updateAmount)
                        .appendQueryParameter("description", updateDescription.replaceAll("\n",", "));
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
                Intent i = new Intent(EditDataActivity.this,DataActivity.class);
                i.putExtra("id", id);
                i.putExtra("date", updateDate);
                i.putExtra("amount", updateAmount);
                i.putExtra("description", updateDescription);
                startActivity(i);
            }else {
                Toast.makeText(EditDataActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
