package com.example.princemanager;

import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    String id ;
    TabledbHelper dbHelper;
    ModelTask task;
    ArrayList<ModelTask> listtasks = new ArrayList<ModelTask>() ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        dbHelper = new TabledbHelper(getApplicationContext());
        id = viewAll();

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


        String url = "https://calvin.estig.ipb.pt/projectman/api/Tasks/FindByTeamAssigned/"+"3"+"/"+ id;
        JsonObjectRequest jsonObjectReques = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jArray = response.getJSONArray("ListTasks");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj =  jArray.getJSONObject(i);
                        ModelTask task = new ModelTask(obj.getString("Id"),obj.getString("Name"),
                                obj.getString("Description"),obj.getString("Duration"),
                                obj.getString("IdTeam"),obj.getString("IdAssigned") ,obj.getString("IdResponsible") , obj.getString("creationDate") );
                        listtasks.add(task);
                    }

                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclertasks);
                    // Create adapter passing in the sample user data
                    AdapterTask adapter = new AdapterTask(listtasks);
                    // Attach the adapter to the recyclerview to populate items
                    recyclerView.setAdapter(adapter);
                    // Set layout manager to position the items
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage() , Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectReques);


    }

    public String viewAll() {
        String id ="" ;

        Cursor res = dbHelper.getAllData();
        if(res.getCount() == 0) {
            // show message
            Toast.makeText(getApplicationContext(),"Error"+"Nothing found" , Toast.LENGTH_LONG).show();
        }

        while (res.moveToNext()) {
            id =  res.getString(0) ;

        }
        return  id ;
    }

}
