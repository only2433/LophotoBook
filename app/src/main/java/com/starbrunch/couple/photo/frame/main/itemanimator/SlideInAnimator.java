package com.starbrunch.couple.photo.frame.main.itemanimator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.littlefox.logmonitor.Log;

/**
 * Created by 정재현 on 2018-01-03.
 */

public class SlideInAnimator extends SimpleItemAnimator
{
    private Context mContext = null;
    public SlideInAnimator(Context context)
    {
        super();
        mContext = context;
    }
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder)
    {
       /* Log.f("Width : "+ holder.itemView.getWidth());
        holder.itemView.setTranslationX(0);
        holder.itemView.setAlpha(1.0f);
        holder.itemView.animate().translationX(holder.itemView.getWidth() == 0 ? 1280 : holder.itemView.getWidth())
                .alpha(0.0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(300)
                .setDuration(600)
                .start();*/
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder)
    {
        Log.f("Width : "+ holder.itemView.getWidth());
        holder.itemView.setTranslationX(holder.itemView.getWidth() == 0 ? 1280 : holder.itemView.getWidth());
        holder.itemView.setAlpha(0.0f);
        holder.itemView.animate().translationX(0)
                .alpha(1.0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(300)
                .setDuration(600)
                .start();
        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations()
    {

    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item)
    {

    }

    @Override
    public void endAnimations()
    {

    }

    @Override
    public boolean isRunning()
    {
        return false;
    }
}
