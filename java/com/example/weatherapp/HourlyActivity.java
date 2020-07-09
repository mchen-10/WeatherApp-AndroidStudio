package com.example.weatherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class HourlyActivity extends AppCompatActivity {
    OwmClient owmClient = new OwmClient();
    private RequestQueue requestQueue;
    private TextView dateVal0;
    private TextView dateVal1;
    private TextView dateVal2;
    private TextView dateVal3;
    private TextView dateVal4;
    private TextView tempVal0;
    private TextView tempVal1;
    private TextView tempVal2;
    private TextView tempVal3;
    private TextView tempVal4;

    private TextView maxVal0;
    private TextView maxVal1;
    private TextView maxVal2;
    private TextView maxVal3;
    private TextView maxVal4;

    private TextView minVal0;
    private TextView minVal1;
    private TextView minVal2;
    private TextView minVal3;
    private TextView minVal4;


    private TextView descripVal0;
    private TextView descripVal1;
    private TextView descripVal2;
    private TextView descripVal3;
    private TextView descripVal4;
    private TextView locationVal;
    private String cityInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle data = getIntent().getExtras();
        owmClient.setLocation(data.getString("location"));
        owmClient.setUnits(data.getBoolean("units"));

        locationVal = findViewById(R.id.locationValue);
        dateVal0 = findViewById(R.id.dateVal0);
        dateVal1 = findViewById(R.id.dateVal1);
        dateVal2 = findViewById(R.id.dateVal2);
        dateVal3 = findViewById(R.id.dateVal3);
        dateVal4 = findViewById(R.id.dateVal4);
        tempVal0 = findViewById(R.id.tempVal0);
        tempVal1 = findViewById(R.id.tempVal1);
        tempVal2 = findViewById(R.id.tempVal2);
        tempVal3 = findViewById(R.id.tempVal3);
        tempVal4 = findViewById(R.id.tempVal4);

        descripVal0 = findViewById(R.id.descripVal0);
        descripVal1 = findViewById(R.id.descripVal1);
        descripVal2 = findViewById(R.id.descripVal2);
        descripVal3 = findViewById(R.id.descripVal3);
        descripVal4 = findViewById(R.id.descripVal4);

        maxVal0 = findViewById(R.id.maxVal0);
        maxVal1 = findViewById(R.id.maxVal1);
        maxVal2 = findViewById(R.id.maxVal2);
        maxVal3 = findViewById(R.id.maxVal3);
        maxVal4 = findViewById(R.id.maxVal4);

        minVal0 = findViewById(R.id.minVal0);
        minVal1 = findViewById(R.id.minVal1);
        minVal2 = findViewById(R.id.minVal2);
        minVal3 = findViewById(R.id.minVal3);
        minVal4 = findViewById(R.id.minVal4);
        requestQueue = Volley.newRequestQueue(this );
        try {
            jsonParseHourly(owmClient);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu3, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button:
                AlertDialog.Builder prompt = new AlertDialog.Builder(this);
                prompt.setTitle("Set Location");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                prompt.setView(input);
                prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("input val", input.getText().toString());
                        cityInput = input.getText().toString();
                        owmClient.setLocation(cityInput);try {
                            jsonParseHourly(owmClient);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }
                });
                prompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                prompt.show();
                return true;
            case R.id.currentMenu:
                Intent intent = new Intent(HourlyActivity.this, MainActivity.class);
                intent.putExtra("location", owmClient.getLocation());
                intent.putExtra("units", owmClient.unitsBool());
                startActivity(intent);
                return true;
            case R.id.forecastMenu:
                Intent intent2 = new Intent(HourlyActivity.this, ForecastActivity.class);
                intent2.putExtra("location", owmClient.getLocation());
                intent2.putExtra("units", owmClient.unitsBool());
                startActivity(intent2);

            case R.id.units:
                Toast.makeText(this, "Choose units", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.fahrenheit:
                owmClient.setUnits(true);
                Toast.makeText(this, "Imperial Units Selected", Toast.LENGTH_SHORT).show();
                try {
                    jsonParseHourly(owmClient);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.celsius:
                owmClient.setUnits(false);
                Toast.makeText(this, "Metric Units Selected", Toast.LENGTH_SHORT).show();
                try {
                    jsonParseHourly(owmClient);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    public void jsonParseHourly(final OwmClient owmClient) throws MalformedURLException {
        String url = owmClient.hourlyURL();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("list");
                    JSONObject location = response.getJSONObject("city");
                    locationVal.setText((location.getString("name")));
                    Log.i("JSON LEngth", String.valueOf(jsonArray.length()));
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject list = jsonArray.getJSONObject(i);
                        String temp = list.getJSONObject("main").getString("temp");
                        Log.i("the temp", temp);
                        JSONArray weather = list.getJSONArray("weather");

                        switch (i) {
                            case 0:
                                descripVal0.setText(weather.getJSONObject(0).getString("main"));
                                dateVal0.setText(list.getString("dt_txt"));             ;
                                tempVal0.setText(" " + temp + owmClient.getUnits() + " ");
                                temp = list.getJSONObject("main").getString("temp_max");
                                maxVal0.setText(" " + temp + owmClient.getUnits() + " ");
                                temp = list.getJSONObject("main").getString("temp_min");
                                minVal0.setText(temp + owmClient.getUnits() + " ");

                                break;
                            case 1:
                                descripVal1.setText(weather.getJSONObject(0).getString("main"));
                                dateVal1.setText(list.getString("dt_txt"));
                                tempVal1.setText(" " + temp + owmClient.getUnits() + " ");
                                temp = list.getJSONObject("main").getString("temp_max");
                                maxVal1.setText(temp + owmClient.getUnits() + " ");
                                temp = list.getJSONObject("main").getString("temp_min");
                                minVal1.setText(temp + owmClient.getUnits() + " ");
                                break;
                            case 2:
                                descripVal2.setText(weather.getJSONObject(0).getString("main"));
                                dateVal2.setText(list.getString("dt_txt"));
                                tempVal2.setText(" " + temp + owmClient.getUnits() + " ");
                                temp = list.getJSONObject("main").getString("temp_max");
                                maxVal2.setText(temp + owmClient.getUnits() + " ");
                                temp = list.getJSONObject("main").getString("temp_min");
                                minVal2.setText(temp + owmClient.getUnits() + " ");
                                break;
                            case 3:
                                descripVal3.setText(weather.getJSONObject(0).getString("main"));
                                dateVal3.setText(list.getString("dt_txt"));
                                tempVal3.setText(" " + temp + owmClient.getUnits() + " ");
                                temp = list.getJSONObject("main").getString("temp_max");
                                maxVal3.setText(temp + owmClient.getUnits() + " ");
                                temp = list.getJSONObject("main").getString("temp_min");
                                minVal3.setText(temp + owmClient.getUnits() + " ");
                                break;
                            case 4:
                                descripVal4.setText(weather.getJSONObject(0).getString("main"));
                                dateVal4.setText(list.getString("dt_txt"));
                                tempVal4.setText(" " + temp + owmClient.getUnits());
                                temp = list.getJSONObject("main").getString("temp_max");
                                maxVal4.setText(temp + owmClient.getUnits());
                                temp = list.getJSONObject("main").getString("temp_min");
                                minVal4.setText(temp + owmClient.getUnits());
                                break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String dateInString = df.format(cal.getTime());
        return dateInString;

    }
}
