package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.FloatBuffer;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private TextView descripVal;
    private TextView locationVal;
    private TextView tempVal;
    private TextView minVal;
    private TextView maxVal;
    private TextView humidityVal;
    private TextView cloudVal;
    private TextView dateTime;
    private MenuItem forcastItem;
    private MenuItem hourlyItem;
    private MenuItem unitItem;
    private ImageView weatherIcon;
    private String cityInput;
    OwmClient owmClient = new OwmClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle data = getIntent().getExtras();
        if(data != null) {
            owmClient.setLocation(data.getString("location"));
            owmClient.setUnits(data.getBoolean("units"));
        }
        locationVal = findViewById(R.id.locationValue);
        descripVal = findViewById(R.id.descripVal);
        tempVal = findViewById(R.id.tempVal);
        minVal = findViewById(R.id.minVal);
        maxVal = findViewById(R.id.maxVal);
        humidityVal = findViewById(R.id.humidityVal);
        cloudVal = findViewById(R.id.cloudVal);
        forcastItem = findViewById(R.id.forecastMenu);
        dateTime = findViewById(R.id.dateVal);
        /*weatherIcon = findViewById(R.id.weatherImage);*/
        requestQueue = Volley.newRequestQueue(this);
        try {
            jsonParseCurrent(owmClient);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
                        owmClient.setLocation(cityInput);
                        try {
                            jsonParseCurrent(owmClient);
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
            case R.id.forecastMenu:
                Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                intent.putExtra("location", owmClient.getLocation());
                intent.putExtra("units", owmClient.unitsBool());
                startActivity(intent);
                return true;
            case R.id.hourlyMenu:
                Intent intent2 = new Intent(MainActivity.this, HourlyActivity.class);
                intent2.putExtra("location", owmClient.getLocation());
                intent2.putExtra("units", owmClient.unitsBool());
                startActivity(intent2);
                return true;
            case R.id.units:
                Toast.makeText(this, "Choose units", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.fahrenheit:
                owmClient.setUnits(true);
                Toast.makeText(this, "Imperial Units Selected", Toast.LENGTH_SHORT).show();
                try {
                    jsonParseCurrent(owmClient);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.celsius:
                owmClient.setUnits(false);
                Toast.makeText(this, "Metric Units Selected", Toast.LENGTH_SHORT).show();
                try {
                    jsonParseCurrent(owmClient);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    public void jsonParseCurrent(final OwmClient owmClient) throws MalformedURLException {
        String url = owmClient.currURL();
        Log.i("bool units", String.valueOf(owmClient.unitsBool()));
        Log.i("Units", owmClient.getUnits());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("weather");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject weather = jsonArray.getJSONObject(i);

                        String descrip = weather.getString("description");
                        descripVal.setText(descrip);

                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject weather = jsonArray.getJSONObject(i);
                        String icon = weather.getString("icon");
                        owmClient.setIconURL(icon);

                    }

                    try {
                        URL imgUrl = new URL(owmClient.getIconURL());
                        Bitmap bmp = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                        weatherIcon.setImageBitmap(bmp);
                        weatherIcon.invalidate();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonTemp = response.getJSONObject("main");
                    String theTemp = jsonTemp.getString("temp");
                    tempVal.setText(theTemp + owmClient.getUnits());

                    JSONObject tempMax = response.getJSONObject("main");
                    String max = tempMax.getString("temp_max");
                    maxVal.setText(max + owmClient.getUnits());
                    JSONObject tempMin = response.getJSONObject("main");
                    String min = tempMin.getString("temp_min");
                    minVal.setText(min + owmClient.getUnits());

                    JSONObject humidity = response.getJSONObject("main");
                    String hum = humidity.getString("humidity");
                    humidityVal.setText(hum + "%");

                    Object location = response.get("name");
                    String theLocation = location.toString();
                    locationVal.setText(theLocation);


                    JSONObject jsonCloud = response.getJSONObject("clouds");
                    String cloud = jsonCloud.getString("all");
                    cloudVal.setText(cloud + "%");

                    //Convert JSON dt timestamp into  a date;
                    Long timeObject = response.getLong("dt");
                    dateTime.setText(getDate(timeObject));

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

