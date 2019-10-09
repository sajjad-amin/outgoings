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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class SignupActivity extends AppCompatActivity {
    private EditText firstnameEt, lastnameEt, emailEt, phoneEt, usernameEt, passwordEt;
    private String firstname, lastname, email, phone, username, password;
    private TextView login_textview;
    private Button signupButton;
    private Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //variable initialization
        //edit texts
        firstnameEt = findViewById(R.id.signup_firstname);
        lastnameEt = findViewById(R.id.signup_lastname);
        emailEt = findViewById(R.id.signup_email);
        phoneEt = findViewById(R.id.signup_phone);
        usernameEt = findViewById(R.id.signup_username);
        passwordEt = findViewById(R.id.signup_password);
        //text view
        login_textview = findViewById(R.id.login_text);
        //button
        signupButton = findViewById(R.id.signup_button);
        //helper
        helper = new Helper(this);
        //set onclick listener
        login_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname = firstnameEt.getText().toString();
                lastname = lastnameEt.getText().toString();
                email = emailEt.getText().toString();
                phone = phoneEt.getText().toString();
                username = usernameEt.getText().toString();
                password = passwordEt.getText().toString();
                if (helper.connectionCheck()){
                    if (!firstname.equals("") && !lastname.equals("") && !email.equals("") && !phone.equals("") && !username.equals("") && !password.equals("")){
                        new sendHttpRequest().execute();
                    }else {
                        Toast.makeText(SignupActivity.this,"Please fill out all field",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(SignupActivity.this,"You are currently offline, please turn on internet",Toast.LENGTH_SHORT).show();
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
                        .appendQueryParameter("action", "signup")
                        .appendQueryParameter("firstname", firstname)
                        .appendQueryParameter("lastname", lastname)
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("phone", phone)
                        .appendQueryParameter("username", username)
                        .appendQueryParameter("password", password);
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
            switch (s){
                case "success":
                    AlertDialog.Builder alb = new AlertDialog.Builder(SignupActivity.this);
                    alb.setTitle("Done");
                    alb.setMessage("Account created successfully !\nClick Login and log into your new account");
                    alb.setIcon(R.drawable.ok);
                    alb.setCancelable(false);
                    alb.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                        }
                    });
                    AlertDialog alertDialog = alb.create();
                    alertDialog.show();
                    break;
                case "usernameexists":
                    Toast.makeText(SignupActivity.this,"Username already exists, try a different username",Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(SignupActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.onPostExecute(s);
        }
    }
}
