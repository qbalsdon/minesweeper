package com.balsdon.minesweeper.view;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.balsdon.minesweeper.R;
import com.balsdon.minesweeper.controller.BoardController;
import com.balsdon.minesweeper.model.MineCell;

public class MineViewHolder {
    public float YSCALE = 1.0f;
    public float XSCALE = 1.0f;
    public float SCALE_FACTOR = 1.5f;

    private ScaleAnimation mScaleAnimation; //if the objects share an animation, they all animate together :(
    private View mParent;
    private TextView mTextView;

    private MineCell mMineCell;

    private enum CellState{
        MINE,
        GRASS,
        FLAG,
        WRONG,
        NUMBER
    }

    public MineViewHolder(MineCell mineCell, View parent){
        mMineCell = mineCell;
        mScaleAnimation = new ScaleAnimation(XSCALE, XSCALE*SCALE_FACTOR, YSCALE, YSCALE*SCALE_FACTOR, Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF);

        mScaleAnimation.setDuration(250);
        mScaleAnimation.setFillAfter(true);
        mScaleAnimation.setFillEnabled(true);

        mScaleAnimation.setRepeatCount(1);
        mScaleAnimation.setRepeatMode(Animation.REVERSE);
        mScaleAnimation.setInterpolator(new LinearInterpolator());
        setView(parent);
    }

    public void setView(View parent){
        mParent = null;
        mParent = parent;
        mTextView = (TextView)mParent.findViewById(R.id.grid_item_text);
        mTextView.setText(String.valueOf(mMineCell.getMineValue()));
        updateView();
    }

    private int getColorForNumber(int number){
        switch(number){
            case 0: return R.color.green;
            case 1: return R.color.one;
            case 2: return R.color.two;
            case 3: return R.color.three;
            case 4: return R.color.four;
            case 5: return R.color.five;
            case 6: return R.color.six;
            case 7: return R.color.seven;
            case 8: return R.color.eight;
        }
        return R.color.green;
    }

    private void showTile(CellState state){

        String text = (mMineCell.getMineValue()==9)?"*":String.valueOf(mMineCell.getMineValue());
        int color = android.R.color.black;
        switch (state){
           case MINE:{
               color = R.color.red;
               text = "";
               mTextView.setBackgroundResource(R.drawable.animation_explosion);
               AnimationDrawable explosion = (AnimationDrawable)mTextView.getBackground();
               explosion.start();
           }break;
            case GRASS:{
                if (!BoardController.CHEAT_MODE) {
                    text = "#";
                    color = R.color.green;
                } else {
                    color = R.color.cheat_color;
                }
            }break;
            case FLAG:{
                text="F";
                color = R.color.flag;
            }break;
            case WRONG:{
                text="X";
                color = R.color.magenta;
            }break;
            case NUMBER:{
                int j=mMineCell.getMineValue();
                color = getColorForNumber(j);
            }break;
        }
        mTextView.setText(text);
        mTextView.setTextColor(mParent.getContext().getResources().getColor(color));
    }

    public void updateView(){

        if (mMineCell.isVisible()){
            if (mMineCell.isMine()){
                if (!mMineCell.isFlagged()){
                    showTile(CellState.MINE);
                } else {
                    showTile(CellState.FLAG);
                }
            } else if(mMineCell.didCauseGameOver() || (mMineCell.isVisible() && mMineCell.isFlagged() && !mMineCell.isMine())) {
                showTile(CellState.WRONG);
            } else {
                showTile(CellState.NUMBER);
            }
        } else if (mMineCell.isFlagged()){
            showTile(CellState.FLAG);
        } else {
            showTile(CellState.GRASS);
        }
    }
    public void open(){
        updateView();
        mParent.startAnimation(mScaleAnimation);
    }
}
