/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deti.ua.pt.LabProject.controllers;

import deti.ua.pt.LabProject.LogConsumer;
import deti.ua.pt.LabProject.LabProjectApplication;
import deti.ua.pt.LabProject.entities.Flight;
import deti.ua.pt.LabProject.repositories.FlightRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class WebController {

    private ArrayList<Flight> blacklist = new ArrayList<>();

    @Autowired
    private FlightRepository flightRepo;

    @GetMapping("/")
    public String index(Model model) throws IOException {
        List<Flight> flights = flightRepo.findAll();

        model.addAttribute("flights", flights);
        return "index";
    }

    @GetMapping("/logs")
    public String logs(Model model) throws IOException {
        //List<Flight> flights = flightRepo.findAll();

        model.addAttribute("logs", LabProjectApplication.c.logs);
        return "logs";
    }
    
    @GetMapping("/events")
    public String events(Model model) throws IOException {
        //List<Flight> flights = flightRepo.findAll();

        model.addAttribute("flights", LabProjectApplication.events.events);
        return "index";
    }
    
    @GetMapping("/delete/{id}")
    public RedirectView deleteUser(@PathVariable("id") String icao24, Model model) {
        Flight flight = flightRepo.findById(icao24)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + icao24));
        flightRepo.delete(flight);
        flightRepo.flush();
        Flight.blacklist.add(flight);
        //model.addAttribute("users", flightRepo.findAll());
        return new RedirectView("/");
    }

}
