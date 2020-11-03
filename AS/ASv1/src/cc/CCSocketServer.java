package cc;

import assets.WorkerState;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
public class CCSocketServer implements Runnable {

    /**
     * Connection condition
     */
    private String state = "not connected";

    /**
     * FI IP address
     */
    private String fiHostname;

    /**
     * FI Port
     */
    private int fiPort;

    /**
     * Local Port
     */
    private int port;

    /**
     * Local IP address
     */
    private String hostname;

    public CCSocketServer() {
    }

    /**
     * ClientSocket Constructor
     *
     * @param ip
     * @param port
     */
    public CCSocketServer(String ip, int port) {
        this.fiHostname = ip;
        this.fiPort = port;
    }

    /**
     * <p>
     * This function returns a string representation of the object.
     * </p>
     *
     * @return the string representation of the object.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    @Override
    public String toString() {
        return "clientSocket{" + "state=" + state + ", hostname=" + hostname + ", port=" + port + '}';
    }

    /**
     * <p>
     * This function returns the Granary Monitor object of the FI.
     * </p>
     *
     * @param msg given message to send to FI.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void send(String msg) {

        try {
            Socket socket = new Socket(fiHostname, fiPort);
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(msg);
            CCUI.logs.add("[Ssocket] Sent " + msg + " to " + fiHostname + ":" + fiPort);

        } catch (IOException ex) {
            Logger.getLogger(CCSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void main() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {

            port = serverSocket.getLocalPort();
            hostname = Server.getLANIP();
            InputStream input;
            BufferedReader reader;
            Socket socket;
            send("ACK:" + port + ":");
            while (true) {

                socket = serverSocket.accept();

                state = "connected";

                input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                String data;
                data = reader.readLine();
                CCUI.logs.add("[SS] Received " + data);
                if (data.contains("Worker")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    CC.workerStates[num] = WorkerState.valueOf(temp[2]);
                }
                if (data.contains("PREPARE")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    send("BOX:" + num + ":" + addFarmertoBoxes(num) + ":");
                }

                if (data.contains("Skip")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    CCUI.cc.msgNodes("SkipMove:" + num + ":" + 0 + ":");
                }

                if (data.contains("Send")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                   int corn = Integer.parseInt(temp[2]);
                   saveCorn(corn);
                   

                }

                if (data.contains("Advance")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    CCUI.cc.msgNodes("Order:" + num + ":" + regOrder(num) + ":");
                    moveForward(num);
                }

                if (data.contains("Retreat")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    CCUI.cc.msgNodes("Order:" + num + ":" + regOrder(num) + ":");
                    if (!moveBackward(num)) {
                        System.err.println("Error in Moving back!");
                    }
                }

                if (data.contains("Back")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    int steps = Integer.parseInt(temp[2]);
                    if (CC.currentTurn >= 0) {
                        if (CC.orderMovement[CC.currentTurn] == num) {

                            if (moveBackward(num, steps)) {
                                CCUI.cc.msgNodes("ConfirmBack:" + num + ":" + steps + ":");
                            } else {
                                CC.finished[CC.currentTurn] = 1;
                                CCUI.cc.msgNodes("EndBack:" + num + ":" + addFarmertoBoxes(num) + ":");
                            }

                            CC.currentTurn = next(CC.currentTurn);

                        } else {
                            CCUI.logs.add("Not your turn" + data);

                        }
                    } else {
                        CCUI.logs.add("Error occure>" + data);
                    }

                }

                if (data.contains("Move")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    int steps = Integer.parseInt(temp[2]);
                    if (CC.currentTurn >= 0) {
                        if (CC.orderMovement[CC.currentTurn] == num) {

                            if (moveForward(num, steps)) {
                                CCUI.cc.msgNodes("ConfirmMove:" + num + ":" + steps + ":");
                            } else {
                                CC.finished[CC.currentTurn] = 1;
                                CCUI.cc.msgNodes("EndMove:" + num + ":" + addFarmertoGaranry(num) + ":");
                            }

                            CC.currentTurn = next(CC.currentTurn);

                        } else {
                        }
                    }

                }

                socket.close();

            }

        } catch (IOException ex) {
        }

    }

    /**
     * <p>
     * This function returns the next Worker in line.
     * </p>
     *
     * @param c current turn
     * @return the Granary Monitor object of the FI.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int next(int c) {
        synchronized (CC.lock) {
            while (!CC.RE.tryLock()) {
                try {
                    CC.lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CCSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            int i = 0;
            while (i < 6) {
                i++;
                c++;
                if (c >= CC.orderMovement.length || CC.orderMovement[c] == -1) {
                    c = 0;
                }
                if (CC.finished[c] == -1) {
                    CC.RE.unlock();
                    CC.lock.notifyAll();
                    return c;
                }
            }
            CC.RE.unlock();
            CC.lock.notifyAll();
        }
        return -127;

    }

    /**
     * <p>
     * This function attributes a Granary box to a given worker.
     * </p>
     *
     * @param w given Worker
     * @return the Granary box number associated.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int addFarmertoGaranry(int w) {
        synchronized (CC.lock) {
            while (!CC.RE.tryLock()) {
                try {
                    CC.lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CCSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (int i = 0; i < CC.granaryBoxes.length; i++) {
                if (CC.granaryBoxes[i] == -1 || CC.granaryBoxes[i] == w) {
                    CC.granaryBoxes[i] = w;
                    CC.RE.unlock();
                    CC.lock.notifyAll();
                    return i;
                }
            }
            CC.RE.unlock();
            CC.lock.notifyAll();
        }

        return -1;
    }

    /**
     * <p>
     * This function attributes a box to a given worker.
     * </p>
     *
     * @param w given Worker
     * @return the box number associated.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int addFarmertoBoxes(int w) {
        synchronized (CC.lock) {
            while (!CC.RE.tryLock()) {
                try {
                    CC.lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CCSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (int i = 0; i < CC.boxes.length; i++) {
                if (CC.boxes[i] == -1 || CC.boxes[i] == w) {
                    CC.boxes[i] = w;
                    CC.RE.unlock();
                    CC.lock.notifyAll();
                    return i;
                }
            }
            CC.RE.unlock();
            CC.lock.notifyAll();
        }

        return -1;
    }

    /**
     * <p>
     * This function checks the column of the map if the Worker already exists.
     * </p>
     *
     * @param w given Worker
     * @param i column
     * @return true if Worker exists, otherwise false.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean checkCollumn(int w, int i) {
        for (int[] map : CC.map) {
            if (map[i] == w) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * This function add the next Worker to the line.
     * </p>
     *
     * @param w given Worker
     * @return the order number.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int regOrder(int w) {
        synchronized (CC.lock) {
            while (!CC.RE.tryLock()) {
                try {
                    CC.lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CCSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (int i = 0; i < CC.orderMovement.length; i++) {
                if (CC.orderMovement[i] == -1) {
                    CC.orderMovement[i] = w;
                    CC.RE.unlock();
                    CC.lock.notifyAll();
                    return i;
                }
            }
            CC.RE.unlock();
            CC.lock.notifyAll();
        }

        return -1;
    }

    /**
     * <p>
     * This function moves the Workers forward in the Path, validates if
     * possible.
     * </p>
     *
     * @param w given Worker
     * @param steps steps given.
     * @return true if the Worker moved backward, false otherwise.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean moveForward(int w, int steps) {
        for (int i = 0; i < steps; i++) {
            if (!moveForward(w)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * This function moves the Workers backward in the Path, validates if
     * possible.
     * </p>
     *
     * @param w given Worker
     * @param steps steps given.
     * @return true if the Worker moved backward, false otherwise.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean moveBackward(int w, int steps) {
        for (int i = 0; i < steps; i++) {
            if (!moveBackward(w)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * This function moves the Workers backward in the Path, validates if
     * possible.
     * </p>
     *
     * @param w given Worker
     * @return true if the Worker moved forward, false otherwise.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean moveForward(int w) {
        synchronized (CC.lock) {
            while (!CC.RE.tryLock()) {
                try {
                    CC.lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CCSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (int i = 0; i < CC.MAXSTEPS; i++) {
                if (checkCollumn(w, i)) {
                    continue;
                }
                for (int[] map : CC.map) {
                    if (map[i] == -1) {
                        map[i] = w;
                        CC.RE.unlock();
                        CC.lock.notifyAll();
                        return true;
                    }
                }
            }
            CC.RE.unlock();
            CC.lock.notifyAll();
        }

        return false;
    }

    /**
     * <p>
     * This function moves the Workers backward in the Path, validates if
     * possible.
     * </p>
     *
     * @param w given Worker
     * @return true if the Worker moved backward, false otherwise.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean moveBackward(int w) {

        synchronized (CC.lock) {
            while (!CC.RE.tryLock()) {
                try {
                    CC.lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CCSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for (int i = CC.MAXSTEPS - 1; i > -1; i--) {
                if (checkCollumn(w, i)) {
                    continue;
                }
                for (int j = CC.map.length - 1; j >= 0; j--) {
                    if (CC.map[j][i] == -1) {
                        CC.map[j][i] = w;
                        CC.RE.unlock();
                        CC.lock.notifyAll();
                        return true;
                    }
                }
            }
            CC.RE.unlock();
            CC.lock.notifyAll();
        }

        return false;
    }
    
    /**
     * <p>
     * This function updates the corn collected.
     * </p>
     *
     * @param corn given corn
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void saveCorn(int corn) {

        synchronized (CC.lock) {
            while (!CC.RE.tryLock()) {
                try {
                    CC.lock.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CCSocketServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            CC.cornCobs+= corn;
            CC.RE.unlock();
            CC.lock.notifyAll();
        }

        
    }

    @Override
    public void run() {
        main();
    }
}
