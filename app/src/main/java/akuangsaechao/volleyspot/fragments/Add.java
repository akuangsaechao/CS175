package akuangsaechao.volleyspot.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import akuangsaechao.volleyspot.DbBitmapUtility;
import akuangsaechao.volleyspot.MyContentProvider;
import akuangsaechao.volleyspot.R;

public class Add extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ImageView volleySpotPicture;
    private Button addVolleySpot;
    private EditText volleySpotTitle, volleySpotLocation;
    public final static int PICK_PHOTO_CODE = 1046;
    private Bitmap selectedImage;

    public Add() {
    }

    public static Add newInstance() {
        Add fragment = new Add();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        volleySpotPicture = rootView.findViewById(R.id.volleySpotImage);
        volleySpotTitle = rootView.findViewById(R.id.volleySpotTitle);
        volleySpotLocation = rootView.findViewById(R.id.volleySpotLocation);
        addVolleySpot = rootView.findViewById(R.id.addVolleySpot);

        volleySpotPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        addVolleySpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = volleySpotTitle.getText().toString();
                String location = volleySpotLocation.getText().toString();
                addVolleySpot(title, location);



            }
        });
        return rootView;
    }

    public void addVolleySpot(String title, String location) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(MyContentProvider.TITLE, title);
        contentValues.put(MyContentProvider.ADDRESS, location);
        if (selectedImage != null){
            contentValues.put(MyContentProvider.IMAGE, DbBitmapUtility.getBytes(selectedImage));
        }

        Uri uri = getActivity().getContentResolver().insert(MyContentProvider.URI, contentValues);

        Toast.makeText(getActivity(), uri.toString(), Toast.LENGTH_LONG).show();

    }

    public void onPickPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            try {
                Uri photoUri = data.getData();
                selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                volleySpotPicture.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
