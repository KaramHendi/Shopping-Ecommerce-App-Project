package com.example.smartkartapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Orders> ordersList;

    public OrdersAdapter(Context context, List<Orders> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders order = ordersList.get(position);

        holder.specTextView.setText("Item: " + order.getSpec());
        holder.priceTextView.setText("Price: ₹" + order.getPrice());
        holder.addressTextView.setText("Address: " + order.getCustaddr());

        String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "unknown";
        switch (status) {
            case "pending":
                holder.statusTextView.setText("Status: Ongoing");
                holder.statusTextView.setTextColor(Color.parseColor("#FFA000")); // Amber
                break;
            case "completed":
                holder.statusTextView.setText("Status: Completed");
                holder.statusTextView.setTextColor(Color.parseColor("#388E3C")); // Green
                break;
            default:
                holder.statusTextView.setText("Status: Unknown");
                holder.statusTextView.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView specTextView, priceTextView, addressTextView, statusTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            specTextView = itemView.findViewById(R.id.order_spec);
            priceTextView = itemView.findViewById(R.id.order_price);
            addressTextView = itemView.findViewById(R.id.order_address);
            statusTextView = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}
