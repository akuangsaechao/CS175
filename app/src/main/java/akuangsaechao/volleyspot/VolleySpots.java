package akuangsaechao.volleyspot;

import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

public class VolleySpots extends FragmentActivity implements VolleySpotListFragment.OnHeadlineSelectedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_volley_spots);

        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            VolleySpotListFragment firstFragment = new VolleySpotListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();

        } else {
            VolleySpotListFragment firstFragment = new VolleySpotListFragment();
            VolleySpotDetailFragment detailFragment = new VolleySpotDetailFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.headlines_fragment, firstFragment).add(R.id.article_fragment, detailFragment).commit();
        }

    }

    public void onArticleSelected(int position) {

        VolleySpotDetailFragment articleFrag = (VolleySpotDetailFragment) getSupportFragmentManager().findFragmentById(R.id.article_fragment);

        if (articleFrag != null) {
            articleFrag.setVisible();
            articleFrag.updateArticleView(position);
        } else {
            VolleySpotDetailFragment newFragment = new VolleySpotDetailFragment();
            Bundle args = new Bundle();
            args.putInt(VolleySpotDetailFragment.ARG_POSITION, position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}
