package edu.upc.arnaubeltra.tfgquibot.ui.interactwithrobot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import edu.upc.arnaubeltra.tfgquibot.MainActivity;
import edu.upc.arnaubeltra.tfgquibot.R;

public class InteractWithRobot extends Fragment {

    public InteractWithRobot() {
        // Required empty public constructor
    }

    private Button btnUp, btnDown, btnRight, btnLeft;
    private RequestQueue queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_interact_with_robot, container, false);

        btnUp = view.findViewById(R.id.btnUp);
        btnUp.setOnClickListener(view1 -> up());


        btnDown = view.findViewById(R.id.btnDown);
        btnDown.setOnClickListener(view1 -> down());

        btnRight = view.findViewById(R.id.btnRight);
        btnRight.setOnClickListener(view1 -> right());

        btnLeft = view.findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(view1 -> left());

        return view;
    }

    private void left() {
        String url = "10.192.123.101:10000/sendInstruction?instruction=up";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("hello", "res");
                    } catch (Exception ex) {
                        Log.d("hello", "onResponse: Error parsing to JSON");
                    }
                }, error -> Log.d("hello", "onErrorResponse: Error trying to obtain the weather info"));
        queue.add(stringRequest);
    }

    private void right() {
        String url = "10.192.123.101:10000/sendInstruction?instruction=down";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("hello", "res");
                    } catch (Exception ex) {
                        Log.d("hello", "onResponse: Error parsing to JSON");
                    }
                }, error -> Log.d("hello", "onErrorResponse: Error trying to obtain the weather info"));
        queue.add(stringRequest);
    }

    private void down() {
        String url = "10.192.123.101:10000/sendInstruction?instruction=right";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("hello", "res");
                    } catch (Exception ex) {
                        Log.d("hello", "onResponse: Error parsing to JSON");
                    }
                }, error -> Log.d("hello", "onErrorResponse: Error trying to obtain the weather info"));
        queue.add(stringRequest);
    }

    private void up() {
        String url = "10.192.123.101:10000/sendInstruction?instruction=left";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("hello", "res");
                    } catch (Exception ex) {
                        Log.d("hello", "onResponse: Error parsing to JSON");
                    }
                }, error -> Log.d("hello", "onErrorResponse: Error trying to obtain the weather info"));
        queue.add(stringRequest);
    }

    private void sendAction(String action) {
        /*String url = "10.192.123.101/sendInstruction?instruction=";

        switch (action) {
            case "up":
                url = "10.192.123.101/sendInstruction?instruction=up";
                break;
            case "down":
                url = "10.192.123.101/sendInstruction?instruction=down";
                break;
            case "right":
                url = "10.192.123.101/sendInstruction?instruction=right";
                break;
            case "left":
                url = "10.192.123.101/sendInstruction?instruction=left";
                break;
        }

        if (queue == null)
            queue = Volley.newRequestQueue(MainActivity.getAppContext());*/

        /*StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("hello", "res");
                    } catch (Exception ex) {
                        Log.d("hello", "onResponse: Error parsing to JSON");
                    }
                }, error -> Log.d("hello", "onErrorResponse: Error trying to obtain the weather info"));
        queue.add(stringRequest);*/
    }
}