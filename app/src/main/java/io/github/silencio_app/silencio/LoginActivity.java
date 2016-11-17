package io.github.silencio_app.silencio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText username_et;
    private EditText password_et;
    public static String USERNAME = "User name of User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username_et = (EditText)findViewById(R.id.username_et);
        password_et = (EditText)findViewById(R.id.password_et);
    }
    public void login(View view){
        String username = username_et.getText().toString();
        String password = password_et.getText().toString();
        if (username.equals("vipin") && password.equals("1234")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(USERNAME, username);
            startActivity(intent);
        }
    }
}
