package com.example.my_savings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeekSpending extends AppCompatActivity {
    private Button mainmenu, budget, profile, expense;
    private TextView totalweekamount;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private WeeksItemAdapter weeksItemAdapter;
    private List<InputData> myDataList;

    private FirebaseAuth mAuth;
    private String onlineUserId= "";
    private DatabaseReference expensesRef;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_spending);

        mainmenu = findViewById(R.id.activityweekspending_mainmenu_Button);
        mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WeekSpending.this, MainMenu.class));
            }
        });
        budget = findViewById(R.id.activityweekspending_budget_Button);
        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WeekSpending.this, Budget.class));
            }
        });
        expense = findViewById(R.id.activityweekspending_expense_Button);
        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WeekSpending.this, TodaySpending.class));
            }
        });
        profile = findViewById(R.id.activityweekspending_profile_Button);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WeekSpending.this, Profile.class));
            }
        });

        totalweekamount = findViewById(R.id.activityweekspending_totalamount_TextView);
        progressBar = findViewById(R.id.activityweekspending_progressBar);
        recyclerView = findViewById(R.id.activityweekspending_list_recyclerview);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();

        myDataList = new ArrayList<>();
        weeksItemAdapter = new WeeksItemAdapter(WeekSpending.this,myDataList);
        recyclerView.setAdapter(weeksItemAdapter);

        if(getIntent().getExtras()!=null){
            type = getIntent().getStringExtra("type");
            if(type.equals("week")){
                readWeekSpending();
            }else if (type.equals("month")){
                readMonthSpending();

            }
        }


    }

    private void readMonthSpending() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = expensesRef.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    InputData data = dataSnapshot.getValue(InputData.class);
                    myDataList.add(data);
                }
                weeksItemAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                int totalAmount=0;
                for(DataSnapshot ds: snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    totalweekamount.setText("Miesięczne Wydatki: " + totalAmount + "zł");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readWeekSpending() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = expensesRef.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    InputData data = dataSnapshot.getValue(InputData.class);
                    myDataList.add(data);
                }
                weeksItemAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                int totalAmount=0;
                for(DataSnapshot ds: snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalweekamount.setText("Tygodniowe Wydatki: " + totalAmount + "zł");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}