/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deti.ua.pt.LabProject;

import deti.ua.pt.LabProject.entities.Flight;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author mint
 */
public class EventConsumer implements Runnable {

    public ArrayList<Flight> events = new ArrayList<>();
    @Override
    public void run() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-group");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        List topics = new ArrayList();
        topics.add("events");
        kafkaConsumer.subscribe(topics);
        try {
            while (true) {

                ConsumerRecords records = kafkaConsumer.poll(Duration.ofSeconds(10));
                //System.out.println(records.toString());
                if (records.count() > 0) {
                    
                    for (Iterator it = records.iterator(); it.hasNext();) {
                        events.clear();
                        ConsumerRecord record = (ConsumerRecord) it.next();
                        System.out.println(record.value().toString());
                        JSONArray jsonArray = new JSONArray(record.value().toString());
                        //System.out.println(jsonArray.toString());
                        //.info(jsonArray.toString());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject temp = jsonArray.getJSONObject(i);

                            Flight flight = new Flight((String) temp.get("icao24"), (int) temp.get("firstSeen"), (String) temp.get("estDepartureAirport"), (int) temp.get("lastSeen"), (String) temp.get("estArrivalAirport"));
                            events.add(flight);
                            //logs.add(record.value().toString());
                        } //for (ConsumerRecord record: records){
                        break;
                    }
                    
                }              //   System.out.println(String.format("Topic - %s, Partition - %d, Value: %s", record.topic(), record.partition(), record.value()));
                // }
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kafkaConsumer.close();
        }
    }

}
