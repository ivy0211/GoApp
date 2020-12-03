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

public class CustomerLogin extends AppCompatActivity {

    private TextView CreateCustomerAccount;
    private TextView TitleCustomer;
    private TextView login;
    private Button LoginCustomerButton;
    private Button RegisterCustomerButton;
    private EditText CustomerEmail;
    private EditText CustomerPassword;

    private DatabaseReference customersDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    private ProgressDialog loadingBar;

    private FirebaseUser currentUser;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    Intent intent = new Intent(CustomerLogin.this, CustomerMaps.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        CreateCustomerAccount = (TextView) findViewById(R.id.customer_register_link);
        login = (TextView) findViewById(R.id.login_customer);
        TitleCustomer = (TextView) findViewById(R.id.customer_status);
        LoginCustomerButton = (Button) findViewById(R.id.customer_login_btn);
        RegisterCustomerButton = (Button) findViewById(R.id.customer_register_btn);
        CustomerEmail = (EditText) findViewById(R.id.customer_email);
        CustomerPassword = (EditText) findViewById(R.id.customer_password);
        loadingBar = new ProgressDialog(this);

        RegisterCustomerButton.setVisibility(View.INVISIBLE);
        RegisterCustomerButton.setEnabled(false);

        CreateCustomerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCustomerAccount.setVisibility(View.INVISIBLE);
                LoginCustomerButton.setVisibility(View.INVISIBLE);
                TitleCustomer.setText("CUSTOMER REGISTRATION");

                login.setVisibility(View.VISIBLE);
                RegisterCustomerButton.setVisibility(View.VISIBLE);
                RegisterCustomerButton.setEnabled(true);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCustomerAccount.setVisibility(View.VISIBLE);
                LoginCustomerButton.setVisibility(View.VISIBLE);
                TitleCustomer.setText("CUSTOMER LOGIN");

                login.setVisibility(View.INVISIBLE);
                RegisterCustomerButton.setVisibility(View.INVISIBLE);
                LoginCustomerButton.setEnabled(true);
            }
        });


        RegisterCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = CustomerEmail.getText().toString();
                String password = CustomerPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(CustomerLogin.this, "PLEASE ENTER YOUR EMAIL", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(CustomerLogin.this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();

                } else {
                    loadingBar.setTitle("PLEASE WAIT :");
                    loadingBar.setMessage("WHILE THE SYSTEM PROCESSING YOUR DATA");
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUserId = mAuth.getCurrentUser().getUid();
                                customersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(currentUserId);
                                customersDatabaseRef.setValue(true);

                                Intent intent = new Intent(CustomerLogin.this, CustomerMaps.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                loadingBar.dismiss();

                            } else {
                                Toast.makeText(CustomerLogin.this, "ERROR. PLEASE TRY AGAIN LATER", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });

        LoginCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = CustomerEmail.getText().toString();
                String password = CustomerPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(CustomerLogin.this, "PLEASE ENTER YOUR EMAIL", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(CustomerLogin.this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("PLEASE WAIT :");
                    loadingBar.setMessage("WHILE THE SYSTEM PROCESSING YOUR DATA");
                    loadingBar.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CustomerLogin.this, "WELCOME BACK", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CustomerLogin.this, CustomerMaps.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                loadingBar.dismiss();

                            } else {
                                Toast.makeText(CustomerLogin.this, "ERROR. PLEASE TRY AGAIN LATER", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}