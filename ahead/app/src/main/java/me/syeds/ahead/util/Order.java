package me.syeds.ahead.util;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
/**
 * Created by syedhaider on 9/20/15.
 */
public class Order implements Parcelable{

    private static final int EMPTY_VALUE = 0;

    private Integer number;
    private String description;
    private Date date;
    private double price;
    private Drawable image;

    public Order() {}

    public Order(Integer number, String description, Date date, double price, Drawable image) {
        this.number = number;
        this.description = description;
        this.date = date;
        this.price = price;
        this.image = image;
    }

    public Order(Parcel src) {
        number = src.readInt();
        description = src.readString();
        long dateTime = src.readLong();
        if (dateTime != EMPTY_VALUE) {
            date = new Date(dateTime);
        }
        price = src.readDouble();
        int imageValue = src.readInt();
        if (imageValue != EMPTY_VALUE) {
            Bitmap bitmap = src.readParcelable(getClass().getClassLoader());
            image = new BitmapDrawable(bitmap);
        }
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Order) {
            return this.number == ((Order) obj).number;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeString(description);
        if (date != null) {
            dest.writeLong(date.getTime());
        } else {
            dest.writeLong(EMPTY_VALUE);
        }
        dest.writeDouble(price);
        if (image != null) {
            dest.writeInt(1);
            Bitmap bitmap = ((BitmapDrawable)image).getBitmap();
            dest.writeParcelable(bitmap, flags);
        } else {
            dest.writeInt(EMPTY_VALUE);
        }
    }
}

