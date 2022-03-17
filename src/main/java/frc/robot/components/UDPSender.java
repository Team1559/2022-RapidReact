package frc.robot.components;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSender {
    private int port;
    private String address;
    private InetAddress to;
    private DatagramSocket soc;

    private boolean socketOpen = false;

    /**
     * Creates and opens a socket to the specidied address and sends data over the
     * specified port
     * 
     * @param address The address to send data to
     * @param port    The port to send data to
     */
    public UDPSender(String address, int port) {
        this.port = port;
        this.address = address;
        openSocket();
    }

    /**
     * Creates and opens a socket to the specified address and sends data over port
     * <code>5005</code>
     * 
     * @param address The address to send data to
     */
    public UDPSender(String address) {
        this(address, 5005);
    }

    /**
     * Creates and opens a socket to <code>10.15.59.17</code> and sends data over
     * the specified port
     * 
     * @param port The port to send data to
     */
    public UDPSender(int port) {
        this("10.15.59.17", port);
    }

    /**
     * Creates and opens a socket to <code>10.15.59.17</code> and sends data over
     * port <code>5005</code>
     */
    public UDPSender() {
        this("10.15.59.17", 5005);
    }

    /**
     * Opens the socket
     */
    public void openSocket() {
        if (!socketOpen) {
            try {
                to = InetAddress.getByName(address);
                soc = new DatagramSocket();
            } catch (Exception e) {
                // e.printStackTrace();
            }
            socketOpen = true;
        }
    }

    /**
     * Sends provided data to the target specified during instantiation (or the
     * default one if none is provided)
     * 
     * @param text Data to be sent
     */
    public void send(String text) {
        byte[] data = text.getBytes();
        try {
            DatagramPacket pac = new DatagramPacket(data, data.length, to, port);
            soc.send(pac);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * Closes the Socket
     */
    public void closeSocket() {
        if (socketOpen) {
            soc.close();
            socketOpen = false;
        }
    }
}
