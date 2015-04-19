package com.trade.bluehole.trad.activity.msg;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActionBarActivity;
import com.trade.bluehole.trad.adaptor.msg.MessagePageAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 消息查询列表
 */
@EActivity(R.layout.activity_message_pageview)
public class MessagePageviewActivity extends BaseActionBarActivity {

    MessagePageAdapter msgPageAdapter;
    @ViewById(R.id.msg_page_view)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @AfterViews
    void initdata(){
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        msgPageAdapter =new MessagePageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.msg_page_view);
        mViewPager.setAdapter(msgPageAdapter);



        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener
            actionBar.addTab(actionBar.newTab().setText("新闻").setTabListener(tabListener));
            actionBar.addTab(actionBar.newTab().setText("通知").setTabListener(tabListener));
            actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.color.secondary));
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message_pageview, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
