package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Builder<T> {

    final DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public T build();
}
