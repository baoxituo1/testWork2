package com.trade.bluehole.trad.activity.login;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.activeandroid.query.Select;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.trade.bluehole.trad.R;
import com.trade.bluehole.trad.activity.BaseActivity;
import com.trade.bluehole.trad.util.model.FirstVisitModel;
import com.trade.bluehole.trad.util.update.UpdateManager;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.util.Random;
@EActivity
public class UpdateVersionActivity extends BaseActivity implements Animator.AnimatorListener{

    FrameLayout mContainer;
    ImageView mView;
    int mIndex=0;
    Random mRandom=new Random();
    int ANIM_COUNT=3;
    Integer []PHOTO=new Integer[]{R.drawable.index_show1,R.drawable.index_show2,R.drawable.super_luncher_index};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_update_version);
        //查询首次访问数据库是否有记录
        FirstVisitModel first= new Select().from(FirstVisitModel.class).executeSingle();
        //如果不存在，插入一条记录 停在此页面
        if(null==first){
            mContainer=new FrameLayout(this);
            mContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            mView=createNewView();
            mContainer.addView(mView);
            nextAnimation();
            setContentView(mContainer);
            //检查更新
            doUpdate();
        }else{
            setContentView(R.layout.activity_update_version);
            // 检查软件更新
            UpdateManager manager = new UpdateManager(this);
            manager.checkUpdate(1);
        }

    }

    @UiThread(delay = 6000)
    void doUpdate(){
        // 检查软件更新
        UpdateManager manager = new UpdateManager(this);
        manager.checkUpdate(1);
    }

    private ImageView createNewView(){
        ImageView ret=new ImageView(this);
        ret.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ret.setScaleType(ImageView.ScaleType.FIT_XY);
        ret.setImageResource(PHOTO[mIndex]);
        mIndex=(mIndex+1<PHOTO.length)?mIndex+1:0;
        return  ret;
    }


    private void nextAnimation(){
        AnimatorSet anim=new AnimatorSet();
        final int index=mRandom.nextInt(ANIM_COUNT);
        switch (index){
            case 0:
                anim.playTogether(
                        ObjectAnimator.ofFloat(mView,"scaleX",1.5f,1f),
                        ObjectAnimator.ofFloat(mView,"scaleY",1.5f,1f)
                );
                break;
            case 1:
                anim.playTogether(
                        ObjectAnimator.ofFloat(mView,"scaleX",1f,1.5f),
                        ObjectAnimator.ofFloat(mView,"scaleY",1f,1.5f)
                );
                break;
            default:
                AnimatorProxy.wrap(mView).setScaleX(1.5f);
                AnimatorProxy.wrap(mView).setScaleY(1.5f);
                anim.playTogether(ObjectAnimator.ofFloat(mView,"translationX",0f,40f));
                break;
        }
        anim.setDuration(3000);
        anim.addListener(this);
        anim.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_version, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mContainer.removeView(mView);
        mView=createNewView();
        mContainer.addView(mView);
        nextAnimation();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
