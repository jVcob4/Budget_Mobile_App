package com.example.my_savings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Budget extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference planbudgetRef;
    private FirebaseAuth mAuth;
    private Button mainmenu, budget, profile, expense;
    private FloatingActionButton choose;
    private ProgressDialog loader;
    private TextView totalbudgetamount;
    private RecyclerView recyclerView;
    private String post_key="";
    private String item="";
    private int amount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        mAuth = FirebaseAuth.getInstance();
        planbudgetRef = FirebaseDatabase.getInstance().getReference().child("Budget").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        totalbudgetamount = findViewById(R.id.activitybudget_totalbudgetamount_TextView);
        recyclerView = findViewById(R.id.activitybudget_list_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mainmenu = (Button) findViewById(R.id.activitybudget_mainmenu_Button);
        mainmenu.setOnClickListener(this);

        budget = (Button) findViewById(R.id.activitybudget_budget_Button);
        budget.setOnClickListener(this);

        expense = (Button) findViewById(R.id.activitybudget_expense_Button);
        expense.setOnClickListener(this);

        profile = (Button) findViewById(R.id.activitybudget_profile_Button);
        profile.setOnClickListener(this);

        planbudgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount=0;
                for(DataSnapshot snap:snapshot.getChildren()){
                    InputData data = snap.getValue(InputData.class);
                    totalAmount += data.getAmount();
                    String sTotal = String.valueOf("Budżet Miesięczny: "+ totalAmount + "zł");
                    totalbudgetamount.setText(sTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        choose = (FloatingActionButton) findViewById(R.id.activitybudget_add_FloatingButton);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planbudget();
            }
        });

    }
    private void planbudget() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layoutincome, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.inputlayoutincome_itemspinner_Spinner);
        final EditText amount = myView.findViewById(R.id.inputlayoutincome_amount_EditText);
        final Button cancel = myView.findViewById(R.id.inputlayoutincome_cancel_Button);
        final Button save = myView.findViewById(R.id.inputlayoutincome_save_Button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetAmount = amount.getText().toString();
                String budgetItem = itemSpinner.getSelectedItem().toString();
                if(TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Wprowadź Kwotę");
                    return;
                }
                if(budgetItem.equals("Kategoria")){
                    Toast.makeText(Budget.this,"Wybierz Kategorię", Toast.LENGTH_SHORT).show();
                }
                else{
                    loader.setMessage("Dodawananie do budżetu");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = planbudgetRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());
                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months months = Months.monthsBetween(epoch, now);
                    Weeks weeks = Weeks.weeksBetween(epoch, now);

                    InputData inputData = new InputData(budgetItem, date, id, null, Integer.parseInt(budgetAmount), months.getMonths(), weeks.getWeeks());
                    planbudgetRef.child(id).setValue(inputData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Budget.this,"Dodano do budżetu", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Budget.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();

                        }
                    });
                }
                dialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activitybudget_mainmenu_Button:
                startActivity(new Intent(this, MainMenu.class));
                break;
            case R.id.activitybudget_budget_Button:
                startActivity(new Intent(this, Budget.class));
                break;
            case R.id.activitybudget_profile_Button:
                startActivity(new Intent(this, Profile.class));
                break;
            case R.id.activitybudget_expense_Button:n:
                startActivity(new Intent(this, TodaySpending.class));
                break;

        }
    }

    protected  void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<InputData> options = new FirebaseRecyclerOptions.Builder<InputData>()
                .setQuery(planbudgetRef, InputData.class)
                .build();

        FirebaseRecyclerAdapter<InputData, MyViewHolder> adapter = new FirebaseRecyclerAdapter<InputData, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i, @NonNull InputData inputData) {
                myViewHolder.setItemAmount("Kwota: "+inputData.getAmount()+ " zł");
                myViewHolder.setDate("Data: " + inputData.getDate());
                myViewHolder.setItemName("Kategoria: " + inputData.getItem());

                myViewHolder.notes.setVisibility(View.GONE);

                switch (inputData.getItem()){
                    case "Wynagrodzenie":
                        myViewHolder.imageView.setImageResource(R.drawable.income);
                        break;
                    case "Wynagrodzenie Partnera/Partnerki":
                        myViewHolder.imageView.setImageResource(R.drawable.partnerincome);
                        break;
                    case "Premia":
                        myViewHolder.imageView.setImageResource(R.drawable.bonus);
                        break;
                    case "Odsetki Bankowe":
                        myViewHolder.imageView.setImageResource(R.drawable.bank);
                        break;
                    case "Sprzedaż":
                        myViewHolder.imageView.setImageResource(R.drawable.sold);
                        break;
                    case "Oszczędności":
                        myViewHolder.imageView.setImageResource(R.drawable.savings);
                        break;
                    case "Inne Przychody":
                        myViewHolder.imageView.setImageResource(R.drawable.other);
                        break;
                }
                myViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view){
                        post_key = getRef(i).getKey();
                        item = inputData.getItem();
                        amount = inputData.getAmount();
                        updateData();

                    }

                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenselist_layout, parent, false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        View view;
        public ImageView imageView;
        public TextView notes, date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imageView = itemView.findViewById(R.id.expenselistlayout_imageView);
            notes = itemView.findViewById(R.id.expenselistlayout_note_TextView);
            date = itemView.findViewById(R.id.expenselistlayout_date_TextView);

        }
        public void setItemName(String itemName){
            TextView item = view.findViewById(R.id.expenselistlayout_item_TextView);
                    item.setText(itemName);
        }
        public void setItemAmount(String itemAmount){
            TextView amount = view.findViewById(R.id.expenselistlayout_amount_TextView);
            amount.setText(itemAmount);
        }
        public void setDate(String itemDate){
            TextView item = view.findViewById(R.id.expenselistlayout_date_TextView);
            date.setText(itemDate);
        }
    }

    private void updateData()
    {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.updatedata_layout, null);
        myDialog.setView(mView);
        myDialog.setView(mView);

        final AlertDialog dialog = myDialog.create();
        final TextView mItem = mView.findViewById(R.id.updatedatalayout_itemName);
        final EditText mAmount = mView.findViewById(R.id.updatedatalayout_amount_EditText);
        final EditText mNotes = mView.findViewById(R.id.updatedatalayout_note_EditText);

        mNotes.setVisibility(View.GONE);
        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button deleteButton = mView.findViewById(R.id.updatedatalayout_delete_Button);
        Button updateButton = mView.findViewById(R.id.updatedata_update_Button);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    amount = Integer.parseInt(mAmount.getText().toString());

                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());
                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months months = Months.monthsBetween(epoch, now);
                    Weeks weeks = Weeks.weeksBetween(epoch, now);

                    InputData inputData = new InputData(item, date, post_key, null, amount, months.getMonths(), weeks.getWeeks());
                    planbudgetRef.child(post_key).setValue(inputData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Budget.this,"Zaktualizowano pomyślnie", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Budget.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.dismiss();

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                planbudgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Budget.this,"Usunięto Element", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Budget.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}