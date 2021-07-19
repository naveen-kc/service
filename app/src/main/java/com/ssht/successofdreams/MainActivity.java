package com.ssht.successofdreams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    SessionManagement sessionManagement;
    String token;
    String time;
    Double lat,log;
    Button fetch,back;
    TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(getApplicationContext(),"Press on Fetch button to see Location",Toast.LENGTH_SHORT).show();
        sessionManagement=new SessionManagement(getApplicationContext());

        timeText=findViewById(R.id.time);
        back=findViewById(R.id.back);
        fetch=findViewById(R.id.fetch);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.map, mapFragment)
                        .commit();


                mapFragment.getMapAsync(MainActivity.this);

                timeText.setText("Location at "+ "\n" +time);

            }
        });



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://lw-node.herokuapp.com/tasks/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface= retrofit.create(ApiInterface.class);



        Call<LocationResponse> call = apiInterface.getLocations(sessionManagement.getusename());
      call.enqueue(new Callback<LocationResponse>() {
          @Override
          public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {

              if(response.isSuccessful()) {

                //  Toast.makeText(MainActivity.this,response.body().getLocations().get(0).getLat()+" "+response.body().getLocations().get(0).getLog() +"  "+response.body().getLocations().get(0).getTime(),Toast.LENGTH_SHORT).show();

                  time=response.body().getLocations().get(0).getTime();
                  lat=Double.valueOf(response.body().getLocations().get(0).getLat());
                  log=Double.valueOf(response.body().getLocations().get(0).getLog());

              }
          }

          @Override
          public void onFailure(Call<LocationResponse> call, Throwable t) {

          }
      });


      back.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              onBackPressed();
          }
      });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(lat,log);
        MarkerOptions options =new MarkerOptions().position(latLng).title("fetched location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        googleMap.addMarker(options);


    }



}