package com.ssht.successofdreams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText user,password;
    Button login;
    LottieAnimationView lottieAnimationView;
    String username,name,img,token;
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lottieAnimationView=findViewById(R.id.progressbar);
        user=findViewById(R.id.user);
        password=findViewById(R.id.password);
        login=findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getText().toString().equals("")
                        || password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Please fill the form", Toast.LENGTH_SHORT).show();

                } else {
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    login.setVisibility(View.INVISIBLE);
                    String url = "https://lw-node.herokuapp.com/tasks/login";
                    StringRequest stringRequest = new StringRequest(1, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getApplicationContext(),response, Toast.LENGTH_SHORT).show();

                                    //if(response.contains("true"))
                                    if(response.contains("success"))
                                    {

                                        JSONObject jSONObject = null;
                                        try {
                                            jSONObject = new JSONObject(response);

                                        username = jSONObject.getString("username");
                                        name = jSONObject.getString("name");
                                        img = jSONObject.getString("img");
                                        token = jSONObject.getString("token");


                                        Intent intent = new Intent(LoginActivity.this, DashBoard.class);
                                        intent.putExtra("name",name);
                                        intent.putExtra("token",token);
                                            startActivity(intent);
                                            session = new SessionManagement(getApplicationContext());
                                            session.createLoginSession(token);
                                            finish();

                                            lottieAnimationView.setVisibility(View.INVISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
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
                            map.put("username",user.getText().toString());
                            map.put("password", password.getText().toString());
                            return map;
                        }

                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
