/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import assets.AppState;
import assets.WorkerState;
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
public class CC implements Runnable {

    /**
     * Synchronized lock object
     */
    public static final Object lock = new Object();

    /**
     * ReentrantLockoObject
     */
    public static ReentrantLock RE = new ReentrantLock();
    
    /**
     * MAXSTEPS constant
     */
    public static final int MAXSTEPS = 10;
    
    /**
     * Workers FI address
     */
    private int[] workerAddress;
    
    /**
     * Workers state array
     */
    public static WorkerState[] workerStates;
    
    /**
     * Array of boxes
     */
    public static int[] boxes;
     
    /**
      * Movement order 
      */
    public static int[] orderMovement;
    
    /**
     * Granary Boxes
     */
    public static int[] granaryBoxes;
    
    /**
     * Finished array
     */
    public static int[] finished;
    
    /**
     *  Server object
     */
    public Server server;
    
    /**
     * Server thread
     */
    public Thread serverT;
    
    /**
     * Application State
     */
    public AppState state = AppState.DEFINITION;
    
    /**
     * App Map
     */
    public static int[][] map;
    
    /**
     *  Corn collected
     */
    public static int cornCobs = 0;
    
    /**
     * Condition to stop Control Center
     */
    public boolean Stop = false;
    
    /**
     * Current turn
     */
    public static int currentTurn = 0;

    
    
    public CC() {

    }

    /**
     * Main Function, this starts the Server
     * @param args 
     */
    public static void main(String[] args) {
        CC cc = new CC();
        cc.server = new Server();
        cc.serverT = new Thread(cc.server);
    }

    
    /**
     * <p>
     * This function returns the number of Nodes being used by this Control Center.
     * </p>
     *
     * @return the number of nodes.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int getNumofNodes() {
        return server.getConnections().size();
    }  

    /**
     * <p>
     * This function sends to System.out connected/found Nodes.
     * </p>
     *
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void printNodes() {
        server.getConnections().forEach((cs) -> {
            System.out.println(cs);
        });
    }

    /**
     * <p>
     * This function sends to System.out registered logs by the Control Center.
     * </p>
     *
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void printLogs() {
        CCUI.logs.forEach((s) -> {
            System.out.println(s);
        });
    }

    /**
     * <p>
     * This function set the current state of the application.
     * </p>
     *
     * @param s given AppState.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void setState(AppState s) {
        this.state = s;
        synchronized (lock) {
            lock.notify();
        }

    }

    /**
     * <p>
     * This function validates all workers states.
     * </p>
     *
     * @param state given WorkerState
     * @return true if workers are on the given state, otherwise false.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean isAllWorkersState(WorkerState state) {

        for (WorkerState s : workerStates) {
            if (s != state) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * This function instantiates the Workers in each Node.
     * </p>
     *
     * @param x number of workers.
     * @param timeout delay of the workers.
     * @param steps max steps of the workers.  
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void createFarmers(int x, int timeout, int steps) { //load balancing

        workerStates = new WorkerState[x];
        boxes = new int[x];
        granaryBoxes = new int[x];
        orderMovement = new int[x];
        finished = new int[x];
        map = new int[x][MAXSTEPS];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < MAXSTEPS; j++) {
                map[i][j] = -1;
            }

        }
        workerAddress = new int[x];
        for (int i = 0; i < x; i++) {
            boxes[i] = -1;
            granaryBoxes[i] = -1;
            orderMovement[i] = -1;
            finished[i] = -1;
            int index = i % server.getConnections().size();
            server.getConnections().get(index).send("CreateWorker:" + i + ":" + timeout + ":" + steps + ":");
            workerStates[i] = WorkerState.NONE;
            workerAddress[i] = index;
            CCUI.logs.add("[CC]Farmer " + i + " on node:" + index + ">" + server.getConnections().get(index));
        }
    }

    /**
     * <p>
     * This function broadcasts a message to all Nodes.
     * </p>
     *
     * @param msg given message to all Nodes.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void msgNodes(String msg) {
        server.getConnections().stream().map((cs) -> {
            cs.send(msg);
            return cs;
        }).forEachOrdered((cs) -> {
            CCUI.logs.add("[CC]Signal Node " + server.getConnections().indexOf(cs) + " " + msg);
        });
    }

    
    /**
     * <p>
     * This function clears the map, used on Collect phase.
     * </p>
     *
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void clearMap() {
        currentTurn = 0;
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = -1;

            orderMovement[i] = -1;
            finished[i] = -1;
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = -1;
            }
        }
    }

    /**
     * <p>
     * This function clears the UI.
     * </p>
     *
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void INIT() {
        Stop = false;
        currentTurn = 0;
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = -1;
            granaryBoxes[i] = -1;
            orderMovement[i] = -1;
            finished[i] = -1;
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = -1;
            }
        }

    }

    /**
     * <p>
     * This function validates selected boxes.
     * </p>
     *
     * @return true if all boxes are selected, otherwise false.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean boxesAreSelected() {
        for (int i : boxes) {
            if (i == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * This function validates selected boxes.
     * </p>
     *
     * @return true if all granaryboxes are selected, otherwise false.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean granaryAreSelected() {
        for (int i : boxes) {
            if (i == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Run function of the Control Center
     * </p>
     *
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    @Override
    public void run() {
        CCUI.logs.add("[CC] CC Started");
        server = new Server();
        serverT = new Thread(server);
        try {
            serverT.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(CC.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverT.start();
        state = AppState.DEFINITION;
        CCUI.logs.add("[CC] Changed state to DEFINITION");
        while (true) {

            synchronized (lock) {

                try {
                    while (state == AppState.DEFINITION) {
                        lock.wait();
                    }

                    while (!boxesAreSelected() && !Stop) {

                        lock.wait();
                    }
                    if (Stop) {
                        continue;
                    }
                    msgNodes("PTP:" + boxes.length + ":");

                    while (!isAllWorkersState(WorkerState.INITIAL) && !Stop) {
                        lock.wait();
                    }

                } catch (InterruptedException ex) {
                    Logger.getLogger(CC.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }

}
