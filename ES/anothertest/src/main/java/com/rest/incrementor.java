/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.assets.DB;
import com.rest.assets.Util2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.json.stream.JsonParser;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author mint
 */
@Singleton
public class incrementor {
    public static int counter = 0;
    public static String raw = "";
    public static boolean ERROR = false;
    public static DB db;
    @Schedule(hour="*", minute="*",second="*/10", persistent=false)
    
    public void myTimer() {
        try {
            Client req = Util2.getUnsecureClient();
            Builder request = req.target("https://opensky-network.org/api/states/all").request();
            Response response = request.get();
            raw = response.readEntity(String.class);
            //System.out.println(response.getEntity());
            counter++;
            db = new DB();
            ObjectMapper mapper = new ObjectMapper();
            try {
                if(incrementor.raw != "")
                db = mapper.readValue(incrementor.raw, DB.class);
            } catch (IOException ex) {
                Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Timer event: " + new Date());
        } catch (Exception ex) {
            Logger.getLogger(incrementor.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
       
       

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
}
