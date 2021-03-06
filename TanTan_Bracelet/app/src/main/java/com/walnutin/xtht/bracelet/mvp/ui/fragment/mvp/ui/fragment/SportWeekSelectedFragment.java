package com.walnutin.xtht.bracelet.mvp.ui.fragment.mvp.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.LogUtils;
import com.jess.arms.utils.UiUtils;

import com.walnutin.xtht.bracelet.ProductList.db.SqlHelper;
import com.walnutin.xtht.bracelet.ProductList.entity.StepInfos;
import com.walnutin.xtht.bracelet.R;
import com.walnutin.xtht.bracelet.app.MyApplication;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.maputils.Data_run;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.maputils.DbAdapter;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.ui.activity.SportDayPageItem;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.ui.activity.SportWeekPageItem;
import com.walnutin.xtht.bracelet.mvp.ui.fragment.di.component.DaggerSportWeekSelectedComponent;
import com.walnutin.xtht.bracelet.mvp.ui.fragment.di.module.SportWeekSelectedModule;
import com.walnutin.xtht.bracelet.mvp.ui.fragment.mvp.contract.SportWeekSelectedContract;
import com.walnutin.xtht.bracelet.mvp.ui.fragment.mvp.presenter.SportWeekSelectedPresenter;
import com.walnutin.xtht.bracelet.mvp.ui.widget.CanotSlidingViewpager;
import com.walnutin.xtht.bracelet.mvp.ui.widget.HistogramView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class SportWeekSelectedFragment extends BaseFragment<SportWeekSelectedPresenter> implements SportWeekSelectedContract.View {

    private String date;

    @BindView(R.id.sport_week_viewpger)
    public CanotSlidingViewpager viewpager;
    private SportWeekPageItem[] items = new SportWeekPageItem[3];

    private SportWeekAdapter viewPagerAdapter;
    private String currentDate;

    private int currentIndexItem = 1001;
    int position_tag = 1001;


    public static SportWeekSelectedFragment newInstance() {
        SportWeekSelectedFragment fragment = new SportWeekSelectedFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
        DaggerSportWeekSelectedComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .sportWeekSelectedModule(new SportWeekSelectedModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SportWeekPageItem item = null;
        for (int i = 0; i < items.length; i++) {
            item = new SportWeekPageItem(this.getActivity());
            items[i] = item;
        }
        return inflater.inflate(R.layout.fragment_sport_week_selected, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        currentDate = this.date;

        viewpager.setScrollble(false);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                position_tag = position;
                updateUi(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        viewPagerAdapter = new SportWeekAdapter(items);
        viewpager.setAdapter(viewPagerAdapter);
        viewpager.setCurrentItem(1001);

    }

    private void updateUi(int position) {
        try {
            //获取当前显示的HomePageItem
            SportWeekPageItem item = items[position % 3];

            String updateDate = "";
            if (position > currentIndexItem) {
                updateDate = getNextWeekToday(currentDate);
            } else if (position < currentIndexItem) {
                updateDate = getLastWeekToday(currentDate);
            }else if(position == currentIndexItem) {
                updateDate = date;
            }

            item.update(updateDate);

            currentIndexItem = position;
            currentDate = item.getDate();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            if (isSameDate(currentDate, df.format(new Date()))) {
                viewpager.setScrollble(false);
            } else {
                viewpager.setScrollble(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSameDate(String date1, String date2)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try
        {
            d1 = format.parse(date1);
            d2 = format.parse(date2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setFirstDayOfWeek(Calendar.MONDAY);//西方周日为一周的第一天，咱得将周一设为一周第一天
        cal2.setFirstDayOfWeek(Calendar.MONDAY);
        cal1.setTime(d1);
        cal2.setTime(d2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (subYear == 0)// subYear==0,说明是同一年
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11) //subYear==1,说明cal比cal2大一年;java的一月用"0"标识，那么12月用"11"
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        else if (subYear == -1 && cal1.get(Calendar.MONTH) == 11)//subYear==-1,说明cal比cal2小一年
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    private String getLastWeekToday(String da) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = format.parse(da);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        long time = calendar.getTimeInMillis();
        System.out.println("time =" + time);
        long time1 = time - 7 * 24 * 60 * 60 * 1000;
        System.out.println("time1=" + time1);
        calendar.setTimeInMillis(time1);

        return format.format(calendar.getTime());
    }

    private String getNextWeekToday(String da) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = format.parse(da);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        long time = calendar.getTimeInMillis();
        System.out.println("time =" + time);
        long time1 = time + 7 * 24 * 60 * 60 * 1000;
        System.out.println("time1=" + time1);
        calendar.setTimeInMillis(time1);

        return format.format(calendar.getTime());
    }

    public void setDate(String date) {
        this.date = date;
    }

    private boolean isNow(String date) {
        //当前时间
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //获取今天的日期
        String nowDay = sf.format(now);

        LogUtils.debugInfo("nowDay=" + nowDay + ", comDate=" + date);
        LogUtils.debugInfo("day.equals(nowDay)=" + date.equals(nowDay));

        return date.equals(nowDay);

    }

    /**
     * 此方法是让外部调用使fragment做一些操作的,比如说外部的activity想让fragment对象执行一些方法,
     * 建议在有多个需要让外界调用的方法时,统一传Message,通过what字段,来区分不同的方法,在setData
     * 方法中就可以switch做不同的操作,这样就可以用统一的入口方法做不同的事
     * <p>
     * 使用此方法时请注意调用时fragment的生命周期,如果调用此setData方法时onActivityCreated
     * 还没执行,setData里调用presenter的方法时,是会报空的,因为dagger注入是在onActivityCreated
     * 方法中执行的,如果要做一些初始化操作,可以不必让外部调setData,在initData中初始化就可以了
     *
     * @param data
     */

    @Override
    public void setData(Object data) {

    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        UiUtils.SnackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        UiUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    private class SportWeekAdapter<V> extends PagerAdapter {

        private V[] items;

        public SportWeekAdapter(V[] items) {
            super();
            this.items = items;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            if (((ViewPager) container).getChildCount() == items.length) {
                ((ViewPager) container).removeView(((SportWeekPageItem) items[position % items.length]).getView());
            }

            ((ViewPager) container).addView(((SportWeekPageItem) items[position % items.length]).getView(), 0);
            return ((SportWeekPageItem) items[position % items.length]).getView();
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) container);
        }
    }

}