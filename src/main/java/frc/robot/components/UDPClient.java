package frc.robot.components;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * This class is responsible for communicating to a server over UDP.
 */
public class UDPClient implements Runnable {

    // private static final String HOST = "169.254.210.151";// "10.15.59.6"; //
    // 169.254.227.6
    private static final int PORT = 5801;

    Thread  clientThread;
    boolean running;

    String         data;
    DatagramSocket socket;
    byte[]         receive;

    public UDPClient() {
        clientThread = new Thread(this);

        receive = new byte[65535];
        try {
            socket = new DatagramSocket(PORT);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        clientThread.start();
    }

    @Override
    public void run() {
        running = true;
        System.out.println("UDPClient thread running");
        while (running) {
            String rec = receive();
            // System.out.println(rec);
            if (rec != null) {
                data = rec;
            }
        }
    }

    public String get() {
        return data;
    }

    public String receive() {
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
