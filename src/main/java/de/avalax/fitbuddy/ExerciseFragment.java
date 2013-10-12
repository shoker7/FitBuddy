package de.avalax.fitbuddy;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import com.google.inject.Inject;
import de.avalax.fitbuddy.workout.Workout;
import roboguice.fragment.RoboFragment;

public class ExerciseFragment extends RoboFragment {

    @Inject
    private Workout workout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exercise_object, container, false);
        Bundle args = getArguments();

        int exerciseIndex = args.getInt("exerciseIndex");

        int maxReps = workout.getCurrentSet(exerciseIndex).getRepsSize();
        int maxSets = workout.getExercise(exerciseIndex).getSetSize();

        FitBuddyProgressBar fitBuddyProgressBarReps = (FitBuddyProgressBar) rootView.findViewById(R.id.progressBarReps);
        fitBuddyProgressBarReps.setMaxValue(maxReps);
        fitBuddyProgressBarReps.setCurrentValue(workout.getCurrentSet(exerciseIndex).getReps());
        FitBuddyProgressBar fitBuddyProgressBarSets = (FitBuddyProgressBar) rootView.findViewById(R.id.progressBarSets);
        fitBuddyProgressBarSets.setMaxValue(maxSets);
        fitBuddyProgressBarSets.setCurrentValue(workout.getExercise(exerciseIndex).getSetNumber());

        fitBuddyProgressBarReps.setOnTouchListener(new ProgressBarGestureListener() {
            @Override
            public void onBottomToTop() {
                Log.d(null,"fitBuddyProgressBarReps: onBottomToTop");
            }

            @Override
            public void onTopToBottom() {
                Log.d(null,"fitBuddyProgressBarReps: onTopToBottom");
            }
        });

        fitBuddyProgressBarSets.setOnTouchListener(new ProgressBarGestureListener() {
            @Override
            public void onBottomToTop() {
                Log.d(null,"fitBuddyProgressBarSets: onBottomToTop");
            }

            @Override
            public void onTopToBottom() {
                Log.d(null,"fitBuddyProgressBarSets: onTopToBottom");
            }
        });

        return rootView;
    }
}
