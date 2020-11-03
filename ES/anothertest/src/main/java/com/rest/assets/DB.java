/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rest.assets;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mint
 */
public class DB {
    private int time;
    private Object[][] states = new Object[1000][16];
    private ArrayList<Aircraft> aircrafts= new ArrayList<>();

   
    
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Object[][] getStates() {
        return states;
    }

    public void setStates(Object[][] states) {
        this.states = states;
        
        for(Object[] objs : states){
           try{ 
            aircrafts.add(new Aircraft(String.valueOf(objs[0]), String.valueOf(objs[1]), String.valueOf(objs[2]), Float.parseFloat( String.valueOf(objs[5])),Float.parseFloat( String.valueOf(objs[6]))));
           }
           catch(Exception e)
           {
               aircrafts.add(new Aircraft(String.valueOf(objs[0]), String.valueOf(objs[1]), String.valueOf(objs[2]), 0,0));
           }
        }
        
    }
   public List<Aircraft> getAircrafts(){
       return aircrafts;
   }
    
    
}
