/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Student of University of Aveiro.
 *
 * @author Isaac dos Anjos
 * @author 78191
 * @version 1.0
 * @since 1.0
 */
public class Server implements Runnable {

    /**
     * Datagram Socket object
     */
    private DatagramSocket socket;

    /**
     * Closing condition
     */
    private boolean running;

    /**
     * Connections array
     */
    private ArrayList<CCSocketServer> connections = new ArrayList<>();

    /**
     * IPs array, avoid duplicated packets
     */
    private ArrayList<String> ips = new ArrayList<>();

    /**
     * <p>
     * This function returns the number of Nodes being used by this Control
     * Center.
     * </p>
     *
     * @return the number of nodes.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    private boolean ipExists(String location) {
        if (ips.stream().anyMatch((ip) -> (ip.equals(location)))) {
            return true;
        }
        return false;
    }

    /**
     * <p>
     * Server Constructor
     * </p>
     *
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public Server() {
        try {
            socket = new DatagramSocket(4445);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>
     * This function returns all connections made.
     * </p>
     *
     * @return ArrayList of connections.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public ArrayList<CCSocketServer> getConnections() {
        return connections;
    }

    @Override
    public void run() {
        running = true;
        // System.out.println("Server running");
        CCUI.logs.add("[Server] Server Started");
        while (running) {
            byte[] buf = new byte[256];
            try {
                DatagramPacket packet
                        = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received
                        = new String(packet.getData(), 0, packet.getLength());
                if (received.contains("Slave")) {// make workers
                    String[] temp = received.split(":");
                    int pport = Integer.parseInt(temp[2]);
                    String ip = temp[1];

                    if (!ipExists(ip + ":" + pport)) {
                        CCSocketServer ttemp = new CCSocketServer(ip, pport);
                        new Thread(ttemp).start();
                        CCUI.logs.add("[Server]Added node: " + ip + ":" + pport);
                        ips.add(ip + ":" + pport);
                        connections.add(ttemp);
                    }

                }

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                socket.close();
            } catch (NumberFormatException e) {
                socket.close();
            }

        }

    }

    /**
     * <p>
     * This function returns the server's ip address.
     * </p>
     *
     * @return the current ip address.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public static String getLANIP() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface
                    .getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                if (!nic.isLoopback()) {
                    Enumeration<InetAddress> addrs = nic.getInetAddresses();
                    while (addrs.hasMoreElements()) {
                        InetAddress addr = addrs.nextElement();
                        if (validate(addr.getHostAddress()) && addr.getHostAddress().contains("192")) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {

        }
        return "localhost";
    }

    /**
     * <p>
     * This function validates if a given string is an ip address.
     * </p>
     *
     * @param ip a string that might represent an ip address.
     * @return a boolean, if the string is a valid ip.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public static boolean validate(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }
}
