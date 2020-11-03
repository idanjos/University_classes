/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deti.ua.pt.LabProject;

import deti.ua.pt.LabProject.entities.Flight;
import deti.ua.pt.LabProject.repositories.FlightRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import deti.ua.pt.LabProject.producer.Sender;

@Component

@EmbeddedKafka(partitions = 1,
    topics = {"eeee"})
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(Scheduled.class);
    @Autowired
    private FlightRepository flightRepo;
    @Autowired
    Sender sender;

    @Scheduled(fixedRate = 10000)
    public void run() throws Exception {

        Instant instant = Instant.now();
        Long before = instant.getEpochSecond() - (60 * 60 * 24);
        Long after = instant.getEpochSecond();

        URL urlForGetRequest = new URL("https://luisoliveira:12345678@opensky-network.org/api/flights/departure?airport=EGLL&begin=" + before + "&end=" + after);
        String readLine = null;
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");

        int responseCode = conection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()))) {
                response = new StringBuilder();
                while ((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
            }

            JSONArray jsonArray = new JSONArray(response.toString());
            //System.out.println(jsonArray.toString());
            //.info(jsonArray.toString());
            sender.send(jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject temp = jsonArray.getJSONObject(i);
                String arrival;
                if (temp.get("estArrivalAirport").equals(null)) {
                    arrival = "the airport could not be identified";
                } else {
                    arrival = (String) temp.get("estArrivalAirport");
                }
                Flight flight = new Flight((String) temp.get("icao24"), (int) temp.get("firstSeen"), (String) temp.get("estDepartureAirport"), (int) temp.get("lastSeen"), arrival);
                if (!Flight.blacklist.contains(flight)) {
                    flightRepo.save(flight);
                }
                flightRepo.flush();
            }
        }
    }

}
