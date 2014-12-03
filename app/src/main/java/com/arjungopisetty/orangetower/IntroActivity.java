package com.arjungopisetty.orangetower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class IntroActivity extends Activity {

    private Button mNextActivityButton;
    private String mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mNextActivityButton = (Button) findViewById(R.id.next_activity_button);
        mNextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String text = getHtml("http://whyisthetowerorange.com/");
                    mStatus =  getStatus(text);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), TowerColorActivity.class);
                intent.putExtra("status", mStatus);
                startActivity(intent);
            }
        });
    }

    public static String getHtml(String url) throws ClientProtocolException, IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpGet, localContext);
        String result = "";

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        response.getEntity().getContent()
                )
        );

        String line = null;
        while ((line = reader.readLine()) != null){
            result += line + "\n";
        }
        return result;
    }

    public static String getStatus(String html){
        Integer pos = html.indexOf("<p class=\"reason\"><a href=");
        //System.out.println("pos of status: " + pos);
        Integer increment = 28;
        String ans = "";
        while (!html.substring(pos + increment, pos + 1 + increment).equals(">") ) {
            //     System.out.println("first while: " + html.substring(pos+increment,pos+1+increment));
            increment++;
        }
        while (!html.substring(pos + increment, pos + 1 + increment).equals("<") ) {
            ans += html.substring(pos+increment,pos + 1 + increment);
            increment++;
        }

        // TODO Why does this happen and why do I need to do this
        if (ans.equals(">It's not.")) {
            ans = ans.substring(1, ans.length() - 1);
        }

        Log.i("ans", ans);
        return ans;
    }
}