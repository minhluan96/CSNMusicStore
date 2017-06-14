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
 * Created by infin on 10/04/2017.
 */

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {
    private Context mContext;
    private List<Album> albumList;


    public AlbumsAdapter(Context context, List<Album> albumList){
        this.mContext = context;
        this.albumList = albumList;
    }


    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
        notifyDataSetChanged();
    }

    @Override
    public AlbumsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlbumsAdapter.MyViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.title.setText(album.getName());
        holder.note.setText(album.getAuthor());
        if(album.getThumbnail().length() <= 0){
            Picasso.with(mContext).load(R.drawable.background).into(holder.thumbnail);
        }
        else {
            Picasso.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
        }


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, note;
        public ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            note = (TextView) itemView.findViewById(R.id.author);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION){
                Intent intent = new Intent(mContext, AlbumDetailActivity.class);
                Album album = albumList.get(pos);
                intent.putExtra("ALBUM", album);
                mContext.startActivity(intent);
            }
        }
    }
}
