package com.starbrunch.couple.photo.frame.main.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.littlefox.logmonitor.ExceptionCheckHandler;

public class BaseActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionCheckHandler(this));

	}
	
	@Override
	protected void onResume()
	{
		super.onResume();

	}
	
	@Override
	protected void onPause()
	{
		super.onPause();

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		
	}

	@Override
	public void finish()
	{
		super.finish();

		
		
	}
	
	
	
}
