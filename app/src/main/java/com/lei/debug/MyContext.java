package com.lei.debug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.lei.core.DialogFactory;
import com.lei.core.IContext;

public class MyContext implements IContext {
    @Override
    public Object[] getPageList() {
        return new Object[]{MyApplication.mObject};
    }

    @Override
    public Object getCurrentPage() {
        return MyApplication.mObject;
    }

    @Override
    public DialogFactory getDialogFactory() {
        return () -> {
            final AlertDialog.Builder listDialog = new AlertDialog.Builder((Context) getCurrentPage());
            return new DialogFactory.Dialog() {
                @Override
                public void show() {
                    listDialog.show();
                }

                @Override
                public void setTitle(String title) {
                    listDialog.setTitle(title);
                }

                @Override
                public void setItems(String[] items, final DialogFactory.Function<DialogFactory.Dialog, Integer> function) {
                    listDialog.setItems(items, (dialogInterface, i) -> function.apply(null, i));
                }

                @Override
                public void setOnCancelListener(final DialogFactory.Function1<DialogFactory.Dialog> o) {
                    listDialog.setOnCancelListener(dialogInterface -> o.apply(null));
                }

                @Override
                public void setOnDismissListener(final DialogFactory.Function1<DialogFactory.Dialog> o) {
                    listDialog.setOnDismissListener(dialogInterface -> o.apply(null));
                }
            };
        };
    }
}
