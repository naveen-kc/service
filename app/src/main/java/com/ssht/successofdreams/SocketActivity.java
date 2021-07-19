package com.ssht.successofdreams;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketActivity extends AppCompatActivity {

    private Socket mSocket;


    EditText id;
    Button send;
    String message,ID;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        {
            try {
                mSocket = IO.socket("https://service.cabstart.co.uk:3000/walker");
            } catch (URISyntaxException e) {}
        }
        mSocket.connect();
        id=findViewById(R.id.text);
        send=findViewById(R.id.button);
        textView=findViewById(R.id.textView);
        ID=id.getText().toString();




                if(mSocket.connected()){
                    setListening();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Socket not connected",Toast.LENGTH_SHORT).show();
                }




    }

    private void setListening() {
        mSocket.on("web_socket.new_request_assigned_22", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    message = messageJson.getString("id");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(message);
                            Toast.makeText(getApplicationContext(),messageJson.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}