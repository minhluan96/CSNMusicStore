package com.luannguyen.csnmusicstore.Fragments;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luannguyen.csnmusicstore.Album;
import com.luannguyen.csnmusicstore.AlbumsAdapter;
import com.luannguyen.csnmusicstore.R;
import com.luannguyen.csnmusicstore.Song;
import com.luannguyen.csnmusicstore.TopSongAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by infin on 10/04/2017.
 */

public class USFragment extends Fragment {
    private RecyclerView recyclerView;
    private static TopSongAdapter songAdapter;
    private static List<Song> songList;


    public USFragment(){}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.usfragment_layout, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_US);
        songList = new ArrayList<>();


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new USFragment.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        songAdapter = new TopSongAdapter(getContext(), songList);
        recyclerView.setAdapter(songAdapter);



        new GetTopMusic().execute("http://chiasenhac.vn/mp3/us-uk/");


        return rootView;
    }

    public static void  configData(List<Song> responseData) {
        songAdapter.setAlbumList(responseData);
    }

    public class GetTopMusic extends AsyncTask<String, Void, List<Song>> {
        private List<Song> arrSong = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Override
        protected List<Song> doInBackground(String... params) {


            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            try {
                Document doc = Jsoup.connect(params[0]).get();
                Element item = doc.select("div.h-main4").first();
                Element subItem = item.select("div.h-center").first();
                Element temp  = subItem.select("div.list-r.list-1").first();

                for (Element childItem : subItem.select("div.list-r.list-1")) {
                    Elements songUrlItem = childItem.select("div.text2.text2x");
                    Elements songItem = songUrlItem.select("a");
                    Elements authorItem = songUrlItem.select("p");

                    String urlSong = songItem.attr("href");
                    String nameSong = songItem.attr("title");
                    String author = authorItem.text();

                    //get thumbnail of album
                    String imageAlbum = "";
                    Document docImage = Jsoup.connect(urlSong).get();
                    Element itemImage = docImage.select("div.pl-c1").first();
                    for(Element img : itemImage.select("img")){
                        if(img.attr("src").contains("/data/cover/")){
                            imageAlbum = img.attr("src");
                        }
                    }

                    Song song = new Song(nameSong,author,imageAlbum,urlSong, "");
                    arrSong.add(song);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return arrSong;
        }


        @Override
        protected void onPostExecute(List<Song> a) {
            configData(a);
        }
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration{
        private int spanCount;
        private int spacing;
        private boolean includeEdge;


        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if(includeEdge){
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if(position < spanCount){
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            }else{
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if(position >= spanCount){
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp){
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
