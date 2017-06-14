package com.luannguyen.csnmusicstore;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MusicActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    private ImageView imgHeader;
    private ImageButton btnPlay;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private ImageView imgSong;
    private TextView txtHeaderName;
    private TextView txtHeaderArtist;
    private SeekBar seekBar;
    private String dataDownload;
    private MediaPlayer player;
    private TextView timeStart;
    private TextView timeEnd;
    private FloatingActionButton fabDownload;

    private String urlStream = "";
    private int totalTime;

    private List<Song> songList;
    private int position;
    private ArrayList<Integer> downloadCode = new ArrayList<>();
    private String[] arrTitleDownload = {"128kbps", "320kbps", "500kbps", "Lossless", "M4A 32kbps"};
    private String[] arrDownloadQuality = {"MP3 128kbps", "MP3 320kbps", "M4A 500kbps", "FLAC Lossless", "M4A 32kbps"};
    private List<String> listUrlDownload;

    private Handler mHandler = new Handler();
    private ProgressDialog progressDialog;
    public static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        songList = new ArrayList<>();

        songList = getIntent().getParcelableArrayListExtra("ALBUM_ARRAY");
        position = getIntent().getIntExtra("POS", 0);

        imgHeader = (ImageView) findViewById(R.id.imgHeader);
        imgSong = (ImageView) findViewById(R.id.imgSong);
        txtHeaderArtist = (TextView) findViewById(R.id.artistSongHeader);
        txtHeaderName = (TextView) findViewById(R.id.nameSongHeader);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPrev = (ImageButton) findViewById(R.id.btnPrev);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        seekBar= (SeekBar) findViewById(R.id.seekbar);
        timeStart = (TextView) findViewById(R.id.txtTimeStart);
        timeEnd = (TextView) findViewById(R.id.txtTimeEnd);
        fabDownload = (FloatingActionButton) findViewById(R.id.fabDownloadMusic);



        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position < songList.size() - 1){
                    mHandler.removeCallbacks(updateTimeTask);
                    position = position + 1;
                    playSong(position);
                }
                else{
                    playSong(position);
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position > 0){
                    mHandler.removeCallbacks(updateTimeTask);
                    position = position -1;
                    playSong(position);
                }
                else{
                    playSong(position);
                }
            }
        });


    }

    private void playSong(int pos){
        txtHeaderName.setText(songList.get(pos).getNameSong());
        txtHeaderArtist.setText(songList.get(pos).getAuthor());


        if(!songList.get(pos).getImageCover().isEmpty()) {
            Picasso.with(this).load(songList.get(pos).getImageCover()).fit().into(imgSong);
            Picasso.with(this).load(songList.get(pos).getImageCover()).transform( new CircleTransform()).into(imgHeader);
        }
        else{
            Picasso.with(this).load(R.drawable.background).fit().into(imgSong);
            Picasso.with(this).load(R.drawable.background).transform( new CircleTransform()).into(imgHeader);
        }

        player = new MediaPlayer();



        try {
            if(!songList.get(pos).getUrl().contains("_download.html")) {
                urlStream = new GetURLStream().execute(songList.get(pos).getUrl()).get();
            }
            else{
                urlStream = songList.get(pos).getUrl();
            }
            dataDownload = new GetMusic().execute(urlStream).get();
            dataDownload = GetSubStringDownload(dataDownload);
            listUrlDownload = getListOfUrlDownload(dataDownload);

            player.reset();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(dataDownload);
            player.prepareAsync();
            player.start();







        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                totalTime = mp.getDuration();
                seekBar.setProgress(player.getCurrentPosition());
                seekBar.setMax(totalTime);
            }
        });


        seekBar.setOnSeekBarChangeListener(this);
        mHandler.removeCallbacks(updateTimeTask);
        mHandler.postDelayed(updateTimeTask, 100);




        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("FAB Download", "Call");
                new MaterialDialog.Builder(MusicActivity.this)
                        .title("Download")
                        .items(arrTitleDownload)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which){
                                    case 0:
                                        new DownloadFile().execute(listUrlDownload.get(0));
                                        break;
                                    case 1:
                                        new DownloadFile().execute(listUrlDownload.get(1));
                                        break;
                                    case 2:
                                        new DownloadFile().execute(listUrlDownload.get(2));
                                        break;
                                    case 3:
                                        new DownloadFile().execute(listUrlDownload.get(3));
                                        break;
                                    case 4:
                                        new DownloadFile().execute(listUrlDownload.get(4));
                                        break;
                                    default:
                                        break;
                                }


                                return true;
                            }
                        }).positiveText("OK").negativeText("Huỷ").show();
            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(player.isPlaying()){
                    player.pause();

                    Picasso.with(getApplicationContext()).load(R.drawable.ic_play_arrow_white_36dp).into(btnPlay);
                }
                else{
                    if(player != null){
                        player.start();
                        timeStart.setText("" + milliSecondsToTimer(player.getCurrentPosition()));
                        timeEnd.setText("" + milliSecondsToTimer(totalTime));
                        Picasso.with(getApplicationContext()).load(R.drawable.ic_pause_white_36dp).into(btnPlay);
                    }
                }

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            mHandler.removeCallbacks(updateTimeTask);
            player = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(player.isPlaying()){
            player.stop();
        }
    }


    private List<String> getListOfUrlDownload(String url){
        List<String> arrDownload = new ArrayList<>();

        if(url.contains("[MP3 128kbps]")
                || url.contains("[MP3 320kbps]") || url.contains("[M4A 500kbps]")
                || url.contains("[FLAC Lossless]") || url.contains("[M4A 32kbps]")
                ){
            int pos = url.indexOf(" [");
            if(pos > 0) {
                String urlNormal = url.substring(0, pos);
                String _128kbps = urlNormal + " [MP3 128kbps].mp3";
                String _320kbps = urlNormal + " [MP3 320kbps].mp3";
                String _500kbps = urlNormal + " [M4A 500kbps].m4a";
                String _Lossless = urlNormal + " [FLAC Lossless].flac";
                String _32kbps = urlNormal + " [M4A 32kbps].m4a";

                _320kbps = _320kbps.replace("/128/", "/320/");
                _500kbps = _500kbps.replace("/128/", "/m4a/");
                _Lossless = _Lossless.replace("/128/", "/flac/");
                _32kbps = _32kbps.replace("/128/", "/32/");

                arrDownload.add(_128kbps);
                arrDownload.add(_320kbps);
                arrDownload.add(_500kbps);
                arrDownload.add(_Lossless);
                arrDownload.add(_32kbps);

                return arrDownload;
            }
        }
        return null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player == null) {
            playSong(position);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case progress_bar_type:
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Đang tải");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                return progressDialog;
            default:
                return null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playSong(position);
    }

    private class DownloadFile extends AsyncTask<String, String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected String doInBackground(String... params) {
            int count;
            try{
                String path = Environment.getExternalStorageDirectory().getPath() + "/Download/";
                File folder = new File(path);
                if(!folder.exists()){
                    folder.mkdirs();
                }


                int posLastCh = params[0].lastIndexOf("/");
                String fileName = params[0].substring(posLastCh + 1);
                fileName = fileName.replace("%20", " ");
                File outputFile = new File(folder, fileName);


                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                InputStream inputStream = new BufferedInputStream(url.openStream());
                OutputStream outputStream = new FileOutputStream(outputFile);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inputStream.read(data)) != -1){
                    total += count;
                    publishProgress(""+(int)((total*100)/lengthOfFile));
                    outputStream.write(data,0,count);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

            }catch (Exception e){
                Log.e("Download Error" , e.getMessage());
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            dismissDialog(progress_bar_type);
            Toast.makeText(getApplicationContext(), "Đã tải về", Toast.LENGTH_SHORT).show();
        }
    }



    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {

            int currentPos = player.getCurrentPosition();
            int maxPos = player.getDuration();
            timeStart.setText("" + milliSecondsToTimer(currentPos));
            timeEnd.setText("" + milliSecondsToTimer(maxPos));
            seekBar.setMax(maxPos);
            seekBar.setProgress(currentPos);
            mHandler.postDelayed(this, 100);

        }
    };




    private String GetSubStringDownload(String s){

        int dataPosFirst = s.indexOf("http://data.chiasenhac.com/downloads/");
        int i = 1;
        while(dataPosFirst < 0){
            String numb = "0" + i;
            if(i > 9){
                numb = "" + i;
            }

            dataPosFirst = s.indexOf("http://data" + numb + ".chiasenhac.com/downloads/" );
            if(dataPosFirst < 0){
                dataPosFirst = s.indexOf("http://data" + i + ".chiasenhac.com/downloads/" );
            }
            i++;
        }

        String subString = s.substring(dataPosFirst);
        int dataPosLast = subString.indexOf(".mp3");
        subString = subString.substring(0, dataPosLast + 4);
        return subString;
    }


    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            seekBar.setProgress(progress);
            player.seekTo(progress);
        }
    }


    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(updateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(player.isPlaying()) {
            mHandler.removeCallbacks(updateTimeTask);

            mHandler.postDelayed(updateTimeTask, 100);
        }
    }


    public class GetURLStream extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String url = "";
            try {
                Document doc = Jsoup.connect(params[0]).get();
                Element item = doc.select("div.pl-c1").first();

                for(Element a : item.select("a")){
                    if(a.attr("title").contains("Click vào đây để download")){
                        url = a.attr("href");
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return url;
        }

        @Override
        protected void onPostExecute(String s) {
            urlStream = s;
        }
    }


    public class GetMusic extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            String urlParse = "";

            try {
                Document doc = Jsoup.connect(params[0]).get();

                Element item = doc.select("div#downloadlink").first();





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
}
