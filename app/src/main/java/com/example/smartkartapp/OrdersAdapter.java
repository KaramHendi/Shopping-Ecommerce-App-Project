package com.example.smartkartapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Orders> orderList;
    private Context context;

    // Constructor that also accepts Context
    public OrdersAdapter(List<Orders> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders order = orderList.get(position);
        holder.orderSpec.setText(order.getSpec());
        holder.orderPrice.setText("Price: " + order.getPrice());
        holder.orderStatus.setText("Status: " + order.getStatus());
        holder.orderAddress.setText("Address: " + order.getCustaddr());

        // Set up "Order Again" button click listener
        holder.orderAgainBtn.setOnClickListener(v -> {
            // Your logic to place the order again, you can call the PlaceOrder activity or any relevant logic
            Intent intent = new Intent(context, PlaceOrder.class);
            intent.putExtra("CUSTNAME", order.getCustname());
            intent.putExtra("CUSTPH", order.getCustphone());
            intent.putExtra("CUSTPASS", order.getCustpass());
            intent.putExtra("ITEMDET", order.getSpec());
            intent.putExtra("item_price", order.getPrice());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderSpec, orderPrice, orderStatus, orderAddress;
        Button orderAgainBtn; // "Order Again" button

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderSpec = itemView.findViewById(R.id.order_spec);
            orderPrice = itemView.findViewById(R.id.order_price);
            orderStatus = itemView.findViewById(R.id.tvOrderStatus);
            orderAddress = itemView.findViewById(R.id.order_address);
            orderAgainBtn = itemView.findViewById(R.id.btnOrderAgain);  // Link to the button in your layout
        }
    }
}
