package de.avalax.fitbuddy.port.adapter.service;

import com.google.gson.Gson;
import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.set.BasicSet;
import de.avalax.fitbuddy.domain.model.set.Set;
import de.avalax.fitbuddy.domain.model.workout.BasicWorkout;
import de.avalax.fitbuddy.domain.model.workout.Workout;
import de.avalax.fitbuddy.domain.model.workout.WorkoutParseException;

import java.util.ArrayList;
import java.util.List;

public class JsonInWorkoutAdapter {
    Gson gson = new Gson();

    public Workout createFromJson(String contents) throws WorkoutParseException {
        if (contents == null || contents.isEmpty()) {
            throw new WorkoutParseException();
        }
        try {
            List jsonWorkout = gson.fromJson(contents, ArrayList.class);
            List<ArrayList> jsonExercises = (List<ArrayList>) jsonWorkout.get(1);
            Workout workout = new BasicWorkout();
            workout.setName((String) jsonWorkout.get(0));

            for (ArrayList jsonExercise : jsonExercises) {
                Exercise exercise = workout.createExercise();
                exercise.setName((String) jsonExercise.get(0));

                List<ArrayList> jsonSets = (List<ArrayList>) jsonExercise.get(1);
                for (ArrayList jsonSet : jsonSets) {
                    double weight = (double) jsonSet.get(1);
                    int maxReps = (int) ((double) jsonSet.get(0));
                    Set set = new BasicSet();
                    set.setWeight(weight);
                    set.setMaxReps(maxReps);
                    exercise.addSet(set);
                }
            }

            return workout;
        } catch (RuntimeException re) {
            throw new WorkoutParseException(re);
        }
    }
}
