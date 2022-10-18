package com.techflux.oyebhangarwalaagent.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techflux.oyebhangarwalaagent.R;
import com.techflux.oyebhangarwalaagent.configData.Config;
import com.techflux.oyebhangarwalaagent.dataModel.UserBean;
import com.techflux.oyebhangarwalaagent.manager.SessionManager;
import com.techflux.oyebhangarwalaagent.validation.MyValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends Activity implements View.OnClickListener {

    SessionManager mSessionManager;
    UserBean mUserBean;
    String userName,userId;
    SharedPreferences pref;
    SharedPreferences.Editor ed;

    ArrayList<UserBean> arraylistBean=new ArrayList<UserBean>();
    private ArrayAdapter<String> adapter;

    private AutoCompleteTextView et_Name;
    private EditText et_Password,et_EmailForgotPassword;
    private Button btn_Login;
    private TextView tv_Forget_Password;
    private CheckBox remember_me;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mUserBean = new UserBean();
        mSessionManager = new SessionManager(getApplicationContext());
        et_Name = (AutoCompleteTextView) findViewById(R.id.et_login_username);
        et_Password = (EditText) findViewById(R.id.et_login_password);
        tv_Forget_Password = (TextView) findViewById(R.id.tv_forgot_password);
        btn_Login = (Button) findViewById(R.id.btn_login);
        remember_me = (CheckBox) findViewById(R.id.checkBox_remember);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

        Loadcredentials();

        btn_Login.setOnClickListener(this);
        tv_Forget_Password.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                boolean flag = true;
                if (!MyValidator.isValidRequired(et_Name)) {
                    flag = false;

                }
                if (!MyValidator.isValidPassword(et_Password)) {
                    flag = false;
                } else {
                    if (flag) {
                        if (remember_me.isChecked()) {
                            String username = et_Name.getText().toString();
                            String password = et_Password.getText().toString();

                            pref = getSharedPreferences("HB", MODE_PRIVATE);
                            ed = pref.edit();
                            ed.putString(username, password);
                            ed.commit();
                        }
                        loginUser();
                    }
                }
                break;

            case R.id.tv_forgot_password:
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.forgot_password);
                dialog.setCancelable(false);

                ImageView closewindow = (ImageView) dialog.findViewById(R.id.fp_windowclose);
                closewindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                et_EmailForgotPassword = (EditText) dialog.findViewById(R.id.et_FP_Email);
                ;

                Button btn_send = (Button) dialog.findViewById(R.id.fp_buttonSubmit);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean flag = true;
                        if (!MyValidator.isValidEmail(et_EmailForgotPassword)) {
                            flag = false;
                        }
                        if (flag) {
                            loginForgotPassword();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                break;
        }
    }

    private void Loadcredentials()
    {

        pref = this.getSharedPreferences("HB", MODE_PRIVATE);
        ed = pref.edit();
        Map map = pref.getAll();

        Set keySet = map.keySet();
        Iterator iterator = keySet.iterator();

        while (iterator.hasNext())
        {
            UserBean bean=new UserBean();
            String key = (String) iterator.next();
            String value = (String)map.get(key);
            Log.i("HashMap","Username==="+ key +" Psw=== " +value);

            bean.setUsername(key);
            bean.setPassword(value);

            arraylistBean.add(bean);
        }
        for (int i = 0; i < arraylistBean.size(); i++)
        {
            UserBean bean=arraylistBean.get(i);
            Log.e("ArrayList","Username=="+ bean.getUsername() +" Psw== " +bean.getPassword());
        }

        List countryList = getCountryList(arraylistBean);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,countryList);
        et_Name.setAdapter(adapter);
        et_Name.setThreshold(1);
        et_Name.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3)
            {
                Toast.makeText(getBaseContext(), ""+view.getItemAtPosition(position),Toast.LENGTH_LONG).show();
                et_Password.setText(arraylistBean.get(position).getPassword());
            }
        });
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getCountryList(ArrayList<UserBean> countries)
    {
        List list = new ArrayList();
        for (UserBean c : countries)
        {
            list.add(c.getUsername());
        }
        return list;
    }

    private void loginUser(){
        pd.show();
        final String username = et_Name.getText().toString().trim();
        final String password = et_Password.getText().toString().trim();
        String url = Config.LOGIN_URL+"?"+ Config.KEY_USEREMAIL+"="+username+"&"+ Config.KEY_PASSWORD+"="+password;
        Log.d("url",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,


                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            loginData(Config.LOGINAGENT_URL+username);
                            pd.dismiss();
                        }
                        else {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Invalid UserName or Password",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USEREMAIL,username);
                params.put(Config.KEY_PASSWORD,password);
                Log.d("url", String.valueOf(params));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loginForgotPassword(){
        final String username = et_EmailForgotPassword.getText().toString().trim();
        Toast.makeText(getApplicationContext(),username,Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FORGOT_PASSWORD_URL,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            if(username.endsWith("gmail.com")){
                                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                                startActivity(intent);
                                finish();}
                            else {
                                Toast.makeText(LoginActivity.this," Check Your Email ",Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this,response.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USEREMAIL,username);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loginData(String subUrl){
        Log.d("url : ", "" + subUrl);
        pd.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(subUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("url", "" + jsonArray);
                displayData(jsonArray);
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

    private void displayData(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                userName = jsonObject.getString(Config.KEY_AGENTNAME);
                userId = jsonObject.getString(Config.KEY_AGENTID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mSessionManager.createLoginSession(et_Name.getText().toString(),userName,userId.toString().trim());
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
