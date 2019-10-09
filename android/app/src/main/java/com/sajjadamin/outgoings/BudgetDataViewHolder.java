package com.sajjadamin.outgoings;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BudgetDataViewHolder extends RecyclerView.ViewHolder {
    TextView id, title, description, date, amount;
    public BudgetDataViewHolder(@NonNull View itemView) {
        super(itemView);
        id = itemView.findViewById(R.id.budget_id);
        title = itemView.findViewById(R.id.budget_title);
        description = itemView.findViewById(R.id.budget_description);
        date = itemView.findViewById(R.id.budget_date);
        amount = itemView.findViewById(R.id.budget_amount);
    }
}
