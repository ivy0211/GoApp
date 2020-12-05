package student.inti.goapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private ImageView closeButton, saveButton;
    private TextView email;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        closeButton = findViewById(R.id.btn_close);
        saveButton = findViewById(R.id.btn_save);
        email = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String forgetEmail = email.getText().toString().trim();
                if (forgetEmail.isEmpty()) {
                    email.setError("PLEASE ENTER AN EMAIL");
                    email.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(forgetEmail).matches()) {
                    email.setError("PLEASE ENTER A VALID EMAIL");
                    email.requestFocus();
                    return;
                }


                mAuth.sendPasswordResetEmail(forgetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            final ResetPassword restPasswordEmail = new ResetPassword(ForgetPassword.this);
                            restPasswordEmail.startLodingDialog();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    restPasswordEmail.dissmissDialog();

                                }
                            }, 3000);
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ForgetPassword.this, CustomerLogin.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            },3000);
                        } else {
                            email.setError("ACCOUNT CANNOT FOUND! PLEASE REGISTER");
                            email.requestFocus();
                        }
                    }
                });
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this, CustomerLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}