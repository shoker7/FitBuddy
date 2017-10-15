package de.avalax.fitbuddy.domain.model.workout;

import java.util.List;

public interface WorkoutRepository {
    void save(Workout workout);

    Workout load(WorkoutId id) throws WorkoutException;

    List<Workout> getWorkouts();

    void delete(WorkoutId id);
}
