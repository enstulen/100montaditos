package com.uc3m.a100montaditos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity  extends AppCompatActivity {

    private SignInButton signInButton;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;


    /**
     * Setup Firebase and Google Signin. Also functionality for the signin button
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_view);
        getSupportActionBar().hide();

        signInButton = findViewById(R.id.signInButton);

        FirebaseApp.initializeApp(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    /**
     * Sign in user
     */
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Result from signing in
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "You got an error", Toast.LENGTH_LONG).show();
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    /**
     * If user successfully logs in, update the UI to go to the MainActivity
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        if (user != null ) {
            final User loggedInUser = new User(user.getUid(), user.getEmail(), user.getDisplayName());
            User.setCurrentUser(loggedInUser);

            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.hasChild(loggedInUser.getUid())) {
                        databaseReference.child(loggedInUser.getUid()).setValue(loggedInUser);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    /**
     * Authenticate user with Firebase
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "You got an error", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
