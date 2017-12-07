package akuangsaechao.volleyspot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
//import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOError;
import java.io.IOException;

public class Profile extends FragmentActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;

    private GoogleSignInClient mGoogleSignInClient;

    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mRevokeButton;
    private ImageView profilePicture;

    private boolean signedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Get references to all of the UI views
        mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        mSignOutButton = findViewById(R.id.sign_out_button);
        mRevokeButton = findViewById(R.id.revoke_access_button);
        profilePicture = findViewById(R.id.profilePicture);

        // Add click listeners for the buttons
        mSignInButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mRevokeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.revoke_access_button:
                revokeAccess();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        if (signedIn) {
            Toast.makeText(this, "Already Signed In", Toast.LENGTH_LONG).show();
        } else {
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        TextView text = findViewById(R.id.emailTextView);
                        text.setText("Signed Out");
                        signedIn = false;
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        TextView text = findViewById(R.id.emailTextView);
                        text.setText("Access Revoked");
                        signedIn = false;
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        TextView text = findViewById(R.id.emailTextView);
        text.setText(account.getEmail());

        String personName = account.getDisplayName();
        String personGivenName = account.getGivenName();
        String personFamilyName = account.getFamilyName();
        String personEmail = account.getEmail();
        String personId = account.getId();
        Uri personPhoto = account.getPhotoUrl();

        //try {
            //Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), personPhoto);
            //profilePicture.setImageBitmap(selectedImage);
       // } catch (IOException e) {

        //}
        signedIn = true;
    }

}
