package com.example.smartkartapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OrdersAdapter extends ArrayAdapter<Orders> {

    private Context context;
    private ArrayList<Orders> ordersList;

    public OrdersAdapter(Context context, ArrayList<Orders> ordersList) {
        super(context, 0, ordersList);
        this.context = context;
        this.ordersList = ordersList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        }

        Orders currentOrder = ordersList.get(position);

        TextView specTextView = convertView.findViewById(R.id.order_spec);
        TextView priceTextView = convertView.findViewById(R.id.order_price);
        TextView addressTextView = convertView.findViewById(R.id.order_address);

        specTextView.setText(currentOrder.getSpec());
        priceTextView.setText(String.valueOf(currentOrder.getPrice()));
        addressTextView.setText(currentOrder.getCustaddr());

        return convertView;
    }
}
