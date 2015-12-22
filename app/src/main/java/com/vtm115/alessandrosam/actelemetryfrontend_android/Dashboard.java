package com.vtm115.alessandrosam.actelemetryfrontend_android;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import simulator.ACDataContainer;
import simulator.DataReceiver;

public class Dashboard extends AppCompatActivity {

    private static final String ERRTAG_DASHBOARD = "Dashboard";
    private DataReceiver dataReceiver;
    private DashboardFragment dashboardFragment;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Intent intent = getIntent();
        this.address = intent.getStringExtra("address");
        this.dashboardFragment = new DashboardFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.dash_fragment, dashboardFragment, "dash_fragment").commit();
    }

    public DataReceiver getDataReceiver() {
        return dataReceiver;
    }

    public DashboardFragment getDashboardFragment() {
        return dashboardFragment;
    }

    static class DashboardUiHandler extends Handler {
        private final WeakReference<Dashboard> dashboardWeakReference;

        DashboardUiHandler(Looper looper, Dashboard dashboard/*, DataReceiver dataReceiver, DashboardFragment dashboardFragment*/) {
            super(looper);
            dashboardWeakReference = new WeakReference<>(dashboard);
        }

        @Override
        public void handleMessage(Message what) {
            Dashboard dashboard = dashboardWeakReference.get();
            if (dashboard != null) {
                DataReceiver dataReceiver = dashboard.getDataReceiver();
                DashboardFragment dashboardFragment = dashboard.getDashboardFragment();
                if (what.what == DataReceiver.MSG_SUCCESS) {
                    ACDataContainer container = dataReceiver.getDataContainer();
                    if (container == null) {
                        Log.e(ERRTAG_DASHBOARD, "Data container is empty !?");
                    } else {
                        switch (container.getDataType()) {
                            case ACDataContainer.MTYPE_EMPTY: {
                                Toast.makeText(dashboard.getApplicationContext(), "Assetto Corsa is not running",
                                        Toast.LENGTH_LONG).show();
                                dashboardFragment.emptyUi();
                                break;
                            }
                            case ACDataContainer.MTYPE_DYNAMIC: {
                                // FIXME На случай лайв-тайминга - нужно будет слать в другой фрагмент
                                dashboardFragment.updateUi(container);
                                break;
                            }
                            default:
                                Log.e(ERRTAG_DASHBOARD, "Unknown message type");

                        }
                    }
                } else if (what.what == DataReceiver.MSG_FAIL) {
                    Toast.makeText(dashboard.getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                    Log.e(ERRTAG_DASHBOARD, "Activity is closing, DataReceiver thread was stopped.");
                    dashboard.finish();
                } else if (what.what == DataReceiver.MSG_CONNECT_FAILED) {
                    Toast.makeText(dashboard.getApplicationContext(), "Cannot connect to AC backend",
                            Toast.LENGTH_SHORT).show();
                    dashboard.finish();
                } else if (what.what == DataReceiver.MSG_STOP) {
                    Toast.makeText(dashboard.getApplicationContext(), "Connection closed",
                            Toast.LENGTH_SHORT).show();
                    dashboard.finish();
                }
            } else {
                Log.d(ERRTAG_DASHBOARD, "Nulls everywhere!!!");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
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
        DashboardUiHandler uiHandler = new DashboardUiHandler(Looper.getMainLooper(), this);
        dataReceiver = new DataReceiver(uiHandler, address);
        dataReceiver.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataReceiver.stopReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //dataReceiver.stopReceiver();

    }
}