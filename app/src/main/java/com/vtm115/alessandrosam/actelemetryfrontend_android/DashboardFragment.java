package com.vtm115.alessandrosam.actelemetryfrontend_android;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import simulator.ACDataContainer;
import simulator.DataReceiver;

/**
 * Фрагмент, который в реальном времени отображает данные, взятые из бекэнда
 */

// TODO (Layout) Графика для некоторых элементов (педали)
// TODO (Layout) Изменение цвета для оборотов/передачи, когда нужно переключаться
// TODO Информация о шинах и топливе
// TODO Отобразить время последнего круга (нет в AC API, но реализуемо)
// TODO Добавить информацию о разрывах во время гонки (может потребовать реализации лайв-тайминга)

public class DashboardFragment extends Fragment {

    private TextView tvSpeed;
    private TextView tvGear;
    private TextView tvRPM;
    private TextView tvThrottle;
    private TextView tvBrake;
    private TextView tvClutch;
    private TextView tvBestLap;
    private TextView tvCurrentLap;
    private TextView tvDelta;
    private TextView tvSessionInfo;
    private TextView tvDriverName;
    private TextView tvCarAndTrackInfo;
    private PedalDrawable pedalDrawable;

    DataReceiver dataReceiver;
    ACDataContainer acDataContainer;

    public DashboardFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private String getStatusString(String status) {
        if (status.equals(ACDataContainer.ST_AC_OFF))
            return getString(R.string.status_ac_off);
        if (status.equals(ACDataContainer.ST_AC_LIVE))
            return getString(R.string.status_ac_live);
        if (status.equals(ACDataContainer.ST_AC_PAUSE))
            return getString(R.string.status_ac_pause);
        if (status.equals(ACDataContainer.ST_AC_REPLAY))
            return getString(R.string.status_ac_replay);
        return "";
    }

    private String getSessionString(String session) { // it's read from JSON so originally it's String, no need to cast
        if (session.equals(ACDataContainer.SESS_AC_UNKNOWN))
            return getString(R.string.session_unknown);
        if (session.equals(ACDataContainer.SESS_AC_PRACTICE))
            return getString(R.string.session_practice);
        if (session.equals(ACDataContainer.SESS_AC_HOTLAP))
            return getString(R.string.session_hotlap);
        if (session.equals(ACDataContainer.SESS_AC_RACE))
            return getString(R.string.session_race);
        if (session.equals(ACDataContainer.SESS_AC_DRIFT))
            return getString(R.string.session_drift);
        if (session.equals(ACDataContainer.SESS_AC_DRAG))
            return getString(R.string.session_drag);
        if (session.equals(ACDataContainer.SESS_AC_QUALIFY))
            return getString(R.string.session_qualify);
        if (session.equals(ACDataContainer.SESS_AC_TIME_ATTACK))
            return getString(R.string.session_timeattack);
        return "";
    }

    public void updateUi(ACDataContainer container) {
        try {
            if (container == null) {
                Log.e("Dashboard", "Container is empty !?");
                return;
            }
            if (container.getDataType().equals(ACDataContainer.MTYPE_STATIC)) {
                // FIXME: (AC 1.2 bug) Имя и фамилия хранятся вместе в поле 'name' (до кучи повторяясь в 'surname')
                tvDriverName.setText(container.getStringField(ACDataContainer.S_playerName) /*+ " " +
                    container.getStringField(ACDataContainer.S_playerSurname)*/);
                tvCarAndTrackInfo.setText(container.getStringField(ACDataContainer.S_carModel) + " @ " +
                        container.getStringField(ACDataContainer.S_track));

            } else if (container.getDataType().equals(ACDataContainer.MTYPE_DYNAMIC)) {
                // Speed
                String speed = container.getStringField(ACDataContainer.P_speedKmh);
                tvSpeed.setText(speed.substring(0, speed.indexOf(".")));
                // Engine RPM
                tvRPM.setText(container.getStringField(ACDataContainer.P_rpms));
                // Gear
                int gear = Integer.decode(container.getStringField(ACDataContainer.P_gear));
                String s_gear;
                if (gear == 0) s_gear = "R";
                else if (gear == 1) s_gear = "N";
                else s_gear = String.valueOf(gear - 1);
                tvGear.setText(s_gear);
                // Pedals - TODO Отобразить состояние педалей графически
                String throttleValue = container.getStringField(ACDataContainer.P_gas);
                tvThrottle.setText(throttleValue);
                pedalDrawable.setGaugeValue(Math.round(Float.valueOf(throttleValue) * 100));

                tvBrake.setText((container.getStringField(ACDataContainer.P_brake)));
                // Lap times
                tvCurrentLap.setText(container.getStringField(ACDataContainer.G_currentTime));
                tvBestLap.setText(container.getStringField(ACDataContainer.G_bestTime));
                tvDelta.setText(container.getStringField(ACDataContainer.G_split));
                // Session
                String sessionInfo = getSessionString(container.getStringField(ACDataContainer.G_session)) +
                        " (" + getStatusString(container.getStringField(ACDataContainer.G_status)) +
                        ")";
                // FIXME AC 1.3: isInPit - поведение изменилось, пока непонятно как
                sessionInfo += container.getStringField(ACDataContainer.G_isInPit);
                tvSessionInfo.setText(sessionInfo);
                // TODO Сделать обработку всех необходимых полей
            }
        } catch (IllegalStateException e) {
            Log.e("Dashboard", "Fragment detached, but still was asked to update");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        tvSpeed = (TextView) getView().findViewById(R.id.SpeedData);
        tvGear = (TextView) getView().findViewById(R.id.GearData);
        tvRPM = (TextView) getView().findViewById(R.id.RPMData);
        tvThrottle = (TextView) getView().findViewById(R.id.ThrottleData);
        tvBrake = (TextView) getView().findViewById(R.id.BrakeData);
        tvClutch = (TextView) getView().findViewById(R.id.ClutchData);
        tvBestLap = (TextView) getView().findViewById(R.id.BestLapData);
        tvCurrentLap = (TextView) getView().findViewById(R.id.CurrentLapData);
        tvDelta = (TextView) getView().findViewById(R.id.DeltaData);
        tvSessionInfo = (TextView) getView().findViewById(R.id.SessionInfo);
        tvDriverName = (TextView) getView().findViewById(R.id.DriverName);
        tvCarAndTrackInfo = (TextView) getView().findViewById(R.id.CarAndTrackInfo);
        pedalDrawable = (PedalDrawable) getView().findViewById(R.id.ThrottleGauge);
    }

    public void emptyUi() {
        tvGear.setText("-");
        tvSpeed.setText("-");
        tvRPM.setText("-");
        tvThrottle.setText("0.00");
        tvBrake.setText("0.00");
        tvClutch.setText("0.00");
        tvBestLap.setText("--:--.---");
        tvCurrentLap.setText("--:--.---");
        tvDelta.setText("---");
        tvSessionInfo.setText("");
        tvDriverName.setText("");
        tvCarAndTrackInfo.setText("");

    }
}
