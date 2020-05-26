package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends ActionBarActivity {

    private EditText text;
    private Button searchButton;
    private String url;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.main);
        text = findViewById(R.id.text);
        message = findViewById(R.id.message);
        searchButton = findViewById(R.id.search);

        /**
         * In Android, localhost is the address of the device or the emulator.
         * To connect to your machine, you need to use the below IP address
         * **/
        url = "https://34.213.200.178:8443/FabFlix/api/";

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    private void search() {

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        final StringRequest searchRequest = new StringRequest(Request.Method.POST, url + "search", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("login.success", response);
                //initialize the activity(page)/destination
                Intent listPage = new Intent(MainActivity.this, ListViewActivity.class);
                //without starting the activity/page, nothing would happen
                listPage.putExtra("movies", response);
                listPage.putExtra("page", "0");
                listPage.putExtra("search", text.getText().toString());

                startActivity(listPage);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("login.error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("title", text.getText().toString());
                params.put("filter_search", "true");
                params.put("limit", "20");

                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}