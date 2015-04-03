package com.balsdon.minesweeper.view;

public abstract class ValidatorInterface<T> {
    public abstract Integer isValid(T Value);

    protected boolean isRequired;
    public ValidatorInterface(){
        isRequired = true;
    }

    public ValidatorInterface(boolean required){
        isRequired = required;
    }
}
