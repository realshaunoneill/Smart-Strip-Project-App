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

    boolean wifiState1, wifiState2, wifiState3, wifiState4;
    boolean infraredState1, infraredState2, infraredState3, infraredState4;

    Button wifiSwitch1, wifiSwitch2, wifiSwitch3, wifiSwitch4;
    Button infraredSwitch1, infraredSwitch2, infraredSwitch3, infraredSwitch4;

    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    InfraRed infraRed;
    private TransmitInfo[] patterns;
    LogToConsole logToConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiSwitch1 = (Button) findViewById(R.id.wifiSwitch1);
        wifiSwitch2 = (Button) findViewById(R.id.wifiSwitch2);
        wifiSwitch3 = (Button) findViewById(R.id.wifiSwitch3);
        wifiSwitch4 = (Button) findViewById(R.id.wifiSwitch4);

        infraredSwitch1 = (Button) findViewById(R.id.infraredSwitch1);
        infraredSwitch2 = (Button) findViewById(R.id.infraredSwitch2);
        infraredSwitch3 = (Button) findViewById(R.id.infraredSwitch3);
        infraredSwitch4 = (Button) findViewById(R.id.infraredSwitch4);


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

    private int currentPattern = 0;
    private void transmitData() {
        TransmitInfo transmitInfo = patterns[currentPattern++];
        if (currentPattern >= patterns.length) currentPattern = 0;
        infraRed.transmit(transmitInfo);
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
        // Wifi
        if (wifiState1 || infraredState1) {
            wifiSwitch1.setBackgroundColor(Color.parseColor("#4CAF50"));
            infraredSwitch1.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            wifiSwitch1.setBackgroundColor(Color.parseColor("#F44336")); // False
            infraredSwitch1.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        if (wifiState2 || infraredState2) {
            wifiSwitch2.setBackgroundColor(Color.parseColor("#4CAF50"));
            infraredSwitch2.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            wifiSwitch2.setBackgroundColor(Color.parseColor("#F44336")); // False
            infraredSwitch2.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        if (wifiState3 || infraredState3) {
            wifiSwitch3.setBackgroundColor(Color.parseColor("#4CAF50"));
            infraredSwitch3.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            wifiSwitch3.setBackgroundColor(Color.parseColor("#F44336")); // False
            infraredSwitch3.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        if (wifiState4 || infraredState4) {
            wifiSwitch4.setBackgroundColor(Color.parseColor("#4CAF50"));
            infraredSwitch4.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            wifiSwitch4.setBackgroundColor(Color.parseColor("#F44336")); // False
            infraredSwitch4.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        /*// Infrared
        if (infraredState1) {
            infraredSwitch1.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            infraredSwitch1.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        if (infraredState2) {
            infraredSwitch2.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            infraredSwitch2.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        if (infraredState3) {
            infraredSwitch3.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            infraredSwitch3.setBackgroundColor(Color.parseColor("#F44336")); // False
        }

        if (infraredState4) {
            infraredSwitch4.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else {
            infraredSwitch4.setBackgroundColor(Color.parseColor("#F44336")); // False
        }*/
    }

    /**
     * Change the state of a button
     * @param button
     * @return
     */
    public void toggleState(View button){
        if (button.getId() == R.id.wifiSwitch1) {
            wifiState1 = !wifiState1; // Toggle state
        }else if (button.getId() == R.id.wifiSwitch2) {
            wifiState2 = !wifiState2; // Toggle state
        }else if (button.getId() == R.id.wifiSwitch3) {
            wifiState3 = !wifiState3; // Toggle state
        }else if (button.getId() == R.id.wifiSwitch4) {
            wifiState4 = !wifiState4; // Toggle state
        }

        // Infrared
        if (button.getId() == R.id.infraredSwitch1) {
            infraredState1 = !infraredState1; // Toggle state
        }else if (button.getId() == R.id.infraredSwitch2) {
            infraredState2 = !infraredState2; // Toggle state
        }else if (button.getId() == R.id.infraredSwitch3) {
            infraredState3 = !infraredState3; // Toggle state
        }else if (button.getId() == R.id.infraredSwitch4) {
            infraredState4 = !infraredState4; // Toggle state
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
