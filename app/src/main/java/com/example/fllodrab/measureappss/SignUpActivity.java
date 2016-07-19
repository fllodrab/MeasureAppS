package com.example.fllodrab.measureappss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class SignUpActivity extends Activity {

    NetworkHelper networkHelper = new NetworkHelper();

    EditText nameInput;
    EditText passwordInput;
    EditText confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        nameInput = (EditText) findViewById(R.id.nameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        confirmPasswordInput = (EditText) findViewById(R.id.confirmPasswordInput);
    }

    public void signup(View view) {
        Log.d("LOG", "llega");
        if (!passwordInput.getText().toString().equals(confirmPasswordInput.getText().toString())) {
            Log.d("Entra IF", "SI IF");
            Toast.makeText(getApplicationContext(), "The passwords do not match", Toast.LENGTH_LONG).show();
        } else {
            Log.d("Entra ELSE", "SI ELSE");
            String json = "{\"name\": \"" + nameInput.getText() + "\", \"password\":\"" + passwordInput.getText() + "\"}";
            networkHelper.post("http://10.0.3.2:8000/signup", json, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    String responseStr = response.body().string();
                    final String messageText = "Status code : " + response.code() +
                            "\n" +
                            "Response body : " + responseStr;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), messageText, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
        Log.d("Sale", "sale");
    }
}