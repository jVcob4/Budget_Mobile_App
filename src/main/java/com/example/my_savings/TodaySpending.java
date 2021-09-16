package com.example.my_savings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TodaySpending extends AppCompatActivity {
    private TextView totalamount;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private String onlineUserId= "";
    private DatabaseReference expensesRef;

    private TodaysItemAdapter todaysItemAdapter;
    private List<InputData> myDataList;
    private Button mainmenu, budget, profile, expense;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);
        totalamount = findViewById(R.id.activitytodayspending_totalamount_TextView);
        progressBar = findViewById(R.id.activitytodayspending_progressBar);
        fab = findViewById(R.id.activitytodayspending_add_FloatingButton);
        loader = new ProgressDialog(this);

        recyclerView = findViewById(R.id.activitytodayspending_list_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myDataList = new ArrayList<>();
        todaysItemAdapter = new TodaysItemAdapter(TodaySpending.this,myDataList);
        recyclerView.setAdapter(todaysItemAdapter);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);

        readItem();

        mainmenu = findViewById(R.id.activitytodayspending_mainmenu_Button);
        mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TodaySpending.this, MainMenu.class));
            }
        });
        budget = findViewById(R.id.activitytodayspending_budget_Button);
        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TodaySpending.this, Budget.class));
            }
        });
        expense = findViewById(R.id.activitytodayspending_expense_Button);
        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TodaySpending.this, TodaySpending.class));
            }
        });
        profile = findViewById(R.id.activitytodayspending_profile_Button);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TodaySpending.this, Profile.class));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSpentOn();
            }
        });
    }

    private void readItem() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    InputData data = dataSnapshot.getValue(InputData.class);
                    myDataList.add(data);
                }
                todaysItemAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount=0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total= map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalamount.setText("Dzisiejsze Wydatki: " +totalAmount + "zł");
                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addSpentOn(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.inputlayout_itemspinner_Spinner);
        final EditText amount = myView.findViewById(R.id.inputlayout_amount_EditText);
        final EditText note = myView.findViewById(R.id.inputlayout_note_EditText);
        final Button cancel = myView.findViewById(R.id.inputlayout_cancel_Button);
        final Button save = myView.findViewById(R.id.inputlayout_save_Button);
        note.setVisibility(View.VISIBLE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ExpenseAmount = amount.getText().toString();
                String Item = itemSpinner.getSelectedItem().toString();
                String notes = note.getText().toString();
                if(TextUtils.isEmpty(ExpenseAmount)){
                    amount.setError("Wprowadź Kwotę");
                    return;
                }
                if(Item.equals("Kategoria")){
                    Toast.makeText(TodaySpending.this,"Wybierz Kategorię", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(notes)){
                    note.setError("Opis jest Wymagany");
                    return;
                }
                else{
                    loader.setMessage("Dodawananie do budżetu");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = expensesRef.push().getKey();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch, now);
                    Months months = Months.monthsBetween(epoch, now);

                    InputData inputData = new InputData(Item, date, id, notes, Integer.parseInt(ExpenseAmount), months.getMonths(), weeks.getWeeks());
                    expensesRef.child(id).setValue(inputData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(TodaySpending.this,"Dodano do budżetu", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(TodaySpending.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
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
}