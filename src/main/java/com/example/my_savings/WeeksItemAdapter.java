package com.example.my_savings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeeksItemAdapter  extends  RecyclerView.Adapter<WeeksItemAdapter.ViewHolder> {

    private Context mContext;
    private List<InputData> myDataList;

    public WeeksItemAdapter(Context mContext, List<InputData> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.expenselist_layout, parent, false);
        return new WeeksItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final InputData data = myDataList.get(position);
        holder.item.setText("Kategoria: "+data.getItem());
        holder.amount.setText("Kwota: "+data.getAmount() + "zł");
        holder.date.setText("Data: "+data.getDate());
        holder.notes.setText("Opis: "+data.getNotes());


        switch (data.getItem()){
            case "Jedzenie":
                holder.imageView.setImageResource(R.drawable.food);
                break;
            case "Mieszkanie/Dom":
                holder.imageView.setImageResource(R.drawable.home);
                break;
            case "Transport":
                holder.imageView.setImageResource(R.drawable.transport);
                break;
            case "Telekomunikacja":
                holder.imageView.setImageResource(R.drawable.telecommunication);
                break;
            case "Opieka Zdrowotna":
                holder.imageView.setImageResource(R.drawable.health);
                break;
            case "Ubrania":
                holder.imageView.setImageResource(R.drawable.clothes);
                break;
            case "Higiena":
                holder.imageView.setImageResource(R.drawable.hygiene);
                break;
            case "Dzieci":
                holder.imageView.setImageResource(R.drawable.children);
                break;
            case "Rozrywka":
                holder.imageView.setImageResource(R.drawable.entertainment);
                break;
            case "Inne Wydatki":
                holder.imageView.setImageResource(R.drawable.other);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView item, amount, date, notes;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.expenselistlayout_item_TextView);
            amount = itemView.findViewById(R.id.expenselistlayout_amount_TextView);
            date = itemView.findViewById(R.id.expenselistlayout_date_TextView);
            notes = itemView.findViewById(R.id.expenselistlayout_note_TextView);
            imageView = itemView.findViewById(R.id.expenselistlayout_imageView);


        }
    }
}
