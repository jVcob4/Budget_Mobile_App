package com.example.my_savings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChartView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.PatternSyntaxException;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private GoogleSignInAccount googleSignInAccount;
    private TextView banner;
    private EditText editTextfullname, editTextage, editTextemailadress, editTextpassword;
    private ProgressBar progress_bar;
    private Button register_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner  = (TextView) findViewById(R.id.activityregisteruser_banner_TextView);
        banner.setOnClickListener(this);

        register_button = (Button) findViewById(R.id.activityregisteruser_register_Button);
        register_button.setOnClickListener(this);

        editTextfullname = (EditText) findViewById(R.id.activityregisteruser_fullname_EditText);
        editTextage = (EditText) findViewById(R.id.activityregisteruser_age_EditText);
        editTextemailadress = (EditText) findViewById(R.id.activityregisteruser_emailadress_EditText);
        editTextpassword = (EditText) findViewById(R.id.activityregisteruser_password_EditText);

        progress_bar = (ProgressBar) findViewById(R.id.activityregisteruser_ProgressBar);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activityregisteruser_banner_TextView:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.activityregisteruser_register_Button:
                registerUser();
                break;
        }
    }
    private void registerUser() {
        String fullname = editTextfullname.getText().toString().trim();
        String age = editTextage.getText().toString().trim();
        String email = editTextemailadress.getText().toString().trim();
        String password = editTextpassword.getText().toString().trim();
        if (fullname.isEmpty()){
            editTextfullname.setError("Wprowadź imię i nazwisko");
            editTextfullname.requestFocus();
            return;
        }
        if (age.isEmpty()){
            editTextage.setError("Wprowadź wiek");
            editTextage.requestFocus();
            return;
        }
        if (email.isEmpty()){
            editTextemailadress.setError("Wprowadź adres e-mail");
            editTextemailadress.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextemailadress.setError("Wprowadź poprawny adres e-mail");
            editTextemailadress.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editTextpassword.setError("Wprowadź hasło");
            editTextpassword.requestFocus();
            return;
        }
        if(password.length() <6){
            editTextpassword.setError("Minimalna długość hasła to 6 znaków");
            editTextpassword.requestFocus();
            return;
        }
        progress_bar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(fullname,age,email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "Pomyślnie zarejestrowano użytkownika", Toast.LENGTH_LONG).show();
                                        progress_bar.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(RegisterUser.this, "Wystąpił błąd podczas rejestracji! Spróbuj ponownie!", Toast.LENGTH_LONG).show();
                                        progress_bar.setVisibility(View.GONE);
                                    }
                                }
                            });

                    }else {
                            Toast.makeText(RegisterUser.this, "Wystąpił błąd podczas rejestracji! Spróbuj ponownie!", Toast.LENGTH_LONG).show();
                            progress_bar.setVisibility(View.GONE);
                        }
                }
        });
    }
}