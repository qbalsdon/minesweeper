package com.balsdon.minesweeper.controller;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.balsdon.minesweeper.model.MineCell;

import java.util.Calendar;
import java.util.Random;

public class BoardController implements Parcelable {
    public static int DEFAULT_DIMEN = 8;
    public static int DEFAULT_BOMBS = 10;
    public static int RECURSIVE_DELAY = 100;

    public static boolean CHEAT_MODE = false;

    private int mWidth, mHeight, mBombs, mFlags, mCounter;

    private MineCell[][] mBoardData;
    private boolean GAME_OVER = false;

    private Handler mDelayHandler;

    public interface GameEventHandler{
        public void onGameOver(boolean success);
        public void updateFlagCount(int flagsLeft);
    }

    private GameEventHandler mGameEventHandler;

    public BoardController(GameEventHandler gameEventHandler){
        this(gameEventHandler,DEFAULT_DIMEN,DEFAULT_DIMEN,DEFAULT_BOMBS);
    }

    public BoardController(GameEventHandler gameEventHandler,int width, int height, int bombs) {
        GAME_OVER = false;
        mGameEventHandler = gameEventHandler;
        mDelayHandler = new Handler();
        mWidth = width;
        mHeight = height;
        mBombs = bombs;
        mCounter = (mWidth * mHeight) - mBombs;
        mBoardData = new MineCell[width][height];
        //set up bombs
        for (int i = 0; i < mWidth; i++) {
            for (int j = 0; j < mHeight; j++) {
                mBoardData[i][j] = new MineCell(false);
            }
        }
        int count = bombs;
        Random rand = new Random(Calendar.getInstance().getTimeInMillis());
        while (count > 0) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);

            if (!mBoardData[x][y].isMine()) {
                mBoardData[x][y].makeMine();
                updateMines(x, y, false);
                count--;
            }
        }
        mFlags = 0;
        mGameEventHandler.updateFlagCount(mBombs - mFlags);
    }

    private void updateMines(int x, int y, int radius, Boolean open){
        for (int row = x-radius; row <= x+radius; row++){
            if (row < 0 || row >= mWidth) continue;
            for (int col = y-radius; col <= y+radius; col++){
                if (col < 0 || col >= mHeight) continue;
                if (row == x && col == y) continue;
                if (open == null) {
                    pulse(row, col, Math.min(Math.abs(row - x), Math.abs(col - y)));
                }
                else if (!open) {
                    mBoardData[row][col].increment();
                }
                else {
                    open(row, col);
                }
            }
        }
    }
    private void updateMines(int x, int y, boolean open){
        updateMines(x,y,1,open);
    }

    public int getTotalSize() {
        return mWidth * mHeight;
    }

    public void setGameEventHandler(GameEventHandler geh){
        mGameEventHandler = geh;
        if (mGameEventHandler != null){
            mGameEventHandler.updateFlagCount(mBombs - mFlags);
        }
    }

    public MineCell getItem(int row, int col) {
        return mBoardData[row][col];
    }

    public MineCell getItemSequentially(int index) {
        int col = index % mWidth;
        int row = index / mWidth;
        return mBoardData[row][col];
    }

    public void pulse(final int x, final int y, int distance){
        mDelayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBoardData[x][y].setVisible();
                mBoardData[x][y].pulse();
            }
        }, RECURSIVE_DELAY * distance);
    }

    public void open(final int x, final int y){
        if (isGameOver()) return;
        if (mBoardData[x][y].isFlagged() || mBoardData[x][y].isVisible()) return;
        if (mBoardData[x][y].isMine()) {
            updateMines(x, y, Math.max(mWidth, mHeight), null);
            GAME_OVER = true;
            if (mGameEventHandler != null){
                mGameEventHandler.onGameOver(false);
            }
            mBoardData[x][y].causedGameOver();
        }
        if (mBoardData[x][y].getMineValue() == 0 && !mBoardData[x][y].isVisible()){
            mCounter-=mBoardData[x][y].setVisible()?1:0;
            mDelayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateMines(x, y, true);
                }
            }, RECURSIVE_DELAY);
        } else {
            mCounter-=mBoardData[x][y].setVisible()?1:0;
        }

        if (mCounter == 0){
            GAME_OVER = true;
            mGameEventHandler.onGameOver(true);
        }
    }

    public boolean validate(){
        if (mFlags < mBombs) return false;
        for (int i = 0; i < mWidth; i++){
            for(int j = 0; j < mHeight; j++){
                if (mBoardData[i][j].isFlagged() && !mBoardData[i][j].isMine()){
                    GAME_OVER = true;
                    mBoardData[i][j].causedGameOver();
                }
                mBoardData[i][j].setVisible();
            }
        }
        if (!GAME_OVER) {
            mGameEventHandler.onGameOver(true);
        } else {
            mGameEventHandler.onGameOver(false);
        }
        GAME_OVER = true;
        return true;
    }

    public void flag(int x, int y){
        if (mFlags == mBombs) return;

        mBoardData[x][y].setFlagged();
        mFlags+=(mBoardData[x][y].isFlagged())?1:-1;
        mGameEventHandler.updateFlagCount(mBombs - mFlags);
    }

    public int getWidth() {
        return mWidth;
    }
    public int getHeight() {
        return mHeight;
    }

    public boolean isGameOver(){
        return GAME_OVER;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mWidth);
        parcel.writeInt(mHeight);
        parcel.writeInt(mBombs);
        parcel.writeInt(mCounter);
        parcel.writeInt(mFlags);
        for (int i=0;i< mWidth;i++){
            for (int j = 0; j < mHeight; j++) {
                parcel.writeParcelable(mBoardData[i][j], flags);
            }
        }
    }

    public BoardController(Parcel in){
        GAME_OVER = false;
        mWidth = in.readInt();
        mHeight = in.readInt();
        mBombs = in.readInt();
        mCounter = in.readInt();
        mFlags = in.readInt();
        mBoardData = new MineCell[mWidth][mHeight];
        mDelayHandler = new Handler();
        for (int i = 0; i < mWidth; i++){
            for (int j = 0; j < mHeight; j++) {
                mBoardData[i][j] = in.readParcelable(MineCell.class.getClassLoader());
            }
        }

        mGameEventHandler.updateFlagCount(mBombs - mFlags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BoardController createFromParcel(Parcel in) {
            return new BoardController(in);
        }

        public BoardController[] newArray(int size) {
            return new BoardController[size];
        }
    };
}
