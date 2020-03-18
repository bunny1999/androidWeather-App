package in.irotech.weather;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class progress_bar extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    RequestQueue requestQueue;
    final String urlInitial = "https://api.openweathermap.org/data/2.5/weather?";
    final String latitude = "lat=";
    final String longnitude = "&lon=";
    final String apiKey = "&appid=1b71f31054b246914d5c6e5220a26940";
    final String forCity = "q=";
    boolean onLocation=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        requestQueue = VollySingletone.getInstance(this).getRequestQueue();
        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("methord").equals("getDataByCity")) {
                getDataByCity(getIntent().getStringExtra("cityName"));
            }
        }else{
            setupLocation();
        }
    }

    private void setupLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            OnGPS();
        } else {
//            Log.d("enter","Enter into the get data");
            getDataByLocation();
        }
    }

    //for go to settings and turnon gps service manualy
    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        getDataByLocation();
    }

    //when got permission and can grab data
    private void getDataByLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationGPS != null) {
                onLocation=true;
                String url = urlInitial + latitude + locationGPS.getLatitude() + longnitude + locationGPS.getLongitude() + apiKey;

                jsonRequest(url,onLocation);

            }
        }
    }

    private void getDataByCity(String cityName) {
        onLocation=false;
        String url=urlInitial+forCity+cityName+apiKey;
        jsonRequest(url,onLocation);
    }

    private void jsonRequest(String url, final boolean onLocati){
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                bundleUpData(response,onLocati);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                errorFeatchingAPIAlert();

            }
        });
        requestQueue.add(request);
    }

    private void bundleUpData(JSONObject response,boolean onLoc) {
        try {
            String city = response.getString("name");
            String temp = convertToCelcius(response.getJSONObject("main").getString("temp"));
            String tempH = convertToCelcius(response.getJSONObject("main").getString("temp_max"));
            String tempL = convertToCelcius(response.getJSONObject("main").getString("temp_min"));
            String pressure = response.getJSONObject("main").getString("pressure");
            String humidity = response.getJSONObject("main").getString("humidity");

            String wSpeed = response.getJSONObject("wind").getString("speed");
            String wDeg = response.getJSONObject("wind").getString("deg");

            String country = response.getJSONObject("sys").getString("country");
            String sunrise = response.getJSONObject("sys").getString("sunrise");
            String sunset = response.getJSONObject("sys").getString("sunset");

            String longni = response.getJSONObject("coord").getString("lon");
            String lati = response.getJSONObject("coord").getString("lat");

            String climate = firstLaterCapital(response.getJSONArray("weather").getJSONObject(0).getString("description"));
            String visiblity = "";
            try {
                visiblity = response.getString("visibility");
            } catch (JSONException e) {
                visiblity="null";
            }

            Intent intent = new Intent(progress_bar.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("city", city);
            bundle.putString("climate", climate);
            bundle.putString("temp", temp+"°C");
            bundle.putString("tempH", tempH+"°C");
            bundle.putString("tempL", tempL+"°C");
            bundle.putString("windSpeed", wSpeed);
            bundle.putString("windDeg", wDeg);
            bundle.putString("pressure", pressure);
            bundle.putString("humidity", humidity);
            bundle.putString("sunrise", sunrise);
            bundle.putString("sunset", sunset);
            bundle.putString("visibility", visiblity);
            bundle.putString("country", country);
            bundle.putString("longnitude", longni);
            bundle.putString("latitude", lati);
            bundle.putBoolean("onLocation",onLoc);
            intent.putExtras(bundle);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void errorFeatchingAPIAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(progress_bar.this);
        builder.create();
        builder.setTitle("ERROR");
        builder.setMessage("Unable Fetching API");
        builder.setCancelable(true);
        builder.show();
    }

    private String convertToCelcius(String value) {
        double doub=Double.parseDouble(value) - 273.15;
        int val=(int)Math.round(doub);
        return String.valueOf(val);
    }

    private String firstLaterCapital(String climate) {
        String[] array = climate.split(" ");
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].substring(0, 1).toUpperCase() + array[i].substring(1);
        }
        return String.join("\n ", array);
    }
}

