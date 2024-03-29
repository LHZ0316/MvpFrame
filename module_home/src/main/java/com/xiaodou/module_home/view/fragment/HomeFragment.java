package com.xiaodou.module_home.view.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.lhz.android.libBaseCommon.base.RouterPath;
import com.lhz.android.libBaseCommon.seniorMvp.annotations.CreatePresenterAnnotation;
import com.lhz.android.baseUtils.widget.TitleBar;
import com.xiaodou.module_home.R;
import com.xiaodou.module_home.R2;
import com.xiaodou.module_home.base.BaseHomeFragment;
import com.xiaodou.module_home.contract.IHomeFragmentContract;
import com.xiaodou.module_home.presenter.HomeFragmentPresenter;
import com.xiaodou.module_home.view.activity.OneActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * lhz  on 2019/8/21.
 */
@CreatePresenterAnnotation(HomeFragmentPresenter.class)
@Route(path = RouterPath.HOME_FRAGMENT) // 路由地址，必须注明
public class HomeFragment extends BaseHomeFragment<IHomeFragmentContract.View, HomeFragmentPresenter>
        implements IHomeFragmentContract.View {
    @BindView(R2.id.view_status_bar)
    View mViewStatusBar;
    @BindView(R2.id.myTitleBar)
    TitleBar myTitleBar;

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {

        myTitleBar.setTitle("首页\n副标题");
        myTitleBar.setLeftText("返回");
//        myTitleBar.setTitleColor(Color.BLACK);
//        myTitleBar.setSubTitleColor(Color.BLACK);
//        myTitleBar.setActionTextColor(Color.BLACK);
        myTitleBar.addAction(new TitleBar.TextAction("发布") {
            @Override
            public void performAction(View view) {
                Toast.makeText(getHoldingActivity(), "点击了发布", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void initData() {

    }


    @OnClick(R2.id.tv_btn_1)
    public void onViewClicked() {
        Intent intent = new Intent(getHoldingActivity(), OneActivity.class);
        startActivity(intent);
    }
}
