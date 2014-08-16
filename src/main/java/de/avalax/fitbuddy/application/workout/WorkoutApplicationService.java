package de.avalax.fitbuddy.application.workout;

import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.exercise.ExerciseNotFoundException;
import de.avalax.fitbuddy.domain.model.set.Set;
import de.avalax.fitbuddy.domain.model.workout.Workout;
import de.avalax.fitbuddy.domain.model.workout.WorkoutNotFoundException;

import java.util.List;

public class WorkoutApplicationService {
    private WorkoutSession workoutSession;

    public WorkoutApplicationService(WorkoutSession workoutSession) {
        this.workoutSession = workoutSession;
    }

    public int countOfCurrentExercises() throws WorkoutNotFoundException {
        return getWorkout().getExercises().size();
    }

    public Exercise requestExercise(int position) throws WorkoutNotFoundException, ExerciseNotFoundException {
        //TODO: move to workout
        List<Exercise> exercises = getWorkout().getExercises();
        if (exercises.size() < position) {
            throw new ExerciseNotFoundException();
        }
        return exercises.get(position);
    }

    public void switchToSet(int position, int moved) throws WorkoutNotFoundException {
        Exercise exercise = getWorkout().getExercises().get(position);
        exercise.setCurrentSet(exercise.indexOfCurrentSet() + moved);
        //TODO only save by android lifecycle
        workoutSession.saveCurrentWorkout();
    }

    public void addRepsToSet(int position, int moved) throws WorkoutNotFoundException {
        Set set = getWorkout().getExercises().get(position).getCurrentSet();
        set.setReps(set.getReps() + moved);
        //TODO only save by android lifecycle
        workoutSession.saveCurrentWorkout();
    }

    public void setSelectedExerciseIndex(int index) throws WorkoutNotFoundException {
        getWorkout().setCurrentExercise(index);
        //TODO only save by android lifecycle
        workoutSession.saveCurrentWorkout();
    }

    public int indexOfCurrentExercise() throws WorkoutNotFoundException {
        return getWorkout().indexOfCurrentExercise();
    }

    public int workoutProgress(int exerciseIndex) throws WorkoutNotFoundException {
        return calculateProgressbarHeight(getWorkout().getProgress(exerciseIndex));
    }

    private int calculateProgressbarHeight(double progess) {
        return (int) Math.round(progess * 100);
    }

    public Workout getWorkout() throws WorkoutNotFoundException {
        return workoutSession.getWorkout();
    }
}