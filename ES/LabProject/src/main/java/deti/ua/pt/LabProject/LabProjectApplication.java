package deti.ua.pt.LabProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class LabProjectApplication {
        public static LogConsumer c = new LogConsumer();
        public static EventConsumer events = new EventConsumer();
	public static void main(String[] args) {
                //Consumer c = new LogConsumer();
                Thread ct = new Thread(c);
                Thread eventss = new Thread(events);
                //ct.join();
                ct.start();
                eventss.start();
		SpringApplication.run(LabProjectApplication.class, args);
	}

}
