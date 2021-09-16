package com.example.my_savings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChartView;
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
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class MainMenu extends AppCompatActivity {
    private Button mainmenu, budget, profile, todayspendings;
    private TextView  weekspendings, monthspendings, budgetvalue, todayvalue, weekvalue, monthvalue, savingstv;

    private FirebaseAuth mAuth;
    private DatabaseReference budgetref, expensesref, personalref;
    private String onlineuserID="";

    private  int totalamounthmonth = 0;
    private  int totalamounthbudget = 0;
    private  int totalamounthbudget2= 0;
    private  int totalamounthbudget3 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        budgetvalue = findViewById(R.id.activitymain_budget_TextView);
        todayvalue = findViewById(R.id.activitymain_Day_TextView);
        weekvalue = findViewById(R.id.activitymain_Week_TextView);
        monthvalue = findViewById(R.id.activitymain_Month_TextView);
        savingstv = findViewById(R.id.activitymain_savings_TextView);

        mAuth = FirebaseAuth.getInstance();
        onlineuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetref = FirebaseDatabase.getInstance().getReference("Budget").child(onlineuserID);
        expensesref = FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserID);
        personalref = FirebaseDatabase.getInstance().getReference("personal").child(onlineuserID);

        mainmenu = findViewById(R.id.activitymain_mainmenu_Button);
        mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MainMenu.this, MainMenu.class);
               startActivity(intent);
            }
        });

        budget = findViewById(R.id.activitymain_budget_Button);
        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, Budget.class);
                startActivity(intent);
            }
        });

        profile = findViewById(R.id.activitymain_profile_Button);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, Profile.class);
                startActivity(intent);
            }
        });

        todayspendings =findViewById(R.id.activitymain_addexpense_Button);
        todayspendings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, TodaySpending.class);
                startActivity(intent);
            }
        });


        weekspendings = findViewById(R.id.activitymain_WeekDescription_TextView);
        weekspendings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, WeekSpending.class);
                intent.putExtra("type", "week");
                startActivity(intent);
            }
        });

        monthspendings = findViewById(R.id.activitymain_MonthDescription_TextView);
        monthspendings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, WeekSpending.class);
                intent.putExtra("type", "month");
                startActivity(intent);
            }
        });


        budgetref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&& snapshot.getChildrenCount()>0){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Map<String,Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalamounthbudget2+=pTotal;
                    }
                    totalamounthbudget3 = totalamounthbudget2;
                    personalref.child("budget").setValue(totalamounthbudget3);
                }else{
                    personalref.child("budget").setValue(0);
                    Toast.makeText(MainMenu.this, "Ustal Budżet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getBudgetAmount();
        getTodaySpentAmount();
        getWeekSpentAmount();
        getMonthSpentAmount();
        getSavings();
    }

    private void getBudgetAmount() {
        budgetref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds :  snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalamounthbudget+=pTotal;
                        budgetvalue.setText(String.valueOf(totalamounthbudget)+ " zł");
                    }
                }else {
                    totalamounthbudget=0;
                    budgetvalue.setText(String.valueOf(0)+ " zł");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTodaySpentAmount(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserID);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    todayvalue.setText(totalAmount + " zł");
                }
               personalref.child("today").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainMenu.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getWeekSpentAmount(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserID);
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    weekvalue.setText(totalAmount + " zł");
                }
               personalref.child("week").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMonthSpentAmount(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineuserID);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    monthvalue.setText(totalAmount + " zł");

                }
                personalref.child("month").setValue(totalAmount);
                totalamounthmonth = totalAmount;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSavings(){
        personalref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int budget;
                    if (snapshot.hasChild("budget")) {
                        budget = Integer.parseInt(snapshot.child("budget").getValue().toString());
                    } else {
                        budget = 0;
                    }
                    int monthSpending;
                    if (snapshot.hasChild("month")) {
                        monthSpending = Integer.parseInt(Objects.requireNonNull(snapshot.child("month").getValue().toString()));
                    } else {
                        monthSpending = 0;
                    }

                    int savings = budget - monthSpending;
                    savingstv.setText(savings + " zł");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainMenu.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

