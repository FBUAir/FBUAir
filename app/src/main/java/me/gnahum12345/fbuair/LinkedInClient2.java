package me.gnahum12345.fbuair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import me.gnahum12345.fbuair.activities.MainActivity;

public class LinkedInClient2 {
/*    private static final String API_KEY = "gfhfghf";
    private static final String SECRET_KEY = "YOUR_API_SECRET";
    private static final String STATE = "E3ZYKC1T6H2yP4z";
    private static final String REDIRECT_URI = "https://me.gnahum12345.fbuair.redirecturl";

    private static LinkedInClient2 single_instance = null;
    private Activity activity;

    private LinkedInClient2() { }

    public static LinkedInClient2 getInstance(Activity activity)  {
        if (single_instance == null) {
            single_instance = new LinkedInClient2();
            single_instance.activity = activity;
        }
        return single_instance;
    }

    private static String getAccessTokenUrl(String authorizationCode){
        String url = String.format("https://www.linkedin.com/oauth/v2/accessToken?grant_type=" +
                "authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
        authorizationCode, API_KEY, REDIRECT_URI, SECRET_KEY);
        return url;
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if(urls.length>0){
                String url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpost = new HttpPost(url);
                try{
                    HttpResponse response = httpClient.execute(httpost);
                    if(response!=null){
                        //If status is OK 200
                        if(response.getStatusLine().getStatusCode()==200){
                            String result = EntityUtils.toString(response.getEntity());
                            //Convert the string result to a JSON Object
                            JSONObject resultJson = new JSONObject(result);
                            //Extract data from JSON Response
                            int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;

                            String accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;
                            Log.e("Token", ""+accessToken);
                            if(expiresIn>0 && accessToken!=null){
                                Log.i("Authorize", "This is the access Token: "+accessToken+". It will expires in "+expiresIn+" secs");

                                //Calculate date of expiration
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.SECOND, expiresIn);
                                long expireDate = calendar.getTimeInMillis();

                                ////Store both expires in and access token in shared preferences
                                SharedPreferences preferences = activity.getSharedPreferences("user_info", 0);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putLong("expires", expireDate);
                                editor.putString("accessToken", accessToken);
                                editor.commit();

                                return true;
                            }
                        }
                    }
                }catch(IOException e){
                    Log.e("Authorize","Error Http response "+e.getLocalizedMessage());
                }
                catch (ParseException e) {
                    Log.e("Authorize","Error Parsing Http response "+e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e("Authorize","Error Parsing Http response "+e.getLocalizedMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status){
            if(status){

            }
        }

    };*/
}
