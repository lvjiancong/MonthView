package com.ishow.ischool.business.student.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.commonlib.util.DateUtil;
import com.commonlib.widget.LabelTextView;
import com.ishow.ischool.R;
import com.ishow.ischool.application.Resourse;
import com.ishow.ischool.bean.student.StudentInfo;
import com.ishow.ischool.bean.university.UniversityInfo;
import com.ishow.ischool.business.student.edit.EditActivity;
import com.ishow.ischool.business.universitypick.UniversityPickActivity;
import com.ishow.ischool.common.base.BaseFragment4Crm;
import com.ishow.ischool.common.manager.JumpManager;
import com.ishow.ischool.util.AppUtil;
import com.ishow.ischool.widget.custom.SelectDialogFragment;
import com.ishow.ischool.widget.pickerview.PickerDialogFragment;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by abel on 16/8/18.
 */

public class StudentInfoFragment extends BaseFragment4Crm<InfoPresenter, InfoModel> implements InfoContract.View {

    private static final String ARG_PARAM = "param";
    private static final int REQUEST_ENGLISH_NAME = 1000;
    private static final int REQUEST_PHONE = 1001;
    private static final int REQUEST_QQ = 1002;
    private static final int REQUEST_SCHOOL = 1003;
    private static final int REQUEST_SPECIALTY = 1004;
    private static final int REQUEST_CLASS = 1005;
    private static final int REQUEST_IDCARD = 1006;
    private static final int REQUEST_CODE_PICK_UNIVERSITY = 1007;
    private static final int REQUEST_WECHAT = 1008;

    private OnFragmentInteractionListener mListener;

    private int province_id, city_id, campus_id, university_id, source_id;
    private UniversityInfo mUniversityInfo;


    @BindView(R.id.student_english_name)
    LabelTextView englishNameTv;
    @BindView(R.id.student_phone)
    LabelTextView phoneTv;
    @BindView(R.id.student_qq)
    LabelTextView qqTv;
    @BindView(R.id.student_wechat)
    LabelTextView wechatTv;
    @BindView(R.id.student_birthday)
    LabelTextView birthdayTv;
    @BindView(R.id.student_school)
    LabelTextView schoolTv;
    @BindView(R.id.student_specialty)
    LabelTextView specialtyTv;
    @BindView(R.id.student_class)
    LabelTextView classTv;
    @BindView(R.id.student_idcard)
    LabelTextView idcardTv;

    public StudentInfoFragment() {
    }

    public static StudentInfoFragment newInstance() {
        StudentInfoFragment fragment = new StudentInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_student_info;
    }

    @Override
    public void init() {
        updateView();
    }

