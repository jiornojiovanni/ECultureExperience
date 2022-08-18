package com.jannuzzi.ecultureexperience;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.jannuzzi.ecultureexperience.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final int JSON_CONFIG = 380;
    private static final int READ_PERMISSION = 208;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent chooseFile;
                    Intent intent;
                    chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                    chooseFile.setType("application/octet-stream");
                    intent = Intent.createChooser(chooseFile, "Choose a file");
                    startActivityForResult(intent, JSON_CONFIG);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_PERMISSION);
                    }
                }
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permessi ricevuti, puoi caricare il file adesso.", Toast.LENGTH_SHORT).show();
                }  else {
                    Toast.makeText(this, "Permessi rifiutati, non puoi procedere con il caricamento del file.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == JSON_CONFIG) {
            try {
                InputStream stream = getContentResolver().openInputStream(data.getData());
                Scanner scanner = new Scanner(stream);
                while (scanner.hasNext()) {
                    Log.w("test", scanner.next());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}