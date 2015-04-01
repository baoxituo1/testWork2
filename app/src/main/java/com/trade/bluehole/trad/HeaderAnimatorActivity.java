package com.trade.bluehole.trad;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.trade.bluehole.trad.animator.IO2014HeaderAnimator;
import com.trade.bluehole.trad.entity.User;
import com.trade.bluehole.trad.entity.shop.ShopCommonInfo;
import com.trade.bluehole.trad.util.ImageManager;
import com.trade.bluehole.trad.util.MyApplication;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import de.hdodenhof.circleimageview.CircleImageView;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;

@EActivity(R.layout.activity_header_animator)
public class HeaderAnimatorActivity extends ActionBarActivity {

    @ViewById(R.id.listview)
    ListView listview;
    @ViewById
    TextView shopName;
    @ViewById
    CircleImageView shop_logo_image;
    @App
    MyApplication myApplication;
    /**
     * 登陆信息
     */
    ShopCommonInfo shop;
    User user;

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_animator);
        mListView = (ListView) this.findViewById(R.id.listview);
        initData();
    }*/

    @AfterViews
    void initData(){
        user=myApplication.getUser();
        shop=myApplication.getShop();
        if(shop!=null){
            shopName.setText(shop.getTitle());
            if(null!=shop.getShopLogo()){
                ImageManager.imageLoader.displayImage("http://125.oss-cn-beijing.aliyuncs.com/" + shop.getShopLogo(),shop_logo_image,ImageManager.options);
            }
        }
        IO2014HeaderAnimator animator = new IO2014HeaderAnimator(this);
        StikkyHeaderBuilder.stickTo(listview)
                .setHeader(R.id.header, (ViewGroup) getWindow().getDecorView())
                .minHeightHeaderDim(R.dimen.min_height_header_materiallike)
                .animator(animator)
                .build();
        populateListView();
    }



    private void populateListView() {

        String[] elements = new String[100];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = "row " + i;
        }

        listview.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, elements));
    }


}
