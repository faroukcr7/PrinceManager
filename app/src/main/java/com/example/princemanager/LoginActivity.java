package com.example.princemanager;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;

public class LoginActivity extends AppCompatActivity {


    EditText email , password;
    String EmailText , PasswordText,RightPassword;
    CheckBox checkBox;
    ProgressBar progressBar;
    boolean checked ;
    TabledbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        progressBar = (ProgressBar)  findViewById(R.id.progressbar) ;
        dbHelper = new TabledbHelper(this) ;



    }


    public void Login (View view) throws JSONException {
        EmailText = String.valueOf(email.getText());
        PasswordText = String.valueOf(password.getText());
        checked = checkBox.isChecked();

        progressBar.setVisibility(View.VISIBLE);


        if (EmailText.isEmpty() || EmailText == null ) {
            email.setError("You have To set the Email ! ");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        /*
        if (!isValidEmail(EmailText) ) {

           email.setError("You have To set a Correct Email !");
            progressBar.setVisibility(View.INVISIBLE);
             return;
        } */
        if (PasswordText.isEmpty() || PasswordText == null) {
            password.setError("You Have To set the password !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        // method to check if the  password is correct or not send an http request and return the hash and compare it


        progressBar.setVisibility(View.INVISIBLE);
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url ="https://calvin.estig.ipb.pt/projectman/api/Login";
        final ModelUser user = new ModelUser("username1" ,"user1") ;
        Gson gson = new Gson();
        String json = gson.toJson(user);



       /* Send
        {
            "username": "aaaa1aa",
                "password": "aaaaa1a"

        }
        */


       /*recieved
        {
    "Id": 90,
    "Name": "aaaaaa1                       ",
    "Email": "aaaaa1a@princsemanager.com              ",
    "BornDate": "2011-01-01T00:00:00",
    "Phone": "545454154      ",
    "Username": "aaaa1aa        ",
    "Password": "aaaaa1a                  ",
    "Gender": "m"
} */
        final JSONObject U = new JSONObject();
        U.put("username",EmailText);
        U.put("password",PasswordText) ;

        JsonObjectRequest jsonObjectReques = new JsonObjectRequest(Request.Method.POST, url, U, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(),"Response is: " + String.valueOf(response),Toast.LENGTH_LONG).show();
                dbHelper.deleteData();

                    boolean isInserted = false;
                    try {
                        dbHelper.deleteData();
                        isInserted = dbHelper.insert(response.getString("Id"),response.getString("Name"),
                                response.getString("Username"),response.getString("Email") ,response.getString("Gender") ,response.getString("Phone")
                                ,response.getString("BornDate") ,response.getString("Password"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"That didn't work!", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),error.toString() , Toast.LENGTH_LONG).show();

            }
        });
// Add the request to the RequestQueue.
        queue.add(jsonObjectReques);




    }

    public void gohome(View view) {

        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
        startActivity(intent);


    }
    public void Gosignup(View view ) {
        Intent intent =new Intent(getApplicationContext() , SignupActivity.class);
        startActivity(intent);

    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


}
