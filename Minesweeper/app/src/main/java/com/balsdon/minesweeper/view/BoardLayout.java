package com.balsdon.minesweeper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.balsdon.minesweeper.R;
import com.balsdon.minesweeper.controller.BoardController;
import com.balsdon.minesweeper.model.MineCell;

/**
 * CUSTOM VIEW FOR DISPLAYING THE GAME BOARD
 * This is necessary because the BoardAdapter introduces complexity for a number of reasons:
 *    1. View recycling : When the views are constantly being recycled, [0,0] will lose it's
 *                        ViewHolder if opened as a result of other views opening. The minus
 *                        point for this view is that the views are not recycled.
 *    2. Spacing        : BoardAdapter will fit to the width of the view, making larger
 *                        boards ugly. The views squash next to each other.
 *    3. Scrolling      : Larger boards need scrolling on both axis
*/

public class BoardLayout extends LinearLayout {

    private BoardController mBoardController;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemTap mTapListener;

    public interface OnItemTap{
        public void onLongTap(int row, int col);
        public void onTap(int row, int col);
    }

    private void init(Context context){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setOrientation(LinearLayout.VERTICAL);
        setWillNotDraw(false);
    }

    public BoardLayout(Context context) {
        super(context);
        init(context);
    }

    public BoardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BoardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setController(BoardController boardController, OnItemTap listener){
        mBoardController = boardController;
        mTapListener = listener;
        removeAllViews();
        setupView();
    }

    private void setupView(){
        LayoutParams LLParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        for (int j = 0; j < mBoardController.getHeight(); j++){
            LinearLayout row = new LinearLayout(mContext);
            row.setLayoutParams(LLParams);
            for (int i = 0; i < mBoardController.getWidth(); i++){
                final MineCell mc = mBoardController.getItem(i, j);
                View view = mInflater.inflate(R.layout.grid_view_item, row, false);
                mc.setView(view);

                final int r = i;
                final int c = j;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mTapListener.onTap(r, c);
                    }
                });

                view.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mTapListener.onLongTap(r, c);
                        return true;
                    }
                });

                row.addView(view);
            }
            addView(row);
        }
    }
}
