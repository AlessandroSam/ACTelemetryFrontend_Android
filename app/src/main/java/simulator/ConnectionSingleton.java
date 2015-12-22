package simulator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ConnectionSingleton {
    private static ConnectionSingleton instance = null;
    private static int SO_TIMEOUT = 5000;
    private Socket socket;

    protected ConnectionSingleton() {
        // Singleton purpose only
    }

    public static ConnectionSingleton getInstance() {
        if(instance == null) {
            instance = new ConnectionSingleton();
        }
        return instance;
    }

    public void createSocket(InetAddress host, int port) throws IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(SO_TIMEOUT);
    }

    public void createSocket(String host, int port) throws IOException {
        SocketAddress sockaddr = new InetSocketAddress(host, port);
        socket = new Socket(host, port);
//        socket.connect(sockaddr, SO_TIMEOUT);
        socket.setSoTimeout(SO_TIMEOUT);
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() throws IOException {
        socket.close();
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(socket.getOutputStream());
    }

}
