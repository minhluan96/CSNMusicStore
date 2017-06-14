package com.luannguyen.csnmusicstore;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity {


    private TextView txtName;
    private TextView txtAuthor;
    private ImageView albumCover;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton fabPlay;
    private static Album album;
    private static SongAdapter songAdapter;
    private static List<Song> listSong = new ArrayList<>();

    getListSongOfAlbum taskGetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        album = getIntent().getParcelableExtra("ALBUM");
        txtName = (TextView) findViewById(R.id.nameAlbum);
        txtAuthor = (TextView) findViewById(R.id.authorAlbum);
        fabPlay = (FloatingActionButton) findViewById(R.id.fabPlay);

        listSong = new ArrayList<>();
        albumCover = (ImageView) findViewById(R.id.imageAlbum);
        txtName.setText(album.getName());
        txtAuthor.setText(album.getAuthor());
        Picasso.with(this).load(album.getThumbnail()).into(albumCover);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_album_detail);
        layoutManager = new LinearLayoutManager(this);
        songAdapter = new SongAdapter(this, listSong);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(songAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    fabPlay.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0 || dy < 0 && fabPlay.isShown()){
                    fabPlay.hide();
                }
            }
        });

        new getListSongOfAlbum().execute(album.getUrl());
    }

    public static void updateAlbum(List<Song> s){
        album.setArrSong(s);
        songAdapter.setAlbumList(s);
    }

    public class getListSongOfAlbum extends AsyncTask<String, Void, List<Song>>{

        private List<Song> arr = new ArrayList<>();
        @Override
        protected List<Song> doInBackground(String... params) {

            try {
                Document doc = Jsoup.connect(album.getUrl()).get();
                Element item = doc.select("table.tbtable").first();
                Element table = item.select("tbody").first();
                for(Element tr : table.select("tr")){
                    for(Element td: tr.select("td")){
                        Elements span = td.select("span.gen");
                        if(span.select("a").first() != null){
                            Element a = span.select("a").first();
                            String url = a.attr("href");
                            String title = a.attr("title");
                            String name = title.substring(("Download").length() + 1);
                            String author = span.text();

                            Song song = new Song(name,author, album.getThumbnail(), url, "");
                            arr.add(song);
                        }
                    }
                }

               /* Elements trList = table.select("tr");
                int size = trList.size();
                for (int i = 0; i < size; i+=2) {
                    Elements trFirst = trList.get(i).select ("td");
                    Elements trSecond = trList.get(i + 1).select("td");
                    int contentSize = Math.min(trFirst.size(), trSecond.size());

                    for (int j = 0; j < contentSize; j++) {
                        Song song = new Song();

                        Element aItem1 = trFirst.get(j).select("a").first();
                        String link = aItem1.attr("href");
                        String imgUrl = aItem1.select("img").first().attr("src");

                        Element aItem2 = trSecond.get(j).select("a").first();
                        String songName = aItem2.text();
                        String songWithAuthor = aItem2.attr("title");
                        String artistName = songWithAuthor.substring(songWithAuthor.lastIndexOf("- ") + 2);

                        String quality = trSecond.get(j).select("span.gen").first().select("span").text();

                        song.setImageCover(imgUrl);
                        song.setUrl(link);
                        song.setNameSong(songName);
                        song.setAuthor(artistName);
                        song.setQuality(quality);

                        arr.add(song);
                    }
                }*/

            } catch (IOException e) {
                e.printStackTrace();
            }

            return arr;

        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            updateAlbum(songs);
        }
    }


}
