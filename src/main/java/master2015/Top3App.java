package master2015;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

public class Top3App {

	private static String apiKey;
	private static String apiSecret;
	private static String token;
	private static String tokenSecret;
	private static String mode; // 1: File, 2: URL
	private static String kafkaURL; // IP:port
	private static String fileName;

	private static TweetReader reader;

	public static void main(String[] args) {

		if ((args.length != 0 && args[0].equals("1") && args.length != 7) || args.length != 6) {
			System.out.println("Incorrect arguments");
			System.exit(1);
		}

		mode = args[0];
		apiKey = args[1];
		apiSecret = args[2];
		token = args[3];
		tokenSecret = args[4];
		kafkaURL = args[5];

		// Init Kafka producer
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURL);
		props.put(ProducerConfig.RETRIES_CONFIG, "3");
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 200);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		KafkaProducer <String, String> producer = new KafkaProducer<>(props);

		reader = new TweetReader(apiKey, apiSecret, token, tokenSecret, producer);
		
		if (mode.equals("1")) {
			fileName = args[6];
			reader.read(fileName);
		}
		else {
			reader.read();
		}

	}

}
