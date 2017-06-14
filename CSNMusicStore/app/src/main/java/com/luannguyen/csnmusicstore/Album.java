package com.luannguyen.csnmusicstore;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by infin on 10/04/2017.
 */

public class Album implements Parcelable {
    private String name;
    private String author;
    private String thumbnail;
    private String url;
    private List<Song> arrSong;

    public Album(String name, String author, String thumbnail, String url, List<Song> arrSong) {
        this.name = name;
        this.author = author;
        this.thumbnail = thumbnail;
        this.url = url;
        this.arrSong = arrSong;
    }

    public Album(){}


    public Album(Parcel in) {
        name = in.readString();
        author = in.readString();
        thumbnail = in.readString();
        url = in.readString();
        in.readList(arrSong,List.class.getClassLoader());
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public List<Song> getArrSong(){ return arrSong; }

    public void setArrSong(List<Song> arrSong){ this.arrSong = arrSong; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(thumbnail);
        dest.writeString(url);
        dest.writeList(arrSong);
    }
}
