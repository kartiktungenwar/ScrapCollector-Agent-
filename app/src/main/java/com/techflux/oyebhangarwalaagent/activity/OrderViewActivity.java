package com.techflux.oyebhangarwalaagent.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.techflux.oyebhangarwalaagent.R;
import com.techflux.oyebhangarwalaagent.adapter.CustomListOrder;
import com.techflux.oyebhangarwalaagent.configData.Config;
import com.techflux.oyebhangarwalaagent.dataModel.ProductModel;
import com.techflux.oyebhangarwalaagent.manager.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    Bitmap bitmap;
    ListView listView;
    ArrayList<ProductModel> productModel;
    String userId,orderId,configUrl,email;
    ProgressDialog pd;
    de.hdodenhof.circleimageview.CircleImageView upload;
    private ImageView done,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        Intent i = getIntent();
        email =  i.getStringExtra("IndexEmail");
        orderId = i.getStringExtra("IndexOrder");
        userId = i.getStringExtra("IndexId");
        productModel = new ArrayList<>();
        configUrl = Config.ORDER_URL+"?customer_email="+email+"&customer_order_id="+orderId;
        listView = (ListView) findViewById(R.id.listView_Order);
        getData(configUrl);
        done = (ImageView) findViewById(R.id.imageViewDone);
        cancel = (ImageView) findViewById(R.id.imageViewCancel);
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
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
                productModel.add(new ProductModel(jsonObject.getString(Config.KEY_USERID),jsonObject.getString(Config.KEY_ORDERID),jsonObject.getString(Config.KEY_PRODUCTID),jsonObject.getString(Config.KEY_PRODUCTYPE),jsonObject.getString(Config.KEY_PRODUCTNAME),jsonObject.getString(Config.KEY_PRODUCTQUANTITY),jsonObject.getString(Config.KEY_ORDERIMAGE)));
                Log.d("Url",jsonObject.getString(Config.KEY_USERID)+jsonObject.getString(Config.KEY_ORDERID)+jsonObject.getString(Config.KEY_PRODUCTID)+jsonObject.getString(Config.KEY_PRODUCTYPE)+jsonObject.getString(Config.KEY_PRODUCTNAME)+jsonObject.getString(Config.KEY_PRODUCTQUANTITY));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        CustomListOrder customListOrder = new CustomListOrder(getApplicationContext(), productModel);
        listView.setAdapter(customListOrder);
        listView.setOnItemClickListener(this);
        listView.setEmptyView(findViewById(R.id.empty));
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
        final Dialog mdialog = new Dialog(this);
        final ProductModel model = productModel.get(i);
        mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mdialog.setContentView(R.layout.editdialogbox);
        mdialog.setCancelable(false);
        ImageView closewindow = (ImageView) mdialog.findViewById(R.id.windowClose_Edit);
        closewindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.cancel();
            }
        });
        upload = (de.hdodenhof.circleimageview.CircleImageView) mdialog.findViewById(R.id.imageUpload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        });
        EditText et_id =  (EditText) mdialog.findViewById(R.id._ProductId);
        et_id.setText(productModel.get(i).getDataId());
        EditText et_name=  (EditText) mdialog.findViewById(R.id._ProductType);
        et_name.setText(productModel.get(i).getDataName());
        EditText et_type=  (EditText) mdialog.findViewById(R.id._ProductName);
        et_type.setText(productModel.get(i).getDataType());
        final EditText et_quantity=  (EditText) mdialog.findViewById(R.id._ProductQuantity);
        et_quantity.setText(productModel.get(i).getDataQuantity());
        de.hdodenhof.circleimageview.CircleImageView circleImageView = (de.hdodenhof.circleimageview.CircleImageView) mdialog.findViewById(R.id.imageUpload);
        Glide.with(getApplicationContext())
                .load(productModel.get(i).getDataImage())
                .centerCrop()
                .error(R.drawable.add_image).into(circleImageView);
        Button btn_send = (Button) mdialog.findViewById(R.id.done_Edit);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap== null){
                    Toast.makeText(OrderViewActivity.this, "Capture Image", Toast.LENGTH_SHORT).show();
                }else {
                    OrderEdit(Config.ORDER_EDIT_URL, model.getmId(), et_quantity.getText().toString(), bitmap);
                    mdialog.dismiss();
                    productModel.clear();
                    Intent i = getIntent();
                    i.putExtra("IndexEmail", email);
                    i.putExtra("IndexOrder", orderId);
                    i.putExtra("IndexId", userId);
                    finish();
                    startActivity(i);
                }
            }
        });
        mdialog.show();

    }

    private void OrderEdit(String orderEditUrl, final String id, final String quantity, Bitmap bitmap) {
        final String userImage = getStringImage(bitmap);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, orderEditUrl,
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
                params.put(Config.KEY_ORDERIMAGE,userImage);
                params.put(Config.KEY_PRODUCTQUANTITY,quantity);
                params.put(Config.KEY_USERID,id);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                bitmap = (Bitmap) data.getExtras().get("data");
                upload.setImageBitmap(bitmap);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageViewDone:
                    if(check()){
                        Intent i = new Intent(getApplicationContext(),OrderFinalActivity.class);
                        i.putExtra("IndexEmail",email);
                        i.putExtra("IndexOrder",orderId);
                        i.putExtra("IndexId",userId);
                        startActivity(i);;
                    }else {
                        Toast.makeText(OrderViewActivity.this, "Add Image to All", Toast.LENGTH_SHORT).show();
                    }

                break;
            case R.id.imageViewCancel:
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                break;
        }
    }

    private boolean check() {
        for(int j = 0; j <= productModel.size(); j++){
            String b = productModel.get(j).getDataImage();
            if(b.equals("null")){
                return false;
            }else {
               return true;
            }
        }
        return false;
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
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            sessionManager.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
