package com.techflux.oyebhangarwalaagent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techflux.oyebhangarwalaagent.R;
import com.techflux.oyebhangarwalaagent.dataModel.OrderView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 07/06/2017.
 */
public class CustomListOrderView extends BaseAdapter {

    private LayoutInflater inflater;
    Context mContext;
    ArrayList<OrderView> mOrderList;
    public CustomListOrderView(Context mContext, ArrayList<OrderView> mOrderList) {
        this.mContext = mContext;
        this.mOrderList = mOrderList;
    }
    @Override
    public int getCount() {
        return mOrderList.size();
    }

    @Override
    public Object getItem(int i) {
        return mOrderList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listview_home, null);
            OrderView mOrderView= mOrderList.get(i);
            TextView userName= (TextView) convertView.findViewById(R.id.textView_name);
            userName.setText(mOrderView.getUserName());
            TextView userLandmark= (TextView) convertView.findViewById(R.id.textView_landmark);
            userLandmark.setText(mOrderView.getUserLandmark());

        return convertView;
    }
}
