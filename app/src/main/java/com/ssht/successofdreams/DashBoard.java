package com.ssht.successofdreams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class DashBoard extends AppCompatActivity {
    TextView name;
    TextView lati;
    TextView logi;
    String Token,Name;
    Double Latitude,Longtitude;
    Button fetch,logout;
    SessionManagement sessionManagement;
    FusedLocationProviderClient fusedLocationProviderClient,client;
    SupportMapFragment supportMapFragment;
    private Handler mHandler= new Handler();
    TextView tvTimer;
    long startTime, timeInMilliseconds = 0;
    Handler customHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        name=findViewById(R.id.name);
        fetch=findViewById(R.id.btn_fetch);
        logout=findViewById(R.id.btn_logout);
        lati=findViewById(R.id.lat);
        logi=findViewById(R.id.log);
        tvTimer = (TextView) findViewById(R.id.loc);

        start();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        client=LocationServices.getFusedLocationProviderClient(this);
       // getLocation();
        startService();

        getMap();
        supportMapFragment =(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.gMap);
        sessionManagement=new SessionManagement(getApplicationContext());
        Intent intent = getIntent();
        Token = intent.getStringExtra("token");
        Name = intent.getStringExtra("name");
        name.setText(Name);

       fetch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i = new Intent(getApplicationContext(),MainActivity.class);
               i.putExtra("token",sessionManagement.getusename());
               i.putExtra("lat",String.valueOf(Latitude));
               i.putExtra("log",String.valueOf(Longtitude));
               startActivity(i);
           }
       });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();

            }



        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(DashBoard.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {

                        try {
                            Geocoder geocoder = new Geocoder(DashBoard.this,
                                    Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );

                            Latitude=addresses.get(0).getLatitude();
                            lati.setText("Latitude : "+Latitude);
                            Longtitude=addresses.get(0).getLongitude();
                            logi.setText("Longitude : "+Longtitude);
                            //Loca=addresses.get(0).getLocality();



                            startUpdating();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        else {
            ActivityCompat.requestPermissions(DashBoard.this
                    , new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 44);
        }
    }

    private void uploadLocation() {

        StringRequest stringRequest = new StringRequest(1, "https://lw-node.herokuapp.com/tasks/location-update",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"Location Updated \n"+response , Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("token",sessionManagement.getusename());
                map.put("latitude", String.valueOf(Latitude));
                map.put("longitude", String.valueOf(Longtitude));
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public static String getDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stop() {
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            tvTimer.setText(getDateFromMillis(timeInMilliseconds));
            customHandler.postDelayed(this, 1000);
        }
    };




    private void getMap() {
        if (ActivityCompat.checkSelfPermission(DashBoard.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latLng = new LatLng(location.getLatitude(),
                                        location.getLongitude());
                                MarkerOptions options =new MarkerOptions().position(latLng).title("You r Here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                                googleMap.addMarker(options);
                            }
                        });
                    }
                }
            });
        }
        else {
            ActivityCompat.requestPermissions(DashBoard.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMap();

            }
        }
    }



    private void startUpdating() {
        runnable.run();
    }


    private  Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //Toast.makeText(DashBoard.this,"Updating",Toast.LENGTH_SHORT).show();
            uploadLocation();
            mHandler.postDelayed(this,60000);
        }
    };

    public  void stopUpdating(){
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Please Logout",Toast.LENGTH_SHORT).show();
        //finish();
    }

    public  void startService(){
        getLocation();
        Intent serviceIntent = new Intent(this,LocationService.class);
        startService(serviceIntent);

    }

    public  void stopService(){
        endService();
        Intent serviceIntent = new Intent(this,LocationService.class);
        stopService(serviceIntent);

    }
    public  void endService(){

        StringRequest stringRequest = new StringRequest(1, "https://lw-node.herokuapp.com/tasks/logout",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response, Toast.LENGTH_SHORT).show();

                        stopUpdating();
                        stop();

                        Intent i = new Intent(DashBoard.this,LoginActivity.class);
                        startActivity(i);
                        finish();



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("token",sessionManagement.getusename());

                return map;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void socket(View view) {
        Intent i = new Intent(DashBoard.this,SocketActivity.class);
        startActivity(i);
    }
}
