package org.smartregister.chw.core.provider;

import android.content.Context;
import android.view.View;

import org.smartregister.chw.hivst.provider.BaseHivstRegisterProvider;

import java.util.Set;

public class CoreHivstRegisterProvider extends BaseHivstRegisterProvider {

    private Context context;

    public CoreHivstRegisterProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener, Set visibleColumns) {
        super(context, paginationClickListener, onClickListener, visibleColumns);
        this.context = context;
    }
}
