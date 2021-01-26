package org.smartregister.chw.core.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.core.BaseUnitTest;
import org.smartregister.chw.core.shadows.ShadowReferralTaskViewActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Period;
import org.smartregister.domain.Task;
import org.smartregister.family.util.DBConstants;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.HashMap;
import java.util.Map;

public class BaseReferralTaskViewActivityTest extends BaseUnitTest {

    private ActivityController<ShadowReferralTaskViewActivity> controller;
    private BaseReferralTaskViewActivity referralTaskViewActivity;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.application;
        Intent intent = new Intent(context, ShadowReferralTaskViewActivity.class);
        intent.putExtra(CoreConstants.INTENT_KEY.USERS_TASKS, getTestTask());
        controller = Robolectric.buildActivity(ShadowReferralTaskViewActivity.class, intent).create().start();
        referralTaskViewActivity = controller.get();
        CommonPersonObjectClient commonPersonObject = new CommonPersonObjectClient("case-id", new HashMap<>(), "StoneField Mack");
        BaseReferralTaskViewActivity.setPersonObjectClient(commonPersonObject);
    }

    @Test
    public void canGetThePersonObjectClient() {
        Assert.assertNotNull(referralTaskViewActivity.getPersonObjectClient());
    }

    @Test
    public void canSetTaskDetailsFromStartActivityIntent() {
        referralTaskViewActivity = Mockito.spy(referralTaskViewActivity);
        referralTaskViewActivity.extraClientTask();
        Mockito.verify(referralTaskViewActivity, Mockito.times(1)).setTask(ArgumentMatchers.any(Task.class));
    }

    @Test
    public void canGetTheInitialisedTask() {
        referralTaskViewActivity.extraClientTask();
        Assert.assertNotNull(referralTaskViewActivity.getTask());
        Assert.assertEquals("192919-test-code", referralTaskViewActivity.getTask().getCode());
    }

    @Test
    public void updateProblemDisplaySetsReferralProblem() throws Exception {
        CustomFontTextView clientReferralProblem = Mockito.mock(CustomFontTextView.class);
        ReflectionHelpers.setField(referralTaskViewActivity, "clientReferralProblem", clientReferralProblem);
        Whitebox.invokeMethod(referralTaskViewActivity, "updateProblemDisplay");
        Mockito.verify(clientReferralProblem, Mockito.times(1)).setText(ArgumentMatchers.eq("ANC Danger Signs; Symptoms: ANC Signs"));
    }


    @Test
    public void getReferralDetailsSetsCorrectValues() {
        CustomFontTextView clientName = Mockito.mock(CustomFontTextView.class);
        CustomFontTextView referralDate = Mockito.mock(CustomFontTextView.class);
        CustomFontTextView careGiverLayout = Mockito.mock(CustomFontTextView.class);
        CustomFontTextView careGiverPhone = Mockito.mock(CustomFontTextView.class);
        CustomFontTextView chwDetailsNames = Mockito.mock(CustomFontTextView.class);
        LinearLayout womanGaLayout = Mockito.mock(LinearLayout.class);
        ReflectionHelpers.setField(referralTaskViewActivity, "clientName", clientName);
        ReflectionHelpers.setField(referralTaskViewActivity, "referralDate", referralDate);
        ReflectionHelpers.setField(referralTaskViewActivity, "careGiverLayout", careGiverLayout);
        ReflectionHelpers.setField(referralTaskViewActivity, "careGiverPhone", careGiverPhone);
        ReflectionHelpers.setField(referralTaskViewActivity, "chwDetailsNames", chwDetailsNames);
        ReflectionHelpers.setField(referralTaskViewActivity, "womanGaLayout", womanGaLayout);

        ReflectionHelpers.setField(referralTaskViewActivity, "name", "StoneField Mack");
        Map<String, String> columnMaps = new HashMap<>();
        columnMaps.put(DBConstants.KEY.DOB, "01-01-2020");

        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("case-id", new HashMap<>(), "StoneField Mack");
        commonPersonObjectClient.setColumnmaps(columnMaps);
        BaseReferralTaskViewActivity.setPersonObjectClient(commonPersonObjectClient);


        referralTaskViewActivity = Mockito.spy(referralTaskViewActivity);
        referralTaskViewActivity.getReferralDetails();
        Mockito.verify(referralTaskViewActivity, Mockito.times(1)).updateProblemDisplay();
        Mockito.verify(clientName, Mockito.times(1)).setText(ArgumentMatchers.anyString());
        Mockito.verify(referralDate, Mockito.times(1)).setText(ArgumentMatchers.anyString());
        Mockito.verify(referralTaskViewActivity, Mockito.times(1)).getFamilyMemberContacts();
        Mockito.verify(careGiverPhone, Mockito.times(1)).setText(ArgumentMatchers.anyString());
        Mockito.verify(chwDetailsNames, Mockito.times(1)).setText(ArgumentMatchers.anyString());
        Mockito.verify(referralTaskViewActivity, Mockito.times(1)).addGaDisplay();

        Assert.assertEquals(View.GONE, careGiverLayout.getVisibility());
    }


    public Task getTestTask() {
        Task task = new Task();
        task.setCode("192919-test-code");
        task.setFocus("ANC Danger Signs");
        task.setLocation("some-location");
        task.setGroupIdentifier("some-group-id");
        task.setDescription("ANC Signs");
        task.setExecutionPeriod(new Period(DateTime.now().minusDays(1), DateTime.now().plusDays(1)));
        return task;
    }

    public void tearDown() {
        try {
            controller.pause().stop().destroy(); //destroy controller if we can
            referralTaskViewActivity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}