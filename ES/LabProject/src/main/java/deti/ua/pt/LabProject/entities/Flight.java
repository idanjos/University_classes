/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deti.ua.pt.LabProject.entities;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.Id;
 


@Entity
public class Flight {
   
    public static ArrayList<Flight> blacklist = new ArrayList<>();
    @Id
    private String icao24;
    private String departureTime;
    private String departureAirport;
    private String arrivalTime;
    private String arrivalAirport;
    
    public Flight(){}
    
    public Flight(String icao24, int firstSeen, String departureAirport, int lastSeen, String arrivalAirport) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        this.icao24 = icao24;
        this.departureTime = dateFormat.format(new Date((long) firstSeen * 1000));
        this.departureAirport = departureAirport;
        this.arrivalTime = dateFormat.format(new Date((long) lastSeen * 1000));
        this.arrivalAirport = arrivalAirport;
    }

    public String getIcao24() {
        return icao24;
    }

    public void setIcao24(String icao24) {
        this.icao24 = icao24;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    @Override
    public String toString() {
        return "Flight{" + "icao24=" + icao24 + ", departureTime=" + departureTime + ", departureAirport=" + departureAirport + ", arrivalTime=" + arrivalTime + ", arrivalAirport=" + arrivalAirport + '}';
    }
    
    
    
}
