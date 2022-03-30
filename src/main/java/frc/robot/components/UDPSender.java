package frc.robot.components;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSender {
    private int port;
    private InetAddress to;
    private DatagramSocket soc;

    public UDPSender(String address, int port) {
        this.port = port;
        try {
            to = InetAddress.getByName(address);
            soc = new DatagramSocket();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public UDPSender(String address) {
        this(address, 5802);
    }

    public UDPSender(int port) {
        this("10.15.59.217", port);
    }

    public UDPSender() {
        this("10.15.59.217", 5802);
    }

    public void send(String text) {
        byte[] data = text.getBytes();
        try {
            DatagramPacket pac = new DatagramPacket(data, data.length, to, port);
            soc.send(pac);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void closeSocket() {
        soc.close();
    }
}
