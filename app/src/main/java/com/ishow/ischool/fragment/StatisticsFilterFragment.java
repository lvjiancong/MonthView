package com.ishow.ischool.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonlib.util.DateUtil;
import com.ishow.ischool.R;
import com.ishow.ischool.application.Cons;
import com.ishow.ischool.bean.university.UniversityInfo;
import com.ishow.ischool.bean.user.Campus;
import com.ishow.ischool.bean.user.User;
import com.ishow.ischool.business.universitypick.UniversityPickActivity;
import com.ishow.ischool.business.user.pick.UserPickActivity;
import com.ishow.ischool.common.api.MarketApi;
import com.ishow.ischool.common.manager.CampusManager;
import com.ishow.ischool.common.manager.UserManager;
import com.ishow.ischool.util.AppUtil;
import com.ishow.ischool.widget.custom.InputLinearLayout;
import com.ishow.ischool.widget.pickerview.PickerDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wqf on 16/8/22.
 */
public class StatisticsFilterFragment extends DialogFragment implements InputLinearLayout.EidttextClick, View.OnTouchListener {
    @BindView(R.id.item_campus)
    InputLinearLayout campusIL;
    @BindView(R.id.time_type)
    TextView timeTypeTv;
    @BindView(R.id.start_time)
    EditText startTimeEt;
    @BindView(R.id.start_time_clear)
    ImageView startTimeIv;
    @BindView(R.id.end_time)
    EditText endTimeEt;
    @BindView(R.id.end_time_clear)
    ImageView endTimeIv;
    @BindView(R.id.item_pay_state)
    InputLinearLayout payStateIL;
    @BindView(R.id.item_source)
    InputLinearLayout sourceIL;
    @BindView(R.id.item_university)
    InputLinearLayout universityIL;
    @BindView(R.id.item_referrer)
    InputLinearLayout referrerIL;

    private Dialog dialog;

    private final int TYPE_START_TIME = 1;
    private final int TYPE_END_TIME = 2;

    private Boolean startTimeFlag = true;
    private GestureDetector mGestureDetector;
    private SimpleDateFormat sdf;

    private boolean isUserCampus;       // 是否是校区员工（非总部员工）
    private String mFilterCampusId;
    private String mFilterTimeType;
    private String mFilterStartTime;
    private String mFilterEndTime;
    private String mFilterPayState;
    private String mFilterSource;
    private String mFilterSourceName;
    private String mFilterUniversityId;
    private String mFilterProvinceId;
    private String mFilterReferrerId;
    private String mFilterCollegeName;
    private String mFilterReferrerName;

    private FilterCallback callback;
    private ArrayList<String> sources;
    private UniversityInfo mUniversityInfo;
    private User mUser;
    private int mPositionId;      // 当前职位


