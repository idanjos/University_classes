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
public class GranaryMonitor {

    /**
     *  ReentrantLock object
     */
    private final ReentrantLock lock = new ReentrantLock();
    public boolean startCollecting = false;

    /**
     * <p>
     * This function represents the act of Entering the Granary for the Workers.
     * </p>
     *
     * @param w is the current Worker thread.   
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment Requirements</a>
     * @since 1.0
     */
    public void EnterGranary(Worker w) {
        synchronized (FI.LOCK) {
            w.setState(WorkerState.WaitToCollect);
            w.getFi().getSs().send(w.toString());
            while (!w.getFi().ProceedToCollect) {
                try {
                    if (w.getFi().Terminate || w.getFi().Reset) {
                        return;
                    }
                    FI.LOCK.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(GranaryMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * <p>
     * This function represents the act Collect for the Workers.
     * </p>
     *
     * @param w is the current Worker thread.   
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment Requirements</a>
     * @since 1.0
     */
    public void Collect(Worker w) {
        w.setState(WorkerState.COLLECT);
        while (w.getCornCollected() < 10) {
            if (w.getFi().Terminate || w.getFi().Reset) {
                return;
            }
            try {
                w.setCornCollected(w.getCornCollected() + 1);
                Thread.sleep(w.getTimeout());
            } catch (InterruptedException ex) {
                Logger.getLogger(GranaryMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
