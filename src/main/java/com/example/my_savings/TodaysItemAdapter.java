package com.example.my_savings;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodaysItemAdapter extends RecyclerView.Adapter<TodaysItemAdapter.ViewHolder>{

    private Context mContext;
    private List<InputData> myDataList;
    private String id="";
    private String item="";
    private String note="";
    private int amount = 0;
    private DatabaseReference reference;

    public TodaysItemAdapter(Context mContext, List<InputData> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.expenselist_layout, parent, false);
        return new TodaysItemAdapter.ViewHolder(view);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                  id = data.getId();
                  item = data.getItem();
                  amount = data.getAmount();
                  note = data.getNotes();
                  updateData();

           }

        });
    }

    private void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.updatedata_layout, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final TextView mItem = mView.findViewById(R.id.updatedatalayout_itemName);
        final EditText mAmount = mView.findViewById(R.id.updatedatalayout_amount_EditText);
        final EditText mNotes = mView.findViewById(R.id.updatedatalayout_note_EditText);

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        mNotes.setText(String.valueOf(note));
        mNotes.setSelection(String.valueOf(note).length());

        Button deleteButton = mView.findViewById(R.id.updatedatalayout_delete_Button);
        Button updateButton = mView.findViewById(R.id.updatedata_update_Button);

        updateButton.setOnClickListener((view) ->  {

                amount = Integer.parseInt(mAmount.getText().toString());
                note = mNotes.getText().toString();

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());
                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Months months = Months.monthsBetween(epoch, now);
                Weeks weeks = Weeks.weeksBetween(epoch, now);

                InputData inputData = new InputData(item, date, id, note, amount, months.getMonths(), weeks.getWeeks());

                reference = FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(id).setValue(inputData).addOnCompleteListener(task ->  {

                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Zaktualizowano pomyślnie", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                });
                dialog.dismiss();



        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference = FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext,"Usunięto Element", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(mContext,task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
      });

        dialog.show();

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
