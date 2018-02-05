package com.starbrunch.couple.photo.frame.main.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssomai.android.scalablelayout.ScalableLayout;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.common.FontManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by only340 on 2018-02-05.
 */

public class DataCommunicateFragment extends Fragment
{



    @BindView(R.id._baseDataLayout)
    ScalableLayout _BaseDataLayout;

    @BindView(R.id._baseDataTitle)
    TextView _BaseDataTitle;

    @BindView(R.id._baseDataLayoutBackgroundImage)
    ImageView _BaseDataLayoutBackgroundImage;

    @BindView(R.id._baseDataPercentBackgroundImage)
    CircleImageView _BaseDataPercentBackgroundImage;

    @BindView(R.id._subDataLayout)
    ScalableLayout _SubDataLayout;

    @BindView(R.id._dataMessageText)
    TextView _DataMessageText;

    @BindView(R.id._dataActionButton)
    ImageView _DataActionButton;

    @BindView(R.id._dataButtonText)
    TextView _DataButtonText;

    private static final int TYPE_SEND      = 0;
    private static final int TYPE_RECEIVE   = 1;

    private Context mContext = null;
    private int mCurrentDataType = TYPE_RECEIVE;

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
        initFont();
        initView();
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

    private void initView()
    {
        if(mCurrentDataType == TYPE_SEND)
        {
            _BaseDataTitle.setText(mContext.getResources().getString(R.string.title_send));
            _BaseDataLayoutBackgroundImage.setBackgroundColor(mContext.getResources().getColor(R.color.color_0ac8e2));
            _BaseDataPercentBackgroundImage.setCircleBackgroundColor(mContext.getResources().getColor(R.color.color_0ac8e2));
            _DataActionButton.setBackgroundColor(mContext.getResources().getColor(R.color.color_0ac8e2));
            _DataMessageText.setTextColor(mContext.getResources().getColor(R.color.color_0ac8e2));
            _DataMessageText.setText(mContext.getResources().getString(R.string.message_data_send_information)+"\n"+"1C:22:DD:CC:F8");
        }
        else if(mCurrentDataType == TYPE_RECEIVE)
        {
            _BaseDataTitle.setText(mContext.getResources().getString(R.string.title_receive));
            _BaseDataLayoutBackgroundImage.setBackgroundColor(mContext.getResources().getColor(R.color.color_eb1e63));
            _BaseDataPercentBackgroundImage.setCircleBackgroundColor(mContext.getResources().getColor(R.color.color_eb1e63));

            _DataActionButton.setBackgroundColor(mContext.getResources().getColor(R.color.color_eb1e63));
            _DataMessageText.setTextColor(mContext.getResources().getColor(R.color.color_eb1e63));
            _DataMessageText.setText(mContext.getResources().getString(R.string.message_data_receive_information)+"\n"+"1C:22:DD:CC:F8");
        }

        _DataActionButton.setOnClickListener(mOnButtonClickListener);
    }

    private void initFont()
    {
        _BaseDataTitle.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
        _DataMessageText.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
        _DataButtonText.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
    }

    private View.OnClickListener mOnButtonClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {

        }
    };
}
