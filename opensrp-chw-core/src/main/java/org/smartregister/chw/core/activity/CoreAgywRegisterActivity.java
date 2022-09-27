package org.smartregister.chw.core.activity;

import android.os.Bundle;

import org.json.JSONObject;
import org.smartregister.chw.agyw.activity.BaseAGYWRegisterActivity;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.fragment.CoreAgywRegisterFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class CoreAgywRegisterActivity extends BaseAGYWRegisterActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(CoreConstants.DrawerMenu.AGYW);
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new CoreAgywRegisterFragment();
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        startActivityForResult(FormUtils.getStartFormActivity(jsonForm, null, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
}
