
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package broadcast;

import assets.Worker;
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
public class SocketServer implements Runnable {

    /**
     * Workers array
     */
    private final Worker[] workers = new Worker[5];

    /**
     * Local port
     */
    public int port = 0;

    /**
     * Control Center ip address
     */
    private String ccHostname;

    /**
     * Control Center Port
     */
    private int ccPort;
    public String state = "not connected";
    FI fi;

    /**
     * Constructor for SocketServer class
     *
     * @param m
     */
    public SocketServer(FI m) {
        fi = m;
        for (int i = 0; i < workers.length; i++) {
            workers[i] = null;
        }
    }

    /**
     * <p>
     * This function sends a socket message to the connected ControlCenter.
     * </p>
     *
     * @param msg payload of the packet.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void send(String msg) {
        if (state.equals("connected")) {
            OutputStream output;
            try {
                //System.out.println(ccHostname);
                Socket socket = new Socket(ccHostname, ccPort);
                output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(msg);

            } catch (IOException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * <p>
     * Function Main, receives and handles connections.
     * </p>
     *
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void main() {

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
            Socket socket;
            InputStream input;
            BufferedReader reader;
            System.out.println("Server is listening on port " + port);
            for (int i = 0; i < workers.length; i++) {
                workers[i] = null;
            }
            while (!fi.Terminate) {
                socket = serverSocket.accept();
                ccHostname = socket.getInetAddress().getHostName();
                state = "connected";
                input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                String data;
                data = reader.readLine();
                if (data.contains("CreateWorker")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    int timeout = Integer.parseInt(temp[2]);
                    int steps = Integer.parseInt(temp[3]);
                    if (FI.currentWorkers >= workers.length) {
                        send("ErrorOnCreation:" + num + ":");
                    } else {
                        for (int i = 0; i < workers.length; i++) {
                            if (workers[i] != null) {
                                continue;
                            }
                            Worker w = new Worker(num, timeout, steps, fi, fi.getPm(), fi.getGm());
                            workers[i] = w;

                            new Thread(w).start();
                            break;
                        }

                    }
                }

                if (data.contains("ACK")) {
                    String[] temp = data.split(":");
                    ccPort = Integer.parseInt(temp[1]);
                    //send("end");
                }

                if (data.contains("Prepare")) {
                    fi.Reset = false;
                    fi.ProceedReturn = false;
                    fi.ProceedToArea = false;
                    fi.ProceedToPath = false;
                    fi.ProceedToCollect = false;
                }
                if (data.contains("Start")) {
                    fi.Reset = false;
                    fi.ProceedReturn = false;
                    fi.ProceedToArea = false;
                    fi.ProceedToPath = false;
                    fi.ProceedToCollect = false;
                    PathMonitor.turns = 0;
                    PathMonitor.finished = 0;
                    if (!areWorkersOnState(WorkerState.INITIAL)) {
                        send("ErrorOnStart:Workers are not all INITIAL!");
                    } else {
                        for (Worker w : workers) {
                            if (w != null) {
                                w.setBox(-1);
                                w.setCornCollected(0);
                                w.setGranaryBox(-1);
                                w.setStepsTaken(0);
                                w.setTurn(-1);

                            }
                        }
                        synchronized (FI.LOCK) {
                            fi.ProceedToArea = true;
                            FI.LOCK.notifyAll();
                        }

                    }
                }
                if (data.contains("Collect")) {
                    synchronized (FI.LOCK) {
                        fi.ProceedToCollect = true;
                        fi.ProceedToArea = false;
                        FI.LOCK.notifyAll();
                    }
                }
                if (data.contains("Return")) {
                    PathMonitor.finished = 0;
                    PathMonitor.turns = 0;
                    for (Worker w : workers) {
                        if (w != null) {
                            w.setBox(-1);
                        }
                    }
                    synchronized (FI.LOCK) {
                        fi.ProceedReturn = true;
                        FI.LOCK.notifyAll();
                    }
                }
                if (data.contains("Stop")) {
                    synchronized (FI.LOCK) {
                        fi.ProceedReturn = false;
                        fi.ProceedToArea = false;
                        fi.ProceedToPath = false;
                        fi.Reset = true;
                        FI.LOCK.notifyAll();
                    }
                }

                if (data.contains("Exit")) {
                    synchronized (FI.LOCK) {
                        fi.Terminate = true;
                        state = "not connected";
                        FI.LOCK.notifyAll();
                        
                    }

                }

                if (data.contains("PTP")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    FI.currentWorkers = num;
                    synchronized (FI.LOCK) {
                        fi.ProceedToPath = true;

                        FI.LOCK.notifyAll();
                    }

                }

                if (data.contains("SkipMove")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    int turn = Integer.parseInt(temp[2]);
                    for (Worker w : workers) {
                        if (w != null) {
                            if (w.getNumber() == num) {
                                PathMonitor.turns--;
                                break;
                            }
                        }
                    }
                    synchronized (FI.LOCK) {
                        PathMonitor.turns++;
                        FI.LOCK.notifyAll();
                    }

                }

                if (data.contains("Order")) {

                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    int turn = Integer.parseInt(temp[2]);
                    for (Worker w : workers) {
                        if (w != null) {
                            if (w.getNumber() == num) {
                                w.setTurn(turn);
                                w.setStepsTaken(1);
                                break;
                            }
                        }
                    }
                    synchronized (FI.LOCK) {

                        FI.LOCK.notifyAll();
                    }
                }

                if (data.contains("ConfirmMove")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    int steps = Integer.parseInt(temp[2]);
                    for (Worker w : workers) {
                        if (w != null) {
                            if (w.getNumber() == num) {
                                w.setStepsTaken(w.getStepsTaken() + steps);

                                break;
                            }
                        }
                    }

                    synchronized (FI.LOCK) {
                        PathMonitor.turns++;
                        FI.LOCK.notifyAll();
                    }
                }

                if (data.contains("EndMove")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    int granary = Integer.parseInt(temp[2]);
                    for (Worker w : workers) {
                        if (w != null) {
                            if (w.getNumber() == num) {
                                w.setGranaryBox(granary);
                                break;
                            }
                        }
                    }
                    synchronized (FI.LOCK) {
                        PathMonitor.finished++;
                        PathMonitor.turns++;
                        FI.LOCK.notifyAll();
                    }
                }
                if (data.contains("ConfirmBack")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    int steps = Integer.parseInt(temp[2]);
                    for (Worker w : workers) {
                        if (w != null) {
                            if (w.getNumber() == num) {
                                w.setStepsTaken(w.getStepsTaken() + steps);

                                break;
                            }
                        }
                    }

                    synchronized (FI.LOCK) {
                        PathMonitor.turns++;
                        FI.LOCK.notifyAll();
                    }
                }

                if (data.contains("EndBack")) {
                    String[] temp = data.split(":");
                    int num = Integer.parseInt(temp[1]);
                    int box = Integer.parseInt(temp[2]);
                    for (Worker w : workers) {
                        if (w != null) {
                            if (w.getNumber() == num) {
                                w.setBox(box);
                                break;
                            }
                        }
                    }
                    synchronized (FI.LOCK) {
                        PathMonitor.finished++;
                        PathMonitor.turns++;
                        FI.LOCK.notifyAll();
                    }
                }
                socket.close();

            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }

    /**
     * <p>
     * This function returns the array of Workers of this FI.
     * </p>
     *
     * @return Workers of this FI.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public Worker[] getWorkers() {
        return workers;
    }

    /**
     * <p>
     * This function checks if all the Workers are on a given state.
     * </p>
     *
     * @param state
     * @return true if all Workers are on the given state, otherwise false.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean areWorkersOnState(WorkerState state) {
        for (Worker w : workers) {
            if (w != null) {
                if (w.getState() != state) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void run() {
        main();
    }
}
