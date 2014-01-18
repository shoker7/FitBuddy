package de.avalax.fitbuddy.app;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import de.avalax.fitbuddy.core.workout.Workout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.FragmentTestUtil;

import static de.avalax.fitbuddy.app.Asserts.assertOnClickListenerRegistered;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class FinishWorkoutFragmentTest {
    @InjectMocks
    private FinishWorkoutFragment finishWorkoutFragment;
    @Mock
    private Workout workout;
    @Mock
    private WorkoutSession workoutSession;
    @Mock
    private ViewPager viewPager;
    @Mock
    private PagerAdapter pagerAdapter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(viewPager.getAdapter()).thenReturn(pagerAdapter);
    }

    @Test
    public void shouldRegisterOnClickEvents() throws Exception {
        FragmentTestUtil.startFragment(finishWorkoutFragment);

        assertOnClickListenerRegistered(finishWorkoutFragment.buttonFinishWorkout);
        assertOnClickListenerRegistered(finishWorkoutFragment.buttonAddWorkout);
        assertOnClickListenerRegistered(finishWorkoutFragment.buttonSwitchWorkout);
    }

    @Test
    public void testFinishWorkout() throws Exception {
        finishWorkoutFragment.finishWorkout();

        InOrder inOrder = inOrder(workoutSession, pagerAdapter,viewPager);
        inOrder.verify(workoutSession).finishWorkout();
        inOrder.verify(pagerAdapter).notifyDataSetChanged();
        inOrder.verify(viewPager).invalidate();
        inOrder.verify(viewPager).setCurrentItem(1,true);
    }
}