package org.smartregister.chw.core.activity;

import android.view.Menu;
import android.view.View;

import org.smartregister.chw.kvp.activity.BaseKvpProfileActivity;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

/**
 * Created by Billy on 20/09/2022.
 */
public class CoreKvpProfileActivity extends BaseKvpProfileActivity {
    //TODO: implement startFormActivity, to load followup forms

    @Override
    public void refreshMedicalHistory(boolean hasHistory) {
        rlLastVisit.setVisibility(View.GONE);
    }

    @Override
    public void refreshFamilyStatus(AlertStatus status) {
        super.refreshFamilyStatus(status);
        rlFamilyServicesDue.setVisibility(View.GONE);
    }

    @Override
    public void refreshUpComingServicesStatus(String service, AlertStatus status, Date date) {
        rlUpcomingServices.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: enable the options menu after figuring out what needs to show up
        return false;
    }
}
