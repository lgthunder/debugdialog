package com.lei.core.page;

import android.app.Application;

public interface Component {
    Object[] getPageList();
    Object getCurrentPage();
    Application getApplication();
}
