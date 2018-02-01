package com.starbrunch.couple.photo.frame.main.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littlefox.library.view.dialog.ProgressWheel;
import com.littlefox.logmonitor.Log;
import com.ssomai.android.scalablelayout.ScalableLayout;
import com.starbrunch.couple.photo.frame.main.R;
import com.starbrunch.couple.photo.frame.main.common.FontManager;
import com.starbrunch.couple.photo.frame.main.object.BluetoothScanInformation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by only340 on 2018-01-26.
 */

public class BluetoothScanDialog extends Dialog
{
    public interface BluetoothScanListener
    {
        public void onSelectDevice(String deviceAddress);
        public void onClickScan();
        public void onClickCancel();
        public void onDismiss();
    }

    @BindView(R.id._searchTitleText)
    TextView _SearchTitleText;

    @BindView(R.id._searchListview)
    RecyclerView _SearchListView;

    @BindView(R.id._searchFailMessageText)
    TextView _SearchFailMessageTextView;

    @BindView(R.id._searchLoadingProgress)
    ProgressWheel _SearchLoadingProgress;

    @BindView(R.id._buttonSearchText)
    TextView _ButtonSearchText;

    @BindView(R.id._buttonCancelText)
    TextView _ButtonCancelText;

    private BluetoothScanListener mBluetoothScanListener = null;
    private ArrayList<BluetoothScanInformation> mBluetoothScanInformationList = null;
    private BluetoothScanInformationAdapter mBluetoothScanInformationAdapter = null;
    private Context mContext = null;

    public BluetoothScanDialog(@NonNull Context context)
    {
        super(context);
        setContentView(R.layout.dialog_bluetooth_scan);
        ButterKnife.bind(this);
        mContext = context;
        init();
        initFont();
        showLoading();

    }

    private void init()
    {
        mBluetoothScanInformationList = new ArrayList<BluetoothScanInformation>();
        initAdapter();
    }

    private void initAdapter()
    {
        mBluetoothScanInformationAdapter = new BluetoothScanInformationAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _SearchListView.setLayoutManager(linearLayoutManager);
        _SearchListView.setAdapter(mBluetoothScanInformationAdapter);
    }

    private void initFont()
    {
        _SearchTitleText.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
        _SearchFailMessageTextView.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
        _ButtonSearchText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());
        _ButtonCancelText.setTypeface(FontManager.getInstance(mContext).getDefaultLightTextFont());

    }

    public void showLoading()
    {
        mBluetoothScanInformationList.clear();
        _SearchListView.setVisibility(View.GONE);
        _SearchLoadingProgress.setVisibility(View.VISIBLE);
        _SearchFailMessageTextView.setVisibility(View.GONE);
        _ButtonSearchText.setVisibility(View.GONE);
    }

    public void addData(String deviceName, String deviceAddress)
    {
        Log.i("deviceName : "+deviceName+", deviceAddress : "+deviceAddress);
        mBluetoothScanInformationList.add(new BluetoothScanInformation(deviceName, deviceAddress));
    }

    public void showData()
    {
        _SearchLoadingProgress.setVisibility(View.GONE);
        _ButtonSearchText.setVisibility(View.VISIBLE);
        if(mBluetoothScanInformationList.size() > 0)
        {
            _SearchFailMessageTextView.setVisibility(View.GONE);
            _SearchListView.setVisibility(View.VISIBLE);
            mBluetoothScanInformationAdapter.notifyDataSetChanged();
        }
        else
        {
            _SearchFailMessageTextView.setVisibility(View.VISIBLE);
            _SearchListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
    }

    @OnClick({R.id._buttonSearchText, R.id._buttonCancelText})
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id._buttonSearchText:
                mBluetoothScanListener.onClickScan();
                break;
            case R.id._buttonCancelText:
                mBluetoothScanListener.onClickCancel();
                break;
        }
    }

    public void setBluetoothScanListener(BluetoothScanListener bluetoothScanListener)
    {
        mBluetoothScanListener = bluetoothScanListener;
    }

    public class BluetoothScanInformationAdapter extends RecyclerView.Adapter<BluetoothScanInformationAdapter.ViewHolder>
    {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position)
        {
            Log.i("position : "+ position+", Name : "+ mBluetoothScanInformationList.get(position).getScanDeviceName()+", Address : "+
                    mBluetoothScanInformationList.get(position).getScanDeviceAddress());
            holder._AdapterDeviceInformationText.setText(mBluetoothScanInformationList.get(position).getScanDeviceName()
            +"\n"+mBluetoothScanInformationList.get(position).getScanDeviceAddress());

            holder._BaseLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.i("position : "+ position + " , Address : "+ mBluetoothScanInformationList.get(position).getScanDeviceAddress());
                    mBluetoothScanListener.onSelectDevice(mBluetoothScanInformationList.get(position).getScanDeviceAddress());
                }
            });
        }

        @Override
        public int getItemCount()
        {
            Log.i("size : "+mBluetoothScanInformationList.size());
            return mBluetoothScanInformationList.size();
        }



        public class ViewHolder extends RecyclerView.ViewHolder
        {
            @BindView(R.id._adapterSearchBaseLayout)
            ScalableLayout _BaseLayout;

            @BindView(R.id._adapterDeviceInformationText)
            TextView _AdapterDeviceInformationText;

            public ViewHolder(View itemView)
            {
                super(itemView);
                ButterKnife.bind(this,itemView);
                initFont();
            }

            private void initFont()
            {
                _AdapterDeviceInformationText.setTypeface(FontManager.getInstance(mContext).getMainTitleFont());
            }
        }
    }
}
