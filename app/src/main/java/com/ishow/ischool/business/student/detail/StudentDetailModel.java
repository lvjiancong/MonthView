package com.ishow.ischool.business.student.detail;

import com.commonlib.http.ApiFactory;
import com.ishow.ischool.bean.ApiResult;
import com.ishow.ischool.bean.student.StudentInfo;
import com.ishow.ischool.common.api.MarketApi;

import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by abel on 16/8/18.
 */
public class StudentDetailModel implements StudentDetailContract.Model {
    @Override
    public Observable<ApiResult<StudentInfo>> getStudent(HashMap<String,String> params) {
        return ApiFactory.getInstance().getApi(MarketApi.class)
                .getStudent(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}