package master2015;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import sun.rmi.rmic.Constants;

import java.io.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ignacio on 15/12/15.
 */

public class Tests implements master2015.Constants{
    public static OAuthService service;
    public static Token accessToken;

    public static void main(String[] args) {
        service = new ServiceBuilder().provider(TwitterApi.class)
                .apiKey("TpAIws7L1xVIxBrauHlUwmGZS")
                .apiSecret("vQB8o2tQy7Rw176y5wrUUEoS8tFh7ZonrMcd8UryOzqamA4VXR")
                .build();

        accessToken = new Token(
                "4428384195-edO0972flnIYe8nXgaObQIUSMGg02OwbTlQ0E2A",
                "m9O59Q6x7WC7VjVru8i8xQ1MEIGZlob8XPDcLdHdqrkFx");

        OAuthRequest req = new OAuthRequest(Verb.GET ,"https://stream.twitter.com/1.1/statuses/sample.json");
        service.signRequest(accessToken,req);
        Response res = req.send();

        if(res.getCode() != 200){
            System.exit(420);
        }
        InputStream in = res.getStream();
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonFactory jsonFactory = mapper.getFactory();
            JsonParser jp = jsonFactory.createParser(in);
            JsonNode node;
            JsonNode hastags;
            JsonToken token;
            while ((token = jp.nextToken()) != null) {
                if(token == JsonToken.START_OBJECT) {
                    //Read Object
                    node = jp.readValueAsTree();
                    //Filter by lang and get hashtags
                    if(node.has(ENTITIES)) {
                        if (node.get(ENTITIES).has(HASHTAGS)) {
                            hastags = node.get(ENTITIES).get(HASHTAGS);
                            for (JsonNode n1 : hastags) {
                                if (n1.has(TEXT)) {
                                    System.out.println(n1.get(LANGUAGE) + ";" + n1.get(TEXT).toString().replace("\"", ""));
                                }
                            }
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
