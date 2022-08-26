package org.smartregister.chw.core.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.widget.TextView;

import org.json.JSONObject;
import org.smartregister.chw.cdp.activity.BaseCdpProfileActivity;
import org.smartregister.chw.cdp.contract.BaseCdpProfileContract;
import org.smartregister.chw.cdp.interactor.BaseCdpProfileInteractor;
import org.smartregister.chw.cdp.presenter.BaseCdpProfilePresenter;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.util.JsonFormUtils;

import androidx.annotation.Nullable;

import static org.smartregister.chw.core.utils.Utils.updateToolbarTitle;

/**
 * Created by Billy on 26/08/2022.
 */
public class CoreCdpProfileActivity extends BaseCdpProfileActivity implements
        FamilyOtherMemberProfileExtendedContract.View, BaseCdpProfileContract.View, FamilyProfileExtendedContract.PresenterCallBack {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        updateToolbarTitle(this, R.id.toolbar_title, memberObject.getFamilyName());
    }


    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        profilePresenter = new BaseCdpProfilePresenter(this, new BaseCdpProfileInteractor(), memberObject);
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = org.smartregister.chw.core.utils.Utils.formActivityIntent(this, jsonForm.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }



    @Override
    public void refreshList() {
        // Implement
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        // Implement
    }

    @Override
    public void setFamilyServiceStatus(String status) {
        // Implement
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void verifyHasPhone() {
        // Implement
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {
        // Implement
    }

    @Override
    public FamilyOtherMemberContract.Presenter presenter() {
        return null;
    }

    @Override
    public void setProfileImage(String s, String s1) {
        // Implement
    }

    @Override
    public void setProfileName(String s) {
        TextView tvName = findViewById(R.id.textview_name);
        tvName.setText(s);
    }

    @Override
    public void setProfileDetailOne(String s) {
        TextView tvGender = findViewById(R.id.textview_gender);
        tvGender.setText(s);
    }

    @Override
    public void setProfileDetailTwo(String s) {
        TextView tvAddress = findViewById(R.id.textview_address);
        tvAddress.setText(s);
    }

    @Override
    public void setProfileDetailThree(String s) {
        // Implement
    }

    @Override
    public void toggleFamilyHead(boolean b) {
        // Implement
    }

    @Override
    public void togglePrimaryCaregiver(boolean b) {
        // Implement
    }
}
