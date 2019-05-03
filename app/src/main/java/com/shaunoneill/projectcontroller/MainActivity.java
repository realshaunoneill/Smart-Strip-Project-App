package com.shaunoneill.projectcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import okhttp3.Call;
import okhttp3.Callback;
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
    public static String connectIp = "smart-strip.local";

    InfraRed infraRed;
    private TransmitInfo[] patterns;
    LogToConsole logToConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiSwitch1 = findViewById(R.id.wifiSwitch1);
        wifiSwitch2 = findViewById(R.id.wifiSwitch2);
        wifiSwitch3 = findViewById(R.id.wifiSwitch3);
        wifiSwitch4 = findViewById(R.id.wifiSwitch4);

        infraredSwitch1 = findViewById(R.id.infraredSwitch1);
        infraredSwitch2 = findViewById(R.id.infraredSwitch2);
        infraredSwitch3 = findViewById(R.id.infraredSwitch3);
        infraredSwitch4 = findViewById(R.id.infraredSwitch4);

        fetchStateColors();
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
        //fetchStateColors();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tutorial:
                Intent tutIntent = new Intent(this, TutorialActivity.class);
                startActivity(tutIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickSwitch(View view) {
        toggleState(view);
        fetchStateColors();

        try {
            if (view.getId() == R.id.wifiSwitch1) post(connectIp, wifiState1, 1);
            else if (view.getId() == R.id.wifiSwitch2) post(connectIp, wifiState2, 2);
            else if (view.getId() == R.id.wifiSwitch3) post(connectIp, wifiState3, 3);
            else if (view.getId() == R.id.wifiSwitch4) post(connectIp, wifiState4, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets the color and default state variables
     */
    public void fetchStateColors() {
        // Wifi
        if (wifiState1 || infraredState1) {
            wifiSwitch1.setBackgroundColor(getResources().getColor(R.color.color_green));
            infraredSwitch1.setBackgroundColor(getResources().getColor(R.color.color_green));

        } else {
            wifiSwitch1.setBackgroundColor(getResources().getColor(R.color.color_red)); // False
            infraredSwitch1.setBackgroundColor(getResources().getColor(R.color.color_red)); // False
        }

        if (wifiState2 || infraredState2) {
            wifiSwitch2.setBackgroundColor(getResources().getColor(R.color.color_green));
            infraredSwitch2.setBackgroundColor(getResources().getColor(R.color.color_green));
        } else {
            wifiSwitch2.setBackgroundColor(getResources().getColor(R.color.color_red)); // False
            infraredSwitch2.setBackgroundColor(getResources().getColor(R.color.color_red)); // False
        }

        if (wifiState3 || infraredState3) {
            wifiSwitch3.setBackgroundColor(getResources().getColor(R.color.color_green));
            infraredSwitch3.setBackgroundColor(getResources().getColor(R.color.color_green));
        } else {
            wifiSwitch3.setBackgroundColor(getResources().getColor(R.color.color_red)); // False
            infraredSwitch3.setBackgroundColor(getResources().getColor(R.color.color_red)); // False
        }

        if (wifiState4 || infraredState4) {
            wifiSwitch4.setBackgroundColor(getResources().getColor(R.color.color_green));
            infraredSwitch4.setBackgroundColor(getResources().getColor(R.color.color_green));
        } else {
            wifiSwitch4.setBackgroundColor(getResources().getColor(R.color.color_red)); // False
            infraredSwitch4.setBackgroundColor(getResources().getColor(R.color.color_red)); // False
        }
    }

    /**
     * Change the state of a button
     *
     * @param button
     * @return
     */
    public void toggleState(View button) {
        if (button.getId() == R.id.wifiSwitch1) {
            wifiState1 = !wifiState1; // Toggle state
        } else if (button.getId() == R.id.wifiSwitch2) {
            wifiState2 = !wifiState2; // Toggle state
        } else if (button.getId() == R.id.wifiSwitch3) {
            wifiState3 = !wifiState3; // Toggle state
        } else if (button.getId() == R.id.wifiSwitch4) {
            wifiState4 = !wifiState4; // Toggle state
        }

        // Infrared
        if (button.getId() == R.id.infraredSwitch1) {
            infraredState1 = !infraredState1; // Toggle state
        } else if (button.getId() == R.id.infraredSwitch2) {
            infraredState2 = !infraredState2; // Toggle state
        } else if (button.getId() == R.id.infraredSwitch3) {
            infraredState3 = !infraredState3; // Toggle state
        } else if (button.getId() == R.id.infraredSwitch4) {
            Intent intent = new Intent(this, IpActivity.class);
            startActivity(intent);
            infraredState4 = !infraredState4; // Toggle state
        }
    }

    /**
     * @param url   - http://<DEVICE URL> with no trailing /
     * @param state - The desired output state (true = on)
     * @throws IOException
     */
    void post(String url, boolean state, int relay) throws IOException {
        Log.d("REQUEST", "Sending request to: " + url + ", data: " + relay + ", - " + state);
        //Toast.makeText(this, "Updating.... please wait", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder()
                .url(url + (state ? "/?enable=" : "/?disable=") + relay)
                .post(RequestBody.create(null, "")).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(MainActivity.this, "Unable to process request, please try again shortly!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(MainActivity.this, "Successfully updated status to: " + state, Toast.LENGTH_SHORT).show());
                //
            }
        });
    }
}
