package com.vessel.gather.app.config.applyOptions;

import android.content.Context;

import com.jess.arms.di.module.ClientModule;
import com.jess.arms.utils.ArmsUtils;
import com.vessel.gather.app.utils.GsonConverterFactory;

import retrofit2.Retrofit;

/**
 * @author vesselzhang
 * @date 2017/11/25
 */

public class MyRetrofitConfiguration implements ClientModule.RetrofitConfiguration {
    @Override
    public void configRetrofit(Context context, Retrofit.Builder builder) {
        // 配置多BaseUrl支持
//        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
//        if (BuildConfig.DEBUG) {
//            clientBuilder.addInterceptor(new LoggingInterceptor());//使用自定义的Log拦截器
//        }
//        clientBuilder.addInterceptor(new UserAgentInterceptor());//使用自定义User-Agent
//        builder.client(RetrofitUrlManager.getInstance().with(clientBuilder)
//                .build());
        builder.addConverterFactory(GsonConverterFactory.create(ArmsUtils.obtainAppComponentFromContext(context).gson()));
    }
}