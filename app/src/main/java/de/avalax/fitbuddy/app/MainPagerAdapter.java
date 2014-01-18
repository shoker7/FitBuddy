package de.avalax.fitbuddy.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.google.inject.Inject;
import de.avalax.fitbuddy.app.resultChart.ResultChartFragment;
import de.avalax.fitbuddy.core.workout.Exercise;
import de.avalax.fitbuddy.core.workout.Workout;
import roboguice.RoboGuice;
import roboguice.inject.InjectResource;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private static final int ADDITIONAL_FRAGMENTS = 3;
    @Inject
    private WorkoutSession workoutSession;
    @InjectResource(R.string.title_result)
    private String resultTitle;
    @InjectResource(R.string.title_start)
    private String startTitle;
    @InjectResource(R.string.title_finish)
    private String finishTitle;
    @InjectResource(R.string.title_exercise)
    private String exerciseTitle;


    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        RoboGuice.getInjector(context).injectMembersWithoutViews(this);
    }

    @Override
    public Fragment getItem(int position) {
        Workout workout = workoutSession.getWorkout();
        if (position == 0 || workout.getExerciseCount() == 0) {
            return new StartWorkoutFragment();
        } else if (getExercisePosition(position) < workout.getExerciseCount()) {
            return CurrentExerciseFragment.newInstance(getExercisePosition(position));
        } else if (getExercisePosition(position) - workout.getExerciseCount() == 0) {
            return new ResultChartFragment();
        } else {
            return new FinishWorkoutFragment();
        }
    }

    @Override
    public int getCount() {
        Workout workout = workoutSession.getWorkout();
        return workout.getExerciseCount() + ADDITIONAL_FRAGMENTS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Workout workout = workoutSession.getWorkout();
        if (position == 0) {
            return startTitle;
        } else if (getExercisePosition(position) < workout.getExerciseCount()) {
            Exercise currentExercise = workout.getExercise(getExercisePosition(position));
            return String.format(exerciseTitle, currentExercise.getName(), currentExercise.getWeight());
        } else if (workout.getExerciseCount() == 0) {
            return startTitle;
        } else if (getExercisePosition(position) - workout.getExerciseCount() == 0) {
            return resultTitle;
        } else {
            return finishTitle;
        }
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }

    private int getExercisePosition(int position) {
        return position - 1;
    }
}