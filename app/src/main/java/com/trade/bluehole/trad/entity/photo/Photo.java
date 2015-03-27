package com.trade.bluehole.trad.entity.photo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片。<br/>
 * <br/>
 *
 */
public class Photo extends Bean
{
    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>()
    {
        public Photo createFromParcel(Parcel in)
        {
            return readFromParcel(in, new Photo());
        }

        public Photo[] newArray(int size)
        {
            return new Photo[size];
        }
    };

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || ((Object) this).getClass() != o.getClass())
        {
            return false;
        }

        Photo photo = (Photo) o;

        return imgPath.equals(photo.imgPath);
    }

    @Override
    public int hashCode()
    {
        return imgPath.hashCode();
    }
}
