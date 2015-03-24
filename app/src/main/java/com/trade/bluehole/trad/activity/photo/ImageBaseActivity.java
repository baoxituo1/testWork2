package com.trade.bluehole.trad.activity.photo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.trade.bluehole.trad.entity.photo.Photo;

import java.util.ArrayList;



/**
 * 图片选择Activity的基类。<br/>
 * <br/>
 *
 */
public class ImageBaseActivity extends ActionBarActivity
{
    protected static ArrayList<Photo> checkList = new ArrayList<Photo>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
