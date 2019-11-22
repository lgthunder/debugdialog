package com.lei.core.ui;

import android.app.AlertDialog;
import android.content.Context;

public class SimpleListDialogFactory implements DialogFactory {
    @Override
    public Dialog createDialog(Context context) {
        return new SimpleListDialog(context);
    }

   public class SimpleListDialog implements Dialog{
       AlertDialog.Builder listDialog;
        public SimpleListDialog(Context context){
             listDialog = new AlertDialog.Builder(context);
        }

       @Override
       public void show() {
           listDialog.show();
       }

       @Override
       public void setTitle(String title) {
           listDialog.setTitle(title);
       }

       @Override
       public void setItems(String[] items, Function<Dialog, Integer> function) {
           listDialog.setItems(items, (dialogInterface, i) -> function.apply(SimpleListDialog.this,i));
       }

       @Override
       public void setOnCancelListener(Function1<Dialog> o) {
           listDialog.setOnCancelListener(dialogInterface -> o.apply(SimpleListDialog.this));
       }

       @Override
       public void setOnDismissListener(Function1<Dialog> o) {
           listDialog.setOnDismissListener(dialogInterface -> o.apply(SimpleListDialog.this));
       }
   }
}
