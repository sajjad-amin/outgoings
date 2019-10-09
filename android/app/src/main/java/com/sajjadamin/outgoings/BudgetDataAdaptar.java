package com.sajjadamin.outgoings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BudgetDataAdaptar extends RecyclerView.Adapter<BudgetDataViewHolder> {
    ArrayList<BudgetDataList> list;
    Context context;
    BudgetClick budgetClick;

    public BudgetDataAdaptar() {
    }

    public BudgetDataAdaptar(ArrayList<BudgetDataList> list, Context context, BudgetClick budgetClick) {
        this.list = list;
        this.context = context;
        this.budgetClick = budgetClick;
    }

    @NonNull
    @Override
    public BudgetDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.budget_row_layout,parent,false);
        final BudgetDataViewHolder budgetDataViewHolder = new BudgetDataViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetClick.onBudgetClick(view,budgetDataViewHolder.getPosition());
            }
        });
        return budgetDataViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetDataViewHolder holder, int position) {
        BudgetDataList currentList = list.get(position);
        holder.id.setText(currentList.getId());
        holder.title.setText(currentList.getTitle());
        holder.date.setText(currentList.getDate());
        holder.amount.setText(currentList.getAmount());
        holder.description.setText(currentList.getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
