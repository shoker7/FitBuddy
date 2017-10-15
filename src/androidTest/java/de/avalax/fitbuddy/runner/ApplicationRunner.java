package de.avalax.fitbuddy.runner;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import de.avalax.fitbuddy.R;
import de.avalax.fitbuddy.application.dialog.WeightDecimalPlaces;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.avalax.fitbuddy.runner.BottomNavigationMatcher.bottomNavItemIsChecked;
import static de.avalax.fitbuddy.runner.BottomNavigationMatcher.bottomNavItemIsNotChecked;
import static de.avalax.fitbuddy.runner.ToastMatcher.isToast;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.not;

public class ApplicationRunner {
    public void showsStartBottomNavAsActive() {
        onView(withId(R.id.navigation_start_item)).check(matches(bottomNavItemIsChecked()));
        onView(withId(R.id.navigation_workout_item)).check(matches(bottomNavItemIsNotChecked()));
        onView(withId(R.id.navigation_statistics_item)).check(matches(bottomNavItemIsNotChecked()));
    }


    public void showsSupportMenuItem() {
        onView(withId(R.id.toolbar_support)).check(matches(isDisplayed()));
    }

    public void hasShownAddNewWorkoutHint() {
        onView(withId(android.R.id.empty)).check(matches(withText(R.string.empty_workout_list)));
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
    }

    public void hasShownAddNewExerciseHint() {
        onView(withId(android.R.id.empty)).check(matches(withText(R.string.empty_exercise_list)));
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
    }

    public void hasShownAddNewSetHint() {
        onView(withId(android.R.id.empty)).check(matches(withText(R.string.empty_set_list)));
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
    }

    public void addWorkout(String name) {
        onView(withId(R.id.fab_add_workout)).perform(click());
        onView(withId(R.id.edit_text_workout_name)).perform(typeText(name), closeSoftKeyboard());
    }

    public void editWorkout(int position) {
        onView(withId(android.R.id.list)).perform(
                actionOnItemAtPosition(position, longClick()));
        onView(withId(R.id.toolbar_edit_workout)).perform(click());
    }

    public void changeWorkout(String name) {
        onView(withId(R.id.edit_text_workout_name)).perform(replaceText(name), closeSoftKeyboard());
    }


    public void deleteWorkout(int position) {
        onView(withId(android.R.id.list)).perform(actionOnItemAtPosition(position, longClick()));
        onView(withId(R.id.toolbar_delete_workout)).perform(click());
    }

    public void hasShownOldWorkoutNameInEditView(String name) {
        onView(withId(R.id.edit_text_workout_name)).check(matches(withText(name)));
    }

    public void addExercise(String name) {
        onView(withId(R.id.fab_add_exercise)).perform(click());
        onView(withId(R.id.edit_text_exercise_name)).perform(typeText(name), closeSoftKeyboard());
    }

    public void editExercise(int position) {
        onView(withId(android.R.id.list)).perform(
                actionOnItemAtPosition(position, click()));
    }

    public void hasShownOldExerciseNameInEditView(String name) {
        onView(withId(R.id.edit_text_exercise_name)).check(matches(withText(name)));
    }

    public void changeExercise(String name) {
        onView(withId(R.id.edit_text_exercise_name)).perform(replaceText(name), closeSoftKeyboard());
    }

    public void addSet(String reps, String weight) {
        onView(withId(R.id.fab_add_set)).perform(click());

        onView(withId(R.id.set_reps)).perform(click());
        onView(withId(R.id.repsNumberPicker)).perform(setNumberPicker(parseInt(reps)));
        onView(withId(R.id.done_button)).perform(click());

        String[] weightSplit = weight.split("\\.");
        onView(withId(R.id.set_weight)).perform(click());
        onView(withId(R.id.numberPicker)).perform(setNumberPicker(parseInt(weightSplit[0])));
        WeightDecimalPlaces weightDecimalPlaces = new WeightDecimalPlaces();
        int position = weightDecimalPlaces.getPosition(Double.parseDouble("0." + weightSplit[1]));
        onView(withId(R.id.decimalPlaces)).perform(setNumberPicker(position));
        onView(withId(R.id.done_button)).perform(click());
    }

    public void editSet(int position) {
        onView(withId(android.R.id.list)).perform(
                actionOnItemAtPosition(position, click()));
    }

    public void hasShownOldSetDetails(String reps, String weight) {
        onView(withId(R.id.set_reps_text_view)).check(matches(withText(reps)));
        onView(withId(R.id.set_weight_text_view)).check(matches(withText(weight)));
    }

    public void changeSet(String reps, String weight) {
        onView(withId(R.id.set_reps)).perform(click());
        onView(withId(R.id.repsNumberPicker)).perform(setNumberPicker(parseInt(reps)));
        onView(withId(R.id.done_button)).perform(click());

        String[] weightSplit = weight.split("\\.");
        onView(withId(R.id.set_weight)).perform(click());
        onView(withId(R.id.numberPicker)).perform(setNumberPicker(parseInt(weightSplit[0])));
        WeightDecimalPlaces weightDecimalPlaces = new WeightDecimalPlaces();
        int position = weightDecimalPlaces.getPosition(Double.parseDouble("0." + weightSplit[1]));
        onView(withId(R.id.decimalPlaces)).perform(setNumberPicker(position));
        onView(withId(R.id.done_button)).perform(click());
    }

