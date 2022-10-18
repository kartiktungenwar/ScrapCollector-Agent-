package com.techflux.oyebhangarwalaagent.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.techflux.oyebhangarwalaagent.R;
import com.techflux.oyebhangarwalaagent.adapter.CustomListOrderFinal;
import com.techflux.oyebhangarwalaagent.configData.Config;
import com.techflux.oyebhangarwalaagent.dataModel.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderFinalActivity extends AppCompatActivity implements View.OnClickListener {

    ListView mList;
    ArrayList<ProductModel> product;
    String orderId,configUrl,email,userId;
    ProgressDialog pd;
    TextView total,order;
    Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_final);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        Intent i = getIntent();
        email =  i.getStringExtra("IndexEmail");
        orderId = i.getStringExtra("IndexOrder");
        userId = i.getStringExtra("IndexId");
        product = new ArrayList<>();
        configUrl = Config.ORDER_URL+"?customer_email="+email+"&customer_order_id="+orderId;
        mList = (ListView) findViewById(R.id.listView_OrderFinal);
        order = (TextView) findViewById(R.id.textView_oder_id);
        order.setText(orderId);
        total = (TextView) findViewById(R.id.textView_total);
        getData(configUrl);
        total.setText("Click here");
        total.setOnClickListener(this);
    }



    private void getData(String subGroupUrl){
        Log.d("url : ", "" + subGroupUrl);
        pd.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(subGroupUrl, new Response.Listener<JSONArray>() {
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
                product.add(new ProductModel(jsonObject.getString(Config.KEY_USERID),jsonObject.getString(Config.KEY_ORDERID),jsonObject.getString(Config.KEY_PRODUCTID),jsonObject.getString(Config.KEY_PRODUCTYPE),jsonObject.getString(Config.KEY_PRODUCTNAME),jsonObject.getString(Config.KEY_PRODUCTQUANTITY),jsonObject.getString(Config.KEY_ORDERIMAGE)));
                Log.d("Url",jsonObject.getString(Config.KEY_USERID)+jsonObject.getString(Config.KEY_ORDERID)+jsonObject.getString(Config.KEY_PRODUCTID)+jsonObject.getString(Config.KEY_PRODUCTYPE)+jsonObject.getString(Config.KEY_PRODUCTNAME)+jsonObject.getString(Config.KEY_PRODUCTQUANTITY));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
            CustomListOrderFinal customListOrderFinal = new CustomListOrderFinal(getApplicationContext(), product);
            mList.setAdapter(customListOrderFinal);
            mList.setEmptyView(findViewById(R.id.empty));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textView_total:
                int sum= 0;
                for (int j = 0; j < product.size(); j++) {
                    View v = mList.getChildAt(j);
                    TextView txt = (TextView) v.findViewById(R.id.text_ProductRate);
                    String quantity =  txt.getText().toString();
                    String[] result = quantity.split("/", 2);
                    String num = result[0];
                    int n = Integer.parseInt(num);
                    sum = sum +n;
                }
                total.setText("Total: "+String.valueOf(sum)+"/-");
                orderEdit(Config.ORDER_EDIT_FINAL_URL,userId,total.getText().toString());
                break;
        }
    }

    private void orderEdit(String orderEditFinalUrl, final String Id, final String total) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, orderEditFinalUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        final Dialog mdialog = new Dialog(c);
                        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mdialog.setContentView(R.layout.orderiddialog);
                        mdialog.setCancelable(false);
                        TextView show = (TextView) mdialog.findViewById(R.id.text_show);
                        show.setText(orderId);
                        TextView btn_send = (TextView) mdialog.findViewById(R.id.text_submit);
                        btn_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mdialog.dismiss();
                            }
                        });
                        mdialog.show();
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
                params.put(Config.KEY_USERID,Id);
                params.put(Config.KEY_ORDERATE,total);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}