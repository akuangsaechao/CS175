package akuangsaechao.volleyspot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import akuangsaechao.volleyspot.fragments.List;

public class VolleySpotListFragment extends ListFragment {

    OnHeadlineSelectedListener mCallback;

    public interface OnHeadlineSelectedListener {
        void onArticleSelected(int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List.refresh();

        int layout = android.R.layout.simple_list_item_activated_1;

        ArrayList<String> titles = new ArrayList<>();
        if (MainActivity.volleySpotList.size() > 0)
            for (int index : MainActivity.volleySpotList.keySet())
                titles.add(MainActivity.volleySpotList.get(index).title);

        setListAdapter(new ArrayAdapter<>(getActivity(), layout, titles));
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                Intent intent = new Intent(getActivity(), SpotDetails.class);
                intent.putExtra("MapLocation", arg2);
                startActivity(intent);
                return true;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getFragmentManager().findFragmentById(R.id.article_fragment) != null) {
            //getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnHeadlineSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onArticleSelected(position);
        //getListView().setItemChecked(position, true);
    }

}