    public static StatisticsFilterFragment newInstance(Map<String, String> params, String source_name, String college_name, String referrer_name) {
        StatisticsFilterFragment fragment = new StatisticsFilterFragment();
        Bundle args = new Bundle();
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            args.putString(entry.getKey().toString(), entry.getValue().toString());
        }
        args.putString("source_name", source_name);
        args.putString("college_name", college_name);
        args.putString("referrer_name", referrer_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mFilterCampusId = bundle.getString("campus_id", "");
            mFilterSource = bundle.getString("source", "");
            mFilterTimeType = bundle.getString("time_type", "");
            mFilterStartTime = bundle.getString("start_time", "");
            mFilterEndTime = bundle.getString("end_time", "");
            mFilterPayState = bundle.getString("pay_state", "");
            mFilterUniversityId = bundle.getString("college_id", "");
            mFilterProvinceId = bundle.getString("province_id", "");
            mFilterReferrerId = bundle.getString("referrer", "");
            mFilterSourceName = bundle.getString("source_name", "");
            mFilterCollegeName = bundle.getString("college_name", "");
            mFilterReferrerName = bundle.getString("referrer_name", "");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getContext(), R.style.Comm_dialogfragment_windowAnimationStyle);
        Window win = dialog.getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.TOP;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.filter_layout, null);
        //  View viewById = contentView.findViewById(R.id.root);
        // viewById.setTop(UIUtil.getToolbarSize(getContext()));
        ButterKnife.bind(this, contentView);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(contentView);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        window.setLayout(-1, -1);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        initFilter();
    }

    void initFilter() {
        mUser = UserManager.getInstance().get();
        mPositionId = mUser.positionInfo.id;
        isUserCampus = (mUser.userInfo.campus_id == Campus.HEADQUARTERS) ? false : true;
        if (!isUserCampus) {        // 总部才显示“所属校区”筛选条件
            campusIL.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(mFilterCampusId) || mFilterCampusId.equals(Campus.HEADQUARTERS + "")) {
                campusIL.setContent("所有校区");
            } else {
                campusIL.setContent(CampusManager.getInstance().getCampusNameById(Integer.parseInt(mFilterCampusId)));
            }
        }
        if (TextUtils.isEmpty(mFilterTimeType) || mFilterTimeType.equals("1")) {
            mFilterTimeType = "1";
            timeTypeTv.setText(getString(R.string.item_register_time));
        } else {
            timeTypeTv.setText(getString(R.string.item_matriculation_time));
        }
        if (!TextUtils.isEmpty(mFilterStartTime)) {
            startTimeEt.setText(sdf.format(new Date(Long.parseLong(mFilterStartTime) * 1000)));
            startTimeIv.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(mFilterEndTime)) {
            endTimeEt.setText(sdf.format(new Date(Long.parseLong(mFilterEndTime) * 1000)));
            endTimeIv.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(mFilterPayState)) {
            payStateIL.setContent(AppUtil.getPayState().get(Integer.parseInt(mFilterPayState) - 1));
        }

        if (mPositionId == Cons.Position.Chendujiangshi.ordinal()) {
            mFilterSource = MarketApi.TYPESOURCE_READING + "";
        } else if (mPositionId == Cons.Position.Xiaoliaozhuanyuan.ordinal()) {
            mFilterSource = MarketApi.TYPESOURCE_CHAT + "";
        } else if (mPositionId == Cons.Position.Xiaoyuanjingli.ordinal() || mPositionId == Cons.Position.Shichangzhuguan.ordinal()) {

            sources = new ArrayList<String>() {{
                add("晨读");
                add("转介绍");
                add("全部来源");
            }};
            sourceIL.setVisibility(View.VISIBLE);
        } else if (mPositionId == Cons.Position.Xiaoliaozhuguan.ordinal()) {

            sources = new ArrayList<String>() {{
                add("校聊");
                add("转介绍");
                add("全部来源");
            }};
            sourceIL.setVisibility(View.VISIBLE);
        } else {
            sources = new ArrayList<String>() {{
                add("晨读");
                add("校聊");
                add("转介绍");
                add("全部来源");
            }};
            sourceIL.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(mFilterSourceName)) {
            sourceIL.setContent(mFilterSourceName);
        }
        if (!TextUtils.isEmpty(mFilterUniversityId)) {
            if (!TextUtils.isEmpty(mFilterCollegeName)) {
                universityIL.setContent(mFilterCollegeName);
            } else {
                mFilterUniversityId = "";
            }
        }
        if (!TextUtils.isEmpty(mFilterReferrerId)) {
            if (!TextUtils.isEmpty(mFilterReferrerName)) {
                referrerIL.setContent(mFilterReferrerName);
            } else {
                mFilterReferrerId = "";
            }
        }
        campusIL.setOnEidttextClick(this);
        payStateIL.setOnEidttextClick(this);
        sourceIL.setOnEidttextClick(this);
        universityIL.setOnEidttextClick(this);
        referrerIL.setOnEidttextClick(this);
        mGestureDetector = new GestureDetector(new Gesturelistener());
        startTimeEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                startTimeFlag = true;
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
        endTimeEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                startTimeFlag = false;
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    @OnClick({R.id.commun_block_top, R.id.commun_block_bottom, R.id.time_type, R.id.start_time_clear, R.id.end_time_clear, R.id.filter_reset, R.id.filter_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_type:
                PickerDialogFragment.Builder timeBuilder = new PickerDialogFragment.Builder();
                timeBuilder.setBackgroundDark(true)
                        .setDialogTitle(-1)
                        .setDialogType(PickerDialogFragment.PICK_TYPE_OTHERS)
                        .setDatas(0, 1, AppUtil.getFilterTimeType());
                PickerDialogFragment timeFragment = timeBuilder.Build();
                timeFragment.show(getChildFragmentManager(), "dialog");
                timeFragment.addMultilinkPickCallback(new PickerDialogFragment.MultilinkPickCallback() {
                    @Override
                    public ArrayList<String> endSelect(int colum, int selectPosition, String text) {
                        return null;
                    }

                    @Override
                    public void onPickResult(Object object, String... result) {
                        timeTypeTv.setText(result[0]);
                        mFilterTimeType = (AppUtil.getFilterTimeType().indexOf(result[0]) + 1) + "";
                    }
                });
                break;
            case R.id.start_time_clear:
                startTimeEt.setText("");
                startTimeIv.setVisibility(View.GONE);
                break;
            case R.id.end_time_clear:
                endTimeEt.setText("");
                endTimeIv.setVisibility(View.GONE);
                break;
            case R.id.filter_reset:
                resetFilter();
                break;
            case R.id.filter_ok:
                this.dismiss();
                if (callback != null) {
                    HashMap<String, String> params = new HashMap<>();
                    if (!TextUtils.isEmpty(mFilterCampusId)) {
                        params.put("campus_id", mFilterCampusId);
                    }
                    if (!TextUtils.isEmpty(mFilterSource)) {
                        params.put("source", mFilterSource);
                    }
                    if (!TextUtils.isEmpty(mFilterStartTime) || !TextUtils.isEmpty(mFilterEndTime)) {
                        params.put("time_type", mFilterTimeType);
                        if (!TextUtils.isEmpty(mFilterStartTime)) {
                            params.put("start_time", mFilterStartTime);
                        }
                        if (!TextUtils.isEmpty(mFilterEndTime)) {
                            params.put("end_time", mFilterEndTime);
                        }
                    }
                    if (!TextUtils.isEmpty(mFilterPayState)) {
                        params.put("pay_state", mFilterPayState);
                    }
                    if (!TextUtils.isEmpty(mFilterUniversityId)) {
                        params.put("college_id", mFilterUniversityId);
                        params.put("province_id", mFilterProvinceId);
                    }
                    if (!TextUtils.isEmpty(mFilterReferrerId)) {
                        params.put("referrer", mFilterReferrerId);
                    }
                    callback.onFinishFilter(params, mFilterSourceName, mFilterCollegeName, mFilterReferrerName);
                }
                break;
            case R.id.commun_block_top:
            case R.id.commun_block_bottom:
                this.dismiss();
                if (callback != null)
                    callback.onCancelDilaog();
                break;
        }
    }

    void resetFilter() {
        campusIL.setContent("");
        startTimeEt.setText("");
        startTimeIv.setVisibility(View.GONE);
        endTimeEt.setText("");
        endTimeIv.setVisibility(View.GONE);
        payStateIL.setContent("");
        sourceIL.setContent("");
        universityIL.setContent("");
        referrerIL.setContent("");
        if (isUserCampus) {
            mFilterCampusId = mUser.userInfo.campus_id + "";
        } else {
            mFilterCampusId = Campus.HEADQUARTERS + "";       // 总部获取学院统计列表campus_id传1
        }
        mFilterStartTime = "";
        mFilterEndTime = "";
        mFilterPayState = "";
        mFilterSource = "";
        if (TextUtils.isEmpty(mFilterSource)) {
            if (mPositionId == Cons.Position.Chendujiangshi.ordinal()) {
                mFilterSource = MarketApi.TYPESOURCE_READING + "";
            } else if (mPositionId == Cons.Position.Xiaoliaozhuanyuan.ordinal()) {
                mFilterSource = MarketApi.TYPESOURCE_CHAT + "";
            } else {
                mFilterSource = "-1";
            }
        }
        mFilterUniversityId = "";
        mFilterProvinceId = "";
        mFilterReferrerId = "";
        mFilterSourceName = "";
        mFilterCollegeName = "";
        mFilterReferrerName = "";
    }

    @Override
    public void onEdittextClick(View view) {
        switch (view.getId()) {
            case R.id.item_campus:
                final ArrayList<String> campusList = CampusManager.getInstance().getCampusNames();
                PickerDialogFragment.Builder campusBuilder = new PickerDialogFragment.Builder();
                campusBuilder.setBackgroundDark(true)
                        .setDialogTitle(R.string.item_campus)
                        .setDialogType(PickerDialogFragment.PICK_TYPE_OTHERS)
                        .setDatas(0, 1, campusList);
                PickerDialogFragment campusFragment = campusBuilder.Build();
                campusFragment.show(getChildFragmentManager(), "dialog");
                campusFragment.addMultilinkPickCallback(new PickerDialogFragment.MultilinkPickCallback() {
                    @Override
                    public ArrayList<String> endSelect(int colum, int selectPosition, String text) {
                        return null;
                    }

                    @Override
                    public void onPickResult(Object object, String... result) {
                        campusIL.setContent(result[0]);
                        int index = campusList.indexOf(result[0]);
                        mFilterCampusId = (CampusManager.getInstance().get().get(index).id + "");
                    }
                });
                break;
            case R.id.item_pay_state:
                PickerDialogFragment.Builder payBuilder = new PickerDialogFragment.Builder();
                payBuilder.setBackgroundDark(true)
                        .setDialogTitle(R.string.item_pay_state)
                        .setDialogType(PickerDialogFragment.PICK_TYPE_OTHERS)
                        .setDatas(0, 1, AppUtil.getPayState());
                PickerDialogFragment payFragment = payBuilder.Build();
                payFragment.show(getChildFragmentManager(), "dialog");
                payFragment.addMultilinkPickCallback(new PickerDialogFragment.MultilinkPickCallback() {
                    @Override
                    public ArrayList<String> endSelect(int colum, int selectPosition, String text) {
                        return null;
                    }

                    @Override
                    public void onPickResult(Object object, String... result) {
                        payStateIL.setContent(result[0]);
                        mFilterPayState = (AppUtil.getPayState().indexOf(result[0]) + 1) + "";
                    }
                });
                break;
            case R.id.item_source:
                PickerDialogFragment.Builder fromBuilder = new PickerDialogFragment.Builder();
                fromBuilder.setBackgroundDark(true)
                        .setDialogTitle(R.string.item_from)
                        .setDialogType(PickerDialogFragment.PICK_TYPE_OTHERS)
                        .setDatas(0, 1, sources);

                PickerDialogFragment fromFragment = fromBuilder.Build();
                fromFragment.show(getChildFragmentManager(), "dialog");
                fromFragment.addMultilinkPickCallback(new PickerDialogFragment.MultilinkPickCallback() {
                    @Override
                    public ArrayList<String> endSelect(int colum, int selectPosition, String text) {
                        return null;
                    }

                    @Override
                    public void onPickResult(Object object, String... result) {
                        mFilterSourceName = result[0];
                        sourceIL.setContent(mFilterSourceName);
                        switch (mFilterSourceName) {
                            case "晨读":
                                mFilterSource = MarketApi.TYPESOURCE_READING + "";
                                break;
                            case "转介绍":
                                mFilterSource = MarketApi.TYPESOURCE_RECOMMEND + "";
                                break;
                            case "校聊":
                                mFilterSource = MarketApi.TYPESOURCE_CHAT + "";
                                break;
                            case "全部来源":
                                mFilterSource = MarketApi.TYPESOURCE_ALL + "";
                                break;
                        }

                    }
                });
                break;
            case R.id.item_university:
                startActivityForResult(new Intent(getActivity(), UniversityPickActivity.class), UniversityPickActivity.REQUEST_CODE_PICK_UNIVERSITY);
                break;
            case R.id.item_referrer:
                Intent intent = new Intent(getActivity(), UserPickActivity.class);
                intent.putExtra(UserPickActivity.P_TITLE,getString(R.string.pick_referrer));
                startActivityForResult(intent, UserPickActivity.REQUEST_CODE_PICK_USER);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
        switch (requestCode) {
            case UniversityPickActivity.REQUEST_CODE_PICK_UNIVERSITY:
                if (data != null) {
                    mUniversityInfo = data.getParcelableExtra(UniversityPickActivity.KEY_PICKED_UNIVERSITY);
                    mFilterCollegeName = mUniversityInfo.name;
                    universityIL.setContent(mFilterCollegeName);
                    mFilterUniversityId = mUniversityInfo.id + "";
                    mFilterProvinceId = mUniversityInfo.prov_id + "";
                    //                    city_id = mUniversityInfo.city_id;
                }
                break;
            case UserPickActivity.REQUEST_CODE_PICK_USER:
                if (data != null) {
                    User user = data.getParcelableExtra(UserPickActivity.PICK_USER);
                    mFilterReferrerName = user.userInfo.user_name;
                    referrerIL.setContent(mFilterReferrerName);
                    mFilterReferrerId = user.userInfo.user_id + "";
                }
                break;
        }
//        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    private class Gesturelistener implements GestureDetector.OnGestureListener {

        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            return false;
        }

        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub
        }

        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
            if (startTimeFlag) {
                showTimePickPop(TYPE_START_TIME);
            } else {
                showTimePickPop(TYPE_END_TIME);
            }
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            return false;
        }

        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // TODO Auto-generated method stub
            return false;
        }
    }

    private void showTimePickPop(final int type) {
        AppUtil.showTimePickerDialog(getChildFragmentManager(), (type == TYPE_START_TIME ? R.string.item_start_time : R.string.item_end_time),
                new PickerDialogFragment.Callback<Integer>() {
                    @Override
                    public void onPickResult(Integer unix, String... result) {
                        if (type == TYPE_START_TIME) {
                            if (!TextUtils.isEmpty(mFilterEndTime) && unix > Integer.parseInt(mFilterEndTime)) {
                                showTimeError();
                                mFilterStartTime = "";
                                startTimeEt.setText("");
                                startTimeIv.setVisibility(View.GONE);
                            } else {
                                startTimeEt.setText(result[0]);
                                mFilterStartTime = String.valueOf(unix);
                                startTimeIv.setVisibility(View.VISIBLE);
                            }
                        } else if (type == TYPE_END_TIME) {
                            long end4Today = DateUtil.getEndTime(new Date((long)unix * 1000)) / 1000;      // 获取当日23:59:59的timestamp
                            mFilterEndTime = String.valueOf(end4Today);
                            if (!TextUtils.isEmpty(mFilterStartTime) && unix < Integer.parseInt(mFilterStartTime)) {
                                showTimeError();
                                mFilterEndTime = "";
                                endTimeEt.setText("");
                                endTimeIv.setVisibility(View.GONE);
                            } else {
                                endTimeEt.setText(result[0]);
                                endTimeIv.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    void showTimeError() {
        final Snackbar snackbar = Snackbar.make(startTimeEt, getString(R.string.time_error), Snackbar.LENGTH_LONG);
        snackbar.setAction("朕知道了", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public interface FilterCallback {
        void onFinishFilter(HashMap<String, String> map, String source_name, String university_name, String referrer_name);

        void onCancelDilaog();
    }

    public void setOnFilterCallback(FilterCallback callback) {
        this.callback = callback;
    }
}