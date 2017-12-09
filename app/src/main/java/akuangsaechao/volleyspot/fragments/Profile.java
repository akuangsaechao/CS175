package akuangsaechao.volleyspot.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.IOException;

import akuangsaechao.volleyspot.MainActivity;
import akuangsaechao.volleyspot.R;

public class Profile extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private static final int RC_SIGN_IN = 0;

    private GoogleSignInClient mGoogleSignInClient;

    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mRevokeButton;
    private ImageView profilePicture;
    private TextView email, firstName, lastName;

    private boolean signedIn = false;
    public Profile() {

    }

    public static Profile newInstance() {
        Profile fragment = new Profile();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mSignInButton = rootView.findViewById(R.id.sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        mSignOutButton = rootView.findViewById(R.id.sign_out_button);
        mRevokeButton = rootView.findViewById(R.id.revoke_access_button);
        profilePicture = rootView.findViewById(R.id.profilePicture);

        firstName = rootView.findViewById(R.id.firstName);
        lastName = rootView.findViewById(R.id.lastName);
        email = rootView.findViewById(R.id.emailTextView);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            email.setText(acct.getEmail());
            firstName.setText(acct.getDisplayName());
            signedIn = true;
        }

        // Add click listeners for the buttons
        mSignInButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mRevokeButton.setOnClickListener(this);

        return rootView;
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
            Toast.makeText(getActivity(), "Already Signed In", Toast.LENGTH_LONG).show();
        } else {
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_LONG).show();
                        signedIn = false;
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Access Revoked", Toast.LENGTH_LONG).show();
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
        //TextView text = findViewById(R.id.emailTextView);
        //text.setText(account.getEmail());
        Uri personPhoto = account.getPhotoUrl();
        try {
            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), personPhoto);
            profilePicture.setImageBitmap(selectedImage);
        } catch (IOException e) {

        }
        signedIn = true;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
