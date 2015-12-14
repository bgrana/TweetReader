package master2015;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public class TweetReader {
	private OAuthService service;
	private Token accessToken;
	
	public TweetReader() {
		service = new ServiceBuilder()
				.provider(TwitterApi.class)
				.apiKey("yourApiKey")
				.apiSecret("yourApiSecret")
				.build();
		
		accessToken = new Token("yourToken", "yourTokenSecret");
	}
	
	
}
