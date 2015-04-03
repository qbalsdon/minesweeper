package com.balsdon.minesweeper.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.balsdon.minesweeper.R;
import com.balsdon.minesweeper.controller.BoardController;
import com.balsdon.minesweeper.model.MineCell;

/***
 * LEFT IN TO SHOW THAT I TRIED TO USE IT.
 */

public class BoardAdapter extends BaseAdapter {

    private BoardController mBoardController;
    private Context mContext;
    private LayoutInflater mInflater;

    public BoardAdapter(Context context, BoardController boardController){
        mBoardController = boardController;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mBoardController == null){
            return 0;
        }
        return mBoardController.getTotalSize();
    }

    @Override
    public Object getItem(int i) {
        return mBoardController.getItemSequentially(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MineCell mc = mBoardController.getItemSequentially(i);
        if (view == null){
            view = mInflater.inflate(R.layout.grid_view_item, viewGroup, false);
            mc.setView(view);
        }
        return view;
    }
}
