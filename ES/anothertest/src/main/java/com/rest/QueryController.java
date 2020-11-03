/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rest;

import com.rest.assets.Aircraft;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Path;
import javax.mvc.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 *
 * @author mint
 */
@Controller
@Path("query")
public class QueryController {

    @GET
    @Produces("application/json")
    public Map<String, String> countries(@QueryParam("country") String country){
        Map<String,String> map = new HashMap<>();
        map.put("time",String.valueOf(incrementor.db.getTime()));
        ArrayList<Aircraft> temp = new ArrayList<>();
        for(Aircraft ac : incrementor.db.getAircrafts()){
            if(ac.getOrigin_country().equalsIgnoreCase(country)){
                temp.add(ac);
            }
        }
        map.put("result",temp.toString());
        return map;
    }
}
