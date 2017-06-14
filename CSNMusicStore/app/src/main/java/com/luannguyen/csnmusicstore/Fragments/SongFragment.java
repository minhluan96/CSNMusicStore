package com.luannguyen.csnmusicstore.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.luannguyen.csnmusicstore.R;
import com.luannguyen.csnmusicstore.SearchResultActivity;
import com.luannguyen.csnmusicstore.Song;
import com.luannguyen.csnmusicstore.SongAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by infin on 25/04/2017.
 */

public class SongFragment extends Fragment {

    private List<Song> arrSong;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private TextView txtError;
    private static boolean isTaskRunning = false;

    private LinearLayoutManager layoutManager;


    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int visibleThreshold = 5;

    private static String url = "";
    private String pageIndicate = "&page=";
    private int pageNumber = 1;

    public SongFragment(){

    }

    public static void setUrl(String u) {

        url = u;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.song_searchfragment_layout, container, false);
        txtError = (TextView) rootView.findViewById(R.id.txtError);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_song_search);
        arrSong = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getContext());
        songAdapter = new SongAdapter(getContext(),arrSong);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(songAdapter);




        if(url.length() > 0) {
            /*url = ((SearchResultActivity) getActivity()).getURL_for_music();*/
            url += pageIndicate + pageNumber;

            new GetListofSong().execute(url);
            new GetImageOfSong().execute(arrSong);


            setupScrollAction();

        }
        else{

        }
        return rootView;
    }



    private void setupScrollAction(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();


                if(!isTaskRunning && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)){
                    pageNumber++;
                    url = url.substring(0, url.length() -1);
                    url += pageNumber;


                    new GetListofSong().execute(url);
                    new GetImageOfSong().execute(arrSong);
                }
            }
        });
    }




    public class GetImageOfSong extends AsyncTask<List<Song>, Void, List<Song>>{






        @Override
        protected List<Song> doInBackground(List<Song>... params) {
            String imageSong = "";
            for(int i = 0; i < params[0].size(); i++) {
                try {

                    Document docImage = Jsoup.connect(params[0].get(i).getUrl()).get();
                    Element itemImage = docImage.select("div.pl-c1").first();
                    for (Element img : itemImage.select("img")) {
                        if (img.attr("src").contains("/data/cover/")) {
                            params[0].get(i).setImageCover(img.attr("src"));
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return params[0];
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            songAdapter.setAlbumList(songs);

        }
    }



    public class GetListofSong extends AsyncTask<String, Void, List<Song>>{
        private List<Song> arr = new ArrayList<>();
        private ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isTaskRunning = true;
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Đang tải danh sách kết quả");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected List<Song> doInBackground(String... params) {

            try {
                Document doc = Jsoup.connect(params[0]).get();
                Element item = doc.select("table.tbtable").first();

                for(int i = 0; i < Math.min(8, item.select("tr").size()); i++){
                    for(Element tr_song : item.select("tr")) {
                        Elements info = tr_song.select("div.tenbh");
                        Elements allInfoSong = info.select("p");
                        Elements infoQuality = tr_song.select("span.gen");
                        Elements qualityStr = infoQuality.select("span");


                        if (allInfoSong.size() > 0) {

                            if (!allInfoSong.first().select("a").isEmpty()) {
                                Elements urlSong = allInfoSong.first().select("a");

                                String url = urlSong.attr("href");
                                String name = allInfoSong.first().text();
                                String author = allInfoSong.next().text();


                                String quality = infoQuality.text().replaceFirst(" ", "\n");
                                if (url.length() + name.length() + author.length() > 0) {
                                    Song song = new Song(name, author, "", url, quality);
                                    arr.add(song);
                                }
                            }
                        }
                    }
                }


                /*for(Element tr_song : item.select("tr")){
                    Elements info = tr_song.select("div.tenbh");
                    Elements allInfoSong = info.select("p");
                    Elements infoQuality = tr_song.select("span.gen");
                    Elements qualityStr = infoQuality.select("span");


                    if(allInfoSong.size()  > 0) {

                        if(!allInfoSong.first().select("a").isEmpty()){
                            Elements urlSong = allInfoSong.first().select("a");

                            String url = urlSong.attr("href");
                            String name = allInfoSong.first().text();
                            String author = allInfoSong.next().text();


                            String quality = infoQuality.text().replaceFirst(" ", "\n");

                            if (url.length() + name.length() + author.length() > 0) {

                                String imageSong = "";
                                Document docImage = Jsoup.connect(url).get();
                                Element itemImage = docImage.select("div.pl-c1").first();
                                for (Element img : itemImage.select("img")) {
                                    if (img.attr("src").contains("/data/cover/")) {
                                        imageSong = img.attr("src");
                                    }
                                }


                                Song song = new Song(name, author, imageSong, url, quality);
                                arr.add(song);
                            }
                        }
                    }
                }*/

                Log.d("temp", item.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return arr;
        }

        @Override
        protected void onPostExecute(List<Song> songs) {

            isTaskRunning = false;
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }


            if(songs.size() > 0) {
                arrSong.addAll(songs);

                int currentPosition = layoutManager.findFirstVisibleItemPosition();
                songAdapter.setAlbumList(arrSong);
                recyclerView.scrollToPosition(currentPosition + 1);

                recyclerView.setVisibility(View.VISIBLE);
                txtError.setVisibility(View.INVISIBLE);

            }
            else{
                recyclerView.setVisibility(View.INVISIBLE);
                txtError.setVisibility(View.VISIBLE);

            }

        }
    }


}
