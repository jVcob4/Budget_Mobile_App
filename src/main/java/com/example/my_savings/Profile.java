package com.example.my_savings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity  implements View.OnClickListener {

    private Button logout, mainmenu, profile, expense, budget, delete;
    private TextView email;
    private DatabaseReference Ref,Ref2,Ref3,Ref4;
    private FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logout = (Button) findViewById(R.id.activityprofile_signOut_Button);
        logout.setOnClickListener(this);

        delete = (Button) findViewById(R.id.activityprofile_deleteAccount_Button);
        delete.setOnClickListener(this);

        mainmenu = (Button) findViewById(R.id.activityprofile_mainmenu_Button);
        mainmenu.setOnClickListener(this);

        profile = (Button) findViewById(R.id.activityprofile_profile_Button);
        profile.setOnClickListener(this);

        expense = (Button) findViewById(R.id.activityprofile_expense_Button);
        expense.setOnClickListener(this);

        budget= (Button) findViewById(R.id.activityprofile_budget_Button);
        budget.setOnClickListener(this);

        email = (TextView)  findViewById(R.id.activityprofile_TextView);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activityprofile_mainmenu_Button:
                startActivity(new Intent(this, MainMenu.class));
                break;

            case R.id.activityprofile_profile_Button:
                startActivity(new Intent(this, Profile.class));
                break;
            case R.id.activityprofile_expense_Button:history_button:
                startActivity(new Intent(this, TodaySpending.class));
                break;
            case R.id.activityprofile_signOut_Button:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this, MainActivity.class));
                break;
            case R.id.activityprofile_budget_Button:
                startActivity(new Intent(this, Budget.class));
                break;
            case R.id.activityprofile_deleteAccount_Button:

                Ref = FirebaseDatabase.getInstance().getReference("Users");
                Ref2 = FirebaseDatabase.getInstance().getReference("Budget");
                Ref3 = FirebaseDatabase.getInstance().getReference("expenses");
                Ref4 = FirebaseDatabase.getInstance().getReference("personal");
                Ref.child(user.getUid()).removeValue();
                Ref2.child(user.getUid()).removeValue();
                Ref3.child(user.getUid()).removeValue();
                Ref4.child(user.getUid()).removeValue();

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Profile.this, "Pomyślnie usunięto użytkownika", Toast.LENGTH_LONG).show();
                                }
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(Profile.this, MainActivity.class));
                            }
                        });

                break;
        }
    }

}

