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

    private TextView CreateDriverAccount;
    private TextView TitleDriver;
    private TextView login;
    private Button LoginDriverButton;
    private Button RegisterDriverButton;
    private EditText DriverEmail;
    private EditText DriverPassword;

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
        CreateDriverAccount = (TextView) findViewById(R.id.create_driver_account);
        TitleDriver = (TextView) findViewById(R.id.title_driver);
        LoginDriverButton = (Button) findViewById(R.id.login_driver_btn);
        RegisterDriverButton = (Button) findViewById(R.id.register_driver_btn);
        DriverEmail = (EditText) findViewById(R.id.driver_email);
        DriverPassword = (EditText) findViewById(R.id.driver_password);
        login = (TextView) findViewById(R.id.login);
        loadingBar = new ProgressDialog(this);

        RegisterDriverButton.setVisibility(View.INVISIBLE);
        RegisterDriverButton.setEnabled(false);

        CreateDriverAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDriverAccount.setVisibility(View.INVISIBLE);
                LoginDriverButton.setVisibility(View.INVISIBLE);
                TitleDriver.setText("DRIVER REGISTRATION");

                login.setVisibility(View.VISIBLE);
                RegisterDriverButton.setVisibility(View.VISIBLE);
                RegisterDriverButton.setEnabled(true);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateDriverAccount.setVisibility(View.VISIBLE);
                LoginDriverButton.setVisibility(View.VISIBLE);
                TitleDriver.setText("DRIVER LOGIN");

                login.setVisibility(View.INVISIBLE);
                RegisterDriverButton.setVisibility(View.INVISIBLE);
                LoginDriverButton.setEnabled(true);
            }
        });

        RegisterDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = DriverEmail.getText().toString();
                String password = DriverPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RiderLogin.this, "PLEASE ENTER YOUR EMAIL", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RiderLogin.this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();

                } else {
                    loadingBar.setTitle("PLEASE WAIT :");
                    loadingBar.setMessage("WHILE THE SYSTEM PROCESSING YOUR DATA INFORMATION");
                    loadingBar.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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


        LoginDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = DriverEmail.getText().toString();
                String password = DriverPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RiderLogin.this, "PLEASE ENTER YOUR EMAIL", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RiderLogin.this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Please wait :");
                    loadingBar.setMessage("While system is performing processing on your data...");
                    loadingBar.show();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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