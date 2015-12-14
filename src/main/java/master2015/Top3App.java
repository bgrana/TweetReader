package master2015;

import org.apache.kafka.clients.producer.KafkaProducer;

public class Top3App {

	public static void main(String[] args) {
		
		KafkaProducer <String, String> producer = null;
		
		System.out.println("Simple Twitter app with param " + args[0]);
	}

}
