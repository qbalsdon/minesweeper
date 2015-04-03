package com.balsdon.minesweeper.view;

import com.balsdon.minesweeper.R;

public class NumberValidator extends ValidatorInterface<String> {

    private int mMinValue, mMaxValue;

    public NumberValidator(int minValue, int maxValue){
        mMinValue = minValue;
        mMaxValue = maxValue;
    }

    @Override
    public Integer isValid(String Value) {

        if (Value == null || Value.isEmpty()) return R.string.required;
        if (!Value.matches("^[0-9]*$")) return R.string.invalid_number;
        int v = Integer.parseInt(Value);
        if (v < mMinValue) return R.string.value_too_small;
        if (v > mMaxValue) return R.string.value_too_large;
        return null;
    }
}
