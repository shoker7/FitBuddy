package de.avalax.fitbuddy.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import de.avalax.fitbuddy.app.edit.EditExerciseActivity;
import de.avalax.fitbuddy.app.edit.WorkoutAdapter;
import de.avalax.fitbuddy.core.workout.Workout;
import de.avalax.fitbuddy.datalayer.WorkoutDAO;

import javax.inject.Inject;
import java.util.List;

public class ManageWorkoutActivity extends ListActivity implements ActionBar.OnNavigationListener {
    private static final String WORKOUT_POSITION = "WORKOUT_POSITION";
    private static final String WORKOUT = "WORKOUT";
    private static final String UNSAVED_CHANGES = "UNSAVED_CHANGES";

    public static final int ADD_EXERCISE_BEFORE = 1;
    public static final int EDIT_EXERCISE = 2;
    public static final int ADD_EXERCISE_AFTER = 3;
    public static final int SAVE_WORKOUT = 1;
    public static final int SWITCH_WORKOUT = 2;
    private boolean initializing;
    private boolean unsavedChanges;
    @Inject
    protected WorkoutDAO workoutDAO;
    @Inject
    protected SharedPreferences sharedPreferences;
    @Inject
    protected WorkoutSession workoutSession;
    @Inject
    protected WorkoutFactory workoutFactory;
    private Workout workout;
    private int workoutPosition;
    private View footer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_workout);
        ButterKnife.inject(this);
        ((FitbuddyApplication) getApplication()).inject(this);
        if (savedInstanceState != null) {
            workoutPosition = savedInstanceState.getInt(WORKOUT_POSITION);
            workout = (Workout) savedInstanceState.getSerializable(WORKOUT);
            unsavedChanges = savedInstanceState.getBoolean(UNSAVED_CHANGES);
        } else {
            workoutPosition = sharedPreferences.getInt(WorkoutSession.LAST_WORKOUT_POSITION, 0);
            workout = workoutDAO.load(workoutPosition);
            unsavedChanges = false;
        }
        footer = findViewById(R.id.footer_undo);
        initActionBar();
        initListView();
    }

    private void initListView() {
        setListAdapter(WorkoutAdapter.newInstance(getApplication(), R.layout.row, workout));
        registerForContextMenu(getListView());
        if (unsavedChanges) {
            showUnsavedChanges();
        }
    }

    private void initActionBar() {
        initializing = true;
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, getWorkouts());

        actionBar.setListNavigationCallbacks(spinnerAdapter, this);
        actionBar.setSelectedNavigationItem(workoutPosition);
    }

    private String[] getWorkouts() {
        List<String> workoutlist = workoutDAO.getWorkoutlist();
        if (workoutlist.size() == workoutPosition) {
            workoutlist.add(workout.getName());
        }
        return workoutlist.toArray(new String[workoutlist.size()]);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = EditExerciseActivity.newEditExerciseIntent(this, position);
        startActivityForResult(intent, EDIT_EXERCISE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_workout_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (initializing) {
            initializing = false;
        } else {
            workout = workoutDAO.load(itemPosition);
            workoutPosition = itemPosition;
            initActionBar();
            initListView();
            showUnsavedChanges();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select_exercise) {
            workoutDAO.save(workout);
            workoutSession.switchWorkout(workoutPosition);
            setResult(RESULT_OK);
            finish();
        } else if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (item.getItemId() == R.id.action_add_workout) {
            final CharSequence[] items = {"Create a new workout", "Scan from QR-Code"};
            final Activity activity = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add a workout");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        createNewWorkout();
                    } else if (item == 1) {
                        IntentIntegrator integrator = new IntentIntegrator(activity);
                        integrator.initiateScan();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
        return true;
    }

    private void createNewWorkout() {
        workout = workoutFactory.createNew();
        workoutPosition = workoutDAO.getWorkoutlist().size();
        initActionBar();
        initListView();
        showUnsavedChanges();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == getListView().getId()) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(workout.getExercise(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.actions_edit_exercise);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int exercisePosition = info.position;
        if (getString(R.string.action_exercise_delete).equals(item.getTitle())) {
            workout.removeExercise(exercisePosition);
            initListView();
        } else if (getString(R.string.action_exercise_add_before_selected).equals(item.getTitle())) {
            Intent intent = EditExerciseActivity.newCreateExerciseIntent(this, exercisePosition, ADD_EXERCISE_BEFORE);
            startActivityForResult(intent, ADD_EXERCISE_BEFORE);
        } else if (getString(R.string.action_exercise_add_behind_selected).equals(item.getTitle())) {
            Intent intent = EditExerciseActivity.newCreateExerciseIntent(this, exercisePosition, ADD_EXERCISE_AFTER);
            startActivityForResult(intent, ADD_EXERCISE_AFTER);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            try {
                Workout workoutFromJson = workoutFactory.createFromJson(scanResult.getContents());
                if (workoutFromJson != null) {
                    workout = workoutFromJson;
                    workoutPosition = workoutDAO.getWorkoutlist().size();
                    initActionBar();
                }
                initListView();
                showUnsavedChanges();
            } catch (WorkoutParseException wpe) {
                Toast toast = Toast.makeText(this, getText(R.string.action_read_qrcode_failed), Toast.LENGTH_LONG);
                Log.d("reading of qrcode failed", wpe.getMessage());
                toast.show();
            }
        } else if (resultCode == Activity.RESULT_OK) {
            initListView();
            showUnsavedChanges();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(WORKOUT_POSITION, workoutPosition);
        savedInstanceState.putSerializable(WORKOUT, workout);
        savedInstanceState.putBoolean(UNSAVED_CHANGES, unsavedChanges);
    }

    @OnClick(R.id.button_undo)
    protected void undoChanges() {
        footer.setVisibility(View.GONE);
        workoutPosition = sharedPreferences.getInt(WorkoutSession.LAST_WORKOUT_POSITION, 0);
        workout = workoutDAO.load(workoutPosition);
        unsavedChanges = false;
        initActionBar();
        initListView();
    }

    private void showUnsavedChanges() {
        unsavedChanges = true;
        footer.setVisibility(View.VISIBLE);
    }
}