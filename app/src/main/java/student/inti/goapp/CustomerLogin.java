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

import org.w3c.dom.Text;

public class CustomerLogin extends AppCompatActivity {

    private TextView createAccount,title, login, forgetPassword;
    private Button btnLogin, btnReg;
    private EditText email, password;

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

        createAccount = (TextView) findViewById(R.id.txt_reg);
        login = (TextView) findViewById(R.id.txt_login);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        title = (TextView) findViewById(R.id.title);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReg = (Button) findViewById(R.id.btn_reg);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        loadingBar = new ProgressDialog(this);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerLogin.this, ForgetPassword.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount.setVisibility(View.INVISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
                forgetPassword.setVisibility(View.INVISIBLE);
                title.setText("CUSTOMER REGISTRATION");

                login.setVisibility(View.VISIBLE);
                btnReg.setVisibility(View.VISIBLE);
                btnReg.setEnabled(true);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                forgetPassword.setVisibility(View.VISIBLE);
                title.setText("CUSTOMER LOGIN");

                login.setVisibility(View.INVISIBLE);
                btnReg.setVisibility(View.INVISIBLE);
                btnLogin.setEnabled(true);
            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customerEmail = email.getText().toString();
                String customerPassword = password.getText().toString();

                if (TextUtils.isEmpty(customerEmail)) {
                    Toast.makeText(CustomerLogin.this, "PLEASE ENTER YOUR EMAIL", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(customerPassword)) {
                    Toast.makeText(CustomerLogin.this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();

                } else {
                    loadingBar.setTitle("PLEASE WAIT:");
                    loadingBar.setMessage("WHILE THE SYSTEM PROCESSING YOUR DATA");
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(customerEmail, customerPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customerEmail = email.getText().toString();
                String customerPassword = password.getText().toString();

                if (TextUtils.isEmpty(customerEmail)) {
                    Toast.makeText(CustomerLogin.this, "PLEASE ENTER YOUR EMAIL", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(customerPassword)) {
                    Toast.makeText(CustomerLogin.this, "PLEASE ENTER YOUR PASSWORD", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("PLEASE WAIT:");
                    loadingBar.setMessage("WHILE THE SYSTEM PROCESSING YOUR DATA");
                    loadingBar.show();

                    mAuth.signInWithEmailAndPassword(customerEmail, customerPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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