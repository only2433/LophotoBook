package com.starbrunch.couple.photo.frame.main.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.littlefox.library.view.dialog.ProgressWheel;
import com.littlefox.logmonitor.Log;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.callback.MainContainerCallback;
import com.starbrunch.couple.photo.frame.main.common.Common;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.contract.presenter.MainContainerPresent;
import com.starbrunch.couple.photo.frame.main.handler.WeakReferenceHandler;
import com.starbrunch.couple.photo.frame.main.handler.callback.MessageHandlerCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by only340 on 2018-02-05.
 */

public class DataCommunicateFragment extends Fragment implements MessageHandlerCallback
{
    @BindView(R.id._baseDataLayout)
    ScalableLayout _BaseDataLayout;

    @BindView(R.id._baseDataTitle)
    TextView _BaseDataTitle;

    @BindView(R.id._baseDataLayoutBackgroundImage)
    ImageView _BaseDataLayoutBackgroundImage;

    @BindView(R.id._baseDataPercentBackgroundImage)
    CircleImageView _BaseDataPercentBackgroundImage;

    @BindView(R.id._dataProgressView)
    ProgressWheel _DataProgressView;

    @BindView(R.id._subDataLayout)
    ScalableLayout _SubDataLayout;

    @BindView(R.id._dataMessageText)
    TextView _DataMessageText;

    @BindView(R.id._dataPercentProgressText)
    TextView _DataPercentProgressText;

    @BindView(R.id._dataActionButton)
    ImageView _DataActionButton;

    @BindView(R.id._dataButtonText)
    TextView _DataButtonText;


    private static final int MESSAGE_END_SCENE = 0;

    private static final int MAX_PERCENT = 100;

    private static final int STATUS_NOT_YET_SEND    = 0;
    private static final int STATUS_SENDING         = 1;
    private int mCurrentSendStatus                  = STATUS_NOT_YET_SEND;

    private Context mContext = null;
    private int mCurrentDataType = Common.RESULT_SETTING_BLUETOOTH_SEND;

    private MainContainerCallback mMainContainerCallback = null;
    private WeakReferenceHandler mWeakReferenceHandler = null;

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
        View view = inflater.inflate(R.layout.fragment_data_communicate, container, false);
        ButterKnife.bind(this, view);
        mWeakReferenceHandler = new WeakReferenceHandler(this);
        initFont();
        initView();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mMainContainerCallback.setMainScene(MainContainerPresent.SCENE_DATA_COMMUNICATE);
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
    public void setReturnTransition(Object transition)
    {
        if(transition != null)
        {
            Transition tempTransition = (Transition) transition;
            tempTransition.addListener(mReturnTransitionListener);
            super.setReturnTransition(transition);
        }
    }

    private void initView()
    {
        Bundle bundle = getArguments();

        mCurrentDataType = bundle.getInt(Common.INTENT_SETTING_SELECT_INDEX, Common.RESULT_SETTING_BLUETOOTH_SEND);

        _DataPercentProgressText.setText("0");
        _DataProgressView.setVisibility(View.INVISIBLE);
        if(mCurrentDataType == Common.RESULT_SETTING_BLUETOOTH_SEND)
        {
            _BaseDataTitle.setText(mContext.getResources().getString(R.string.title_send));
            _BaseDataLayoutBackgroundImage.setBackgroundColor(mContext.getResources().getColor(R.color.color_0ac8e2));
            _BaseDataPercentBackgroundImage.setCircleBackgroundColor(mContext.getResources().getColor(R.color.color_0ac8e2));
            _DataActionButton.setBackgroundColor(mContext.getResources().getColor(R.color.color_0ac8e2));
            _DataMessageText.setTextColor(mContext.getResources().getColor(R.color.color_0ac8e2));
            _DataMessageText.setText(mContext.getResources().getString(R.string.message_data_send_information)+"\n"+"1C:22:DD:CC:F8");
            _DataButtonText.setText(mContext.getResources().getString(R.string.button_send));
        }
        else if(mCurrentDataType == Common.RESULT_SETTING_BLUETOOTH_RECEIVE)
        {
            _BaseDataTitle.setText(mContext.getResources().getString(R.string.title_receive));
            _BaseDataLayoutBackgroundImage.setBackgroundColor(mContext.getResources().getColor(R.color.color_eb1e63));
            _BaseDataPercentBackgroundImage.setCircleBackgroundColor(mContext.getResources().getColor(R.color.color_eb1e63));

            _DataActionButton.setBackgroundColor(mContext.getResources().getColor(R.color.color_eb1e63));
            _DataMessageText.setTextColor(mContext.getResources().getColor(R.color.color_eb1e63));
            _DataMessageText.setText(mContext.getResources().getString(R.string.message_data_receive_information)+"\n"+"1C:22:DD:CC:F8");
            _DataButtonText.setText(mContext.getResources().getString(R.string.button_cancel));
        }

        _DataActionButton.setOnClickListener(mOnButtonClickListener);
    }

