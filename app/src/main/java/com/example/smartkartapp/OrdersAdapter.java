package com.example.smartkartapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Orders> orderList;
    private Context context;

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

        // Order Again button
        holder.orderAgainBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaceOrder.class);
            intent.putExtra("CUSTNAME", order.getCustname());
            intent.putExtra("CUSTPH", order.getCustphone());
            intent.putExtra("CUSTPASS", order.getCustpass());
            intent.putExtra("ITEMDET", order.getSpec());
            intent.putExtra("item_price", order.getPrice());
            context.startActivity(intent);
        });

        // Delete Order button
        holder.removeOrderBtn.setOnClickListener(v -> {
            showConfirmationDialog(order, position);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void showConfirmationDialog(Orders order, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to remove this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteOrder(order, position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteOrder(Orders order, int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String orderId = order.getId();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("userOrders").child(userId).child(orderId).removeValue();
        rootRef.child("orders").child(orderId).removeValue();
        rootRef.child("deliverorder").child(orderId).removeValue(); // <- also remove from deliverorder

        orderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderList.size());
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderSpec, orderPrice, orderStatus, orderAddress;
        Button orderAgainBtn, removeOrderBtn;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderSpec = itemView.findViewById(R.id.order_spec);
            orderPrice = itemView.findViewById(R.id.order_price);
            orderStatus = itemView.findViewById(R.id.tvOrderStatus);
            orderAddress = itemView.findViewById(R.id.order_address);
            orderAgainBtn = itemView.findViewById(R.id.btnOrderAgain);
            removeOrderBtn = itemView.findViewById(R.id.btnRemoveOrder);
        }
    }
}
