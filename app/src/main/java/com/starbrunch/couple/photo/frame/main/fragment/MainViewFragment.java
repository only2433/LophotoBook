package com.starbrunch.couple.photo.frame.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.littlefox.logmonitor.Log;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.callback.MainContainerCallback;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.contract.presenter.MainContainerPresent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 정재현 on 2017-12-13.
 */

public class MainViewFragment extends Fragment
{
    private Context mContext = null;
    private MainMonthAdapter mMainMonthAdapter = null;

    private MainContainerCallback mMainContainerCallback = null;
    private FragmentManager mFragmentManager = null;

    private int mSelectPositionColor = 0;

    private Transition mExitTransition = null;
    private Transition mReEnterTransition = null;

    @BindView(R.id._monthItemList)
    RecyclerView _MonthItemList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i("");
        mMainContainerCallback.setMainScene(MainContainerPresent.SCENE_MAIN_VIEW);
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }


    @Override
    public void setExitTransition(Object transition)
    {
        Log.i("");
        if(transition != null)
        {
            Transition tempTransition = (Transition) transition;
            mExitTransition = tempTransition;

            tempTransition.addListener(mExitTransitionListener);
            super.setExitTransition(transition);
        }

    }

    @Override
    public void setReenterTransition(Object transition)
    {
        Log.i("");
        if(transition != null)
        {
            Log.i("");
            Transition tempTransition = (Transition) transition;
            mReEnterTransition = tempTransition;
            tempTransition.addListener(mReenterTransitionListener);
            super.setReenterTransition(transition);
        }

    }



    private void initRecyclerView()
    {
        mMainMonthAdapter = new MainMonthAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        _MonthItemList.setLayoutManager(linearLayoutManager);
        _MonthItemList.setAdapter(mMainMonthAdapter);
    }

    private Transition.TransitionListener mExitTransitionListener = new Transition.TransitionListener()
    {
        @Override
        public void onTransitionStart(@NonNull Transition transition)
        {
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition)
        {
            Log.i("");
            mMainContainerCallback.onChangeMonthListViewSetting();
        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {}

        @Override
        public void onTransitionPause(@NonNull Transition transition) {}

        @Override
        public void onTransitionResume(@NonNull Transition transition) {}
    };

    private Transition.TransitionListener mReenterTransitionListener = new Transition.TransitionListener()
    {
        @Override
        public void onTransitionStart(@NonNull Transition transition) {

            Log.i("");

            mMainContainerCallback.onChangeMainViewSetting();
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionPause(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionResume(@NonNull Transition transition) {

        }
    };

    public class MainMonthAdapter extends RecyclerView.Adapter<MainMonthAdapter.ViewHolder>
    {
        private static final int MONTH_MAX_COUNT = 12;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_mainlist_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            final int imageColorResource = mContext.getResources().getIdentifier("color_month_"+(position+1),"color", Common.PACKAGE_NAME);

            holder._ItemMonthBackground.setBackgroundColor(getResources().getColor(imageColorResource));
            holder._ItemMonthNumberText.setText(String.valueOf(position+1));

            holder._ItemMonthTitleText.setText(Common.MONTH_TEXT_LIST[position]);
            holder._MonthBaseLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mSelectPositionColor = imageColorResource;
                    mMainContainerCallback.onSelectMonth(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return MONTH_MAX_COUNT;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            @BindView(R.id._itemLayout)
            ScalableLayout _MonthBaseLayout;

            @BindView(R.id._itemMonthBackground)
            ImageView _ItemMonthBackground;

            @BindView(R.id._itemMonthNumberText)
            TextView _ItemMonthNumberText;

            @BindView(R.id._itemMonthTitleText)
            TextView _ItemMonthTitleText;


            public ViewHolder(View view)
            {
                super(view);
                ButterKnife.bind(this, view);
                initFont();
            }

            private void initFont()
            {
                _ItemMonthNumberText.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
                _ItemMonthTitleText.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
            }

        }
    }

    public void setMainContainerCallback(MainContainerCallback baseContainerCallback)
    {
        mMainContainerCallback = baseContainerCallback;
    }
}
