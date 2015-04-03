package com.balsdon.minesweeper.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class ValidateEditText extends EditText implements TextWatcher {

    private ValidatorInterface<String> mValidator;
    private Context mContext;

    public ValidateEditText setValidator(ValidatorInterface<String> validator){
        mValidator = validator;
        return this;
    }

    public ValidateEditText(Context context) {
        super(context);
        mContext = context;
        this.addTextChangedListener(this);

    }

    public ValidateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.addTextChangedListener(this);
    }

    public ValidateEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        this.addTextChangedListener(this);
    }

    public boolean isValid(){

        performValidation();
        return getError() == null;
    }

    private void performValidation(){
        if (mValidator != null) {
            Integer errorRef = mValidator.isValid(getText().toString());
            if (errorRef != null) {
                setError(mContext.getString(errorRef));
            }
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused && getText().toString().length() == 0) return;

        performValidation();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        setError(null);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

}

