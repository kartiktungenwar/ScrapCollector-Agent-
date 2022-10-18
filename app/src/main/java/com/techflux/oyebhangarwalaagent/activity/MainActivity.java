package com.techflux.oyebhangarwalaagent.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techflux.oyebhangarwalaagent.R;
import com.techflux.oyebhangarwalaagent.adapter.CustomListOrderView;
import com.techflux.oyebhangarwalaagent.configData.Config;
import com.techflux.oyebhangarwalaagent.dataModel.OrderView;
import com.techflux.oyebhangarwalaagent.manager.SessionManager;
import com.techflux.oyebhangarwalaagent.util.NotificationUtils;
import com.techflux.oyebhangarwalaagent.validation.MyValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Context c = this;
    HashMap<String, String> user;
    private ProgressDialog pd;
    SessionManager mSessionManager;
    private ListView mListView;
    ArrayList<OrderView> orderList;
    private int RequestPermissionCode = 1;
    private static final String TAG = "Firebase";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String regId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkPermission()){

        }else {
            requestPermission();
        }

        //Creating a firebase object
        Firebase firebase = new Firebase(com.techflux.oyebhangarwalaagent.app.Config.FIREBASE_APP);

        //Pushing a new element to firebase it will automatically create a unique id
        Firebase newFirebase = firebase.push();

        String refreshedToken = newFirebase.getKey();

        Log.d("Tag", refreshedToken);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(com.techflux.oyebhangarwalaagent.app.Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(com.techflux.oyebhangarwalaagent.app.Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(com.techflux.oyebhangarwalaagent.app.Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

        displayFirebaseRegId();

        mSessionManager = new SessionManager(getApplicationContext());
        mSessionManager.checkLogin();
        orderList = new ArrayList<>();
        user = mSessionManager.getUserDetails();
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        mListView = (ListView) findViewById(R.id.listView_OrderView);
        String id=user.get(mSessionManager.KEY_ID);
        try{
            getOrderViewData(Config.ORDER_VIEW_URL+id);
        }catch (Exception ee) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mSessionManager.logoutUser();
            return true;
        }
        if(id == R.id.action_update){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_FIREBASE_AGENT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(Config.KEY_USEREMAIL,user.get(mSessionManager.KEY_EMAIL));
                    params.put(Config.KEY_FIREBASE,regId);
                    Log.d("url",params.toString());
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getOrderViewData(String url) {
        Log.d("url : ", "" + url);
        pd.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("url", "" + jsonArray);
                showData(jsonArray);
                pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void showData(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                orderList.add(new OrderView(jsonObject.getString(Config.KEY_USERID),jsonObject.getString(Config.KEY_USERNAME),jsonObject.getString(Config.KEY_CUSTOMEREMAIL),jsonObject.getString(Config.KEY_USERADDRESS),jsonObject.getString(Config.KEY_USERCONTACT),jsonObject.getString(Config.KEY_LANDMARK),jsonObject.getString(Config.KEY_ORDERID)));
            } catch (JSONException e){
                e.printStackTrace();
            }

        }
        CustomListOrderView customListOrderView = new CustomListOrderView(getApplicationContext(),orderList);
        mListView.setAdapter(customListOrderView);
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(findViewById(R.id.textView_EmptyList));
}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Dialog dialog = new Dialog(this);
        final OrderView mOrderView = orderList.get(i);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.checkadddressdiaogbox);
        dialog.setCancelable(false);

        ImageView closewindow = (ImageView) dialog.findViewById(R.id.fp_windowclose);
        closewindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        TextView tv = (TextView) dialog.findViewById(R.id.tv_show);
        String s = orderList.get(i).getUserAddress();
        s = s.replace(",",",\n");
        tv.setText(orderList.get(i).getUserName()+"\n"+s+"\nPhone No. : "+mOrderView.getUserMobile());
        Button btn_send = (Button) dialog.findViewById(R.id.fp_buttonSubmit);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                final Dialog mdialog = new Dialog(c);
                mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mdialog.setContentView(R.layout.orderdialogbox);
                mdialog.setCancelable(false);

                ImageView closewindow = (ImageView) mdialog.findViewById(R.id.fp_windowclose);
                closewindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mdialog.cancel();
                    }
                });
                TextView textView = (TextView) mdialog.findViewById(R.id.textViewDialogTitle);
                textView.setText("Enter OrderId");
                final EditText et_EmailForgotPassword = (EditText) mdialog.findViewById(R.id.et_FP_Email);
                et_EmailForgotPassword.setHint("Enter OrderId");
                et_EmailForgotPassword.setText("OrderId_");
                Button btn_send = (Button) mdialog.findViewById(R.id.fp_buttonSubmit);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean flag = true;
                        if (!MyValidator.isValidRequired(et_EmailForgotPassword)) {
                            flag = false;
                        }
                        if (flag) {
                            mdialog.dismiss();
                            Intent i = new Intent(getApplicationContext(),OrderViewActivity.class);
                            i.putExtra("IndexEmail",mOrderView.getUserEmail());
                            i.putExtra("IndexOrder",et_EmailForgotPassword.getText().toString());
                            i.putExtra("IndexId",mOrderView.getUserId());
                            startActivity(i);
                        }
                    }
                });
                mdialog.show();
            }
        });
        dialog.show();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(com.techflux.oyebhangarwalaagent.app.Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.e(TAG, "Firebase Reg Id: " + regId);
        else
            Log.e(TAG, "Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(com.techflux.oyebhangarwalaagent.app.Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(com.techflux.oyebhangarwalaagent.app.Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{ READ_EXTERNAL_STORAGE}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_EXTERNAL_STORAGE);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }
}