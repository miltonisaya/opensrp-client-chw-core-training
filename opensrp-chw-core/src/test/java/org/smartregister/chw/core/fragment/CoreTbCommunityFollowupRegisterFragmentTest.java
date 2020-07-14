package org.smartregister.chw.core.fragment;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.core.BaseUnitTest;

import java.util.Set;

public class CoreTbCommunityFollowupRegisterFragmentTest extends BaseUnitTest {

    @Mock
    private CoreTbCommunityFollowupRegisterFragment coreTbCommunityFollowupRegisterFragment;

    @Mock
    private View view;

    @Mock
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetupViews() {
        Mockito.doNothing().when(coreTbCommunityFollowupRegisterFragment).setupViews(view);
        coreTbCommunityFollowupRegisterFragment.setupViews(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(coreTbCommunityFollowupRegisterFragment, Mockito.times(1)).setupViews(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }

    @Test
    public void testInitializeAdapter() {
        Mockito.doNothing().when(coreTbCommunityFollowupRegisterFragment).initializeAdapter(visibleColumns);
        coreTbCommunityFollowupRegisterFragment.initializeAdapter(visibleColumns);

        ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(coreTbCommunityFollowupRegisterFragment, Mockito.times(1)).initializeAdapter(captor.capture());
        Assert.assertEquals(captor.getValue(), visibleColumns);
    }

    
    @Test
    public void testOnViewClicked() {
        Mockito.doNothing().when(coreTbCommunityFollowupRegisterFragment).onViewClicked(view);
        coreTbCommunityFollowupRegisterFragment.onViewClicked(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(coreTbCommunityFollowupRegisterFragment, Mockito.times(1)).onViewClicked(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }

    @Test
    public void testDueFilters() {
        Mockito.doNothing().when(coreTbCommunityFollowupRegisterFragment).dueFilter(view);
        coreTbCommunityFollowupRegisterFragment.dueFilter(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(coreTbCommunityFollowupRegisterFragment, Mockito.times(1)).dueFilter(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }

    @Test
    public void testToggleFilterSelection() {
        Mockito.doNothing().when(coreTbCommunityFollowupRegisterFragment).toggleFilterSelection(view);
        coreTbCommunityFollowupRegisterFragment.toggleFilterSelection(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(coreTbCommunityFollowupRegisterFragment, Mockito.times(1)).toggleFilterSelection(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }
    
    @Test
    public void testFilterDue() {
        String filterString = "filterString";
        String joinTableString = "joinTableString";
        String mainConditionString = "mainConditionString";
        Mockito.doNothing().when(coreTbCommunityFollowupRegisterFragment).filterDue(filterString, joinTableString, mainConditionString);
        coreTbCommunityFollowupRegisterFragment.filterDue(filterString, joinTableString, mainConditionString);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(coreTbCommunityFollowupRegisterFragment, Mockito.times(1)).filterDue(captor.capture(), captor1.capture(), captor2.capture());
        Assert.assertEquals(captor.getValue(), filterString);
        Assert.assertEquals(captor1.getValue(), joinTableString);
        Assert.assertEquals(captor2.getValue(), mainConditionString);
    }
    
    @Test
    public void testNormalFilter() {
        Mockito.doNothing().when(coreTbCommunityFollowupRegisterFragment).normalFilter(view);
        coreTbCommunityFollowupRegisterFragment.normalFilter(view);

        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(coreTbCommunityFollowupRegisterFragment, Mockito.times(1)).normalFilter(captor.capture());
        Assert.assertEquals(captor.getValue(), view);
    }


}
