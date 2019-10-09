package com.sajjadamin.outgoings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdaptar extends RecyclerView.Adapter<DataViewHolder> {
    ArrayList<DataList> list;
    Context context;
    DataClick click;

    public DataAdaptar() {
    }

    public DataAdaptar(ArrayList<DataList> list, Context context, DataClick click) {
        this.list = list;
        this.context = context;
        this.click = click;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_row_layout,parent,false);
        final DataViewHolder dataViewHolder = new DataViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.onDataClick(view);
            }
        });
        return dataViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        DataList currentList = list.get(position);
        holder.id.setText(currentList.getId());
        holder.date.setText(currentList.getDate());
        holder.amount.setText(currentList.getAmount());
        holder.description.setText(currentList.getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilter(ArrayList<DataList> newList){
        list = new ArrayList<>();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}
