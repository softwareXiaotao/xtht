package com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.ui.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.base.BaseApplication;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.DataHelper;
import com.jess.arms.utils.LogUtils;
import com.jess.arms.utils.PermissionUtil;
import com.jess.arms.utils.UiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.walnutin.xtht.bracelet.R;
import com.walnutin.xtht.bracelet.app.utils.PictureCutUtils;
import com.walnutin.xtht.bracelet.mvp.ui.activity.di.component.DaggerPersonal_dataComponent;
import com.walnutin.xtht.bracelet.mvp.ui.activity.di.module.Personal_dataModule;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.contract.Personal_dataContract;
import com.walnutin.xtht.bracelet.mvp.ui.activity.mvp.presenter.Personal_dataPresenter;
import com.walnutin.xtht.bracelet.mvp.ui.widget.defineddialog.AlertView;
import com.walnutin.xtht.bracelet.mvp.ui.widget.defineddialog.OnItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class Personal_dataActivity extends BaseActivity<Personal_dataPresenter> implements Personal_dataContract.View, OnItemClickListener {


    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
    @BindView(R.id.check_head_photo)
    ImageView check_head_photo;

    AlertView alertView;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.tv_target)
    TextView tvTarget;

    private RxPermissions mRxPermissions;

    private TimePickerView pvCustomTime;
    private OptionsPickerView pvCustomOptions_height,pvCustomOptions_weight;

    private ArrayList<String> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<String> options1Items_weight = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items_weight = new ArrayList<>();
    /**
     * 最终剪切后的结果
     **/
    private static final int CODE_RESULT_REQUEST = 2;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        this.mRxPermissions = new RxPermissions(this);
        DaggerPersonal_dataComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .personal_dataModule(new Personal_dataModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_personal_data; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        toolbarRight.setText(getString(R.string.ok));
        initOptionData();
        initCustomTimePicker();
        initCustomOptionPicker_height();
        initCustomOptionPicker_weight();
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
        finish();
    }

    @OnClick({R.id.check_head_photo, R.id.tv_birthday, R.id.tv_height,R.id.tv_weight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_head_photo:
                checkphoto();
                break;
            case R.id.tv_birthday:
                pvCustomTime.show();
                break;
            case R.id.tv_height:
                pvCustomOptions_height.show();
                break;
            case R.id.tv_weight:
                pvCustomOptions_weight.show();
                break;
        }
    }

    private void checkphoto() {
        alertView = new AlertView(null, null, null, null, new String[]{getString(R.string.local_photo), getString(R.string.camera)}, this, AlertView.Style.ActionSheet, this);
        alertView.show();
    }

    @Override
    public void onItemClick(Object o, int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);//本地相册
                break;
            case 1:

                //请求外部存储权限用于适配android6.0的权限管理机制
                PermissionUtil.launchCamera(new PermissionUtil.RequestPermission() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        selectPicFromCamera();
                    }

                    @Override
                    public void onRequestPermissionFailure() {
                        new AlertDialog.Builder(Personal_dataActivity.this).setTitle(getString(R.string.hint)).setMessage(getString(R.string.camelbypermisstion)).setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent localIntent = new Intent();
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                launchActivity(localIntent);
                            }
                        }).setNegativeButton(getString(R.string.canecl), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                }, mRxPermissions, mPresenter.getmErrorHandler());
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    //剪切图片
                    PictureCutUtils.cropRaWPhoto(Personal_dataActivity.this,
                            data.getData(), CODE_RESULT_REQUEST);
                    break;
                case 1:
                    LogUtils.debugInfo("huilai");
                    if (photoUri == null) {
                        LogUtils.debugInfo("photoUri=null");
                    } else {
                        //剪切图片
                        PictureCutUtils.cropRaWPhoto(this,
                                photoUri, CODE_RESULT_REQUEST);
                    }
                    break;
                case 2:
                    PictureCutUtils.setImageToHeadView(data, DataHelper.getSharedPerference(BaseApplication.getAppContext()), check_head_photo);//设置图片框,并且保存
                    break;

            }

        }
    }

    public Uri photoUri;

    /**
     * 拍照获取图片
     */
    private void selectPicFromCamera() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            /**-----------------*/
            startActivityForResult(intent, 1);
        } else {
            LogUtils.debugInfo("外部存储不可用");
            Toast.makeText(this, "外部存储不可用", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (alertView != null) {
                if (alertView.isShowing()) {
                    alertView.dismiss();
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        selectedDate.set(1992, 0, 1);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1960, 0, 1);
        Calendar endDate = Calendar.getInstance();
        Calendar c = Calendar.getInstance();//
        endDate.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                tvBirthday.setText(getTime(date));
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.WHITE)
                .build();

    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void initCustomOptionPicker_height() {//条件选择器初始化，自定义布局
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        pvCustomOptions_height = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1) + options2Items.get(options1);
                tvHeight.setText(tx);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions_height.returnData();
                                pvCustomOptions_height.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions_height.dismiss();
                            }
                        });

                    }
                })
                .isDialog(true)
                .build();

        pvCustomOptions_height.setPicker(options1Items, options2Items);//添加数据
        pvCustomOptions_height.setSelectOptions(110, 0);

    }
    private void initCustomOptionPicker_weight() {//条件选择器初始化，自定义布局
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        pvCustomOptions_weight = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items_weight.get(options1) + options2Items_weight.get(options1);
                tvHeight.setText(tx);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions_weight.returnData();
                                pvCustomOptions_weight.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions_weight.dismiss();
                            }
                        });

                    }
                })
                .isDialog(true)
                .build();

        pvCustomOptions_weight.setPicker(options1Items_weight, options2Items_weight);//添加数据
        pvCustomOptions_weight.setSelectOptions(15, 0);

    }

    void initOptionData() {

        for (int i = 60; i < 240; i++) {
            options1Items.add(i + "");
        }

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<>();
        options2Items_01.add("cm");
        for (int i = 0; i < options1Items.size(); i++) {
            options2Items.add(options2Items_01);
        }

        for (int i = 30; i < 130; i++) {
            options1Items_weight.add(i + "");
        }

        //选项2
        ArrayList<String> options2Items_02 = new ArrayList<>();
        options2Items_02.add("kg");
        for (int i = 0; i < options1Items_weight.size(); i++) {
            options2Items_weight.add(options2Items_02);
        }

    }


}