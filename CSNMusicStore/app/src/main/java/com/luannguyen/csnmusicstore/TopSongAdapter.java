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
 * Created by infin on 02/05/2017.
 */

public class TopSongAdapter extends RecyclerView.Adapter<TopSongAdapter.MyViewHolder> {


    private Context mContext;
    private List<Song> arrSong;

    public TopSongAdapter(Context mContext, List<Song> arrSong) {
        this.mContext = mContext;
        this.arrSong = arrSong;
    }

    public void setAlbumList(List<Song> arrSong) {
        this.arrSong = arrSong;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Song song = arrSong.get(position);
        holder.title.setText(song.getNameSong());
        holder.author.setText(song.getAuthor());
        if(song.getImageCover().length() <= 0){
            Picasso.with(mContext).load(R.drawable.background).into(holder.thumbnail);
        }
        else {
            Picasso.with(mContext).load(song.getImageCover()).into(holder.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return arrSong.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, author;
        public ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

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