    private void initFont()
    {
        _BaseDataTitle.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
        _DataMessageText.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
        _DataButtonText.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
        _DataPercentProgressText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
    }

    private void changeDataActionButton()
    {
        if(mCurrentSendStatus == STATUS_NOT_YET_SEND)
        {
            _DataButtonText.setText(mContext.getResources().getString(R.string.button_send));
        }
        else if(mCurrentSendStatus == STATUS_SENDING)
        {
            _DataButtonText.setText(mContext.getResources().getString(R.string.button_cancel));
        }
    }

    public void startTransferData()
    {
        _DataProgressView.setVisibility(View.VISIBLE);
    }

    public void endTransferData()
    {
        _DataProgressView.setVisibility(View.INVISIBLE);
        _DataButtonText.setVisibility(View.INVISIBLE);
        _DataActionButton.setVisibility(View.INVISIBLE);

        _DataPercentProgressText.setText(String.valueOf(MAX_PERCENT));
        _DataMessageText.setText(mContext.getResources().getString(R.string.message_data_success));
    }

    public void closeFragment()
    {
        mWeakReferenceHandler.sendEmptyMessageDelayed(MESSAGE_END_SCENE, Common.DURATION_LONGER);
    }

    public void setTransferPercent(int percent)
    {
        _DataPercentProgressText.setText(String.valueOf(percent));
    }



    public void setMainContainerCallback(MainContainerCallback baseContainerCallback)
    {
        mMainContainerCallback = baseContainerCallback;
    }

    @Override
    public void handlerMessage(Message message)
    {
        if(message.what == MESSAGE_END_SCENE)
        {
            ((AppCompatActivity)mContext).onBackPressed();
        }

    }

    private Transition.TransitionListener mReturnTransitionListener = new Transition.TransitionListener()
    {
        @Override
        public void onTransitionStart(Transition transition)
        {

        }

        @Override
        public void onTransitionEnd(Transition transition)
        {
            Log.i("");
            mMainContainerCallback.onDataTransferEnd();
        }

        @Override
        public void onTransitionCancel(Transition transition)
        {

        }

        @Override
        public void onTransitionPause(Transition transition)
        {

        }

        @Override
        public void onTransitionResume(Transition transition)
        {

        }
    };

    private View.OnClickListener mOnButtonClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            if(mCurrentDataType == Common.RESULT_SETTING_BLUETOOTH_SEND)
            {
                if(mCurrentSendStatus == STATUS_NOT_YET_SEND)
                {
                    mCurrentSendStatus = STATUS_SENDING;
                    mMainContainerCallback.startFileTransfer();
                }
                else if(mCurrentSendStatus == STATUS_SENDING)
                {
                    mCurrentSendStatus = STATUS_NOT_YET_SEND;

                }

                changeDataActionButton();
            }
            else
            {
                ((AppCompatActivity)mContext).onBackPressed();
            }
        }
    };
}
