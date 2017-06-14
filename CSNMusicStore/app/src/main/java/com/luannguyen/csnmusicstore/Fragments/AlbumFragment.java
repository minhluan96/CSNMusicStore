package com.luannguyen.csnmusicstore.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.luannguyen.csnmusicstore.Album;
import com.luannguyen.csnmusicstore.AlbumsAdapter;
import com.luannguyen.csnmusicstore.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by infin on 25/04/2017.
 */

public class AlbumFragment extends Fragment {

    private static List<Album> albumList;
    private RecyclerView recyclerView;
    private static AlbumsAdapter albumsAdapter;
    private TextView txtError;
    private static boolean isTaskRunning = false;
    private static String albumName = "";

    private static Context appContext;

    private GridLayoutManager layoutManager;


    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int visibleThreshold = 5;

    private static String url = "";
    private String pageIndicate = "&page=";
    private int pageNumber = 1;
    private String startIndicate = "&start=";
    private int count = 80;


    public AlbumFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.album_searchfragment_layout, container, false);

        txtError = (TextView) rootView.findViewById(R.id.txtErrorAlbum);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_album_search);
        albumList = new ArrayList<>();

        appContext = getContext();

        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        albumsAdapter = new AlbumsAdapter(getContext(), albumList);
        recyclerView.setAdapter(albumsAdapter);

            if(url.length() > 0 && albumName.length() > 0) {

                url += pageIndicate + pageNumber;

                new GetAlbum().execute(url);



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
                    count+=80;
                    int countLetters = (count + "").length();
                    url = url.substring(0, url.length() - countLetters - startIndicate.length());
                    url += pageNumber + startIndicate + count;


                    new GetAlbum().execute(url);
                }
            }
        });
    }


    public static void  configData(List<Album> responseData) {
        albumsAdapter.setAlbumList(responseData);
        Toast.makeText(appContext, "Task done!", Toast.LENGTH_SHORT).show();
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }


    public class GetAlbum extends AsyncTask<String, Void, List<Album>> {
        private List<Album> arrAlbum = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Album> doInBackground(String... params) {


            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            try {
                Document doc = Jsoup.connect(params[0]).get();
                Element item = doc.select("table.tbtable").first();
                Element subItem = item.select("tbody").first();




                for (Element childItem : subItem.select("tr")) {
                   for(Element tdItem : childItem.select("td")){
                        String title = tdItem.attr("title");
                        Elements img = tdItem.select("img");
                        Elements a = tdItem.select("a");
                        String url = a.attr("href");
                        String imgThumbnail = img.attr("src");

                        int pos = title.indexOf(" ~ ") + 3;
                        int pos2 = title.length() -1;
                        if(title.contains("2.")){
                            pos2 = title.indexOf("\n");
                        }

                        String author = title.substring(pos,pos2);
                        String name = configStrAlbumName(albumName);

                        Album album = new Album(name,author,imgThumbnail,url, null);
                        arrAlbum.add(album);

                   }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return arrAlbum;
        }


        @Override
        protected void onPostExecute(List<Album> a) {
            configData(a);

        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String getAuthor(String str){
        int pos = str.indexOf("~ ");
        String subStr = str.substring(pos + 1);
        if(subStr.contains("2.")){
            int pos2nd = subStr.indexOf("\n");
            subStr = subStr.substring(0, pos2nd);
        }
        // delete empty space
        return subStr;
    }

    private String configStrAlbumName(String str){
        return str.substring(0,1).toUpperCase() + str.substring(1);
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
