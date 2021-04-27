package de.andicodes.vergissnix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import de.andicodes.vergissnix.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setSupportActionBar(findViewById(R.id.toolBar));
    }
}