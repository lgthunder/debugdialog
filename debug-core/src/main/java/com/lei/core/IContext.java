package com.lei.core;

public interface IContext {
    Object[] getPageList();
    Object getCurrentPage();

    DialogFactory getDialogFactory();


}
