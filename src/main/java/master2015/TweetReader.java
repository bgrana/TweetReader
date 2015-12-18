package master2015;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kafka.admin.AdminUtils;

public class TweetReader implements Reader {
	private OAuthService service;
	private Token accessToken;
	private KafkaProducer <String, String> producer;

	public TweetReader(String apiKey, String apiSecret, String token,
			String tokenSecret, KafkaProducer<String, String> producer) {
		
		service = new ServiceBuilder()
				.provider(TwitterApi.class)
				.apiKey(apiKey)
				.apiSecret(apiSecret)
				.build();

		accessToken = new Token(token, tokenSecret);
		this.producer = producer;
	}

	private boolean isStartObject(JsonToken token) {
		if (token == JsonToken.START_OBJECT) {
			return true;
		}
		return false;
	}

	private boolean checkNode(JsonNode node, String toCheck) {
		if (node.has(toCheck)) {
			return true;
		}
		return false;
	}
	
	private void readHashtags( JsonNode node) {
		String value;
		String topic;
		String timestamp;
		for (JsonNode hashtag : node.get(ENTITIES).get(HASHTAGS)) {
			if (checkNode(hashtag, TEXT)) {
				value = hashtag.get(TEXT).toString().replace("\"", "");
				topic = node.get(LANGUAGE).toString().replace("\"", "");
				timestamp = node.get(TIMESTAMP).toString().replace("\"", "");
				producer.send(new ProducerRecord<String, String>(topic,timestamp + ";" + value));
				//TODO Comment debug
				//System.out.println("Hashtag sent: " + topic+";"+value+";"+timestamp);
			}
		}
	}

	private void readStream(InputStream stream) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory factory = mapper.getFactory();
			JsonParser parser = factory.createParser(stream);
			JsonToken token;
			JsonNode node;

			while ((token = parser.nextToken()) != null) {
				
				if (isStartObject(token)) {
					
					node = parser.readValueAsTree();
					if (checkNode(node, LANGUAGE) && checkNode(node, ENTITIES) &&
							checkNode(node.get(ENTITIES), HASHTAGS)) {
						
						readHashtags(node);
					}
				}
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void read() {
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://stream.twitter.com/1.1/statuses/sample.json");
		service.signRequest(accessToken, request);
		Response response = request.send();
		readStream(response.getStream());
	}
	
	public void read(String fileName) {
		File file = new File(fileName);
		try {
			readStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

}
