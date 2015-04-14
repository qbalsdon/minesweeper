package com.balsdon.minesweeper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balsdon.minesweeper.controller.BoardController;
import com.balsdon.minesweeper.view.BoardLayout;
import com.balsdon.minesweeper.view.NumberValidator;
import com.balsdon.minesweeper.view.ValidateEditText;
import com.crashlytics.android.Crashlytics;


public class MineActivity extends Activity {

    private static String BOARD = "BOARD";
    private static String WINS = "WINS";
    private static String LOSSES = "LOSSES";

    private BoardLayout mMineGrid;
    private BoardController mBoardController;
    private TextView mFlagCountText;
    private TextView mWins;

    private TextView mLosses;

    private void initFlagCounter(){
        mFlagCountText = (TextView)findViewById(R.id.activity_mine_flag_counter);
        mFlagCountText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!BoardController.CHEAT_MODE)
                    Toast.makeText(MineActivity.this, "CHEATER!", Toast.LENGTH_SHORT).show();

                BoardController.CHEAT_MODE = !BoardController.CHEAT_MODE;
                updateBoard();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWins = (TextView)findViewById(R.id.activity_mine_wins);
        mLosses = (TextView)findViewById(R.id.activity_mine_losses);
        updateStats();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        initFlagCounter();
        mMineGrid = (BoardLayout)findViewById(R.id.activity_mine_mine_grid);

