package com.luannguyen.csnmusicstore;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by infin on 26/04/2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private Context mContext;
    private List<Song> arrSong;

    public SongAdapter(Context mContext, List<Song> arrSong) {
        this.mContext = mContext;
        this.arrSong = arrSong;
    }

    public void setAlbumList(List<Song> arrSong) {
        if (arrSong == null)
            return;
        this.arrSong = arrSong;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Song song = arrSong.get(position);
        holder.nameSong.setText(song.getNameSong());
        holder.artist.setText(song.getAuthor());
        holder.quality.setText(song.getQuality());
        if(song.getImageCover().length() > 0){
            Picasso.with(mContext).load(song.getImageCover()).transform(new CircleTransform()).into(holder.thumbnailSong);
        }
        else{
            Picasso.with(mContext).load(R.drawable.background).transform(new CircleTransform()).into(holder.thumbnailSong);
        }
    }

    @Override
    public int getItemCount() {
        return arrSong.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nameSong, artist, quality;
        public ImageView thumbnailSong;


        public MyViewHolder(View itemView) {
            super(itemView);
            nameSong = (TextView) itemView.findViewById(R.id.nameSong);
            artist = (TextView) itemView.findViewById(R.id.artistSong);
            quality = (TextView) itemView.findViewById(R.id.qualitySong);
            thumbnailSong = (ImageView) itemView.findViewById(R.id.thumbnailSong);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION){
                Intent intent = new Intent(mContext, MusicActivity.class);

                intent.putParcelableArrayListExtra("ALBUM_ARRAY", (ArrayList<? extends Parcelable>) arrSong);
                intent.putExtra("POS", pos);

                mContext.startActivity(intent);
            }
        }
    }
}