    public void saveSet() {
        onView(withId(R.id.toolbar_save_set)).perform(click());
    }

    public void enterSetSelectionMode(int position) {
        onView(withId(android.R.id.list)).perform(
                actionOnItemAtPosition(position, longClick()));
        onView(withId(R.id.toolbar_delete_sets)).check(matches(withtMenuTitle(valueOf(1))));
    }

    public void selectSets(int... positions) {
        for (int position : positions) {
            onView(withId(android.R.id.list)).perform(
                    actionOnItemAtPosition(position, click()));
        }
    }

    public void hasShownDeleteSetsCount(int count) {
        onView(withId(R.id.toolbar_delete_sets)).check(matches(withtMenuTitle(valueOf(count))));
    }

    public void deleteSelectedSets() {
        onView(withId(R.id.toolbar_delete_sets)).perform(click());
    }

    public void hasShownCantSaveExerciseWithoutSets() {
        onView(withText(R.string.message_save_exercise_without_sets)).inRoot(isToast()).check(matches(isDisplayed()));
    }

    public void hasShownSetAddedToExercise(String reps, String weight) {
        onView(withId(android.R.id.list)).check(matches(hasChildCount(1)));
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));
        onView(withId(R.id.item_title)).check(matches(withText(reps)));
        onView(withId(R.id.item_subtitle)).check(matches(withText(weight)));
    }

    public void enterExerciseSelectionMode(int position) {
        onView(withId(android.R.id.list)).perform(
                actionOnItemAtPosition(position, longClick()));
        onView(withId(R.id.toolbar_delete_exercices)).check(matches(withtMenuTitle(valueOf(1))));
    }

    public void selectExercises(int... positions) {
        for (int position : positions) {
            onView(withId(android.R.id.list)).perform(
                    actionOnItemAtPosition(position, click()));
        }
    }

    public void hasShownDeleteExerciseCount(int count) {
        onView(withId(R.id.toolbar_delete_exercices)).check(matches(withtMenuTitle(valueOf(count))));
    }

    public void deleteSelectedExercices() {
        onView(withId(R.id.toolbar_delete_exercices)).perform(click());
    }

    public void hasShownCantSaveWorkoutWithoutExercices() {
        onView(withText(R.string.message_save_workout_without_exercices)).inRoot(isToast()).check(matches(isDisplayed()));
    }

    public void saveExercise() {
        onView(withId(R.id.toolbar_save_exercise)).perform(click());
    }

    public void hasShownExerciseDetails(String name, String details) {
        onView(withId(android.R.id.list)).check(matches(hasChildCount(1)));
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));
        onView(withId(R.id.item_title)).check(matches(withText(name)));
        onView(withId(R.id.item_subtitle)).check(matches(withText(details)));
    }

    public void saveWorkout() {
        onView(withId(R.id.toolbar_save_workout)).perform(click());
    }

    public void hasShownWorkoutDetails(int position, String name) {
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));
        onView(withId(android.R.id.list))
                .perform(RecyclerViewActions.scrollToPosition(position))
                .check(matches(itemAtPosition(position, withText(name), R.id.card_title)))
                .check(matches(itemAtPosition(position, withText("Executed 0 times"), R.id.card_subtitle)))
                .check(matches(itemAtPosition(position, withText("never"), R.id.card_date)))
                .check(matches(itemAtPosition(position, withText(R.string.btn_start_workout), R.id.btn_start_workout)));
    }

    public void hasShownSetDetails(String reps, String weight) {
        onView(withId(R.id.set_reps_text_view)).check(matches(withText(reps)));
        onView(withId(R.id.set_weight_text_view)).check(matches(withText(weight)));
    }

    private static ViewAction setNumberPicker(final int value) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                ((NumberPicker) view).setValue(value);
            }

            @Override
            public String getDescription() {
                return "Set the value into a NumberPicker";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }
        };
    }

    public static ViewAction clickChildButtonWithId(final int id) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(Button.class);
            }
        };
    }

    public static Matcher<View> withtMenuTitle(String label) {
        return new BoundedMatcher<View, ActionMenuItemView>(ActionMenuItemView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has label " + label);
            }

            @Override
            public boolean matchesSafely(final ActionMenuItemView actionMenuItemView) {
                MenuItem supportMenuItem = actionMenuItemView.getItemData();
                return label.matches(supportMenuItem.getTitle().toString());
            }
        };
    }

    public static Matcher<View> itemAtPosition(int position, Matcher<View> itemMatcher, int id) {

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has view id " + itemMatcher + " at position " + position);
            }

            @Override
            public boolean matchesSafely(final RecyclerView recyclerView) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                View targetView = viewHolder.itemView.findViewById(id);
                return itemMatcher.matches(targetView);
            }
        };
    }
}
