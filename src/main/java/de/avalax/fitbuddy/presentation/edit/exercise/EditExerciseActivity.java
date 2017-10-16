package de.avalax.fitbuddy.presentation.edit.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import de.avalax.fitbuddy.R;
import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.set.BasicSet;
import de.avalax.fitbuddy.domain.model.set.Set;
import de.avalax.fitbuddy.presentation.edit.set.EditSetActivity;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static java.lang.String.valueOf;

public class EditExerciseActivity extends AppCompatActivity {

    public static final int EDIT_SET = 5;
    public static final int ADD_SET = 6;
    private EditText nameEditText;
    private Exercise exercise;
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);
        Toolbar toolbar = findViewById(R.id.toolbar_exercise_edit);
        setSupportActionBar(toolbar);
        nameEditText = findViewById(R.id.edit_text_exercise_name);
        exercise = (Exercise) getIntent().getSerializableExtra("exercise");
        nameEditText.setText(exercise.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_edit_exercise, menu);

        return true;
    }

    public void onAddSetButtonClick(View view) {
        Intent intent = new Intent(this, EditSetActivity.class);
        intent.putExtra("set", new BasicSet());
        startActivityForResult(intent, ADD_SET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_SET && resultCode == Activity.RESULT_OK) {
            Set set = (Set) data.getSerializableExtra("set");
            exercise.getSets().add(set);

            SetListFragment setListFragment = (SetListFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_content);
            setListFragment.notifyItemInserted();
        }
        if (requestCode == EDIT_SET && resultCode == Activity.RESULT_OK) {
            Integer position = data.getIntExtra("position", -1);
            Set set = (Set) data.getSerializableExtra("set");
            exercise.getSets().set(position, set);

            SetListFragment setListFragment = (SetListFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_content);
            setListFragment.notifyItemChanged(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_save_exercise) {
            if (exercise.getSets().size() == 0) {
                Context context = getApplicationContext();
                makeText(context, R.string.message_save_exercise_without_sets, LENGTH_SHORT)
                        .show();
            } else {
                Intent intent = new Intent();
                exercise.setName(nameEditText.getText().toString());
                intent.putExtra("exercise", exercise);
                int position = getIntent().getIntExtra("position", -1);
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
        }
        if (item.getItemId() == R.id.toolbar_delete_sets) {
            SetListFragment setListFragment = (SetListFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_content);
            setListFragment.removeSelections();
            return true;
        }
        return false;
    }

    public void updateToolbar(int selectionCount) {
        if (selectionCount > 0) {
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_edit_exercise_delete_sets, menu);
            MenuItem item = menu.findItem(R.id.toolbar_delete_sets);
            item.setTitle(valueOf(selectionCount));
        } else {
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_edit_exercise, menu);
        }
    }
}