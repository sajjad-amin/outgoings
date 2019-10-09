package com.sajjadamin.outgoings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.util.ArrayList;
import java.util.Objects;

public class BudgetActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView budget_issue_date_tv, budget_amount_tv, budget_spend_tv, budget_cash_tv;
    private FloatingActionButton add_data_btn;
    private String budget_id, budget_title, budget_date, budget_amount, budget_description;
    private RecyclerView recyclerView;
    private ArrayList<DataList> arrayList;
    private DataAdaptar adaptar;
    StringBuffer send_quee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        //initialize variables;
        budget_id = getIntent().getStringExtra("id");
        budget_title = getIntent().getStringExtra("title");
        budget_date = getIntent().getStringExtra("date");
        budget_amount = getIntent().getStringExtra("amount");
        budget_description = getIntent().getStringExtra("description");

        //setup toolbar
        toolbar = findViewById(R.id.budget_activity_toolbar);
        toolbar.setTitle(budget_title);
        setSupportActionBar(toolbar);

        //set listener into button
        add_data_btn = findViewById(R.id.add_data_button);
        add_data_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(BudgetActivity.this,AddDataActivity.class);
                i.putExtra("budget_id", budget_id);
                i.putExtra("budget_title", budget_title);
                i.putExtra("budget_date", budget_date);
                i.putExtra("budget_amount", budget_amount);
                i.putExtra("budget_description", budget_description);
                startActivity(i);
            }
        });

        //find text view
        budget_issue_date_tv = findViewById(R.id.budget_issue_date);
        budget_amount_tv = findViewById(R.id.budget_amount);

        //set text into text view
        budget_issue_date_tv.setText(budget_date);
        budget_amount_tv.setText(budget_amount);

        //setup recycler view
        Helper helper = new Helper(this);
        recyclerView = findViewById(R.id.data_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();
        if (helper.connectionCheck()){
            new sendHttpRequest().execute();
        }else {
            Toast.makeText(BudgetActivity.this,"You are currently offline, please turn on internet and reload",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.budget_activity_menue, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.data_search_menu:
                SearchView searchView = (SearchView) item.getActionView();

                searchView.setQueryHint("Search");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        String keyword = newText.toLowerCase();
                        ArrayList<DataList> newList = new ArrayList<>();
                        for (DataList dataList : arrayList){
                            String date = dataList.getDate().toLowerCase();
                            String amount = dataList.getAmount().toLowerCase();
                            String description = dataList.getDescription().toLowerCase();
                            if (date.contains(keyword) || amount.contains(keyword) || description.contains(keyword)){
                                newList.add(dataList);
                            }
                        }
                        adaptar.setFilter(newList);
                        return true;
                    }
                });
                break;
            case R.id.delete_budget:
                if (new Helper(this).connectionCheck()){
                    AlertDialog.Builder alb = new AlertDialog.Builder(BudgetActivity.this);
                    alb.setTitle("Delete Budget");
                    alb.setMessage("Do you want to delete this budget ?");
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
                            new sendBudgetDeleteRequest().execute();
                        }
                    });
                    AlertDialog alertDialog = alb.create();
                    alertDialog.show();
                    break;
                }else {
                    Toast.makeText(this,"You are in offline, please turn on internet",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edit_budget:
                if (new Helper(this).connectionCheck()){
                    finish();
                    Intent i = new Intent(BudgetActivity.this,EditBudgetActivity.class);
                    i.putExtra("id", budget_id);
                    i.putExtra("title", budget_title);
                    i.putExtra("description", budget_description);
                    i.putExtra("date", budget_date);
                    i.putExtra("amount", budget_amount);
                    startActivity(i);
                }else {
                    Toast.makeText(this,"You are in offline, please turn on internet",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.send_budget:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, budget_title);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, send_quee.toString());
                startActivity(Intent.createChooser(sharingIntent, "Send using..."));
        }
        return super.onOptionsItemSelected(item);
    }

    private class sendHttpRequest extends AsyncTask<String, String, String> {
        int budget_spend = 0;
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://outgoings.sajjadamin.com/api.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("action", "fetch_data")
                        .appendQueryParameter("user_id", new Helper(BudgetActivity.this).getSessionData("user_id"))
                        .appendQueryParameter("budget_id", budget_id);
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
            JSONArray jsonArray = null;
            try {
                send_quee = new StringBuffer();
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String date = jsonObject.getString("date");
                    String amount = jsonObject.getString("amount");
                    String description = jsonObject.getString("description");
                    budget_spend += Integer.parseInt(amount);
                    arrayList.add(new DataList(id, date, amount, description));
                    send_quee.append("Date : "+date+"\nAmount : "+amount+"\nDescription : "+description+"\n\n");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adaptar = new DataAdaptar(arrayList, getApplicationContext(), new DataClick() {
                @Override
                public void onDataClick(View view) {
                    TextView idTv = view.findViewById(R.id.data_id);
                    TextView dateTv = view.findViewById(R.id.data_date);
                    TextView amountTv = view.findViewById(R.id.data_amount);
                    TextView descriptionTv = view.findViewById(R.id.data_description);
                    String id = idTv.getText().toString();
                    String date = dateTv.getText().toString();
                    String amount = amountTv.getText().toString();
                    String description = descriptionTv.getText().toString();
                    Intent i = new Intent(BudgetActivity.this,DataActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("date", date);
                    i.putExtra("amount", amount);
                    i.putExtra("description", description);
                    startActivity(i);
                }
            });
            recyclerView.setAdapter(adaptar);

            budget_spend_tv = findViewById(R.id.budget_spend);
            budget_cash_tv = findViewById(R.id.budget_cash);
            budget_spend_tv.setText(Integer.toString(budget_spend));
            int budget_cash = Integer.parseInt(budget_amount) - budget_spend;
            budget_cash_tv.setText(Integer.toString(budget_cash));
        }
    }

    private class sendBudgetDeleteRequest extends AsyncTask<String, String, String> {
        Helper helper = new Helper(BudgetActivity.this);
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://outgoings.sajjadamin.com/api.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("action", "delete_budget")
                        .appendQueryParameter("user_id", helper.getSessionData("user_id"))
                        .appendQueryParameter("budget_id", budget_id)
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
                startActivity(new Intent(BudgetActivity.this,MainActivity.class));
            }else {
                Toast.makeText(BudgetActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
