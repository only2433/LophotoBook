package com.starbrunch.couple.photo.frame.main.fragment;


import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.littlefox.logmonitor.Log;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.callback.MainContainerCallback;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.CommonUtils;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.contract.presenter.MainContainerPresent;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextView;
import com.starbrunch.couple.photo.frame.main.hanks.htextview.HTextViewType;
import com.starbrunch.couple.photo.frame.main.object.PhotoInformationObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

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


    private Context mContext = null;
    private MainContainerCallback mMainContainerCallback = null;
    private MonthPictureAdapter mMonthPictureAdapter = null;
    private LinearLayoutManager mLinearLayoutManager = null;
    private boolean isEnterAnimation = false;

    private int mDeleteIndex = 0;
    private boolean isCreate = false;

     ArrayList<PhotoInformationObject> mPhotoInformationList = null;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_month, container, false);
        ButterKnife.bind(this,view);
        isCreate = true;
        init();
        initFont();

        Log.i("mPhotoInformationList size : "+mPhotoInformationList.size());
        if(mPhotoInformationList.size() > 0)
        {
            showMonthPictureList();
            initRecyclerView();
        }
        else
        {
            showEmptyData();
            showEmptyDataAnimation(Common.DURATION_DEFAULT);
        }

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mMainContainerCallback.setMainScene(MainContainerPresent.SCENE_MONTH_LIST_VIEW);
        Log.i("");
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


    private void init()
    {
        Log.i("");
        Bundle bundle = getArguments();

        if (bundle != null)
        {
            mPhotoInformationList = bundle.getParcelableArrayList(Common.INTENT_MONTH_PHOTO_LIST);
        }

    }

    private void initFont()
    {
        _EmptyDataMessageTextView.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
    }



    private void addPhotoInformation(PhotoInformationObject object)
    {

        mPhotoInformationList.add(object);
        Log.f("PhotoInformationList size : "+ mPhotoInformationList.size());
    }

    private void showMonthPictureList()
    {
        _EmptyDataMessageTextView.reset("");
        _EmptyDataMessageTextView.animateText("");
        _EmptyDataLayout.setVisibility(View.GONE);
        _MonthPictureList.setVisibility(View.VISIBLE);

    }

    private void showEmptyData()
    {
        _EmptyDataLayout.setVisibility(View.VISIBLE);
        _MonthPictureList.setVisibility(View.GONE);
    }


    private void initRecyclerView()
    {
        if(mMonthPictureAdapter == null)
        {
            _MonthPictureList.setItemAnimator(new SlideInRightAnimator());

            mMonthPictureAdapter = new MonthPictureAdapter();
            mLinearLayoutManager = new LinearLayoutManager(mContext);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            _MonthPictureList.setLayoutAnimation(getMonthPictureAnimationController());
            _MonthPictureList.setLayoutManager(mLinearLayoutManager);
            _MonthPictureList.setAdapter(mMonthPictureAdapter);
        }
        else if(mMonthPictureAdapter  != null && isCreate == true)
        {
            mMonthPictureAdapter = new MonthPictureAdapter();
            mLinearLayoutManager = new LinearLayoutManager(mContext);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            _MonthPictureList.setLayoutManager(mLinearLayoutManager);
            _MonthPictureList.setAdapter(mMonthPictureAdapter);
        }

        isCreate = false;

    }

    private LayoutAnimationController getMonthPictureAnimationController()
    {
        AnimationSet animationSet = new AnimationSet(true);

        TranslateAnimation translateAnimation = (TranslateAnimation) CommonUtils.getInstance(mContext).getTranslateXAnimation(
                CommonUtils.getInstance(mContext).getDisplayWidth()/2, 0, 0, 0,
                new AccelerateDecelerateInterpolator());
        AlphaAnimation alphaAnimation = (AlphaAnimation) CommonUtils.getInstance(mContext).getAlphaAnimation(0, 0.0f, 1.0f);

        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setStartOffset(Common.DURATION_SHORT);
        animationSet.setDuration(Common.DURATION_DEFAULT);
        LayoutAnimationController controller = new LayoutAnimationController(animationSet, 0.25f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);

        return controller;
    }

    public void insertItem(PhotoInformationObject object)
    {
        addPhotoInformation(object);
        showMonthPictureList();
        initRecyclerView();
        mMonthPictureAdapter.notifyItemRangeInserted(mPhotoInformationList.size() == 0 ? 0 : mPhotoInformationList.size(), 1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                _MonthPictureList.smoothScrollToPosition(mPhotoInformationList.size()-1);
            }
        }, 500);
    }

    /**
     * 아이템을 삭제해도 Adapter의 index 정보가 갱신되지 않는  Google 버그를 해결
     */
    public void deleteItem()
    {
        Log.i("index : "+ mDeleteIndex);

        mPhotoInformationList.remove(mDeleteIndex);
        mMonthPictureAdapter.notifyItemRemoved(mDeleteIndex);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMonthPictureAdapter.notifyDataSetChanged();
            }
        }, 500);

    }

    public void notifyChanged(int position, PhotoInformationObject object)
    {
        Log.i("position : "+position);
        mPhotoInformationList.set(position, object);
        mMonthPictureAdapter.notifyItemRangeChanged(position,1);
    }

    private void showDeletePhotoConfirmDialog(final int position)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.message_question_delete_data));
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                mDeleteIndex = position;
                mMainContainerCallback.onDeletePhoto(mPhotoInformationList.get(position).getKeyID());
            }
        });

        AlertDialog dialog = builder.show();
        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);

        dialog.show();
    }

    /**
     * 데이터가 없을때 에니메이션을 동작 시킨다.
     * @param delay 지연 시키는 값.
     */
    private void showEmptyDataAnimation(int delay)
    {
        _EmptyDataLayout.setVisibility(View.VISIBLE);
        Animation animation = CommonUtils.getInstance(mContext).getTranslateYAnimation(_EmptyDataLayout.getScaleHeight(), 0, Common.DURATION_SHORT, delay, new AnticipateOvershootInterpolator());

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
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            Log.i("position : "+position);
            if(position == 0)
            {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder._ItemCardView.getLayoutParams();
                params.topMargin = 0;
                holder._ItemCardView.setLayoutParams(params);
            }

            //int imageResource = mContext.getResources().getIdentifier("test_image_"+(position+1),"drawable", Common.PACKAGE_NAME);
            holder._PhotoImage.setImageBitmap(BitmapFactory.decodeFile(Common.PATH_IMAGE_ROOT+mPhotoInformationList.get(position).getFileName()));

            holder._PhotoDayTimeText.setText(CommonUtils.getInstance(mContext).getDateClock(mPhotoInformationList.get(position).getDateTime()));
            holder._photoDayNumberText.setText(CommonUtils.getInstance(mContext).getDateDay(mPhotoInformationList.get(position).getDateTime()));
            holder._PhotoFullDateText.setText(CommonUtils.getInstance(mContext).getDateFullText(mPhotoInformationList.get(position).getDateTime()));

            holder._PhotoImage.setTransitionName(Common.SHARED_PHOTO_IMAGE +"_"+ mPhotoInformationList.get(position).getKeyID());

            holder._PhotoDeleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    showDeletePhotoConfirmDialog(position);
                }
            });

            holder._PhotoImage.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View view)
                {
                    Pair requestPair = new Pair(holder._PhotoImage, holder._PhotoImage.getTransitionName());
                    mMainContainerCallback.onModifiedPhoto(position, requestPair);
                }
            });

        }

        @Override
        public int getItemCount()
        {
            return mPhotoInformationList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements AnimateViewHolder
        {
            @BindView(R.id._photoBaseLayout)
            ScalableLayout _PhotoBaseLayout;

            @BindView(R.id._itemCardView)
            CardView _ItemCardView;

            @BindView(R.id._photoImage)
            ImageView _PhotoImage;

            @BindView(R.id._photoDayTimeText)
            TextView _PhotoDayTimeText;

            @BindView(R.id._photoDayNumberText)
            TextView _photoDayNumberText;

            @BindView(R.id._photoFullDateText)
            TextView _PhotoFullDateText;

            @BindView(R.id._photoDeleteButton)
            ImageView _PhotoDeleteButton;


            public ViewHolder(View view)
            {
                super(view);
                ButterKnife.bind(this,view);
                initFont();
            }

            private void initFont()
            {
                _PhotoDayTimeText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
                _photoDayNumberText.setTypeface(FontManager.getInstance(mContext).getDefaultBoldTextFont());
                _PhotoFullDateText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
            }

            @Override
            public void preAnimateAddImpl(RecyclerView.ViewHolder holder)
            {
                holder.itemView.setTranslationX(holder.itemView.getWidth());
                holder.itemView.setAlpha(0.0f);
            }

            @Override
            public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder)
            {
                holder.itemView.setTranslationX(0);
                holder.itemView.setAlpha(1.0f);
            }

            @Override
            public void animateAddImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener)
            {
                holder.itemView.animate()
                        .translationX(0)
                        .alpha(1.0f)
                        .setDuration(Common.DURATION_DEFAULT)
                        .setStartDelay(Common.DURATION_SHORT)
                        .setListener(new Animator.AnimatorListener()
                        {
                            @Override
                            public void onAnimationStart(Animator animator) {}

                            @Override
                            public void onAnimationEnd(Animator animator)
                            {
                                mMonthPictureAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {}

                            @Override
                            public void onAnimationRepeat(Animator animator) {}
                        })
                        .start();
            }

            @Override
            public void animateRemoveImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener)
            {
                holder.itemView.animate()
                        .translationX(holder.itemView.getWidth())
                        .alpha(0.0f)
                        .setDuration(Common.DURATION_DEFAULT)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {}

                            @Override
                            public void onAnimationEnd(Animator animator)
                            {
                                mMonthPictureAdapter.notifyDataSetChanged();
                                if(mPhotoInformationList.size() <= 0)
                                {
                                    showEmptyData();
                                    showEmptyDataAnimation(0);
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {}

                            @Override
                            public void onAnimationRepeat(Animator animator) {}
                        })
                        .start();
            }
        }
    }

    public void setMainContainerCallback(MainContainerCallback baseContainerCallback)
    {
        mMainContainerCallback = baseContainerCallback;
    }
}
