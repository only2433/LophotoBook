package com.starbrunch.couple.photo.frame.main.fragment;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.littlefox.logmonitor.Log;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.callback.MainContainerCallback;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.database.PhotoInformationDBHelper;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextView;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextViewType;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 정재현 on 2017-12-13.
 */

public class MonthListViewFragment extends Fragment
{
    @BindView(R.id._emptyDataLayout)
    ScalableLayout _EmptyDataLayout;

    @BindView(R.id._emptyDataImage)
    ImageView _EmptyDataImage;

    @BindView(R.id._emptyDataMessageText)
    HTextView _EmptyDataMessageTextView;

    @BindView(R.id._monthPictureList)
    RecyclerView _MonthPictureList;

    @BindView(R.id._photoFloatingButton)
    FloatingActionButton _PhotoFloatingButton;

    private Context mContext = null;
    private Bundle mBundle = null;
    private MainContainerCallback mMainContainerCallback = null;
    private MonthPictureAdapter mMonthPictureAdapter = null;
    private LinearLayoutManager mLinearLayoutManager = null;
    private boolean isEnterAnimation = false;
    private int mMonthBackgroundColor = 0;
    private int mFlotingButtonHeight = 0;
    private int mCurrentMonthPosition = 0;

    private CoordinatorLayout.LayoutParams mCoordinatorLayoutParams = null;
    private PhotoInformationDBHelper photoInformationDBHelper = null;

    private static final int HEIGHT_FLOTING_BUTTON_DP = 56;
    private ArrayList<PhotoInformationObject> mPhotoInformationList = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_month, container, false);
        ButterKnife.bind(this,view);
        init();
        initFont();
        showEmptyDataAnimation();

        updateView();
        showFloatingButton();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
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


    private void init() {
        photoInformationDBHelper = PhotoInformationDBHelper.getInstance(mContext);
        mBundle = getArguments();

        if (mBundle != null)
        {
            mCurrentMonthPosition = mBundle.getInt(Common.SHARED_ELEMENT_MONTH_POSITION);

            Log.f("mCurrentMonthPosition : "+mCurrentMonthPosition);
            mMonthBackgroundColor = mContext.getResources().getIdentifier("color_month_" + (mCurrentMonthPosition + 1), "color", Common.PACKAGE_NAME);
        }
        mCoordinatorLayoutParams = (CoordinatorLayout.LayoutParams) _PhotoFloatingButton.getLayoutParams();

        mFlotingButtonHeight = (int) CommonUtils.getInstance(mContext).convertDpToPixel(HEIGHT_FLOTING_BUTTON_DP);

    }

    private void initFont()
    {
        _EmptyDataMessageTextView.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
    }

    public void updateView()
    {
        mPhotoInformationList = new ArrayList<PhotoInformationObject>();
        mPhotoInformationList = photoInformationDBHelper.getPhotoInformationListByMonth(Common.MONTH_TEXT_LIST[mCurrentMonthPosition]);
        Log.f("PhotoInformationList size : "+ mPhotoInformationList.size());
        if(mPhotoInformationList.size() > 0)
        {
            _EmptyDataLayout.setVisibility(View.GONE);
            _MonthPictureList.setVisibility(View.VISIBLE);
            initRecyclerView();
        }
        else
        {
            _EmptyDataLayout.setVisibility(View.VISIBLE);
            _MonthPictureList.setVisibility(View.GONE);
        }
    }

    private void initRecyclerView()
    {
        if(mMonthPictureAdapter == null)
        {
            mMonthPictureAdapter = new MonthPictureAdapter();
            mLinearLayoutManager = new LinearLayoutManager(mContext);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            _MonthPictureList.setLayoutManager(mLinearLayoutManager);
            _MonthPictureList.setAdapter(mMonthPictureAdapter);
        }
        else
        {
            mMonthPictureAdapter.notifyDataSetChanged();
        }

    }

    private void showFloatingButton()
    {
        _PhotoFloatingButton.setVisibility(View.VISIBLE);
        _PhotoFloatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(mMonthBackgroundColor)));

        Animation animation = CommonUtils.getInstance(mContext).getTranslateYAnimation(CommonUtils.getInstance(mContext).getPixel(mFlotingButtonHeight) + mCoordinatorLayoutParams.bottomMargin, 0, Common.DURATION_SHORT, Common.DURATION_DEFAULT, new AccelerateInterpolator());
        _PhotoFloatingButton.startAnimation(animation);
        _PhotoFloatingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mMainContainerCallback.onAddPicture();
            }
        });
    }

    private void showEmptyDataAnimation()
    {
        _EmptyDataLayout.setVisibility(View.VISIBLE);
        Animation animation = CommonUtils.getInstance(mContext).getTranslateYAnimation(_EmptyDataLayout.getScaleHeight(), 0, Common.DURATION_SHORT, Common.DURATION_DEFAULT, new AnticipateOvershootInterpolator());

        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                Log.i("");
                _EmptyDataMessageTextView.setAnimateType(HTextViewType.TYPER);

                _EmptyDataMessageTextView.animateText(mContext.getResources().getString(R.string.message_empty_picture_data));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        _EmptyDataImage.startAnimation(animation);
    }

    public int getPhotoInformationSize()
    {
        return mPhotoInformationList.size();
    }


    public class MonthPictureAdapter extends RecyclerView.Adapter<MonthPictureAdapter.ViewHolder>
    {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_photo_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            if(position == 0)
            {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder._ItemCardView.getLayoutParams();
                params.topMargin = 0;
                holder._ItemCardView.setLayoutParams(params);
            }

            //int imageResource = mContext.getResources().getIdentifier("test_image_"+(position+1),"drawable", Common.PACKAGE_NAME);
            Glide.with(mContext).load(Common.PATH_IMAGE_ROOT+mPhotoInformationList.get(position).getFileName()).into(holder._PhotoImage);
        }

        @Override
        public int getItemCount()
        {
            return mPhotoInformationList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            @BindView(R.id._photoBaseLayout)
            ScalableLayout _PhotoBaseLayout;

            @BindView(R.id._itemCardView)
            CardView _ItemCardView;

            @BindView(R.id._photoImage)
            ImageView _PhotoImage;

            @BindView(R.id._photoMonthText)
            TextView _photoMonthText;

            @BindView(R.id._photoDayNumberText)
            TextView _photoDayNumberText;

            @BindView(R.id._photoDayNameText)
            TextView _photoDayNameText;


            public ViewHolder(View view)
            {
                super(view);
                ButterKnife.bind(this,view);
                initFont();
            }

            private void initFont()
            {
                _photoMonthText.setTypeface(FontManager.getInstance(mContext).getMainMonthTextFont());
                _photoDayNumberText.setTypeface(FontManager.getInstance(mContext).getDefaultBoldTextFont());

                _photoDayNameText.setTypeface(FontManager.getInstance(mContext).getMainMonthTextFont());
            }
        }
    }
    public void setMainContainerCallback(MainContainerCallback baseContainerCallback)
    {
        mMainContainerCallback = baseContainerCallback;
    }

}
