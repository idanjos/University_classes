/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assets;

import broadcast.FI;
import broadcast.GranaryMonitor;
import broadcast.PathMonitor;
import java.util.Random;

/**
 * Student of University of Aveiro.
 *
 * @author Isaac dos Anjos
 * @author 78191
 * @version 1.0
 * @since 1.0
 */
public class Worker implements Runnable {

    /**
     *  Id number
     */
    private final int number;
    
    /**
     *  Delay time of each action
     */
    private int timeout;
    
    /**
     *  Worker State
     */
    private WorkerState state = WorkerState.NONE;
    
    /**
     *  Max number of steps
     */
    private int steps;
    
    /**
     *  FI Monitor object
     */
    private final FI fi;
    
    /**
     *  PathMonitor object
     */
    private final PathMonitor pm;
    
    /**
     *  Granary Monitor object
     */
    private final GranaryMonitor gm;
    
    /**
     *  steps taken
     */
    private int stepsTaken = 0;
    
    /**
     *  Corn collected
     */
    private int cornCollected = 0;
    
    /**
     *  Box number
     */
    private int box=-1;
    
    /**
     *  Granary box number
     */
    private int granaryBox = -1;
    
    /**
     *  Assigned turn
     */
    private int turn = -1;

    /**
     * Constructor for class Worker
     *
     * @param number id of the Worker
     * @param timeout delay in milliseconds
     * @param steps max steps of the Worker
     * @param fi FI Monitor object
     * @param pm PathMonitor object
     * @param gm Granary Monitor object
     */
    public Worker(int number, int timeout, int steps, FI fi, PathMonitor pm, GranaryMonitor gm) {
        this.number = number;
        this.timeout = timeout;
        this.steps = steps;
        this.fi = fi;
        this.pm = pm;
        this.gm = gm;
    }

    /**
     * <p>
     * This function returns the timeout value of the Worker.
     * </p>
     *
     * @return the amount of delay time milliseconds.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * <p>
     * This function sets the timeout value of the Worker.
     * </p>
     *
     * @param timeout is the amount of delay time milliseconds.
     * @see
     * <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * <p>
     * This function returns the step value of the Worker.
     * </p>
     *
     * @return the max amount of steps the Worker can take.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int getSteps() {
        return steps;
    }

    /**
     * <p>
     * This function sets the step value of the Worker.
     * </p>
     *
     * @param steps is the amount of delay time milliseconds.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }

    /**
     * <p>
     * This function returns the turn value of the Worker.
     * </p>
     *
     * @return the turn of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int getTurn() {
        return turn;
    }

    /**
     * <p>
     * This function sets the turn value of the Worker.
     * </p>
     *
     * @param turn is the turn of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * <p>
     * This function returns the granaryBox value of the Worker.
     * </p>
     *
     * @return the current GranaryBox of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int getGranaryBox() {
        return granaryBox;
    }

    /**
     * <p>
     * This function sets the granaryBox value of the Worker.
     * </p>
     *
     * @param granaryBox is the current GranaryBox of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void setGranaryBox(int granaryBox) {
        this.granaryBox = granaryBox;
    }

    /**
     * <p>
     * This function returns the cornCollected value of the Worker.
     * </p>
     *
     * @return the amount of corn collected by the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int getCornCollected() {
        return cornCollected;
    }

    /**
     * <p>
     * This function sets the cornCollected value of the Worker.
     * </p>
     *
     * @param cornCollected is he amount of corn collected by the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void setCornCollected(int cornCollected) {
        this.cornCollected = cornCollected;
    }

    /**
     * <p>
     * This function returns the box value of the Worker.
     * </p>
     *
     * @return the current box of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int getBox() {
        return box;
    }

    /**
     * <p>
     * This function returns the stepsTaken value of the Worker.
     * </p>
     *
     * @return the current steps taken by the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int getStepsTaken() {
        return stepsTaken;
    }

    /**
     * <p>
     * This function sets the stepsTaken value of the Worker.
     * </p>
     *
     * @param stepsTaken is the current steps taken by the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void setStepsTaken(int stepsTaken) {
        this.stepsTaken = stepsTaken;
    }

    /**
     * <p>
     * This function sets the box value of the Worker.
     * </p>
     *
     * @param box is the current box of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void setBox(int box) {
        this.box = box;
    }

    /**
     * <p>
     * This function sets the state value of the Worker.
     * </p>
     *
     * @param state is the current state of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public void setState(WorkerState state) {
        this.state = state;
    }

    /**
     * <p>
     * This function returns the state value of the Worker.
     * </p>
     *
     * @return the current state of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public WorkerState getState() {
        return state;
    }

    /**
     * <p>
     * This function returns the number value of the Worker.
     * </p>
     *
     * @return the number of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int getNumber() {
        return number;
    }

    /**
     * <p>
     * This function returns the FI Monitor of the Worker.
     * </p>
     *
     * @return the FI Monitor.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public FI getFi() {
        return fi;
    }

    /**
     * <p>
     * This function returns the string that represents the Worker.
     * </p>
     *
     * @return the string of the Worker.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    @Override
    public String toString() {
        return "Worker:" + number + ":" + state + ":";
    }

    /**
     * <p>
     * This function returns the random value between 1 and steps value of the
     * Worker.
     * </p>
     *
     * @return the number of steps.
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    public int nextStep() {
        Random r = new Random();
        return r.nextInt((steps - 1) + 1) + 1;
    }

    /**
     * <p>
     * This function is the Worker's lifecycle.
     * </p>
     *
     * @see <a href="https://elearning.ua.pt/pluginfile.php/969564/mod_resource/content/3/Practical_Assignment_1920_I.pdf">Assignment
     * Requirements</a>
     * @since 1.0
     */
    @Override
    public void run() {
        while (!fi.Terminate) {
            fi.StoreHouse(this);
            fi.StandingArea(this);
            pm.Enter(this);
            pm.Move(this);
            gm.EnterGranary(this);
            gm.Collect(this);
            fi.waitForReturn(this);
            pm.Retreat(this);
            pm.Return(this);
            fi.saveCorn(this);
        }
    }

}
