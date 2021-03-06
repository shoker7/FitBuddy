package de.avalax.fitbuddy.port.adapter.persistence;

import android.content.Context;

import org.assertj.core.util.DateUtil;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.List;

import de.avalax.fitbuddy.BuildConfig;
import de.avalax.fitbuddy.R;
import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.finished_exercise.FinishedExercise;
import de.avalax.fitbuddy.domain.model.finished_exercise.FinishedExerciseRepository;
import de.avalax.fitbuddy.domain.model.finished_set.FinishedSetRepository;
import de.avalax.fitbuddy.domain.model.finished_workout.FinishedWorkout;
import de.avalax.fitbuddy.domain.model.finished_workout.FinishedWorkoutException;
import de.avalax.fitbuddy.domain.model.finished_workout.FinishedWorkoutId;
import de.avalax.fitbuddy.domain.model.finished_workout.FinishedWorkoutRepository;
import de.avalax.fitbuddy.domain.model.workout.BasicWorkout;
import de.avalax.fitbuddy.domain.model.workout.Workout;
import de.avalax.fitbuddy.domain.model.workout.WorkoutId;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.hasSize;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SQLiteFinishedWorkoutRepositoryTest {
    private FinishedWorkoutRepository finishedWorkoutRepository;
    private Workout workout;
    private Date date;

    @Before
    public void setUp() throws Exception {
        date = DateUtil.parse("2017-12-31");
        Context context = RuntimeEnvironment.application.getApplicationContext();
        FitbuddySQLiteOpenHelper sqLiteOpenHelper = new FitbuddySQLiteOpenHelper("SQLiteSetRepositoryTest", 1, context, R.raw.fitbuddy_db);
        FinishedSetRepository finishedSetRepository = new SQLiteFinishedSetRepository(sqLiteOpenHelper);
        FinishedExerciseRepository finishedExerciseRepository = new SQLiteFinishedExerciseRepository(sqLiteOpenHelper, finishedSetRepository);
        finishedWorkoutRepository = new SQLiteFinishedWorkoutRepository(sqLiteOpenHelper, finishedExerciseRepository) {
            @Override
            protected long getDate() {
                return date.getTime();
            }
        };
        workout = new BasicWorkout();
        workout.setName("basicWorkout");
        workout.setWorkoutId(new WorkoutId("42"));
    }

    @Test(expected = FinishedWorkoutException.class)
    public void loadWithNullInstance_shouldThrowWorkoutNotFoundException() throws Exception {
        finishedWorkoutRepository.load(null);
    }

    @Test(expected = FinishedWorkoutException.class)
    public void loadByUnknownWorkoutId_shouldThrowWorkoutNotFoundException() throws Exception {
        finishedWorkoutRepository.load(new FinishedWorkoutId("21"));
    }

    @Test
    public void saveWorkout_shouldInsertWorkoutWithNewFinishedWorkoutId() throws Exception {
        FinishedWorkoutId finishedWorkoutId = finishedWorkoutRepository.saveWorkout(workout);

        assertThat(finishedWorkoutId, any(FinishedWorkoutId.class));
    }

    @Test
    public void saveWorkout_shouldInsertWorkoutInformationIntoDatabase() throws Exception {
        FinishedWorkoutId finishedWorkoutId = finishedWorkoutRepository.saveWorkout(workout);

        FinishedWorkout finishedWorkout = finishedWorkoutRepository.load(finishedWorkoutId);

        assertThat(finishedWorkout.getFinishedWorkoutId(), equalTo(finishedWorkoutId));
        assertThat(finishedWorkout.getWorkoutId(), equalTo(workout.getWorkoutId()));
        assertThat(finishedWorkout.getName(), equalTo(workout.getName()));
        assertThat(finishedWorkout.getCreated(), equalTo(date.getTime()));
    }

    @Test
    public void emptyTable_shouldReturnSizeFromTable() throws Exception {
        long size = finishedWorkoutRepository.size();

        assertThat(size, equalTo(0L));
    }

    @Test
    public void oneWorkoutSaved_shouldReturnSizeFromTable() throws Exception {
        finishedWorkoutRepository.saveWorkout(workout);

        long size = finishedWorkoutRepository.size();

        assertThat(size, equalTo(1L));
    }

    @Test
    public void saveWorkout_shouldAlsoInsertExerciseInformationIntoDatabase() throws Exception {
        Exercise exercise1 = workout.getExercises().createExercise();
        exercise1.setName("first exercise");
        Exercise exercise2 = workout.getExercises().createExercise();
        exercise2.setName("second exercise");

        FinishedWorkoutId finishedWorkoutId = finishedWorkoutRepository.saveWorkout(workout);

        FinishedWorkout finishedWorkout = finishedWorkoutRepository.load(finishedWorkoutId);
        List<FinishedExercise> finishedExercises = finishedWorkout.getFinishedExercises();
        assertThat(finishedExercises, hasSize(2));
        assertThat(finishedExercises.get(0).getName(), equalTo("first exercise"));
        assertThat(finishedExercises.get(1).getName(), equalTo("second exercise"));
    }

    @Test
    public void noFinishedWorkouts_shouldReturnEmptyList() throws Exception {
        List<FinishedWorkout> finishedWorkouts = finishedWorkoutRepository.loadAll();

        assertThat(finishedWorkouts, emptyCollectionOf(FinishedWorkout.class));
    }

    @Test
    public void twoPersistedFinishedWorkouts_shouldReturnListWithThem() throws Exception {
        FinishedWorkoutId finishedWorkoutId1 = finishedWorkoutRepository.saveWorkout(new BasicWorkout());
        FinishedWorkoutId finishedWorkoutId2 = finishedWorkoutRepository.saveWorkout(new BasicWorkout());

        List<FinishedWorkout> finishedWorkouts = finishedWorkoutRepository.loadAll();

        assertThat(finishedWorkouts, hasSize(2));
        assertThat(finishedWorkouts.get(0).getFinishedWorkoutId(), Matchers.equalTo(finishedWorkoutId1));
        assertThat(finishedWorkouts.get(1).getFinishedWorkoutId(), Matchers.equalTo(finishedWorkoutId2));
    }

    @Test
    public void deleteWorkoutWithNull_shouldDoNothing() throws Exception {
        finishedWorkoutRepository.delete(null);
    }

    @Test(expected = FinishedWorkoutException.class)
    public void persistedWorkout_shouldBeRemovedFromPersistence() throws Exception {
        FinishedWorkoutId finishedWorkoutId = finishedWorkoutRepository.saveWorkout(new BasicWorkout());

        finishedWorkoutRepository.delete(finishedWorkoutId);

        finishedWorkoutRepository.load(finishedWorkoutId);
    }

    @Test
    public void noFinishedWorkoutForWorkoutId_shouldReturnDefaults() throws Exception {
        assertThat(finishedWorkoutRepository.lastCreation(new WorkoutId("42")), nullValue());
        assertThat(finishedWorkoutRepository.count(new WorkoutId("42")), equalTo(0L));
    }

    @Test
    public void twoFinishedWorkouts_shouldReturnTwoCount() throws Exception {
        finishedWorkoutRepository.saveWorkout(workout);
        finishedWorkoutRepository.saveWorkout(workout);

        assertThat(finishedWorkoutRepository.count(workout.getWorkoutId()), equalTo(2L));
    }

    @Test
    public void threeFinishedWorkouts_shouldReturnMostRecentCreationDate() throws Exception {
        date = DateUtil.parse("2017-12-30");
        finishedWorkoutRepository.saveWorkout(workout);
        date = DateUtil.parse("2017-12-31");
        finishedWorkoutRepository.saveWorkout(workout);
        date = DateUtil.parse("2017-12-29");
        finishedWorkoutRepository.saveWorkout(workout);

        assertThat(finishedWorkoutRepository.lastCreation(workout.getWorkoutId()), equalTo(DateUtil.parse("2017-12-31").getTime()));
    }

    @Test
    public void updateCreation_shouldReturnPersistNewCreationDate() throws Exception {
        FinishedWorkoutId finishedWorkoutId = finishedWorkoutRepository.saveWorkout(workout);
        date = DateUtil.parse("2017-12-30");

        finishedWorkoutRepository.updateCreation(finishedWorkoutId, date.getTime());

        FinishedWorkout finishedWorkout = finishedWorkoutRepository.load(finishedWorkoutId);
        assertThat(finishedWorkout.getCreated(), equalTo(date.getTime()));
    }
}