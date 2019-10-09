package com.sajjadamin.outgoings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class AccountActivity extends AppCompatActivity {

    private TextView acc_name, acc_email, acc_phone, acc_regdate;
    private Button logout_btn, edit_btn;
    private Helper helper;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //initialize variables
        helper = new Helper(this);
        toolbar = findViewById(R.id.account_activity_toolbar);
        setSupportActionBar(toolbar);
        acc_name = findViewById(R.id.account_name);
        acc_email = findViewById(R.id.account_email);
        acc_phone = findViewById(R.id.account_phone);
        acc_regdate = findViewById(R.id.account_reg_date);
        edit_btn = findViewById(R.id.account_edit_btn);
        logout_btn = findViewById(R.id.account_logout_btn);
        String fullname = helper.getSessionData("firstname")+" "+helper.getSessionData("lastname");

        //set text into account details
        acc_name.setText(fullname);
        acc_email.setText(helper.getSessionData("email"));
        acc_phone.setText(helper.getSessionData("phone"));
        acc_regdate.setText(helper.getSessionData("reg_date"));

        //adding listener into buttons
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this,EditInfoActivity.class));
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("session_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", "");
        editor.putString("firstname", "");
        editor.putString("lastname", "");
        editor.putString("email", "");
        editor.putString("phone", "");
        editor.putString("reg_date", "");
        editor.putString("password", "");
        startActivity(new Intent(AccountActivity.this,LoginActivity.class));
        editor.apply();
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.account_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_account:
                AlertDialog.Builder alb = new AlertDialog.Builder(AccountActivity.this);
                alb.setTitle("Delete Account");
                alb.setMessage("Do you want to delete this account ?");
                alb.setIcon(R.drawable.delete);
                alb.setCancelable(false);
                alb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (helper.connectionCheck()){
                            new sendHttpRequest().execute();
                        }else {
                            Toast.makeText(AccountActivity.this,"You are currently offline, please turn on internet",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alertDialog = alb.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class sendHttpRequest extends AsyncTask<String, String, String> {
        Helper helper = new Helper(AccountActivity.this);
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://outgoings.sajjadamin.com/api.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("action", "delete_profile")
                        .appendQueryParameter("user_id", helper.getSessionData("user_id"))
                        .appendQueryParameter("password", helper.getSessionData("password"));
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
                logout();
            }else {
                Toast.makeText(AccountActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
