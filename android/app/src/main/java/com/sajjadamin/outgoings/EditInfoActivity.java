package com.sajjadamin.outgoings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.util.Objects;

public class EditInfoActivity extends AppCompatActivity {
    private Helper helper;
    private Toolbar toolbar;
    Button cancel_btn, update_btn;
    private EditText firstnameEt, lastnameEt, emailEt, phoneEt, passwordEt;
    private String firstname, lastname, email, phone, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        helper = new Helper(this);
        toolbar = findViewById(R.id.edit_account_toolbar);
        toolbar.setTitle("Edit Account Info");
        setSupportActionBar(toolbar);

        //initializing variables
        firstnameEt = findViewById(R.id.edit_account_firstname);
        lastnameEt = findViewById(R.id.edit_account_lastname);
        emailEt = findViewById(R.id.edit_account_email);
        phoneEt = findViewById(R.id.edit_account_phone);
        passwordEt = findViewById(R.id.edit_account_password);
        cancel_btn = findViewById(R.id.edit_account_cancel);
        update_btn = findViewById(R.id.edit_account_update);

        //set current info into edit text
        firstnameEt.setText(helper.getSessionData("firstname"));
        lastnameEt.setText(helper.getSessionData("lastname"));
        emailEt.setText(helper.getSessionData("email"));
        phoneEt.setText(helper.getSessionData("phone"));
        passwordEt.setText(helper.getSessionData("password"));

        //add onclick listener into buttons
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname = firstnameEt.getText().toString();
                lastname = lastnameEt.getText().toString();
                email = emailEt.getText().toString();
                phone = phoneEt.getText().toString();
                password = passwordEt.getText().toString();
                if (helper.connectionCheck()){
                    new sendHttpRequest().execute();
                }else {
                    Toast.makeText(EditInfoActivity.this,"You are currently offline, please turn on internet",Toast.LENGTH_SHORT).show();
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
                        .appendQueryParameter("action", "update_profile")
                        .appendQueryParameter("user_id", helper.getSessionData("user_id"))
                        .appendQueryParameter("password", helper.getSessionData("password"))
                        .appendQueryParameter("firstname", firstname)
                        .appendQueryParameter("lastname", lastname)
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("phone", phone)
                        .appendQueryParameter("new_password", password);
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
                SharedPreferences sharedPreferences = getSharedPreferences("session_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("firstname", firstname);
                editor.putString("lastname", lastname);
                editor.putString("email", email);
                editor.putString("phone", phone);
                editor.putString("password", password);
                editor.apply();
                finish();
                startActivity(new Intent(EditInfoActivity.this,AccountActivity.class));
            }else {
                Toast.makeText(EditInfoActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
