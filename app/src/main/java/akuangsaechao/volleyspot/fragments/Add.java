package akuangsaechao.volleyspot.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import akuangsaechao.volleyspot.DbBitmapUtility;
import akuangsaechao.volleyspot.Item;
import akuangsaechao.volleyspot.MainActivity;
import akuangsaechao.volleyspot.MyContentProvider;
import akuangsaechao.volleyspot.R;

public class Add extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ImageView volleySpotPicture;
    private Button addVolleySpot;
    private EditText volleySpotTitle, volleySpotLocation, volleySpotDescription;
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
        volleySpotDescription = rootView.findViewById(R.id.volleySpotDescription);
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
                String description = volleySpotDescription.getText().toString();
                addVolleySpot(title, location, description);


            }
        });
        return rootView;
    }

    public void addVolleySpot(String title, String location, String description) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(MyContentProvider.TITLE, title);
        contentValues.put(MyContentProvider.ADDRESS, location);
        contentValues.put(MyContentProvider.DESCRIPTION, description);
        if (selectedImage != null) {
            contentValues.put(MyContentProvider.IMAGE, DbBitmapUtility.getBytes(selectedImage));
        } else {
            //Bitmap bitmap =
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
            contentValues.put(MyContentProvider.IMAGE, DbBitmapUtility.getBytes(icon));
        }

        Uri uri = getActivity().getContentResolver().insert(MyContentProvider.URI, contentValues);

        Toast.makeText(getActivity(), "Volley Spot Added", Toast.LENGTH_LONG).show();

        volleySpotTitle.setText("");
        volleySpotLocation.setText("");
        volleySpotDescription.setText("");
        volleySpotPicture.setBackgroundResource(R.drawable.app_icon);

        getAllVolleySpots();
        List.refresh();


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
                volleySpotPicture.setImageResource(android.R.color.transparent);
                volleySpotPicture.setBackgroundResource(0);
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

    public void getAllVolleySpots() {
        String URL = "content://akuangsaechao.myapplication.myprovider/volleyspots";
        Uri devices = Uri.parse(URL);
        Cursor c = getActivity().getContentResolver().query(devices, null, null, null, "title");

        if (c.moveToFirst()) {
            do {
                String ID = c.getString(c.getColumnIndex(MyContentProvider._ID));

                if (!MainActivity.volleySpotList.containsKey(Integer.parseInt(ID))) {

                    String title = c.getString(c.getColumnIndex(MyContentProvider.TITLE));
                    String address = c.getString(c.getColumnIndex(MyContentProvider.ADDRESS));
                    String description = c.getString(c.getColumnIndex(MyContentProvider.DESCRIPTION));
                    byte[] image = c.getBlob(c.getColumnIndex(MyContentProvider.IMAGE));
                    Bitmap bitmap = null;
                    if (image != null)
                        bitmap = DbBitmapUtility.getImage(image);

                    String[] addressBreakDown = address.split(" ");

                    String zipCode = addressBreakDown[addressBreakDown.length - 1];

                    Item item = new Item();

                    item.id = ID;
                    item.title = title;
                    item.address = address;
                    item.image = bitmap;
                    item.description = description;

                    MainActivity.volleySpotList.put(Integer.parseInt(ID), item);

                    Add.GeoCoderFromAddress geoCoderFromAddress = new Add.GeoCoderFromAddress(address, Integer.parseInt(ID));
                    //GeoCoderFromAddress geoCoderFromAddress = new GeoCoderFromAddress("427 S. 5th Street, San Jose, CA 95112");
                    geoCoderFromAddress.execute();

                    Add.WeatherAPI weatherAPI = new Add.WeatherAPI(zipCode, Integer.parseInt(ID));
                    //WeatherAPI weatherAPI = new WeatherAPI("95112");
                    weatherAPI.execute();
                }
            } while (c.moveToNext());
        }

    }

    public class GeoCoderFromAddress extends AsyncTask<Void, Void, StringBuilder> {

        String place;
        int id;

        public GeoCoderFromAddress(String place, int id) {
            super();
            this.place = place;
            this.id = id;
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                //String googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyDvcHktVxizrhdJ-TS1txRMOgGuxO0hPOI";
                String after = place.trim().replaceAll(" ", "+");
                String googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + after + "&key=AIzaSyDvcHktVxizrhdJ-TS1txRMOgGuxO0hPOI";
                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {

                JSONObject jsonObj = new JSONObject(result.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");
                JSONObject before_geometry_jsonObj = resultJsonArray.getJSONObject(0);
                JSONObject geometry_jsonObj = before_geometry_jsonObj.getJSONObject("geometry");
                JSONObject location_jsonObj = geometry_jsonObj.getJSONObject("location");

                String lat_helper = location_jsonObj.getString("lat");
                String lng_helper = location_jsonObj.getString("lng");

                double lat = Double.valueOf(lat_helper);
                double lng = Double.valueOf(lng_helper);

                putGeoCode(lat, lng, id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class WeatherAPI extends AsyncTask<Void, Void, StringBuilder> {

        String place;
        int id;

        public WeatherAPI(String place, int id) {
            super();
            this.place = place;
            this.id = id;
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "https://api.openweathermap.org/data/2.5/weather?zip=" + place + ",us&units=imperial&APPID=7424bb17c1738362907a47e05e8686ee";
                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            try {

                JSONObject jsonObj = new JSONObject(result.toString());
                JSONObject location_jsonObj = jsonObj.getJSONObject("main");
                String temperature = location_jsonObj.getString("temp");

                putWeather(temperature, id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void putGeoCode(double lat, double lng, int id) {

        Item item = MainActivity.volleySpotList.get(id);
        item.latitude = lat;
        item.longitude = lng;
        MainActivity.volleySpotList.put(id, item);

    }

    public void putWeather(String temperature, int id) {

        Item item = MainActivity.volleySpotList.get(id);
        item.temperature = temperature;
        MainActivity.volleySpotList.put(id, item);

    }
}
