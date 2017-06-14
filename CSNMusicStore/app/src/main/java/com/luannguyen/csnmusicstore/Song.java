package com.luannguyen.csnmusicstore;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by infin on 24/04/2017.
 */

public class Song implements Parcelable {
    private String nameSong;
    private String author;
    private String imageCover;
    private String url;
    private String quality;

    public Song(String nameSong, String author, String imageCover, String url, String quality) {
        this.nameSong = nameSong;
        this.author = author;
        this.imageCover = imageCover;
        this.url = url;
        this.quality = quality;
    }





    public Song(String nameSong, String author, String url, String quality) {
        this.nameSong = nameSong;
        this.author = author;
        this.url = url;
        this.quality = quality;
    }





    public Song(){

    }

    protected Song(Parcel in) {
        nameSong = in.readString();
        author = in.readString();
        imageCover = in.readString();
        url = in.readString();
        quality = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getNameSong() {
        return nameSong;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageCover() {
        return imageCover;
    }

    public void setImageCover(String imageCover) {
        this.imageCover = imageCover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameSong);
        dest.writeString(author);
        dest.writeString(imageCover);
        dest.writeString(url);
        dest.writeString(quality);
    }
}
