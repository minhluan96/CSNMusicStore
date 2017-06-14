package com.luannguyen.csnmusicstore;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.lapism.searchview.SearchView;
import com.luannguyen.csnmusicstore.Fragments.AlbumFragment;
import com.luannguyen.csnmusicstore.Fragments.ArtistFragment;
import com.luannguyen.csnmusicstore.Fragments.SongFragment;
import com.luannguyen.csnmusicstore.Fragments.USFragment;
import com.luannguyen.csnmusicstore.Fragments.VNFragment;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingSearchView floatingSearchView;
    private String URL_Root = "http://search.chiasenhac.vn:80/search.php?s=";
    private String prefix_artist = "artist";
    private String prefix_album = "album";
    private String suffix_cat = "&cat=music";
    private String prefix_mode = "&mode=";
    private String URL_for_music = "";
    private String URL_for_artist = "";
    private String URL_for_album = "";
    private String albumName = "";
    private ViewPagerAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_seach_result, null, false);
        drawer.addView(contentView,2);



        hideFloatActionButton();

        viewPager = (ViewPager) findViewById(R.id.viewpager_search);
        tabLayout = (TabLayout) findViewById(R.id.viewpagerTab_search);

        SetupViewPager(viewPager, null);

        floatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        floatingSearchView.attachNavigationDrawerToMenuButton(drawer);
        floatingSearchView.setDimBackground(false);


        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                currentQuery = currentQuery.replace(" ", "+");
                List<String> arrUrl = new ArrayList<String>();


                URL_for_music = URL_Root + currentQuery + prefix_mode + suffix_cat;
                URL_for_artist = URL_Root + currentQuery + prefix_mode + prefix_artist + suffix_cat;
                URL_for_album = URL_Root + currentQuery + prefix_mode + prefix_album + suffix_cat;
                arrUrl.add(URL_for_music);
                arrUrl.add(URL_for_artist);
                arrUrl.add(URL_for_album);

                albumName = currentQuery;


                SetupViewPager(viewPager, arrUrl);



            }
        });


        tabLayout.setupWithViewPager(viewPager);

    }





    private void SetupViewPager(ViewPager view, List<String> arrUrl){
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        SongFragment songFragment = new SongFragment();
        ArtistFragment artistFragment = new ArtistFragment();
        AlbumFragment albumFragment = new AlbumFragment();



        if (arrUrl != null ) {
            songFragment.setUrl(arrUrl.get(0));
            artistFragment.setUrl(arrUrl.get(1));
            albumFragment.setUrl(arrUrl.get(2));
            albumFragment.setAlbumName(albumName);

        }

        adapter.addFragment(songFragment, "Bài hát");
        adapter.addFragment(artistFragment, "Ca sĩ");
        adapter.addFragment(albumFragment, "Album");


        view.setAdapter(adapter);
    }


    public String getURL_for_music() {
        return URL_for_music;
    }

    public String getURL_for_artist() {
        return URL_for_artist;
    }

    public String getURL_for_album() {
        return URL_for_album;
    }
}
