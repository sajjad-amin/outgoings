package com.sajjadamin.outgoings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

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

public class MainActivity extends AppCompatActivity {
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FloatingActionButton add_budget_button;
    private RecyclerView recyclerView;
    private ArrayList<BudgetDataList> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set toolbar
        setUpToolbar();
        //set navigation menu click event
        setNavigationClick();
        //redirect login activity if user don't logged in
        Helper helper = new Helper(this);
        recyclerView = findViewById(R.id.budget_recycelerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();

        if (!helper.sessionCheck()){
            finish();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }else {
            if (helper.connectionCheck()){
                new sendHttpRequest().execute();
            }else {
                Toast.makeText(MainActivity.this,"You are currently offline, please turn on internet and reload",Toast.LENGTH_SHORT).show();
            }
        }

        if (helper.connectionCheck()){
            add_budget_button = findViewById(R.id.add_budget_button);
            add_budget_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this,AddBudgetActivity.class));
                }
            });
        }else {
            Toast.makeText(MainActivity.this,"You are currently offline, please turn on internet",Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpToolbar() {
        DrawerLayout drawerLayout = findViewById(R.id.main_activity_drawer);
        toolbar = findViewById(R.id.main_activity_toolbar);
        toolbar.setTitle("Budgets");
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menue, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.reload:
                if (new Helper(this).connectionCheck()){
                    finish();
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                }else {
                    Toast.makeText(this,"You are in offline, please turn on internet",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNavigationClick() {
        navigationView = findViewById(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.account_nav:
                        startActivity(new Intent(MainActivity.this, AccountActivity.class));
                        break;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this,AboutActivity.class));
                        break;
                    case R.id.exit_app_nav:
                        finishAffinity();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alb = new AlertDialog.Builder(MainActivity.this);
        alb.setTitle("Exit App");
        alb.setMessage("Do you want to exit this app ?");
        alb.setIcon(R.drawable.exit);
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
                finishAffinity();
            }
        });
        AlertDialog alertDialog = alb.create();
        alertDialog.show();
    }

    private class sendHttpRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://outgoings.sajjadamin.com/api.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("action", "fetch_budget")
                        .appendQueryParameter("user_id", new Helper(MainActivity.this).getSessionData("user_id"));
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
                jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String title = jsonObject.getString("title");
                    String description = jsonObject.getString("description");
                    String date = jsonObject.getString("date");
                    String amount = jsonObject.getString("amount");
                    arrayList.add(new BudgetDataList(id, title, description, date, amount));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BudgetDataAdaptar adaptar = new BudgetDataAdaptar(arrayList, getApplicationContext(), new BudgetClick() {
                @Override
                public void onBudgetClick(View view, int position) {
                    String id = arrayList.get(position).getId();
                    String title = arrayList.get(position).getTitle();
                    String date = arrayList.get(position).getDate();
                    String amount = arrayList.get(position).getAmount();
                    String description = arrayList.get(position).getDescription();
                    Intent i = new Intent(MainActivity.this,BudgetActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("title", title);
                    i.putExtra("date", date);
                    i.putExtra("amount", amount);
                    i.putExtra("description", description);
                    startActivity(i);
                }
            });
            recyclerView.setAdapter(adaptar);
            super.onPostExecute(s);
        }
    }
}