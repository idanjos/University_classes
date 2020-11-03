/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package broadcast;

import assets.Worker;
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
public class PathMonitor {

    /**
     *  turns counter
     */
    public static int turns = 0;
    
    /**
     *  ReentrantLock object
     */
    private final ReentrantLock lock = new ReentrantLock();
    
    /**
     * finished counter
     */
    public static int finished = 0;

    public PathMonitor() {

    }

    /**
     * <p>
     * This function represents the Retreat for the Workers.
     * </p>
     *
     * @param w is the current Worker thread.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void Retreat(Worker w) {
        synchronized (FI.LOCK) {

            while (!lock.tryLock()) {
                try {
                    if (w.getFi().Terminate || w.getFi().Reset) {
                        return;
                    }
                    FI.LOCK.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PathMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            w.getFi().getSs().send("Retreat:" + w.getNumber() + ":");
            lock.unlock();
            FI.LOCK.notifyAll();

        }

    }

    /**
     * <p>
     * This function represents the Enter to Path for the Workers.
     * </p>
     *
     * @param w is the current Worker thread.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void Enter(Worker w) {
        synchronized (FI.LOCK) {
            while (!lock.tryLock()) {
                try {
                    if (w.getFi().Terminate || w.getFi().Reset) {
                        return;
                    }
                    FI.LOCK.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PathMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            w.getFi().getSs().send("Advance:" + w.getNumber() + ":");
            lock.unlock();
            FI.LOCK.notifyAll();
        }
    }

    /**
     * <p>
     * This function represents the Return for the Workers.
     * </p>
     *
     * @param w is the current Worker thread.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void Return(Worker w) {
        synchronized (FI.LOCK) {
            try {
                w.setState(WorkerState.RETURN);
                while (w.getStepsTaken() != 1) {
                    if (w.getFi().Terminate || w.getFi().Reset) {
                        return;
                    }
                    FI.LOCK.wait();
                }
                while (finished < FI.currentWorkers) {
                    while (turns % FI.currentWorkers != w.getTurn()) {
                        if (w.getFi().Terminate || w.getFi().Reset) {
                            return;
                        }
                        FI.LOCK.wait();
                    }
                    if (w.getBox() != -1) {
                        turns++;
                       
                        FI.LOCK.notifyAll();
                        w.getFi().getSs().send("Skip:" + w.getNumber() + ":" + 0 + ":");

                        continue;
                    }

                    while (!lock.tryLock()) {
                         if (w.getFi().Terminate || w.getFi().Reset) {
                            return;
                        }
                        FI.LOCK.wait();

                    }
                    int currentSteps = w.getStepsTaken();

                    while (currentSteps == w.getStepsTaken() && w.getBox() == -1) {
                        Thread.sleep(w.getTimeout());
                        w.getFi().getSs().send("Back:" + w.getNumber() + ":" + w.nextStep() + ":");
                        if (w.getFi().Terminate || w.getFi().Reset) {
                            return;
                        }
                        FI.LOCK.wait();

                    }

                    lock.unlock();
                    FI.LOCK.notifyAll();
                }
                //System.out.println("Moving?");
            } catch (InterruptedException ex) {
                Logger.getLogger(PathMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * <p>
     * This function verify if all Workers have received their Granary box.
     * </p>
     *
     * @param obj is the current Worker thread.
     * @return boolean true: if all workers have a box, other wise false.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public boolean areInGranaryBoxes(Worker obj) {
        for (Worker w : obj.getFi().getSs().getWorkers()) {
            if (w != null && w.getGranaryBox() == -1) {
                return false;
            }

        }
        return true;
    }

    /**
     * <p>
     * This function represents the Movement for the Workers.
     * </p>
     *
     * @param w is the current Worker thread.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void Move(Worker w) {

        synchronized (FI.LOCK) {
            try {
                w.setState(WorkerState.WALK);

                while (finished < FI.currentWorkers) {
                    while (turns % FI.currentWorkers != w.getTurn()) {
                       if (w.getFi().Terminate || w.getFi().Reset) {
                            return;
                        }
                        FI.LOCK.wait();
                    }
                    if (w.getGranaryBox() != -1) {
                        turns++;
                        
                        FI.LOCK.notifyAll();
                        w.getFi().getSs().send("Skip:" + w.getNumber() + ":" + 0 + ":");

                        continue;
                    }

                    while (!lock.tryLock()) {
                        if (w.getFi().Terminate || w.getFi().Reset) {
                            return;
                        }
                        FI.LOCK.wait();

                    }
                    int currentSteps = w.getStepsTaken();

                    while (currentSteps == w.getStepsTaken() && w.getGranaryBox() == -1) {
                        Thread.sleep(w.getTimeout());
                        w.getFi().getSs().send("Move:" + w.getNumber() + ":" + w.nextStep() + ":");
                        if (w.getFi().Terminate || w.getFi().Reset) {
                            return;
                        }
                        FI.LOCK.wait();

                    }

                    lock.unlock();
                    FI.LOCK.notifyAll();
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(PathMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
