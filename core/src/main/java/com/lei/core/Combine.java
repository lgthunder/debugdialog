package com.lei.core;

public class Combine {
    private Action action;
    private Object target;

    public Combine(Action action, Object target) {
        this.action = action;
        this.target = target;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}