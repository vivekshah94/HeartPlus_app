package com.sgh.swinburne.heartplus;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sgh.swinburne.heartplus.helper.SQLiteHandler;
import com.sgh.swinburne.heartplus.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Saad on 11/5/2015.
 */
public class BPActivity extends Activity
{
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private SessionManager session;

    JSONParser jsonParser = new JSONParser();
    EditText inputS;
    EditText inputDate;
    EditText inputD;
    EditText remark;
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    private static String url_create_bp = "http://188.166.237.51/android_login_api/create_bp.php";
    private static final String TAG_SUCCESS = "success";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bp_layout);


        inputS = (EditText) findViewById(R.id.inputS);
        inputD = (EditText) findViewById(R.id.inputD);
        remark = (EditText) findViewById(R.id.remark);
        dateView = (TextView) findViewById(R.id.inputDate);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);


        Button btnCreateGlucose = (Button) findViewById(R.id.btnCreateGlucose);
        btnCreateGlucose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateNewBP().execute();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
        Log.d("date: ", dateView.toString());
    }

    class CreateNewBP extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BPActivity.this);
            pDialog.setMessage("Posting Blood Pressure...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            //pDialog.show();

        }

        protected String doInBackground(String... args) {
            db = new SQLiteHandler(getApplicationContext());
            session = new SessionManager(getApplicationContext());
            HashMap<String, String> user = db.getUserDetails();
            String email = user.get("email");
            Log.d("email ", email);
            String systolic = inputS.getText().toString();
            String diastolic = inputD.getText().toString();
            String inputremark = remark.getText().toString();
            String date = dateView.getText().toString();
            Log.d("BPS ", systolic);
            Log.d("BPD ", diastolic);
            Log.d("date ", date);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("systolic", systolic));
            params.add(new BasicNameValuePair("diastolic", diastolic));
            params.add(new BasicNameValuePair("date", date));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("remark", inputremark));
            Log.d("params ", params.toString());
            JSONObject json = jsonParser.makeHttpRequest(url_create_bp, "POST", params);
            Log.d("Create Response", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    finish();
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }
}
