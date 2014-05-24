package de.avalax.fitbuddy.datalayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.avalax.fitbuddy.core.workout.*;
import de.avalax.fitbuddy.core.workout.Set;
import de.avalax.fitbuddy.core.workout.basic.BasicExercise;
import de.avalax.fitbuddy.core.workout.basic.BasicSet;
import de.avalax.fitbuddy.core.workout.basic.BasicWorkout;
import de.avalax.fitbuddy.datalayer.sqlite.WorkoutSQLiteOpenHelper;

import java.util.*;

public class SqliteWorkoutDAO implements WorkoutDAO {
    private static final String WORKOUT_DB = "workout";
    private static final int WORKOUT_DB_VERSION = 1;
    private WorkoutSQLiteOpenHelper workoutSQLiteOpenHelper;

    public SqliteWorkoutDAO(Context context, int createRessourceId) {
        this.workoutSQLiteOpenHelper = new WorkoutSQLiteOpenHelper(WORKOUT_DB, WORKOUT_DB_VERSION, context, createRessourceId);
    }

    @Override
    public void save(Workout workout) {
        SQLiteDatabase database = workoutSQLiteOpenHelper.getWritableDatabase();
        if (workout.getId() == null) {
            workout.setId(new WorkoutId(database.insert("workout", null, getContentValues(workout))));
            for (Exercise exercise : workout.getExercises()) {
                saveExercise(workout.getId(), exercise);
            }
        } else {
            database.update("workout", getContentValues(workout), "id=?", new String[]{String.valueOf(workout.getId())});
        }
        database.close();
    }

    @Override
    public void saveExercise(WorkoutId id, Exercise exercise) {
        SQLiteDatabase database = workoutSQLiteOpenHelper.getWritableDatabase();
        if (exercise.getId() == null) {
            exercise.setId(new ExerciseId(database.insert("exercise", null, getContentValues(id, exercise))));
            for (Set set: exercise.getSets()) {
                saveSet(exercise.getId(), set);
            }
        } else {
            database.update("exercise", getContentValues(id, exercise), "id=?", new String[]{String.valueOf(id.getId())});
        }
        database.close();
    }

    @Override
    public void deleteExercise(ExerciseId id) {
        if (id == null) {
            return;
        }
        SQLiteDatabase database = workoutSQLiteOpenHelper.getWritableDatabase();
        int deleteCount = database.delete("exercise", "id=?", new String[] {String.valueOf(id.getId())});
        Log.d("delete exercise with id" + id.getId(), String.valueOf(deleteCount));
        database.close();
    }

    @Override
    public void saveSet(ExerciseId id, Set set) {
        SQLiteDatabase database = workoutSQLiteOpenHelper.getWritableDatabase();
        if (set.getId() == null) {
            set.setId(new SetId(database.insert("sets", null, getContentValues(id, set))));
        } else {
            database.update("sets", getContentValues(id, set), "id=?", new String[]{String.valueOf(set.getId())});
        }
        database.close();
    }

    @Override
    public void deleteSet(SetId id) {
        if (id == null) {
            return;
        }
        SQLiteDatabase database = workoutSQLiteOpenHelper.getWritableDatabase();
        int deleteCount = database.delete("sets", "id=?", new String[] {String.valueOf(id.getId())});
        Log.d("delete set with id" + id.getId(), String.valueOf(deleteCount));
        database.close();
    }

    private ContentValues getContentValues(ExerciseId id, Set set) {
        ContentValues values = new ContentValues();
        values.put("exercise_id", id.getId());
        values.put("weight", set.getWeight());
        values.put("reps", set.getMaxReps());
        return values;
    }

    private ContentValues getContentValues(WorkoutId id, Exercise exercise) {
        ContentValues values = new ContentValues();
        values.put("workout_id", id.getId());
        values.put("name", exercise.getName());
        return values;
    }

    private ContentValues getContentValues(Workout workout) {
        ContentValues values = new ContentValues();
        values.put("name", workout.getName());
        return values;
    }

    @Override
    public Workout load(WorkoutId id) {
        Workout workout = null;
        SQLiteDatabase database = workoutSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("workout", new String[]{"id", "name"},
                "id=?", new String[]{String.valueOf(id.getId())}, null, null, null);
        if (cursor.getCount() == 1 && cursor.moveToFirst()) {
            workout = createWorkout(database, cursor);
        }
        database.close();
        return workout;
    }

    private Workout createWorkout(SQLiteDatabase database, Cursor cursor) {
        Workout workout;
        LinkedList<Exercise> exercises = new LinkedList<>();
        workout = new BasicWorkout(exercises);
        workout.setId(new WorkoutId(cursor.getLong(0)));
        workout.setName(cursor.getString(1));
        addExercises(database, workout.getId(), exercises);
        return workout;
    }

    private void addExercises(SQLiteDatabase database, WorkoutId workoutId, LinkedList<Exercise> exercises) {
        Cursor cursor = database.query("exercise", new String[]{"id", "name"},
                "workout_id=?", new String[]{String.valueOf(workoutId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                List<Set> sets = new ArrayList<>();
                Exercise exercise = new BasicExercise(cursor.getString(1), sets, 0.0);
                exercise.setId(new ExerciseId(cursor.getLong(0)));
                addSets(database, exercise.getId(), sets);
                exercises.add(exercise);
            } while (cursor.moveToNext());
        }
    }

    private void addSets(SQLiteDatabase database, ExerciseId exerciseId, List<Set> sets) {
        Cursor cursor = database.query("sets", new String[]{"id", "weight", "reps"},
                "exercise_id=?", new String[]{String.valueOf(exerciseId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Set set = new BasicSet(cursor.getDouble(1), cursor.getInt(2));
                set.setId(new SetId(cursor.getLong(0)));
                sets.add(set);
            } while (cursor.moveToNext());
        }
    }

    @Override
    public List<Workout> getList() {
        List<Workout> workoutList = new ArrayList<>();
        SQLiteDatabase database = workoutSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("workout", new String[]{"id", "name"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                BasicWorkout workout = new BasicWorkout(new LinkedList<Exercise>());
                workout.setId(new WorkoutId(cursor.getLong(0)));
                workout.setName(cursor.getString(1));
                workoutList.add(workout);
            } while (cursor.moveToNext());
        }
        database.close();
        return workoutList;
    }

    @Override
    public void delete(WorkoutId id) {
        if (id == null) {
            return;
        }
        SQLiteDatabase database = workoutSQLiteOpenHelper.getWritableDatabase();
        int deleteCount = database.delete("workout", "id=" + id, null);
        Log.d("delete workout with id " + id, String.valueOf(deleteCount));
        database.close();
    }

    @Override
    public Workout getFirstWorkout() {
        Workout workout = null;
        SQLiteDatabase database = workoutSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("workout", new String[]{"id", "name"},
                null, null, null, null, null);
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            workout = createWorkout(database, cursor);
        }
        database.close();
        return workout;
    }
}
