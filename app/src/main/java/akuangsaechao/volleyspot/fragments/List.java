package akuangsaechao.volleyspot.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import akuangsaechao.volleyspot.Item;
import akuangsaechao.volleyspot.MainActivity;
import akuangsaechao.volleyspot.R;
import akuangsaechao.volleyspot.VolleySpotDetailFragment;
import akuangsaechao.volleyspot.VolleySpotListFragment;

public class List extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static ArrayList<Item> items = new ArrayList<>();
    public int position = -1;
    public static String POSITION = "POSITION";

    static {
        if (MainActivity.volleySpotList.size() > 0)
            for (int index : MainActivity.volleySpotList.keySet())
                items.add(MainActivity.volleySpotList.get(index));
    }

    public static void refresh(){
        items = new ArrayList<>();
        if (MainActivity.volleySpotList.size() > 0)
            for (int index : MainActivity.volleySpotList.keySet())
                items.add(MainActivity.volleySpotList.get(index));
    }

    public List() {
    }


    public static List newInstance() {
        Bundle bundle = new Bundle();
        List fragment = new List();

        return fragment;
    }

    public static List newInstance(int position) {
        Bundle bundle = new Bundle();
        List fragment = new List();
        bundle.putInt(POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh();
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
        } else {
            position = -1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        if (rootView.findViewById(R.id.fragment_container) != null) {
            if (position >= 0){
                VolleySpotDetailFragment article = new VolleySpotDetailFragment();
                Bundle args = new Bundle();
                args.putInt(VolleySpotDetailFragment.ARG_POSITION, position);
                article.setArguments(args);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_container, article).commit();
            } else {
                VolleySpotListFragment firstFragment = new VolleySpotListFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, firstFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else {
            VolleySpotListFragment firstFragment = new VolleySpotListFragment();
            VolleySpotDetailFragment article = new VolleySpotDetailFragment();
            if (position >= 0){
                Bundle args = new Bundle();
                args.putInt(VolleySpotDetailFragment.ARG_POSITION, position);
                article.setArguments(args);
            }
            getChildFragmentManager().beginTransaction().add(R.id.headlines_fragment, firstFragment).add(R.id.article_fragment, article).commit();
        }
        return rootView;
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
