package com.vessel.gather.module.me.di;

import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.vessel.gather.app.utils.CommonUtils;
import com.vessel.gather.event.Event;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;

import static com.vessel.gather.event.Event.EVENT_ADDRESS_UPDATE;

/**
 * @author vesselzhang
 * @date 2017/11/25
 */

@ActivityScope
public class AddressEditPresenter extends BasePresenter<AddressEditContract.Model, AddressEditContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private AppManager mAppManager;

    @Inject
    public AddressEditPresenter(AddressEditContract.Model model, AddressEditContract.View rootView
            , RxErrorHandler handler, Application application, AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mAppManager = appManager;
    }

    public void saveAddress(int addressId, String name, String phone, String region, String street, String postcode, String detailed) {
        if (TextUtils.isEmpty(name)) {
            mRootView.showMessage("请输入联系人");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            mRootView.showMessage("请输入电话");
            return;
        }
        if (TextUtils.isEmpty(region)) {
            mRootView.showMessage("请填写省市区");
            return;
        }
        if (TextUtils.isEmpty(detailed)) {
            mRootView.showMessage("请输入地址");
            return;
        }
        if (!CommonUtils.isMobile(phone) && !CommonUtils.isFourMobile(phone) && !CommonUtils.isPhone(phone)) {
            mRootView.showMessage("请填写正确的电话号码");
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone", phone);
        map.put("region", region);
        map.put("detailed", detailed);
        map.put("street", street);
        if (!TextUtils.isEmpty(postcode)) {
            map.put("postcode", postcode);
        }
        if (addressId != -1) {
            map.put("addressId", addressId);
        }
        mModel.saveAddress(map)
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(
                        new ErrorHandleSubscriber<Boolean>(mErrorHandler) {
                            @Override
                            public void onNext(Boolean aBoolean) {
                                EventBus.getDefault().post(new Event(), EVENT_ADDRESS_UPDATE);
                                mRootView.showMessage("保存成功");
                                mRootView.killMyself();
                            }
                        }
                );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mApplication = null;
    }
}