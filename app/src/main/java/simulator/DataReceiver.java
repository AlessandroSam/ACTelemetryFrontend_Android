package simulator;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Receives JSON data from backend, decodes it and notifies handler which triggers UI update
 * Created by AlessandroSam on 06.08.2015.
 */

// TODO Implement subscription mechanism to make protocol safer and reduce data rate (in case of per-field subscription)
// TODO Implement binary protocol for dynamic info (much much more efficient, but pretty hard to make)
// TODO Make use of AC UDP telemetry for data that is unavailable through shared memory

public class DataReceiver extends Thread {
    // Receives data from network (using ConnectionSingleton's socket streams
    // Decodes JSONs, saves data and notifies UI thread through Handler
    // Note that we should ensure that message queue isn't filling

    public static final int MSG_SUCCESS = 0;
    public static final int MSG_EMPTY = 1;
    public static final int MSG_FAIL = 2;
    public static final int MSG_CONNECT_FAILED = 3;
    public static final int MSG_STOP = 4;


    public static final String ERRTAG_RECEIVER = "DataReceiver";
    public static final int PORT = 50000;

    public static final String SUBSCRIPTION_STRING =
            "packetId,gas,brake,fuel,gear,rpms,speedKmh,drs,tc,abs,status,session,currentTime,bestTime,split,isInPit.";

    private ConnectionSingleton conn;
    private BufferedReader in;
//    DataQueue queue; // unused
    private boolean isStopped;
    private ACDataContainer dataContainer;
    private Handler uiHandler;
    private String address;

    public DataReceiver(Handler uiHandler, String address) {
        conn = ConnectionSingleton.getInstance();
        dataContainer = null;
        this.uiHandler = uiHandler;
        this.address = address;
    }

    public boolean connect(String address, int port) {
        /**
         * Подключение к клиенту с адресом address и портом port, и подписываемся на данные.
         */
        try {
            // Подключаемся
            conn.createSocket(address, port);
            in = conn.getReader();
            // Подписываемся на конкретные данные
            PrintWriter out = conn.getWriter();
            out.write(DataReceiver.SUBSCRIPTION_STRING);
            out.flush();
            // больше отправлять нечего, причём гарантированно
            conn.getSocket().shutdownOutput();
        }
        catch (SocketTimeoutException e) {
            uiHandler.sendEmptyMessage(MSG_CONNECT_FAILED);
            Log.e(ERRTAG_RECEIVER, "Connection timeout");
            return false;
        }
        catch (IOException e) {
            uiHandler.sendEmptyMessage(MSG_FAIL);
            Log.e(ERRTAG_RECEIVER, "Cannot connect to " + address + ":" + port);
            Log.e(ERRTAG_RECEIVER, e.getMessage());
            return false;
        }
        // всё закончилось хорошо, подключение работает, поток данных должен идти
        return true;
    }

    public void stopReceiver() {
        isStopped = true;
        uiHandler.removeMessages(MSG_SUCCESS);
        uiHandler.removeMessages(MSG_EMPTY);
        dataContainer = null;
    }

    @Override
    public void start() {
        super.start();
        isStopped = false;
    }


    @Override
    public void run() {
        if (!connect(this.address, PORT)) {
            uiHandler.sendEmptyMessage(MSG_FAIL);
            return;
        }
        Log.i(ERRTAG_RECEIVER, "DataReceiver has started");
        String json;
        try {
            while (!isStopped) { // max SO_TIMEOUT for iteration
                try {
                    json = in.readLine();
                } catch (SocketTimeoutException et) {
                    Log.i(ERRTAG_RECEIVER, "SocketTimeoutException! No data for 5 seconds, check the backend!");
                    uiHandler.sendEmptyMessage(DataReceiver.MSG_EMPTY);
                    // продолжаем, возможно, на следующей итерации будет останов
                    continue;
                }
                if (json != null) {
                    try {
                        dataContainer = new ACDataContainer(json);
                        if (!uiHandler.hasMessages(MSG_SUCCESS) && !uiHandler.hasMessages(MSG_EMPTY))
                            uiHandler.sendEmptyMessage(MSG_SUCCESS);
                        else {
                            Log.e(ERRTAG_RECEIVER, "Dropped a message - sender is too fast!");
                        }
                    } catch (ParseException e) {
                        Log.e(ACDataContainer.ERRTAG_DATA, "Got broken JSON packet");
                    }
                } else break;
            }
            Log.i(ERRTAG_RECEIVER, "Data receiver was normally stopped");
            uiHandler.sendEmptyMessage(MSG_STOP); // TODO: Разделить нормальный и ошибочный останов
        } catch (IOException e) {
            Log.e(ERRTAG_RECEIVER, "Connection reset");
            uiHandler.sendEmptyMessage(MSG_FAIL); // // TODO: Вылет при Connection reset - требует проверки
        } finally {
            try {
                conn.close();
            } catch (IOException e) {
                Log.e(ERRTAG_RECEIVER, "Tried to close the connection but something went wrong");
            }
        }
    }

    // It is syncronized, but probably that isn't really needed
    public synchronized ACDataContainer getDataContainer() {
        return dataContainer;
    }
}