        if (savedInstanceState != null){
            mBoardController = savedInstanceState.getParcelable(BOARD);
            if (mBoardController == null) {
                createNewGame();
                return;
            }
            mBoardController.setGameEventHandler(mGameHandler);
            updateBoard();
            return;
        }
        findViewById(R.id.activity_mine_validate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBoardController.isGameOver()){
                    createNewGame();
                    return;
                }
                if(!mBoardController.validate()){
                    Toast.makeText(MineActivity.this, getString(R.string.not_enough_flags), Toast.LENGTH_SHORT).show();
                }
            }
        });

        createNewGame();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null) return;
        super.onSaveInstanceState(outState);
        if (!mBoardController.isGameOver()) {
            Crashlytics.log(String.format("SAVING BOARD [%d] width [%d] height [%d] bombs",mBoardController.getWidth(), mBoardController.getHeight(), mBoardController.getBombs()));
            outState.putParcelable(BOARD, mBoardController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_about: {
                showAbout();
            }
            break;
            case R.id.action_new_game: {
                askIfSure(false);
            }
            break;
            case R.id.action_custom:{
                askIfSure(true);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewGame() {
        ((ImageButton)findViewById(R.id.activity_mine_validate)).setImageResource(R.drawable.validate_button);
        findViewById(R.id.dialog).setVisibility(View.GONE);
        mBoardController = new BoardController(mGameHandler);
        updateBoard();
    }

    private void updateBoard() {
        mMineGrid.setController(mBoardController, new BoardLayout.OnItemTap() {
            @Override
            public void onLongTap(int row, int col) {
                mBoardController.flag(row, col);
            }

            @Override
            public void onTap(int row, int col) {
                mBoardController.open(row, col);
            }
        });
    }

    private LinearLayout makeScreen(int layoutResourceId){
        return makeScreen(layoutResourceId, Color.BLACK);
    }

    private LinearLayout makeScreen(int layoutResourceId, int col){
        LinearLayout layout = (LinearLayout)findViewById(R.id.dialog);
        layout.removeAllViews();
        View content = getLayoutInflater().inflate(layoutResourceId, layout, false);

        layout.addView(content);

        layout.setBackgroundColor(col);
        return layout;
    }

    private Dialog makeDialog(int layoutResourceId){
        return makeDialog(layoutResourceId, false);
    }

    private Dialog makeDialog(int layoutResourceId, boolean isTransparent){
        Dialog dialog = new Dialog(MineActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutResourceId);
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        if (isTransparent) window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private BoardController.GameEventHandler mGameHandler = new BoardController.GameEventHandler() {
        @Override
        public void onGameOver(boolean success) {
            SharedPreferences preferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            String ref= (success)?WINS:LOSSES;
            editor.putInt(ref,preferences.getInt(ref,0)+1);
            editor.apply();
            updateStats();

            LinearLayout dialog = makeScreen(R.layout.dialog_finish, Color.TRANSPARENT);

            dialog.findViewById(R.id.dialog_image_win).setVisibility((success)?View.VISIBLE:View.GONE);
            dialog.setVisibility(View.VISIBLE);

            ((ImageButton)findViewById(R.id.activity_mine_validate)).setImageResource((success) ? R.drawable.win : R.drawable.lose);
        }

        @Override
        public void updateFlagCount(int flagsLeft) {
            mFlagCountText.setText(String.format("%s: %s",getString(R.string.flags_left),flagsLeft));
        }
    };

    private int getValidNumber(TextView textView, int defaultValue){
        if (textView == null ||
                textView.getText() == null ||
                textView.getText().toString() == null ||
                textView.getText().toString().length() == 0){
            return defaultValue;
        }
        return Integer.parseInt(textView.getText().toString());
    }

    private void createCustomGame(){
        final Dialog dialog = makeDialog(R.layout.dialog_custom_game);
        ((ValidateEditText)dialog.findViewById(R.id.dialog_custom_game_width)).setValidator(new NumberValidator(4,50));
        ((ValidateEditText)dialog.findViewById(R.id.dialog_custom_game_height)).setValidator(new NumberValidator(4,50));
        ((ValidateEditText)dialog.findViewById(R.id.dialog_custom_game_bombs)).setValidator(new NumberValidator(1, Integer.MAX_VALUE));

        dialog.findViewById(R.id.dialog_custom_game_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateEditText width = (ValidateEditText)dialog.findViewById(R.id.dialog_custom_game_width);
                ValidateEditText height = (ValidateEditText)dialog.findViewById(R.id.dialog_custom_game_height);
                ValidateEditText bombs = (ValidateEditText)dialog.findViewById(R.id.dialog_custom_game_bombs);

                if (width.isValid() && height.isValid() && bombs.isValid()) {

                    int iWidth, iHeight, iBombs;

                    iWidth = getValidNumber(width, BoardController.DEFAULT_DIMEN);
                    iHeight = getValidNumber(height, BoardController.DEFAULT_DIMEN);
                    iBombs = getValidNumber(bombs, BoardController.DEFAULT_BOMBS);

                    if (iBombs <= ((iWidth * iHeight) / 2)) {

                        mBoardController = new BoardController(mGameHandler, iWidth, iHeight, iBombs);
                        updateBoard();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MineActivity.this, getString(R.string.too_hard), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
    }

    private void showAbout(){
        final Dialog dialog = makeDialog(R.layout.dialog_about);

        StringBuilder stringBuilder = new StringBuilder();
        String[] refs = getResources().getStringArray(R.array.thank_you);
        for (String str : refs){
            stringBuilder.append(str);
            stringBuilder.append("\r\n\r\n");
        }

        ((TextView)dialog.findViewById(R.id.dialog_about_references)).setText(stringBuilder.toString());

        dialog.findViewById(R.id.dialog_custom_game_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void askIfSure(final boolean customNewGame){
        if (mBoardController.isGameOver()) {
            findViewById(R.id.dialog).setVisibility(View.GONE);
            if (customNewGame)
                createCustomGame();
            else
                createNewGame();
            return;
        }

        final LinearLayout dialog = makeScreen(R.layout.dialog_are_you_sure);

        dialog.findViewById(R.id.dialog_sure_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customNewGame)
                    createCustomGame();
                else
                    createNewGame();
                findViewById(R.id.dialog).setVisibility(View.GONE);
            }
        });

        dialog.findViewById(R.id.dialog_sure_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.dialog).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.dialog).setVisibility(View.VISIBLE);
    }

    private void updateStats(){
        SharedPreferences sharedpreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mWins.setText(String.valueOf(sharedpreferences.getInt(WINS, 0)));
        mLosses.setText(String.valueOf(sharedpreferences.getInt(LOSSES, 0)));
    }
}
