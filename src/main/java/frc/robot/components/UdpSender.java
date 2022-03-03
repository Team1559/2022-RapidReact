package frc.robot.components;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSender {
    private String address;
    private int port;

    public UdpSender(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public UdpSender(String address) {
        this(address, 5003);
    }

    public UdpSender(int port) {
        this("10.15.59.17", port);
    }

    public UdpSender() {
        this("10.15.59.17", 5003);
    }

    private void udpSender(String text, boolean close) throws java.io.IOException {
        InetAddress to = InetAddress.getByName(address);
        DatagramSocket soc = new DatagramSocket();
        if (close) {
            soc.close();
        }
        byte[] data = text.getBytes();
        DatagramPacket pac = new DatagramPacket(data, data.length, to, port);
        soc.send(pac);
    }

    public void send(String text) {
        try {
            udpSender(text, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            udpSender("", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
