package com.trade.bluehole.trad.entity.photo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * JavaBean的基类。<br/>
 * <br/>
 *
 */
public class Bean implements Parcelable
{
    public String id;
    public String text;
    public String imgPath;
    public String fileName;
    public String dataType;

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(imgPath);
        dest.writeString(fileName);
        dest.writeString(dataType);
    }

    public static <T extends Bean> T readFromParcel(Parcel in, T t)
    {
        if (t == null)
        {
            return null;
        }

        if (in == null)
        {
            return null;
        }

        t.id = in.readString();
        t.text = in.readString();
        t.imgPath = in.readString();
        t.fileName = in.readString();
        t.dataType = in.readString();
        return t;
    }

    public static final Creator<Bean> CREATOR
            = new Creator<Bean>()
    {
        public Bean createFromParcel(Parcel in)
        {
            return readFromParcel(in, new Bean());
        }

        public Bean[] newArray(int size)
        {
            return new Bean[size];
        }
    };
}
