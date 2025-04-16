package com.example.smartkartapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class OrdersAdapter extends ArrayAdapter<Orders> {
    private Activity context;
    private List<Orders> orders;

    public OrdersAdapter(Activity context, List<Orders> orders) {
        super(context, R.layout.order_item_layout, orders);
        this.context = context;
        this.orders = orders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Use convertView for efficient view recycling
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.order_item_layout, parent, false);
        }

        // Initialize the views
        TextView orderDetails = convertView.findViewById(R.id.tvOrderDetails);
        TextView orderPrice = convertView.findViewById(R.id.tvOrderPrice);

        // Get the order object
        Orders order = orders.get(position);

        // Set the order details and price
        orderDetails.setText("Order: " + order.getSpec());
        orderPrice.setText("Price: $" + order.getPrice());  // Add currency formatting

        return convertView;
    }
}
