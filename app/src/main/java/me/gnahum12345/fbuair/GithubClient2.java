package me.gnahum12345.fbuair;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.wuman.android.auth.OAuthManager;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.gnahum12345.fbuair.activities.SignUpActivity;

public class GithubClient2 {
    private static GithubClient2 single_instance = null;
    private final String CALLBACK_URL =  "https://fbuair.redirect";

    private GithubClient2() { }

    public static GithubClient2 getInstance()  {
        if (single_instance == null) {
            single_instance = new GithubClient2();
        }
        return single_instance;
    }

    JsonFactory jsonFactory = new JacksonFactory();
    HttpTransport httpTransport = new NetHttpTransport();

    public AuthorizationCodeFlow getFlow(Context context) {
        final String CLIENT_ID = context.getResources().getString(R.string.github_client_id);
        final String CLIENT_SECRET = context.getResources().getString(R.string.github_client_secret);
        final String AUTHORIZATION_URL = "https://github.com/login/oauth/authorize";
        final String GENERIC_URL = "https://github.com/login/oauth/access_token";
        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                httpTransport, jsonFactory,
                new GenericUrl(GENERIC_URL),
                new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
                CLIENT_ID,
                AUTHORIZATION_URL).build();
        return flow;
    }

    public String getToken(Context context) {
        AuthorizationCodeFlow flow = getFlow(context);
        TokenRequest tokenRequest;
        TokenResponse tokenResponse;
        try {
            tokenRequest = flow.newTokenRequest("code")
                    .setRequestInitializer(new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) throws IOException {
                            request.getHeaders().setAccept("application/json");
                        }
                    });
            tokenResponse = tokenRequest.execute();
            return tokenResponse.getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*class RetrieveFeedTask extends AsyncTask<String, Void, RSSFeed> {

        private Exception exception;

        protected RSSFeed doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader xmlreader = parser.getXMLReader();
                RssHandler theRSSHandler = new RssHandler();
                xmlreader.setContentHandler(theRSSHandler);
                InputSource is = new InputSource(url.openStream());
                xmlreader.parse(is);

                return theRSSHandler.getFeed();
            } catch (Exception e) {
                this.exception = e;

                return null;
            } finally {
                is.close();
            }
        }

        protected void onPostExecute(RSSFeed feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }*/

}
