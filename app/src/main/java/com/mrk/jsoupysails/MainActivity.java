package com.mrk.jsoupysails;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;
import com.mrk.jsoupysails.databinding.ActivityMainBinding;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setRequestMultiplePermissionsLauncher(registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissionsStatusMap -> {
            // Handle the results in here
            boolean allPermissionsGranted = true;
            for (Map.Entry<String, Boolean> entry : permissionsStatusMap.entrySet()) {
                if (!entry.getValue()) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {    // All permissions granted
                Toast.makeText(getApplicationContext(), R.string.granted, Toast.LENGTH_SHORT).show();
            } else {                        // At least one permission denied
                Toast.makeText(getApplicationContext(), R.string.denied, Toast.LENGTH_LONG).show();
                onDestroy();
            }
        }));
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        requestPermissions();
        // String txt = WebClient.getTemplates();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public ActivityResultLauncher<String[]> getRequestMultiplePermissionsLauncher() {
        return requestMultiplePermissionsLauncher;
    }

    public void setRequestMultiplePermissionsLauncher(ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher) {
        this.requestMultiplePermissionsLauncher = requestMultiplePermissionsLauncher;
    }

    private void requestPermissions() {
        MainActivity activity = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.getRequestMultiplePermissionsLauncher().launch(new String[]{
                    Manifest.permission.NEARBY_WIFI_DEVICES,
                    Manifest.permission.ACCESS_FINE_LOCATION
            });
        } else {
            activity.getRequestMultiplePermissionsLauncher().launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            });
        }
    }

}