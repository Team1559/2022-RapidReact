package frc.robot.components;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * This class is responsible for listining over UDP.
 */
public class UDPReceiver implements Runnable {
    // private static final String HOST = "169.254.210.151";// "10.15.59.6"; //
    // 169.254.227.6
    private int port;
    private Thread clientThread;
    boolean running;
    String data;
    DatagramSocket socket;
    byte[] receive;

    /**
     * Creates a listening socket that listens on port <code>5801</code>
     */
    public UDPReceiver() {
        this(5801);
    }

    /**
     * Creates a listening socket that listens on the specified port
     * 
     * @param port The port to listen on
     */
    public UDPReceiver(int port) {
        this.port = port;
        clientThread = new Thread(this);
        receive = new byte[65535];

        try {
            socket = new DatagramSocket(this.port);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        clientThread.start();
    }

    /**
     * Receives data periodically
     */
    @Override
    public void run() {
        running = true;
        System.out.println("UDPClient thread running");
        while (running) {
            String rec = receive();
            if (rec != null) {
                data = rec;
            }
        }
    }

    /**
     * Returns the most recent data received
     * 
     * @return The most current data
     */
    public String getData() {
        return data;
    }

    /**
     * Gets the most current data
     * 
     * @return The most current data
     */
    private String receive() {
        String ret = null;
        DatagramPacket DpReceive = new DatagramPacket(receive, receive.length);

        try {
            socket.receive(DpReceive);
            InputStream is = new ByteArrayInputStream(receive);
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(is));
            ret = bfReader.readLine();
            // System.out.println(ret);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return ret;
    }
}