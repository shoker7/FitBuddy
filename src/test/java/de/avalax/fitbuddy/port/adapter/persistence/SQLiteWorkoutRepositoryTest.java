package de.avalax.fitbuddy.port.adapter.persistence;

import android.app.Activity;
import de.avalax.fitbuddy.application.R;
import de.avalax.fitbuddy.application.manageWorkout.ManageWorkoutActivity;
import de.avalax.fitbuddy.domain.model.exercise.BasicExercise;
import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.exercise.ExerciseRepository;
import de.avalax.fitbuddy.domain.model.set.Set;
import de.avalax.fitbuddy.domain.model.workout.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class SQLiteWorkoutRepositoryTest {

    private WorkoutRepository workoutRepository;

    private ExerciseRepository exerciseRepository;

    private WorkoutId createWorkout(String name) {
        Workout workout = new BasicWorkout(new LinkedList<Exercise>());
        workout.setName(name);
        workoutRepository.save(workout);
        return workout.getWorkoutId();
    }

    @Before
    public void setUp() throws Exception {
        Activity activity = Robolectric.buildActivity(ManageWorkoutActivity.class).create().get();
        FitbuddySQLiteOpenHelper sqLiteOpenHelper = new FitbuddySQLiteOpenHelper("SQLiteWorkoutRepositoryTest", 1, activity, R.raw.fitbuddy_db);
        exerciseRepository = mock(ExerciseRepository.class);
        workoutRepository = new SQLiteWorkoutRepository(sqLiteOpenHelper, exerciseRepository);
    }

    @Test
    public void saveUnpersistedWorkout_shouldAssignNewWorkoutId() {
        Workout workout = new BasicWorkout(new LinkedList<Exercise>());

        assertThat(workout.getWorkoutId(), nullValue());
        workoutRepository.save(workout);
        assertThat(workout.getWorkoutId(), any(WorkoutId.class));
    }

    @Test
    public void savePersistedWorkout_shouldKeepWorkoutId() {
        Workout workout = new BasicWorkout(new LinkedList<Exercise>());
        workoutRepository.save(workout);
        WorkoutId workoutId = workout.getWorkoutId();

        workoutRepository.save(workout);
        assertThat(workout.getWorkoutId(), equalTo(workoutId));
    }

    @Test
    public void saveWorkout_shouldAlsoSaveExercises() {
        LinkedList<Exercise> exercises = new LinkedList<>();
        BasicExercise exercise = new BasicExercise("exercise", new ArrayList<Set>());
        exercises.add(exercise);
        Workout workout = new BasicWorkout(exercises);

        workoutRepository.save(workout);

        verify(exerciseRepository).save(workout.getWorkoutId(), exercise);
    }

    @Test
    public void loadByUnknownWorkoutId_shouldReturnNullValue() {
        Workout workout = workoutRepository.load(new WorkoutId("21"));
        assertThat(workout, nullValue());
    }

    @Test
    public void loadByWorkoutId_shouldReturnWorkoutWithWorkoutId() {
        Workout workout = new BasicWorkout(new LinkedList<Exercise>());
        workoutRepository.save(workout);
        WorkoutId workoutId = workout.getWorkoutId();

        Workout loadedWorkout = workoutRepository.load(workoutId);
        assertThat(loadedWorkout.getWorkoutId(), equalTo(workoutId));
    }

    @Test
    public void loadByWorkoutId_shouldReturnWorkoutWithExercises() {
        LinkedList<Exercise> exercises = new LinkedList<>();
        Exercise exercise1 = new BasicExercise("exercise1", new ArrayList<Set>());
        exercises.add(exercise1);
        Exercise exercise2 = new BasicExercise("exercise2", new ArrayList<Set>());
        exercises.add(exercise2);
        Workout workout = new BasicWorkout(exercises);
        workoutRepository.save(workout);
        when(exerciseRepository.allExercisesBelongsTo(workout.getWorkoutId())).thenReturn(exercises);
        WorkoutId workoutId = workout.getWorkoutId();

        Workout loadedWorkout = workoutRepository.load(workoutId);

        assertThat(loadedWorkout.getExercises().size(), equalTo(2));
        assertThat(loadedWorkout.getExercises().get(0), equalTo(exercise1));
        assertThat(loadedWorkout.getExercises().get(1), equalTo(exercise2));
    }

    @Test
    public void saveWorkout_shouldUpdateName() {
        Workout workout = new BasicWorkout(new LinkedList<Exercise>());
        workoutRepository.save(workout);
        WorkoutId workoutId = workout.getWorkoutId();

        Workout loadedWorkout = workoutRepository.load(workoutId);
        loadedWorkout.setName("new name");
        workoutRepository.save(loadedWorkout);
        Workout reloadedWorkout = workoutRepository.load(workoutId);
        assertThat(reloadedWorkout.getName(), equalTo("new name"));
    }

    @Test
    public void emptyWorkoutList_shouldReturnTheListOfWorkouts() throws Exception {
        List<WorkoutListEntry> workoutList = workoutRepository.getWorkoutList();

        assertThat(workoutList.size(), equalTo(0));
    }

    @Test
    public void workoutList_shouldReturnTheListOfWorkouts() throws Exception {
        WorkoutId workoutId = createWorkout("workout1");
        WorkoutId workoutId2 = createWorkout("workout2");

        List<WorkoutListEntry> workoutList = workoutRepository.getWorkoutList();

        assertThat(workoutList.size(), equalTo(2));
        assertThat(workoutList.get(0).getWorkoutId(), equalTo(workoutId));
        assertThat(workoutList.get(0).toString(), equalTo("workout1"));
        assertThat(workoutList.get(1).getWorkoutId(), equalTo(workoutId2));
        assertThat(workoutList.get(1).toString(), equalTo("workout2"));
    }

    @Test
    public void deleteWorkoutByWorkoutId_shouldRemoveItFromPersistence() throws Exception {
        WorkoutId workoutId = createWorkout("workout1");

        workoutRepository.delete(workoutId);

        assertThat(workoutRepository.load(workoutId), nullValue());
    }
}