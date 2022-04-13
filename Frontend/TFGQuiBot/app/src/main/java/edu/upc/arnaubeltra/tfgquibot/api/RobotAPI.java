package edu.upc.arnaubeltra.tfgquibot.api;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import edu.upc.arnaubeltra.tfgquibot.UserNavigation;

public class RobotAPI extends AppCompatActivity {


    private static final String BASE_URL = "https://localhost:5000";
    private static final String TAG = "RobotAPI";

    private static RobotAPI instance;

    private RequestQueue queue;

    public static RobotAPI getInstance() {
        if (instance == null) instance = new RobotAPI();
        return instance;
    }

    public void interactWithRobot(String interaction) {
        String url = BASE_URL;

        if (queue == null)
            queue = Volley.newRequestQueue(UserNavigation.getInstance());

        switch (interaction) {
            case "forward":
                url += "/sendInstruction?action=up";
                break;
            case "backwards":
                url += "/sendInstruction?action=down";
                break;
            case "left":
                url += "/sendInstruction?action=left";
                break;
            case "right":
                url += "/sendInstruction?action=right";
                break;
            case "raise_pipette":
                url += "/sendInstruction?action=raise_pipette";
                break;
            case "lower_pipette":
                url += "/sendInstruction?action=lower_pipette";
                break;
            case "suck":
                url += "/sendInstruction?action=suck";
                break;
            case "reset":
                url += "/sendInstruction?action=reset";
                break;
            case "readColor":
                url += "/sendInstruction?action=readColor";
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Log.d(TAG, "onResponse: " + response);
            } catch (Exception ex) {
                Log.d(TAG, "onResponse: Cannot execute action (API)");
            }
        }, error -> Log.d(TAG, "interactWithRobot: Error trying to connect with API"));
        queue.add(stringRequest);
    }
}
