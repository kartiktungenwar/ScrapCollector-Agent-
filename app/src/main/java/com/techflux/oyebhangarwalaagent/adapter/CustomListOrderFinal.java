package com.techflux.oyebhangarwalaagent.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.techflux.oyebhangarwalaagent.R;
import com.techflux.oyebhangarwalaagent.configData.Config;
import com.techflux.oyebhangarwalaagent.dataModel.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lenovo on 09/06/2017.
 */
public class CustomListOrderFinal extends BaseAdapter {

    private LayoutInflater flater;
    Context mContext;
    ArrayList<ProductModel> product;
    ArrayList<String> rate;
    String glass,rubber,plastic,paper,cardboard,metal;

    public String getGlass() {
        return glass;
    }

    public void setGlass(String glass) {
        this.glass = glass;
    }

    public String getRubber() {
        return rubber;
    }

    public void setRubber(String rubber) {
        this.rubber = rubber;
    }

    public String getPlastic() {
        return plastic;
    }

    public void setPlastic(String plastic) {
        this.plastic = plastic;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public String getCardboard() {
        return cardboard;
    }

    public void setCardboard(String cardboard) {
        this.cardboard = cardboard;
    }

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
    }

    public CustomListOrderFinal(Context mContext, ArrayList<ProductModel> product) {
        this.mContext = mContext;
        this.product = product;
    }

    @Override
    public int getCount() {
        return product.size();
    }

    @Override
    public Object getItem(int i) {
        return product.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View mView, ViewGroup viewGroup) {
        flater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = flater.inflate(R.layout._listview_order, null);
        ProductModel mProduct = product.get(i);
        TextView productName= (TextView) mView.findViewById(R.id.text_ProductName);
        productName.setText(mProduct.getDataType());
        Log.d("url", mProduct.getDataType());
        TextView productType= (TextView) mView.findViewById(R.id.text_ProductType);
        productType.setText(mProduct.getDataName());
        Log.d("url", mProduct.getDataName());
        TextView productQuanitity= (TextView) mView.findViewById(R.id.text_ProductQuantity);
        productQuanitity.setText(mProduct.getDataQuantity());
        getData(Config.PRODUCT_RATES,mView,mProduct);
        return mView;
    }


    private void getData(String subGroupUrl, final View mView, final ProductModel mProduct){
        Log.d("url : ", "" + subGroupUrl);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(subGroupUrl, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("url", "" + jsonArray);
                    rate = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            jsonObject.getString(Config.KEY_PRODUCTRATE);
                            rate.add(jsonObject.getString(Config.KEY_PRODUCTRATE));
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    TextView productRate = (TextView) mView.findViewById(R.id.text_ProductRate);
                    if (mProduct.getDataType().equals("Glass Product")){
                        String quantity =  mProduct.getDataQuantity();
                        String[] result1 = quantity.split(" ", 2);
                        String num1 = result1[0];
                        int n1 = Integer.parseInt(num1);
                        int n2 = Integer.parseInt(rate.get(0));
                        int mul = n1 * n2;
                        productRate.setText(mul+"/-" );
                    }
                    if (mProduct.getDataType().equals("Rubber Product")){
                        String quantity =  mProduct.getDataQuantity();
                        String[] result1 = quantity.split(" ", 2);
                        String num1 = result1[0];
                        int n1 = Integer.parseInt(num1);
                        int n2 = Integer.parseInt(rate.get(1));;
                        int mul = n1 * n2;
                        productRate.setText(mul+"/-" );
                    }
                    if (mProduct.getDataType().equals("Plastic Product")){
                        String quantity =  mProduct.getDataQuantity();
                        String[] result1 = quantity.split(" ", 2);
                        String num1 = result1[0];
                        int n1 = Integer.parseInt(num1);
                        int n2 = Integer.parseInt(rate.get(2));;
                        int mul = n1 * n2;
                        productRate.setText(mul+"/-" );
                    }
                    if (mProduct.getDataType().equals("Paper Product")){
                        String quantity =  mProduct.getDataQuantity();
                        String[] result1 = quantity.split(" ", 2);
                        String num1 = result1[0];
                        int n1 = Integer.parseInt(num1);
                        int n2 = Integer.parseInt(rate.get(3));;
                        int mul = n1 * n2;
                        productRate.setText(mul+"/-" );
                    }
                    if (mProduct.getDataType().equals("Cardboard Product")){
                        String quantity =  mProduct.getDataQuantity();
                        String[] result1 = quantity.split(" ", 2);
                        String num1 = result1[0];
                        int n1 = Integer.parseInt(num1);
                        int n2 = Integer.parseInt(rate.get(4));;
                        int mul = n1 * n2;
                        productRate.setText(mul+"/-" );
                    }
                    if (mProduct.getDataType().equals("Metal Product")){
                        String quantity =  mProduct.getDataQuantity();
                        String[] result1 = quantity.split(" ", 2);
                        String num1 = result1[0];
                        int n1 = Integer.parseInt(num1);
                        int n2 = Integer.parseInt(rate.get(5));;
                        int mul = n1 * n2;
                        productRate.setText(mul+"/-" );
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonArrayRequest);
    }

}
