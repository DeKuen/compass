package ch.dekuen.android.compassapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import de.mannodermaus.junit5.ActivityScenarioExtension;

class MainActivityTest {
    @RegisterExtension
    final ActivityScenarioExtension<MainActivity> scenarioExtension = ActivityScenarioExtension.launch(MainActivity.class);

    @Test
    void myTest() {
        ActivityScenario<MainActivity> scenario = scenarioExtension.getScenario();
        // Use the scenario...
    }
}