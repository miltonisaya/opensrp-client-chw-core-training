package org.smartregister.chw.core.interactor;

import org.smartregister.chw.core.contract.CoreFamilyPlanningMemberProfileContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.fp.interactor.BaseFpProfileInteractor;
import org.smartregister.repository.AllSharedPreferences;

public class CoreFamilyPlanningProfileInteractor extends BaseFpProfileInteractor implements CoreFamilyPlanningMemberProfileContract.Interactor {

    @Override
    public void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String entityID) throws Exception {
        CoreReferralUtils.createReferralEvent(allSharedPreferences, jsonString, CoreConstants.TABLE_NAME.FP_REFERRAL, entityID);
    }
}