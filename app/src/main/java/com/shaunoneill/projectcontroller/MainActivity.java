package com.shaunoneill.projectcontroller;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    boolean state1, state2, state3, state4;
    Button switch1, switch2, switch3, switch4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = (Button) findViewById(R.id.switch1);
        switch2 = (Button) findViewById(R.id.switch2);
        switch3 = (Button) findViewById(R.id.switch3);
        switch4 = (Button) findViewById(R.id.switch4);

        fetchStateColors();
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
}
