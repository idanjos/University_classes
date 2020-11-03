/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deti.ua.pt.LabProject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import deti.ua.pt.LabProject.entities.Log;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 *
 * @author mint
 */
public class LogConsumer implements Runnable {
    public ArrayList<Log> logs = new ArrayList<>();
    public void main() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-group");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        List topics = new ArrayList();
        topics.add("logs");
        kafkaConsumer.subscribe(topics);
        try{
            while (true){
                
                ConsumerRecords records = kafkaConsumer.poll(Duration.ofSeconds(10));
                //System.out.println(records.toString());
                if(records.count() >0){
                   
                for (Iterator it = records.iterator(); it.hasNext();) {
                    ConsumerRecord record = (ConsumerRecord)it.next();
                    //System.out.println("KAFKA: "+record.value().toString());
                    String[] temp = record.value().toString().split(",");
                    logs.add(new Log(temp[0],temp[1]));
                    
                } //for (ConsumerRecord record: records){
                //   System.out.println(String.format("Topic - %s, Partition - %d, Value: %s", record.topic(), record.partition(), record.value()));
                // }
                }
                Thread.sleep(10000);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            kafkaConsumer.close();
        }
    }

   
    

    @Override
    public void run() {
        main();
    }
    
}
