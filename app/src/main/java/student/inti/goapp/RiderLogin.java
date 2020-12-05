package student.inti.goapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RiderLogin extends AppCompatActivity {

    private TextView regTxt, title, login, forget;
    private Button btn_login, btn_reg;
    private EditText email, password;

    private DatabaseReference driversDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    private ProgressDialog loadingBar;
    private FirebaseUser currentUser;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_login);

        mAuth = FirebaseAuth.getInstance();
        regTxt = (TextView) findViewById(R.id.txt_reg);
        title = (TextView) findViewById(R.id.title);
        forget = (TextView) findViewById(R.id.forgetPassword);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_reg = (Button) findViewById(R.id.btn_reg);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (TextView) findViewById(R.id.login);
        loadingBar = new ProgressDialog(this);

        btn_reg.setVisibility(View.INVISIBLE);
        btn_reg.setEnabled(false);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RiderLogin.this, DriverForgetPassword.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        regTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regTxt.setVisibility(View.INVISIBLE);
                btn_login.setVisibility(View.INVISIBLE);
                title.setText("DRIVER REGISTRATION");

                login.setVisibility(View.VISIBLE);
                btn_reg.setVisibility(View.VISIBLE);
                btn_reg.setEnabled(true);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regTxt.setVisibility(View.VISIBLE);
                btn_login.setVisibility(View.VISIBLE);
                title.setText("DRIVER LOGIN");

                login.setVisibility(View.INVISIBLE);
                btn_reg.setVisibility(View.INVISIBLE);
                btn_login.setEnabled(true);
            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String driverEmail = email.getText().toString();
                String driverPassword = password.getText().toString();

                if (TextUtils.isEmpty(driverEmail)) {
                    Toast.makeText(RiderLogin.this, "PLEASE ENTER YOUR EMAIL", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(driverPassword)) {
                    Toast.makeText(RiderLogin.this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();

                } else {
                    loadingBar.setTitle("PLEASE WAIT :");
                    loadingBar.setMessage("WHILE THE SYSTEM PROCESSING YOUR DATA INFORMATION");
                    loadingBar.show();
                    mAuth.createUserWithEmailAndPassword(driverEmail, driverPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUserId = mAuth.getCurrentUser().getUid();
                                driversDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(currentUserId);
                                driversDatabaseRef.setValue(true);
                                Intent intent = new Intent(RiderLogin.this, DriverMaps.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(RiderLogin.this, "ERROR. PLEASE TRY AGAIN LATER", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String driverEmail = email.getText().toString();
                String driverPassword = password.getText().toString();

                if (TextUtils.isEmpty(driverEmail)) {
                    Toast.makeText(RiderLogin.this, "PLEASE ENTER YOUR EMAIL", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(driverPassword)) {
                    Toast.makeText(RiderLogin.this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Please wait :");
                    loadingBar.setMessage("While system is performing processing on your data...");
                    loadingBar.show();
                    mAuth.signInWithEmailAndPassword(driverEmail, driverPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RiderLogin.this, "WELCOME BACK.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RiderLogin.this, DriverMaps.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RiderLogin.this, "ERROR. PLEASE TRY AGAIN LATER ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}