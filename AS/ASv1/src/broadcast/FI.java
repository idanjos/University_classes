/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package broadcast;

import assets.Worker;
import assets.WorkerState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantLock;
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
public class FI implements Runnable {

    /**
     * FI Lock object
     */
    public static final Object LOCK = new Object();

    /**
     * Current active Workers
     */
    public static int currentWorkers = 0;

    /**
     * ReentrantLock object
     */
    private final ReentrantLock lLOCK = new ReentrantLock();

    /**
     * PathMonitor object
     */
    private final PathMonitor pm = new PathMonitor();

    /**
     * Granary Monitor object
     */
    private final GranaryMonitor gm = new GranaryMonitor();
    
    public boolean ProceedToPath = false;
    public boolean ProceedToArea = false;
    public boolean ProceedReturn = false;
    public boolean ProceedToCollect = false;
    public boolean Reset = false;
    public boolean Terminate = false;
    private DatagramSocket socket = null;
    private SocketServer ss;

    /**
     * <p>
     * This function returns the Path Monitor object of the FI.
     * </p>
     *
     * @return the Path Monitor object of the FI.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public PathMonitor getPm() {
        return pm;
    }

    /**
     * <p>
     * This function returns the Granary Monitor object of the FI.
     * </p>
     *
     * @return the Granary Monitor object of the FI.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public GranaryMonitor getGm() {
        return gm;
    }
    
    public static void main(String[] args) {
        
        new FI().run();
    }

    /**
     * <p>
     * This function represents the StandingArea for the Workers.
     * </p>
     *
     * @param w is the current Worker thread.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void StandingArea(Worker w) {
        
        synchronized (LOCK) {
            try {
                
                w.setState(WorkerState.PREPARE);
                w.getFi().ss.send(w.toString());
                
                while (!ProceedToPath) {
                    if (Terminate || Reset) {
                        return;
                    }
                    LOCK.wait();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * <p>
     * This function represents the corn storage for the Workers.
     * </p>
     *
     * @param w is the current Worker thread.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void saveCorn(Worker w) {
        synchronized (LOCK) {
            w.setState(WorkerState.STORE);
            w.getFi().ss.send(w.toString());
            ss.send("Send:" + w.getNumber() + ":" + w.getCornCollected() + ":");
        }
    }

    /**
     * <p>
     * This function represents the StoreHouse for the Workers.
     * </p>
     *
     * @param w is the current Worker thread.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void StoreHouse(Worker w) {
        synchronized (LOCK) {
            w.setState(WorkerState.INITIAL);
            w.getFi().ss.send(w.toString());
            while (!ProceedToArea) {
                try {
                    if (Terminate) {
                        return;
                    }
                    LOCK.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(FI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * <p>
     * This function constraint the Workers, wait until CC orders to return.
     * </p>
     *
     * @param w is the current Worker thread.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void waitForReturn(Worker w) {
        synchronized (LOCK) {
            
            w.setState(WorkerState.WaitToReturn);
            w.getFi().ss.send(w.toString());
            while (!w.getFi().ProceedReturn) {
                try {
                    if (Terminate || Reset) {
                        return;
                    }
                    LOCK.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(FI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            LOCK.notifyAll();
            
            
        }
    }

    /**
     * <p>
     * The goal of FI is to broadcast their location. Once CC connects, they
     * stop broadcasting.
     * </p>
     *
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
                Terminate = false;
                ProceedToArea = false;
                currentWorkers = 0;
                //PathMonitor.finished =0;
                ss = new SocketServer(this);
                Thread ssT = new Thread(ss);
                ssT.join();
                ssT.start();
                Thread.sleep(100);
                
                while (ss.state.equals("not connected")) {
                    int port = ss.port;
                    String ip = getLANIP();
                    broadcast("Slave:" + ip + ":" + String.valueOf(port) + ":", InetAddress.getByName("255.255.255.255"));
                    Thread.sleep(100); //Avoid SPAM
                }
                while (ss.state.equals("connected")) {
                    synchronized (LOCK) {
                        
                        LOCK.wait();
                        
                    }
                }
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(FI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    /**
     * <p>
     * This function returns the socketServer of FI.
     * </p>
     *
     * @return SocketServer object.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public SocketServer getSs() {
        return ss;
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
            e.printStackTrace();
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

    /**
     * <p>
     * This function create and deploy datagrampackets.
     * </p>
     *
     * @param broadcastMessage, payload of the packet.
     * @param address destination.
     *
     * @throws java.io.IOException
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void broadcast(
            String broadcastMessage, InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        
        byte[] buffer = broadcastMessage.getBytes();
        
        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
        socket.close();
    }
    
}
