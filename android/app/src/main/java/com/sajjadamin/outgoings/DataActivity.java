package com.sajjadamin.outgoings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
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
import java.util.Objects;

public class DataActivity extends AppCompatActivity {
    String id, date, amount, description, send_quee;
    TextView dateTv, amountTv, descriptionTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        showData();

        send_quee = "Date : "+date+"\nAmount : "+amount+"\nDescription : "+description+"\n\n";

        Toolbar toolbar = findViewById(R.id.data_activity_toolber);
        toolbar.setTitle(date);
        setSupportActionBar(toolbar);
    }

    private void showData() {
        dateTv = findViewById(R.id.data_activity_date);
        amountTv = findViewById(R.id.data_activity_amount);
        descriptionTv = findViewById(R.id.data_activity_description);

        id = getIntent().getStringExtra("id");
        date = getIntent().getStringExtra("date");
        amount = getIntent().getStringExtra("amount");
        description = getIntent().getStringExtra("description").replaceAll(", ","\n");

        dateTv.setText(date);
        amountTv.setText(amount);
        descriptionTv.setText(description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.data_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_record:
                if (new Helper(this).connectionCheck()){
                    AlertDialog.Builder alb = new AlertDialog.Builder(DataActivity.this);
                    alb.setTitle("Delete Record");
                    alb.setMessage("Do you want to delete this record ?");
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
                            new sendDataDeleteRequest().execute();
                        }
                    });
                    AlertDialog alertDialog = alb.create();
                    alertDialog.show();
                    break;
                }else {
                    Toast.makeText(this,"You are in offline, please turn on internet",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edit_record:
                if (new Helper(this).connectionCheck()){
                    finish();
                    Intent i = new Intent(this,EditDataActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("date", date);
                    i.putExtra("amount", amount);
                    i.putExtra("description", description);
                    startActivity(i);
                }else {
                    Toast.makeText(this,"You are in offline, please turn on internet",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.send_record:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, send_quee);
                startActivity(Intent.createChooser(sharingIntent, "Send using..."));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class sendDataDeleteRequest extends AsyncTask<String, String, String> {
        Helper helper = new Helper(DataActivity.this);
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://outgoings.sajjadamin.com/api.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("action", "delete_data")
                        .appendQueryParameter("user_id", helper.getSessionData("user_id"))
                        .appendQueryParameter("data_id", id)
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
                finish();
                Toast.makeText(DataActivity.this,"Deleted successfully!\nThis record will terminate after reload.",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(DataActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
