package com.vtm115.alessandrosam.actelemetryfrontend_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ConnectActivity extends AppCompatActivity {

    private static final String ERRTAG = "ConnectActivity";
    private static final String SETTINGS_FILE = "connection_settings";
    private static final String CS_IP_ADDRESS = "ip_address";

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Пытаемся открыть файл настроек
        settings = getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE);
        // Если его нет, не делаем ничего. Только отпишемся в лог для сведения.
        String address = settings.getString(CS_IP_ADDRESS, "");
        ((EditText) findViewById(R.id.et_ipaddress)).setText(address);
    }

    public void connect(View view) {
        // Достаём (IP-)адрес бекэнда из текстового поля
        String address = ((EditText) findViewById(R.id.et_ipaddress)).getText().toString();
        // Запоминаем последний введённый адрес
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CS_IP_ADDRESS, address);
        editor.apply();
        // Запускаем саму приборную панель - Dashboard
        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra("address", address);
        startActivity(intent);
    }
}
