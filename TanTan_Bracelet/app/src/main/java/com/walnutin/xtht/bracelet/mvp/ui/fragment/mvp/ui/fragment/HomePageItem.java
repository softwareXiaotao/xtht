package com.walnutin.xtht.bracelet.mvp.ui.fragment.mvp.ui.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.utils.LogUtils;
import com.walnutin.xtht.bracelet.R;
import com.walnutin.xtht.bracelet.mvp.model.entity.HealthPageData;
import com.walnutin.xtht.bracelet.mvp.ui.adapter.HomePageAdapter;
import com.walnutin.xtht.bracelet.mvp.ui.adapter.HomeViewPagerAdapter;
import com.walnutin.xtht.bracelet.mvp.ui.widget.CustomLinearLayoutManager;
import com.walnutin.xtht.bracelet.mvp.ui.widget.CylinderView;
import com.walnutin.xtht.bracelet.mvp.ui.widget.RecycleViewDivider;
import com.walnutin.xtht.bracelet.mvp.ui.widget.StepArcView;
import com.walnutin.xtht.bracelet.mvp.ui.widget.defineddialog.FullyLinearLayoutManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Leiht on 2017/7/2.
 */

public class HomePageItem {

    private Context mContext;

    private int position;

    View view;

    private TextView currentDateTv;

    public TextView dayTv;

    private ImageView sleepIconIv;

    private StepArcView stepArcView;

    private CylinderView cylinderView;

    private RecyclerView healthRv;

    private List<HealthPageData> healthDatas;

    private HomePageAdapter homePagerAdapter;

    private CustomLinearLayoutManager mLayoutManager;

    private MainFragment mainFragment;

    public HomePageItem(Context context, MainFragment mainFragment) {
        this.mContext = context;
        this.mainFragment = mainFragment;

        this.view = LayoutInflater.from(mContext).inflate(R.layout.fragment_main_item, null);

        currentDateTv = (TextView) view.findViewById(R.id.current_date_tv);

        dayTv = (TextView) view.findViewById(R.id.tv_day);

        dayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        sleepIconIv = (ImageView) view.findViewById(R.id.iv_data);

        stepArcView = (StepArcView) view.findViewById(R.id.arc_view);

        stepArcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepArcView.setVisibility(View.GONE);
                cylinderView.setVisibility(View.VISIBLE);

                mainFragment.setArcOrLinder(false);
            }
        });

        cylinderView = (CylinderView) view.findViewById(R.id.cylinder_view);

        cylinderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cylinderView.setVisibility(View.GONE);
                stepArcView.setVisibility(View.VISIBLE);

                mainFragment.setArcOrLinder(true);
            }
        });

        healthRv = (RecyclerView) view.findViewById(R.id.health_rv);

        healthRv.setHasFixedSize(true);
        healthRv.setNestedScrollingEnabled(false);
    }

    public void update(int position) {

        //更新页面最上端中间显示日期
        String currentDate = "";
        if(position == 1001) {
            currentDate = "今天";
        }else if(position == 1000) {
            currentDate = "昨天";
        }else {
            currentDate = getDistanceDate(position - 1001);
        }
        currentDateTv.setText(currentDate);

        if(mainFragment.isArcOrLinder()) {
            cylinderView.setVisibility(View.GONE);
            stepArcView.setVisibility(View.VISIBLE);
        }else {
            stepArcView.setVisibility(View.GONE);
            cylinderView.setVisibility(View.VISIBLE);
        }

        //更新左上角当前的日
        String tempDate = getDistanceDate(position - 1001);
        tempDate = tempDate.substring(tempDate.length() - 2 ,tempDate.length());
        if(tempDate.startsWith("0")) {
            tempDate = tempDate.substring(1, tempDate.length());
        }
        dayTv.setText(tempDate);

        stepArcView.setCurrentCount(7000, 6000);
        if(healthDatas == null) {
            healthDatas = new ArrayList<>();
        }

        healthDatas.clear();

        //模拟从数据库查询当天对应的数据
        HealthPageData data1 = new HealthPageData(1, "14:25", "心率", "", "62", "78", "", true, 1);
        HealthPageData data2 = new HealthPageData(2, "14:25", "血压", "", "90", "110", "", true, 2);
        HealthPageData data3 = new HealthPageData(3, "14:25", "血氧", "", "90%", "", "", true, 3);
        HealthPageData data4 = new HealthPageData(4, "14:25", "低运动量", "12", "48", "42", "100", false, 4);
        HealthPageData data5 = new HealthPageData(5, "14:25", "散步", "12", "12", "42", "100", false, 5);
        HealthPageData data6 = new HealthPageData(6, "14:25", "跑步", "12", "48", "42", "100", false, 6);
        HealthPageData data7 = new HealthPageData(7, "14:25", "睡眠", "", "5h 42min", "良好", "", false, 7);
        HealthPageData data8 = new HealthPageData(8, "14:25", "摘下", "", "5h42min", "", "", false, 8);

        healthDatas.add(data1);
        healthDatas.add(data2);
        healthDatas.add(data3);
        healthDatas.add(data4);
        healthDatas.add(data5);
        healthDatas.add(data6);
        healthDatas.add(data7);
        healthDatas.add(data8);

        if(homePagerAdapter == null) {
            homePagerAdapter = new HomePageAdapter(mContext, healthDatas);
//            FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(mContext);
//            fullyLinearLayoutManager.setScrollEnabled(false);

            mLayoutManager = new CustomLinearLayoutManager(mContext);

            mLayoutManager.setSmoothScrollbarEnabled(true);
            mLayoutManager.setAutoMeasureEnabled(true);

            mLayoutManager.setScrollEnabled(false);
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            healthRv.setLayoutManager(mLayoutManager);

            healthRv.addItemDecoration(new RecycleViewDivider(
                    mContext, LinearLayoutManager.HORIZONTAL, R.drawable.divider_mileage));

            healthRv.setAdapter(homePagerAdapter);
        }else {
            homePagerAdapter.notifyDataSetChanged();
        }
        healthRv.setNestedScrollingEnabled(false);
    }

    public View getView() {
        return this.view;
    }

    /**
     * 获取前n天日期、后n天日期
     *
     * @param distance 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    private String getDistanceDate(int distance) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distance);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }

}