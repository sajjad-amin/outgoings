package com.sajjadamin.outgoings;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataViewHolder extends RecyclerView.ViewHolder {
    TextView id, date, amount, description;
    public DataViewHolder(@NonNull View itemView) {
        super(itemView);
        id = itemView.findViewById(R.id.data_id);
        date = itemView.findViewById(R.id.data_date);
        amount = itemView.findViewById(R.id.data_amount);
        description = itemView.findViewById(R.id.data_description);
    }
}
