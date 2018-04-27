package com.vessel.gather.module.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.addresspicker.huichao.addresspickerlibrary.CityPickerDialog;
import com.addresspicker.huichao.addresspickerlibrary.InitAreaTask;
import com.addresspicker.huichao.addresspickerlibrary.address.Province;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.jess.arms.di.component.AppComponent;
import com.vessel.gather.R;
import com.vessel.gather.app.base.MySupportFragment;
import com.vessel.gather.app.constant.Constants;
import com.vessel.gather.app.data.entity.IndexResponse;
import com.vessel.gather.widght.AutoScrollViewPager;
import com.vessel.gather.widght.BaseViewPagerAdapter;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author vesselzhang
 * @date 2017/11/25
 */

public class HomeTabFragment extends MySupportFragment {
    @BindView(R.id.city)
    TextView cityTV;
    @BindView(R.id.view_pager)
    AutoScrollViewPager mViewPager;

    private ArrayList<Province> provinces = new ArrayList<>();
    private BaseViewPagerAdapter<String> mBaseViewPagerAdapter;
    private List<String> imageUrls = new ArrayList<>();
    private List<IndexResponse.BannersBean> banners = new ArrayList<>();

    public static HomeTabFragment newInstance() {
        Bundle args = new Bundle();

        HomeTabFragment fragment = new HomeTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mBaseViewPagerAdapter = new BaseViewPagerAdapter<String>(getActivity().getApplicationContext(), listener) {
            @Override
            public void loadImage(ImageView view, int position, String url) {
                Glide.with(getActivity().getApplicationContext()).load(url).into(view);
            }

            @Override
            public void setSubTitle(TextView textView, int position, String s) {
                textView.setText(s);
            }
        };
        mViewPager.setAdapter(mBaseViewPagerAdapter);
        InitAreaTask initAreaTask = new InitAreaTask(getActivity(), provinces);
        initAreaTask.execute(0);
        initAreaTask.setOnLoadingAddressListener(new InitAreaTask.onLoadingAddressListener() {
            @Override
            public void onLoadFinished() {

            }

            @Override
            public void onLoading() {

            }
        });
    }

    private void showAddressDialog() {
        new CityPickerDialog(getActivity(), provinces,
                null, null, null, (selectProvince, selectCity, selectCounty) -> {
            StringBuilder address = new StringBuilder();
            address.append(
                    selectProvince != null ? selectProvince
                            .getAreaName() : "")
                    .append(selectCity != null ? selectCity
                            .getAreaName() : "");
            cityTV.setText(address.toString());
        }).show();
    }

    @Override
    public void setData(Object data) {

    }

    @Subscriber(tag = Constants.TAG_BANNERS_REFRESH)
    public void onRefresh(IndexResponse indexResponse) {
        banners.clear();
        banners.addAll(indexResponse.getBanners());
        for (IndexResponse.BannersBean bean : indexResponse.getBanners()) {
            imageUrls.add(bean.getBannerPic());
        }
        mBaseViewPagerAdapter.add(imageUrls);
    }

    BaseViewPagerAdapter.OnAutoViewPagerItemClickListener listener = (position, o) -> {
        int type = banners.get(position).getLinkType();
        ARouter.getInstance().build("/app/web").navigation();
    };

    @OnClick({R.id.layout_shangjia, R.id.layout_shifu, R.id.ll_search
            , R.id.tuijianshangjia, R.id.tuijianshifu, R.id.location})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tuijianshangjia:
            case R.id.layout_shangjia:
                ARouter.getInstance().build("/app/container")
                        .withSerializable(Constants.PAGE, Constants.PAGE_SELLER_LIST)
                        .navigation();
                break;
            case R.id.layout_shifu:
            case R.id.tuijianshifu:
                ARouter.getInstance().build("/app/container")
                        .withSerializable(Constants.PAGE, Constants.PAGE_WORKER_LIST)
                        .navigation();
                break;
            case R.id.location:
                showAddressDialog();
                break;
            case R.id.ll_search:
//                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
//                ArmsUtils.startActivity(searchIntent);
                break;
        }
    }
}
