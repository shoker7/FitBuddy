package de.avalax.fitbuddy.runner;

import android.support.test.rule.ActivityTestRule;

import java.util.List;

import javax.inject.Inject;

import de.avalax.fitbuddy.application.edit.workout.EditWorkoutApplicationService;
import de.avalax.fitbuddy.application.workout.WorkoutApplicationService;
import de.avalax.fitbuddy.domain.model.workout.Workout;
import de.avalax.fitbuddy.presentation.MainActivity;

import static de.avalax.fitbuddy.runner.TestFitbuddyApplication.TestComponent;

public class FitbuddyActivityTestRule extends ActivityTestRule<MainActivity> {
    @Inject
    EditWorkoutApplicationService editWorkoutApplicationService;
    @Inject
    WorkoutApplicationService workoutApplicationService;

    public FitbuddyActivityTestRule(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();
        TestFitbuddyApplication application = (TestFitbuddyApplication) getActivity().getApplication();
        ((TestComponent) application.getComponent()).inject(this);
    }

    public void deleteWorkouts() throws Exception {
        List<Workout> workouts = editWorkoutApplicationService.loadAllWorkouts();
        for (Workout workout : workouts) {
            editWorkoutApplicationService.deleteWorkout(workout);
        }
        workoutApplicationService.finishCurrentWorkout();
    }
}
