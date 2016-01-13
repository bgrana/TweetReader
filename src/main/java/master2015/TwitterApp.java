package master2015;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

public class TwitterApp {

	private static String kafkaURL; // IP:port
	private static Properties props;

	public static void main(String[] args) {

		if( args.length != 7){
			System.err.println("Incorrect number of arguments:\n" +
					"Usage: mode{1|2} apiKey apiSecret token tokenSecret kafkaUrl filename");
			System.exit(4);
		}

		//Always use 7 parameters
		String mode = args[0];
		String apiKey = args[1];
		String apiSecret = args[2];
		String token = args[3];
		String tokenSecret = args[4];
		kafkaURL = args[5];
		String fileName = args[6];

		initProperties();
		KafkaProducer <String, String> producer = new KafkaProducer<>(props);
		TweetReader reader = new TweetReader(producer);

		switch (mode){
			case "1": //Read from file
				reader.read(fileName);
				break;
			case "2": //Read from twitter live stream
				reader.connect(apiKey, apiSecret, token, tokenSecret);
				reader.read();
				break;
			default:
				System.err.println("Mode \'" + mode + "\' is not valid. \n" +
						"Valid modes: \n" +
						"\t1 (to read from file)\n" +
						"\t2 (to read from Twitter live stream");
				System.exit(5);
		}
	}

	private static void initProperties(){
		// Init Kafka producer
		props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaURL);
		props.put(ProducerConfig.RETRIES_CONFIG, "3");
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 200);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
	}

}
