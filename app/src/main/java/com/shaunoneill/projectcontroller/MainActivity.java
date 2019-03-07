package com.shaunoneill.projectcontroller;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.obd.infrared.InfraRed;
import com.obd.infrared.log.LogToConsole;
import com.obd.infrared.patterns.PatternAdapter;
import com.obd.infrared.patterns.PatternConverter;
import com.obd.infrared.patterns.PatternType;
import com.obd.infrared.transmit.TransmitInfo;
import com.obd.infrared.transmit.TransmitterType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    boolean state1, state2, state3, state4;
    Button switch1, switch2, switch3, switch4;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    InfraRed infraRed;
    private TransmitInfo[] patterns;
    LogToConsole logToConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = (Button) findViewById(R.id.switch1);
        switch2 = (Button) findViewById(R.id.switch2);
        switch3 = (Button) findViewById(R.id.switch3);
        switch4 = (Button) findViewById(R.id.switch4);

        /**
         * Setup the IR blaster
         */
        logToConsole = new LogToConsole("INFRARED");
        infraRed = new InfraRed(this, logToConsole);

        TransmitterType transmitterType = infraRed.detect();
        infraRed.createTransmitter(transmitterType);

        List<PatternConverter> rawPatterns = new ArrayList<>();

        rawPatterns.add(new PatternConverter(PatternType.Cycles, 38000, 493));

        PatternAdapter patternAdapter = new PatternAdapter(logToConsole, transmitterType);
        TransmitInfo[] transmitInfoArray = new TransmitInfo[rawPatterns.size()];
        for (int x = 0; x < transmitInfoArray.length; x++) {
            transmitInfoArray[x] = patternAdapter.createTransmitInfo(rawPatterns.get(x));
        }
        this.patterns = transmitInfoArray;

        /**
         * Set the button colours based on their state
         */
        fetchStateColors();
    }

    @Override
    protected void onResume() {
        super.onResume();

        infraRed.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        infraRed.stop();
    }

    public void clickSwitch (View view) {
        toggleState(view);
        fetchStateColors();
    }

    /**
     * Sets the color and default state variables
     */
    public void fetchStateColors() {
        if (state1) {
            switch1.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            switch1.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        if (state2) {
            switch2.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            switch2.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        if (state3) {
            switch3.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            switch3.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        if (state4) {
            switch4.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            switch4.setBackgroundColor(Color.parseColor("#F44336")); // False
        }
    }

    /**
     * Change the state of a button
     * @param button
     * @return
     */
    public void toggleState(View button){
        if (button.getId() == R.id.switch1) {
            state1 = !state1; // Toggle state


        }else if (button.getId() == R.id.switch2) {
            state2 = !state2; // Toggle state
        }else if (button.getId() == R.id.switch3) {
            state3 = !state3; // Toggle state
        }else if (button.getId() == R.id.switch4) {
            state4 = !state4; // Toggle state
        }
    }

    String post(String url, int switchNumber, boolean state) throws IOException {
        RequestBody body = RequestBody.create(JSON, buildPostData(switchNumber, state));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
        }
        return null;
    }

    /**
     * Builds the JSON data for the post request
     * @param switchNumber
     * @param state
     * @return
     */
    String buildPostData (int switchNumber, boolean state) {
        return "{"
                + "'switchNumber': " + switchNumber
                + ",'state': " + state
                +"}";
    }
}
