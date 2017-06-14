package com.luannguyen.csnmusicstore;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.fabtransitionactivity.SheetLayout;
import com.luannguyen.csnmusicstore.Fragments.USFragment;
import com.luannguyen.csnmusicstore.Fragments.VNFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends BaseActivity {

    private String URL = "http://chiasenhac.vn/mp3/vietnam/v-rap-hiphop/tu-bo~khac-viet~tsvqz77bqe1ttq_download.html";
    private String dataDownload;

    private ViewPager viewpager;
    private TabLayout tabLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.content_main, null, false);
        drawer.addView(contentView, 0);

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.viewpagerTab);


        SetupViewPager(viewpager);
        tabLayout.setupWithViewPager(viewpager);



        /*try {
            dataDownload = new GetMusic().execute(URL).get();
            dataDownload = GetSubStringDownload(dataDownload);

            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(dataDownload);
            player.prepare();
            player.start();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/



    }

    private void SetupViewPager(ViewPager view){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new VNFragment(), "Việt Nam");
        adapter.addFragment(new USFragment(), "USUK");
        view.setAdapter(adapter);
    }





    public class GetMusic extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            String urlParse = "";

            try {
                Document doc = Jsoup.connect(params[0]).get();

                Element item = doc.select("div#downloadlink").first();
                Log.e("URL ", item.toString());


                Elements scripts = item.getElementsByTag("script");
                for(Element element : scripts){

                    for(DataNode node : element.dataNodes()){

                        urlParse = node.getWholeData().toString();
                        Log.e("Test", node.getWholeData());
                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
            }


            return urlParse;
        }

        @Override
        protected void onPostExecute(String s) {
            dataDownload = s;

        }
    }


    private String GetSubStringDownload(String s){

        int dataPosFirst = s.indexOf("http://data.chiasenhac.com/downloads/");
        String subString = s.substring(dataPosFirst);
        int dataPosLast = subString.indexOf(".mp3");
        subString = subString.substring(0, dataPosLast + 4);
        return subString;
    }








}
