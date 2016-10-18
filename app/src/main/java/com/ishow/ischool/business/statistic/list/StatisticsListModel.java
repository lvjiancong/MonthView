package com.ishow.ischool.business.statistic.list;

import com.commonlib.Conf;
import com.commonlib.http.ApiFactory;
import com.ishow.ischool.application.Resource;
import com.ishow.ischool.bean.ApiResult;
import com.ishow.ischool.bean.student.StudentList;
import com.ishow.ischool.common.api.MarketApi;

import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wqf on 16/8/14.
 */
public class StatisticsListModel implements StatisticsListContract.Model {

    public Observable<ApiResult<StudentList>> getList4StudentStatistics(HashMap<String, String> params, int page) {
        return ApiFactory.getInstance().getApi(MarketApi.class).listStudentStatistics(Resource.MARKET_STUDENT_STATISTICS, params, Conf.DEFAULT_PAGESIZE_LISTVIEW, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

}