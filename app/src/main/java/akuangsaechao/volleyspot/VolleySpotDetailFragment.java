package akuangsaechao.volleyspot;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class VolleySpotDetailFragment extends Fragment {

    public final static String ARG_POSITION = "position";
    public static Marker previousMarker = null;
    public int mCurrentPosition = -1;
    public GoogleMap googleMap;
    public MapView mMapView;
    public ImageView imageView;
    public TextView title;
    public LinearLayout linearLayout, parentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        View rootView = inflater.inflate(R.layout.fragment_volley_spot_detail, container, false);

        imageView = rootView.findViewById(R.id.volleySpotImage);
        title = rootView.findViewById(R.id.volleySpotTitle);
        linearLayout = rootView.findViewById(R.id.informationLayout);
        parentLayout = rootView.findViewById(R.id.parentLayout);
        setInvisible();
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                makeMap();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            updateArticleView(args.getInt(ARG_POSITION));
            setVisible();
        } else if (mCurrentPosition != -1) {
            updateArticleView(mCurrentPosition);
            makeMap();
            setInvisible();
        }
    }

    public void updateArticleView(int position) {
        if (mCurrentPosition != position) {
            mCurrentPosition = position;
            Item item = VolleySpots.items.get(position);
            if (item.image != null){
                imageView.setImageResource(android.R.color.transparent);
                imageView.setBackgroundResource(0);
                imageView.setImageBitmap(item.image);

            }
            title.setText(item.title);

            if (googleMap != null)
                makeMap();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }

    public void makeMap() {

        if (mCurrentPosition >= 0) {

            if (previousMarker != null)
                previousMarker.remove();

            Item item = VolleySpots.items.get(mCurrentPosition);
            LatLng latLng = new LatLng(item.latitude, item.longitude);
            previousMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

    }

    public void setInvisible() {
        parentLayout.setVisibility(View.INVISIBLE);
    }

    public void setVisible() {
        parentLayout.setVisibility(View.VISIBLE);
    }

}