    public void onButtonPressed(Bundle data) {
        if (mListener != null) {
            mListener.onFragmentInteraction(data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refresh() {
        updateView();
    }

    private void updateView() {
        StudentInfo mStudent = getStudentInfo();
        if (mStudent == null) {
            return;
        }
        englishNameTv.setText(mStudent.english_name);
        phoneTv.setText(mStudent.mobile);
        qqTv.setText(mStudent.qq + "");
        birthdayTv.setText(DateUtil.parseDate2Str((long) mStudent.birthday * 1000, "yyyy-MM-dd"));
        schoolTv.setText(mStudent.college_name);
        specialtyTv.setText(mStudent.major);
        classTv.setText(mStudent.grade);
        idcardTv.setText(mStudent.idcard);
        wechatTv.setText(mStudent.wechat);
    }

    private StudentInfo getStudentInfo() {
        return ((StudentDetailActivity) getActivity()).getStudentInfo();
    }

    @Override
    public void onEditStudentSuccess(HashMap<String, String> params) {
        if (params.containsKey("college_id")) {
            schoolTv.setText(params.get("college_name"));
        } else if (params.containsKey("birthday")) {
            birthdayTv.setText(params.get("birthday"));
        }
    }

    @Override
    public void onEditStudentFailed(String msg) {
        showToast(msg);
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Bundle data);
    }

    @OnClick({R.id.student_english_name, R.id.student_phone, R.id.student_qq, R.id.student_birthday,
            R.id.student_school, R.id.student_specialty, R.id.student_wechat,
            R.id.student_class, R.id.student_idcard,})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.student_english_name: {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(EditActivity.P_TITLE, getString(R.string.label_student_info_english_name));
                intent.putExtra(EditActivity.P_TYPE, R.id.student_english_name);
                intent.putExtra(EditActivity.P_STUDENT_ID, getStudentInfo().student_id);
                intent.putExtra(EditActivity.P_TEXT, getStudentInfo().english_name);
                JumpManager.jumpActivityForResult(this, intent, REQUEST_ENGLISH_NAME);
                break;
            }
            case R.id.student_phone: {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(EditActivity.P_TITLE, getString(R.string.label_student_phone));
                intent.putExtra(EditActivity.P_TYPE, R.id.student_phone);
                intent.putExtra(EditActivity.P_STUDENT_ID, getStudentInfo().student_id);
                intent.putExtra(EditActivity.P_TEXT, getStudentInfo().mobile);
                JumpManager.jumpActivityForResult(this, intent, REQUEST_PHONE);
                break;
            }
            case R.id.student_qq: {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(EditActivity.P_TITLE, getString(R.string.label_student_qq));
                intent.putExtra(EditActivity.P_TYPE, R.id.student_qq);
                intent.putExtra(EditActivity.P_STUDENT_ID, getStudentInfo().student_id);
                intent.putExtra(EditActivity.P_TEXT, getStudentInfo().qq);
                JumpManager.jumpActivityForResult(this, intent, REQUEST_QQ);
            }
            break;

            case R.id.student_wechat: {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(EditActivity.P_TITLE, getString(R.string.label_student_wechat));
                intent.putExtra(EditActivity.P_TYPE, R.id.student_wechat);
                intent.putExtra(EditActivity.P_STUDENT_ID, getStudentInfo().student_id);
                intent.putExtra(EditActivity.P_TEXT, getStudentInfo().wechat);
                JumpManager.jumpActivityForResult(this, intent, REQUEST_WECHAT);
            }
            break;
            case R.id.student_birthday: {
                AppUtil.showTimePickerDialog(getActivity().getSupportFragmentManager(), new PickerDialogFragment.Callback() {
                    @Override
                    public void onPickResult(Object object, String... result) {
                        HashMap<String, String> params = AppUtil.getParamsHashMap(Resourse.COMMUNICATION_EDIT);
                        params.put("id", getStudentInfo().student_id + "");
                        params.put("birthday", String.valueOf(object));
                        mPresenter.editStudent(params);
                    }
                });
            }
            break;
            case R.id.student_school: {
                startActivityForResult(new Intent(getActivity(), UniversityPickActivity.class), REQUEST_CODE_PICK_UNIVERSITY);
            }
            break;
            case R.id.student_specialty: {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(EditActivity.P_TITLE, getString(R.string.label_student_specialty));
                intent.putExtra(EditActivity.P_TYPE, R.id.student_specialty);
                intent.putExtra(EditActivity.P_STUDENT_ID, getStudentInfo().student_id);
                intent.putExtra(EditActivity.P_TEXT, getStudentInfo().major);
                JumpManager.jumpActivityForResult(this, intent, REQUEST_SPECIALTY);
            }
            break;
            case R.id.student_class: {
                AppUtil.showItemDialog(getChildFragmentManager(), AppUtil.getGradeList(), new SelectDialogFragment.OnItemSelectedListner() {

                    @Override
                    public void onItemSelected(int position, String txt) {
                        HashMap<String, String> params = AppUtil.getParamsHashMap(Resourse.COMMUNICATION_EDIT);
                        params.put("id", getStudentInfo().student_id + "");
                        params.put("grade", String.valueOf(position + 1));
                        mPresenter.editStudent(params);
                    }
                });
            }
            break;
            case R.id.student_idcard: {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(EditActivity.P_TITLE, getString(R.string.label_student_idcard));
                intent.putExtra(EditActivity.P_TYPE, R.id.student_idcard);
                intent.putExtra(EditActivity.P_STUDENT_ID, getStudentInfo().student_id);
                intent.putExtra(EditActivity.P_TEXT, getStudentInfo().idcard);
                JumpManager.jumpActivityForResult(this, intent, REQUEST_IDCARD);
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            String text = data.getStringExtra("data");
            switch (requestCode) {
                case REQUEST_CODE_PICK_UNIVERSITY:
                    mUniversityInfo = data.getParcelableExtra(UniversityPickActivity.KEY_PICKED_UNIVERSITY);
                    university_id = mUniversityInfo.id;
                    province_id = mUniversityInfo.prov_id;
                    city_id = mUniversityInfo.city_id;
                    HashMap<String, String> params = AppUtil.getParamsHashMap(Resourse.STUDENT_EDIT);
                    params.put("college_id", university_id + "");
                    params.put("id", getStudentInfo().student_id + "");
                    params.put("college_name", mUniversityInfo.name);
                    mPresenter.editStudent(params);

                    break;
                case REQUEST_ENGLISH_NAME:
                    englishNameTv.setText(text);
                    getStudentInfo().english_name = text;
                    break;
                case REQUEST_PHONE:
                    phoneTv.setText(text);
                    getStudentInfo().mobile = text;
                    break;
                case REQUEST_QQ:
                    qqTv.setText(text);
                    getStudentInfo().qq = text;
                    break;
                case REQUEST_WECHAT:
                    wechatTv.setText(text);
                    getStudentInfo().wechat = text;
                    break;
                case REQUEST_SCHOOL:
                    schoolTv.setText(text);
                    getStudentInfo().college_name = text;
                    break;
                case REQUEST_SPECIALTY:
                    specialtyTv.setText(text);
                    getStudentInfo().major = text;
                    break;
                case REQUEST_CLASS:
                    classTv.setText(text);
                    getStudentInfo().grade = text;
                    break;
                case REQUEST_IDCARD:
                    idcardTv.setText(text);
                    getStudentInfo().idcard = text;
                    break;

            }
        }

    }
}