package activity.liuxing.tv.com.tvliuxing.activity.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import activity.liuxing.tv.com.tvliuxing.R;
import activity.liuxing.tv.com.tvliuxing.activity.Adapter.DevicesAdapter;
import activity.liuxing.tv.com.tvliuxing.activity.Utils.SocketSingle;
import activity.liuxing.tv.com.tvliuxing.activity.Utils.Utils;
import activity.liuxing.tv.com.tvliuxing.activity.base.BaseActivity;
import activity.liuxing.tv.com.tvliuxing.activity.entity.DataServer;
import activity.liuxing.tv.com.tvliuxing.activity.entity.PmAllData;
import activity.liuxing.tv.com.tvliuxing.activity.entity.PmBean;
import activity.liuxing.tv.com.tvliuxing.activity.entity.UserShareName;
import activity.liuxing.tv.com.tvliuxing.activity.entity.Userdevs;
import activity.liuxing.tv.com.tvliuxing.activity.interfaces.StatisConstans;
import activity.liuxing.tv.com.tvliuxing.activity.request.DataServerRequest;
import activity.liuxing.tv.com.tvliuxing.activity.request.OutdoorPMRequest;
import activity.liuxing.tv.com.tvliuxing.activity.request.UserShareNameRequest;
import activity.liuxing.tv.com.tvliuxing.activity.request.UserdevsBybingedRequest;
import activity.liuxing.tv.com.tvliuxing.activity.socket.Protocal;
import activity.liuxing.tv.com.tvliuxing.activity.socket.ScoketOFFeON;
import activity.liuxing.tv.com.tvliuxing.activity.util.AddressUtils;
import activity.liuxing.tv.com.tvliuxing.activity.util.ThreadPoolUtils;
import activity.liuxing.tv.com.tvliuxing.activity.util.TimeUtils;
import activity.liuxing.tv.com.tvliuxing.activity.view.SmoothScrollListView;
import butterknife.BindView;
import butterknife.BindViews;

