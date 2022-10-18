package com.techflux.oyebhangarwalaagent.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.techflux.oyebhangarwalaagent.R;
import com.techflux.oyebhangarwalaagent.dataModel.ProductModel;

import java.util.List;

/**
 * Created by Lenovo on 07/06/2017.
 */
public class CustomListOrder extends BaseAdapter {
    
    Context mContext;
    List<ProductModel> products;
    private LayoutInflater mInflater;
    public CustomListOrder(Context mContext, List<ProductModel> products) {
        this.mContext = mContext;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
            mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.listview_order, null);
        ProductModel mProductModel = products.get(i);
        TextView productName= (TextView) view.findViewById(R.id.textView_ProductName);
        productName.setText(mProductModel.getDataType());
        Log.d("url", mProductModel.getDataType());
        TextView productType= (TextView) view.findViewById(R.id.textView_ProductType);
        productType.setText(mProductModel.getDataName());
        Log.d("url", mProductModel.getDataName());
        TextView productQuanitity= (TextView) view.findViewById(R.id.textView_ProductQuanitity);
        productQuanitity.setText(mProductModel.getDataQuantity());
        Log.d("url", mProductModel.getDataQuantity());
        ImageView imageView = (ImageView) view.findViewById(R.id.product_image);
        if (mProductModel.getDataType().equals("Glass Product")){
            imageView.setImageResource(R.drawable.i7);
        }
        if (mProductModel.getDataType().equals("Rubber Product")){
            imageView.setImageResource(R.drawable.i8);
        }
        if (mProductModel.getDataType().equals("Plastic Product")){
            imageView.setImageResource(R.drawable.i9);
        }
        if (mProductModel.getDataType().equals("Paper Product")){
            imageView.setImageResource(R.drawable.i10);
        }
        if (mProductModel.getDataType().equals("Cardboard Product")){
            imageView.setImageResource(R.drawable.i11);
        }
        if (mProductModel.getDataType().equals("Metal Product")){
            imageView.setImageResource(R.drawable.i12);
        }
        return view;
    }
}
