package com.balsdon.minesweeper.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.balsdon.minesweeper.view.MineViewHolder;

public class MineCell implements Parcelable{

    public static int MAX = 8;
    public static int MINE = 9;

    private int mCellValue;
    private boolean mVisible = false;
    private boolean mFlagged = false;
    private MineViewHolder mViewHolder;
    private boolean mIncorrectFlag;

    public void setFlagged(){
        mFlagged = (mVisible)?false:!mFlagged;
        mViewHolder.updateView();
    }

    public boolean setVisible(){
        boolean animate = !mVisible;
        mVisible = true;
        if (animate && mViewHolder!=null) {
            mViewHolder.open();
        }

        return animate;
    }

    public void pulse(){
        mViewHolder.open();
    }

    public int getMineValue(){
        return mCellValue;
    }

    public MineCell(boolean isMine){
        if (isMine){
            mCellValue = MINE;
        } else {
            mCellValue = 0;
        }
        if (mViewHolder!=null)mViewHolder.updateView();
    }

    public boolean isFlagged(){
        return mFlagged;
    }
    public boolean isVisible(){
        return mVisible;
    }

    public boolean isMine(){
        return mCellValue == MINE;
    }

    public void makeMine(){
        mCellValue = MINE;
        if (mViewHolder!=null)mViewHolder.updateView();
    }
    public void makeZero(){
        mCellValue = 0;
        if (mViewHolder!=null)mViewHolder.updateView();
    }

    public void increment(){
        if (mCellValue < MAX){
            mCellValue++;
        }
        if (mViewHolder!=null)mViewHolder.updateView();
    }

    public void decrement(){
        if (mCellValue > 0 && mCellValue != MINE){
            mCellValue--;
        }
        if (mViewHolder!=null)mViewHolder.updateView();
    }

    public MineCell(Parcel in){
        mCellValue = in.readInt();
        mVisible = Boolean.valueOf(in.readString());
        mFlagged = Boolean.valueOf(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCellValue);
        dest.writeString(String.valueOf(mVisible));
        dest.writeString(String.valueOf(mFlagged));
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MineCell createFromParcel(Parcel in) {
            return new MineCell(in);
        }

        public MineCell[] newArray(int size) {
            return new MineCell[size];
        }
    };

    public void setView(View view){
        if (mViewHolder == null)
            mViewHolder = new MineViewHolder(this, view);
        else
            mViewHolder.setView(view);
    }

    public void causedGameOver(){
        mIncorrectFlag = true;
    }

    public boolean didCauseGameOver(){
        return mIncorrectFlag;
    }
}
