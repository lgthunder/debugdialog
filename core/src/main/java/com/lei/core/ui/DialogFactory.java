package com.lei.core.ui;

import android.content.Context;

public interface DialogFactory {

    Dialog createDialog(Context context);

    interface Dialog {
        void show();

        void setTitle(String title);

        void setItems(String[] items,Function<Dialog,Integer> function);

        void setOnCancelListener(Function1<Dialog> o);

        void setOnDismissListener(Function1<Dialog> o);
    }

    interface Function<T, T2> {
        void apply(T t, T2 t2);
    }

    interface Function1<T> {
        void apply(T t);
    }
}
