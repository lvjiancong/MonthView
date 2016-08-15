package com.ishow.ischool.business.login;

import com.ishow.ischool.bean.user.User;
import com.ishow.ischool.common.api.ApiObserver;
import com.ishow.ischool.common.manager.TokenManager;


/**
 * Created by wqf on 16/8/13.
 */
public class LoginPresenter extends LoginContract.Presenter {

    @Override
    public void login(String name, String pass) {
        mModel.login(name, pass)
                .subscribe(new ApiObserver<User>(User.class) {
                    @Override
                    public void onSuccess(User user) {
                        mView.loginSuccess();
                        TokenManager.init(user.getToken());
                    }

                    @Override
                    public void onError(String msg) {
                        mView.loginError(msg);
                    }
                });
    }

}
