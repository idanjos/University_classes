/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.assets.DB;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import javax.json.stream.JsonParser;


/**
 *
 * @author mint
 */
@Provider
@Path("tests")
public class test {
    @Path("ping")
    @GET
    public String ping(){
        return "pong";
    }
    
    @Path("pingJSON")
    @GET
    @Produces("application/json")
    public Map<String,String> helloJSONList(){
        //List<String> jsonList = new ArrayList<String>();
        Map<String,String> map = new HashMap<>();
        map.put("ping", "pong");
        map.put("counter", String.valueOf(incrementor.counter));
        map.put("raw", String.valueOf(incrementor.raw));
        return map;
                
    }

    @Path("states")
    @GET
    @Produces("application/json")
    public Map<String,String> JSON_States(){
        DB db = new DB();
        Map<String,String> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            if(incrementor.raw != "")
            db = mapper.readValue(incrementor.raw, DB.class);
        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
        map.put("time",String.valueOf(db.getTime()));
        map.put("states", db.getAircrafts().toString());
        return map;
                
    }
}
