package com.sajjadamin.outgoings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

public class LoginActivity extends AppCompatActivity {
    private Button loginbutton;
    private EditText usernameEt, passwordEt;
    private String login_username, login_password;
    private Helper helper;
    TextView signup_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        helper = new Helper(this);
        signup_text = findViewById(R.id.signup_text);
        loginbutton = findViewById(R.id.login_button);
        usernameEt = findViewById(R.id.login_username);
        passwordEt = findViewById(R.id.login_password);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_username = usernameEt.getText().toString();
                login_password = passwordEt.getText().toString();
                if (helper.connectionCheck()){
                    new sendHttpRequest().execute();
                }else {
                    Toast.makeText(LoginActivity.this,"You are currently offline, please turn on internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
        signup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
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
                        .appendQueryParameter("action", "login")
                        .appendQueryParameter("username", login_username)
                        .appendQueryParameter("password", login_password);
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
            String user_id = null, firstname = null, lastname = null, email = null, phone = null, reg_date = null;
            if (!s.equals("failed")){
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    user_id = jsonObject.getString("id");
                    firstname = jsonObject.getString("firstname");
                    lastname = jsonObject.getString("lastname");
                    email = jsonObject.getString("email");
                    phone = jsonObject.getString("phone");
                    reg_date = jsonObject.getString("reg_date");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences sharedPreferences = getSharedPreferences("session_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_id", user_id);
                editor.putString("firstname", firstname);
                editor.putString("lastname", lastname);
                editor.putString("email", email);
                editor.putString("phone", phone);
                editor.putString("reg_date", reg_date);
                editor.putString("password", login_password);
                editor.apply();
                finish();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }else {
                Toast.makeText(LoginActivity.this,"Login failed, make sure that username and password is correct",Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(s);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