public class MainActivity extends BaseActivity {
    public final long SYNC_RATE = 3600 * 1000;// 同步頻率 生产24小时
    @BindViews({R.id.ll_pm_indoor, R.id.ll_co_indoor, R.id.ll_wethar_indoor, R.id.ll_wethar_outdoor})
    List<LinearLayout> linearLayouts;
    @BindViews({R.id.tv_share_name, R.id.tv_city, R.id.tv_area, R.id.tv_sys_time, R.id.tv_outdoor_pm, R.id.tv_indoor_pm, R.id.tv_co, R.id.tv_wethar_indoor, R.id.tv_wethar_outdoor})
    List<TextView> textViews;
    @BindViews({R.id.tv_pm, R.id.tv_co_ier, R.id.tv_indoor, R.id.tv_outdoor})
    List<TextView> buttons;
    @BindView(R.id.image_refeur)
    ImageView imageRefeur;
    @BindView(R.id.image_wifi)
    ImageView imageWifi;
    int time = 2000;    //设置等待时间，单位为毫秒
    @BindView(R.id.lv_classify)
    SmoothScrollListView listView;
    DevicesAdapter adapter;
    int clickPosition = 0;
    Socket socket;
    Protocal protocal;
    ThreadPoolUtils threadPoolUtils;
    DataServer dataServer;
    private int location;
    private String host;
    Userdevs userdevs;
    private Timer mTimer = null;
    String mac = "";

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case StatisConstans.MSG_CYCLIC_TRANSMISSION:
                    threadPoolUtils.execute(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ScoketOFFeON.sendMessage(socket, protocal, mac);
                                Log.i("mac", mac);
                            } catch (Exception e) {
                                handler.sendEmptyMessage(StatisConstans.FAIL_TWO);
                                e.printStackTrace();
                            }
                        }
                    }));
                    break;
                case StatisConstans.FAIL_TWO:
                    if (!TextUtils.isEmpty(host)) {
                        Log.d("ConnectionManager", host);
                        threadPoolUtils.execute(new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (Utils.isNetworkAvailable(MainActivity.this)) {
                                    try {
                                        if (socket != null) {
                                            socket.close();
                                            socket = null;
                                        }
                                        socket = SocketSingle.getInstance(host, location, true);
                                        ScoketOFFeON.receMessage(socket, protocal, handler);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    imageWifi.setImageResource(R.drawable.wifi);
                                }
                            }
                        }));
                    }
                    restoreData();
                    break;
                case StatisConstans.MSG_OUTDOOR_PM:
                    PmBean pmBean = (PmBean) msg.obj;
                    textViews.get(4).setText(pmBean.getPm25());
                    break;
                case StatisConstans.MSG_MODIFY_NAME:
                    String str = (String) msg.obj;
                    if (TextUtils.isEmpty(str) || str.equals("0")) {
                        try {
                            Thread.sleep(6000);
                            getLoaction();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String[] arr = str.split("\\|");
                        textViews.get(1).setText(arr[0]);
                        if (arr.length > 1) {
                            textViews.get(2).setText(arr[1]);
                        }
                        getOutPM();
                    }
                    break;
                case StatisConstans.CONFIG_REGULAR:
                    dataServer = (DataServer) msg.obj;
                    host = dataServer.getTvDataServerConfig().getPrimary_server_address();
                    location = dataServer.getTvDataServerConfig().getPrimary_server_port();
                    threadPoolUtils.execute(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            request(host, location);
                        }
                    }));
                    break;
                case StatisConstans.MSG_IMAGE_SUCCES:
                    dataServer = (DataServer) msg.obj;
                    threadPoolUtils.execute(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            request(dataServer.getTvDataServerConfig().getPrimary_server_address(), dataServer.getTvDataServerConfig().getPrimary_server_port());
                        }
                    }));
                    break;
                case StatisConstans.MSG_RECEIVED_REGULAR:
                    UserShareName user_share_name = (UserShareName) msg.obj;
                    textViews.get(0).setText(user_share_name.getShareName());
                    try {
                        UserdevsBybingedRequest UserdevsBybingedRequest = new UserdevsBybingedRequest(MainActivity.this, sharedPreferencesDB, handler);
                        UserdevsBybingedRequest.requestCode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case StatisConstans.MSG_QUEST_SERVER:
                    PmAllData pmAllData = (PmAllData) msg.obj;
                    getTime();
                    if (pmAllData.getFanFreq() > 9) {
                        if (userdevs.getUserDevList().get(clickPosition).getIson() == 0) {
                            userdevs.getUserDevList().get(clickPosition).setIson(1);
                            adapter.setList(userdevs.getUserDevList());
                        }
                        upData(pmAllData);
                    } else {
                        if (userdevs.getUserDevList().get(clickPosition).getIson() == 1) {
                            userdevs.getUserDevList().get(clickPosition).setIson(0);
                            adapter.setList(userdevs.getUserDevList());
                        }
                        restoreData();
                    }
                    break;
                case StatisConstans.MSG_RECEIVED_CODE:
                    userdevs = (Userdevs) msg.obj;
                    clickPosition = 0;
                    mac = userdevs.getUserDevList().get(0).getDevice_mac();
                    adapter = new DevicesAdapter(MainActivity.this, userdevs.getUserDevList());
                    adapter.setClickPosition(clickPosition);
                    listView.setAdapter(adapter);
                    break;
                case StatisConstans.MSG_RECEIVED_BOUND:
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onMultiClick(View v) {
        switch (v.getId()) {
            case R.id.image_refeur:
                try {
                    UserShareNameRequest userShareNameRequest = new UserShareNameRequest(MainActivity.this, sharedPreferencesDB, handler);
                    userShareNameRequest.requestCode();
                    getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
        Log.d("onResume", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("onResume", "onStop");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setView();
        threadPoolUtils = new ThreadPoolUtils(ThreadPoolUtils.Type.CachedThread, 10);
        //当计时结束时，跳转至主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(sharedPreferencesDB.getString(StatisConstans.TVTOKEN, ""))) {
                    startActivity(new Intent(MainActivity.this, LodingActivity.class));
                    finish();
                } else {
                    startResult();
                }
            }
        }, time);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mac = userdevs.getUserDevList().get(position).getDevice_mac();
                stopTimer();
                startTimer();
                if (position != clickPosition) {
                    clickPosition = position;
                }
                adapter.setClickPosition(clickPosition);
                adapter.notifyDataSetChanged();
            }
        });
        imageRefeur.setOnClickListener(this);
    }

    private void startResult() {
        try {
            UserShareNameRequest userShareNameRequest = new UserShareNameRequest(MainActivity.this, sharedPreferencesDB, handler);
            userShareNameRequest.requestCode();
            getLoaction();
            getTime();
            getDataServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOutPM() {
        if (!TextUtils.isEmpty(textViews.get(1).getText().toString().trim())) {
            try {
                OutdoorPMRequest OutdoorPMRequest = new OutdoorPMRequest(MainActivity.this, sharedPreferencesDB, textViews.get(1).getText().toString().trim().substring(0, textViews.get(1).getText().toString().trim().length() - 1), handler);
                OutdoorPMRequest.requestCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getTime() {
        String time = TimeUtils.getNowTimeString();
        textViews.get(3).setText(time);
    }

    public void getLoaction() {
        threadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                String address = AddressUtils.getProvinceName();
                handler.sendMessage(handler.obtainMessage(StatisConstans.MSG_MODIFY_NAME, address));
            }
        });
    }

    public void getDataServer() {
        try {
            DataServerRequest dataServerRequest = new DataServerRequest(MainActivity.this, sharedPreferencesDB, handler);
            dataServerRequest.requestCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setView() {
        WindowManager wm = this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) ((height / 2) - 30), (int) ((height / 2) - 30));
        params.gravity = Gravity.CENTER;
        linearLayouts.get(0).setLayoutParams(params);
        LinearLayout.LayoutParams paramsCo = new LinearLayout.LayoutParams((int) ((height / 4) - 30), (int) ((height / 4) - 30));
        linearLayouts.get(1).setLayoutParams(paramsCo);
        LinearLayout.LayoutParams paramsIndoor = new LinearLayout.LayoutParams((int) ((height / 4) - 30), (int) ((height / 4) - 30));
        paramsIndoor.setMargins(0, 30, 0, 30);
        linearLayouts.get(2).setLayoutParams(paramsIndoor);
        LinearLayout.LayoutParams paramsOutDoor = new LinearLayout.LayoutParams((int) ((height / 4) - 30), (int) ((height / 4) - 30));
        linearLayouts.get(3).setLayoutParams(paramsOutDoor);
        LinearLayout.LayoutParams paramsPm = new LinearLayout.LayoutParams((int) ((height / 2) * 0.4), ActionBar.LayoutParams.WRAP_CONTENT);
        paramsPm.gravity = Gravity.CENTER_HORIZONTAL;
        buttons.get(0).setLayoutParams(paramsPm);
        LinearLayout.LayoutParams params_text = new LinearLayout.LayoutParams((int) ((height / 4) * 0.4), ActionBar.LayoutParams.WRAP_CONTENT);
        params_text.gravity = Gravity.CENTER_HORIZONTAL;
        buttons.get(1).setLayoutParams(params_text);
        buttons.get(2).setLayoutParams(params_text);
        buttons.get(3).setLayoutParams(params_text);

    }

    public void request(final String host, final int location) {
        try {
            // 1.连接服务器
            socket = SocketSingle.getInstance(host, location, false);
            Log.d("ConnectionManager", "AbsClient*****已经建立连接");
            protocal = Protocal.getInstance();
            threadPoolUtils.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    ScoketOFFeON.receMessage(socket, protocal, handler);
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            stopTimer();
            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimer != null) {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(StatisConstans.MSG_CYCLIC_TRANSMISSION);
                }
            }, 0, 6000);
        }
    }


    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    private void upData(PmAllData pmall) {
        textViews.get(5).setText(pmall.getIndoorPmThickness() + "");
        if (pmall.getIndoorPmThickness() >= 0 && pmall.getIndoorPmThickness() <= 100) {
            linearLayouts.get(0).setBackgroundResource(R.drawable.crcip_bg);
            if (pmall.getIndoorPmThickness() <= 50) {
                buttons.get(0).setText("优");
            } else {
                buttons.get(0).setText("良");
            }
            buttons.get(0).setBackgroundResource(R.drawable.text_bg);
        } else if (pmall.getIndoorPmThickness() > 100 && pmall.getIndoorPmThickness() <= 200) {
            linearLayouts.get(0).setBackgroundResource(R.drawable.crcip_bg_co);
            if (pmall.getIndoorPmThickness() <= 150) {
                buttons.get(0).setText("轻度污染");
            } else {
                buttons.get(0).setText("中度污染");
            }
            buttons.get(0).setBackgroundResource(R.drawable.text_co_bg);
        } else if (pmall.getIndoorPmThickness() > 200) {
            linearLayouts.get(0).setBackgroundResource(R.drawable.crcip_bg_outdoor_wethar);
            if (pmall.getIndoorPmThickness() <= 300) {
                buttons.get(0).setText("重度污染");
            } else {
                buttons.get(0).setText("严重污染");
            }
            buttons.get(0).setBackgroundResource(R.drawable.text_wethar_outdoor_bg);
        }
        textViews.get(6).setText(pmall.getCo2Thickness() + "");
        if (pmall.getCo2Thickness() >= 0 && pmall.getCo2Thickness() <= 1000) {
            linearLayouts.get(1).setBackgroundResource(R.drawable.crcip_bg_indoor_wethar);
            buttons.get(1).setText("新鲜");
            buttons.get(1).setBackgroundResource(R.drawable.text_bg);
        } else if (pmall.getCo2Thickness() > 1000 && pmall.getCo2Thickness() <= 2000) {
            linearLayouts.get(1).setBackgroundResource(R.drawable.crcip_bg_co);
            buttons.get(1).setText("浑浊");
            buttons.get(1).setBackgroundResource(R.drawable.text_co_bg);
        } else if (pmall.getCo2Thickness() > 2000) {
            linearLayouts.get(1).setBackgroundResource(R.drawable.crcip_bg_outdoor_wethar);
            if (pmall.getCo2Thickness() <= 5000) {
                buttons.get(1).setText("缺氧");
                buttons.get(1).setBackgroundResource(R.drawable.text_wethar_outdoor_bg);
            } else {
                buttons.get(1).setText("严重缺氧");
                buttons.get(1).setBackgroundResource(R.drawable.text_wethar_outdoor_bg);
            }
        }
        double sensorIndoorTemp = (double) pmall.getSensorIndoorTemp() / 10;
        double sensorOutdoorTemp = (double) pmall.getSensorOutdoorTemp() / 10;
        textViews.get(7).setText(sensorIndoorTemp + "");
        if (sensorIndoorTemp >= 0 && sensorIndoorTemp <= 4d) {
            linearLayouts.get(2).setBackgroundResource(R.drawable.crcip_bg_outdoor_wethar);
            buttons.get(2).setText("很冷");
            buttons.get(2).setBackgroundResource(R.drawable.text_wethar_outdoor_bg);
        } else if (sensorIndoorTemp > 4d && sensorIndoorTemp <= 18d) {
            linearLayouts.get(2).setBackgroundResource(R.drawable.crcip_bg_co);
            if (sensorIndoorTemp <= 8d) {
                buttons.get(2).setText("冷");
                buttons.get(2).setBackgroundResource(R.drawable.text_co_bg);
            } else {
                buttons.get(2).setText("凉");
                buttons.get(2).setBackgroundResource(R.drawable.text_co_bg);
            }
        } else if (sensorIndoorTemp > 18d && sensorIndoorTemp <= 23d) {
            buttons.get(2).setText("舒适");
            linearLayouts.get(2).setBackgroundResource(R.drawable.crcip_bg);
            buttons.get(2).setBackgroundResource(R.drawable.text_bg);
        } else if (sensorIndoorTemp > 23d && sensorIndoorTemp <= 29d) {
            buttons.get(2).setText("温暖");
            linearLayouts.get(2).setBackgroundResource(R.drawable.crcip_bg);
            buttons.get(2).setBackgroundResource(R.drawable.text_bg);
        } else if (sensorIndoorTemp > 29d && sensorIndoorTemp <= 35d) {
            linearLayouts.get(2).setBackgroundResource(R.drawable.crcip_bg_co);
            buttons.get(2).setText("热");
            buttons.get(2).setBackgroundResource(R.drawable.text_co_bg);
        } else if (sensorIndoorTemp > 35d) {
            linearLayouts.get(2).setBackgroundResource(R.drawable.crcip_bg_outdoor_wethar);
            buttons.get(2).setText("酷热");
            buttons.get(2).setBackgroundResource(R.drawable.text_wethar_outdoor_bg);
        }
        textViews.get(8).setText(sensorOutdoorTemp + "");
        if (sensorOutdoorTemp >= 0 && sensorOutdoorTemp <= 4d) {
            linearLayouts.get(3).setBackgroundResource(R.drawable.crcip_bg_outdoor_wethar);
            buttons.get(3).setText("很冷");
            buttons.get(3).setBackgroundResource(R.drawable.text_wethar_outdoor_bg);
        } else if (sensorOutdoorTemp > 4d && sensorOutdoorTemp <= 18d) {
            linearLayouts.get(3).setBackgroundResource(R.drawable.crcip_bg_co);
            if (sensorOutdoorTemp <= 8d) {
                buttons.get(3).setText("冷");
                buttons.get(3).setBackgroundResource(R.drawable.text_co_bg);
            } else {
                buttons.get(3).setText("凉");
                buttons.get(3).setBackgroundResource(R.drawable.text_co_bg);
            }
        } else if (sensorOutdoorTemp > 18d && sensorOutdoorTemp <= 23d) {
            buttons.get(3).setText("舒适");
            linearLayouts.get(2).setBackgroundResource(R.drawable.crcip_bg);
            buttons.get(3).setBackgroundResource(R.drawable.text_bg);
        } else if (sensorOutdoorTemp > 23d && sensorOutdoorTemp <= 29d) {
            buttons.get(3).setText("温暖");
            linearLayouts.get(3).setBackgroundResource(R.drawable.crcip_bg);
            buttons.get(3).setBackgroundResource(R.drawable.text_bg);
        } else if (sensorOutdoorTemp > 29d && sensorOutdoorTemp <= 35d) {
            linearLayouts.get(3).setBackgroundResource(R.drawable.crcip_bg_co);
            buttons.get(3).setText("热");
            buttons.get(3).setBackgroundResource(R.drawable.text_co_bg);
        } else if (sensorOutdoorTemp > 35d) {
            linearLayouts.get(3).setBackgroundResource(R.drawable.crcip_bg_outdoor_wethar);
            buttons.get(3).setText("酷热");
            buttons.get(3).setBackgroundResource(R.drawable.text_wethar_outdoor_bg);
        }
    }

    public void restoreData() {
        textViews.get(5).setText("--");
        textViews.get(6).setText("--");
        textViews.get(7).setText("--");
        textViews.get(8).setText("--");
        buttons.get(0).setText("--");
        buttons.get(0).setBackgroundResource(R.drawable.text_bg);
        buttons.get(1).setText("--");
        buttons.get(1).setBackgroundResource(R.drawable.text_bg);
        buttons.get(2).setText("--");
        buttons.get(2).setBackgroundResource(R.drawable.text_bg);
        buttons.get(3).setText("--");
        buttons.get(3).setBackgroundResource(R.drawable.text_bg);
        linearLayouts.get(0).setBackgroundResource(R.drawable.crcip_bg);
        linearLayouts.get(1).setBackgroundResource(R.drawable.crcip_bg);
        linearLayouts.get(2).setBackgroundResource(R.drawable.crcip_bg);
        linearLayouts.get(3).setBackgroundResource(R.drawable.crcip_bg);
    }

}
