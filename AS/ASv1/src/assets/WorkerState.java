/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assets;

/**
 *
 * @author mint
 */
// Worker States
public enum WorkerState {
    NONE,
    INITIAL,
    PREPARE,
    WALK,
    WaitToCollect,
    COLLECT,
    WaitToReturn,
    RETURN,
    STORE
}
