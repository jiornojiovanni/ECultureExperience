package com.jannuzzi.ecultureexperience;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.jannuzzi.ecultureexperience.data.Path;
import com.jannuzzi.ecultureexperience.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int JSON_CONFIG = 380;
    private static final int READ_PERMISSION = 208;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private List<Path> pathList = new ArrayList<>();



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
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_PERMISSION);
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
                    Toast.makeText(this, R.string.succesful_permission, Toast.LENGTH_SHORT).show();
                }  else {
                    Toast.makeText(this, R.string.refused_permission, Toast.LENGTH_SHORT).show();
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

    private Path readPath(JsonReader reader) throws IOException {
        reader.beginObject();
        String name = "", description = "", tag = "";

        while(reader.hasNext()) {
            String token = reader.nextName();
            switch (token) {
                case "name":
                    name = reader.nextString();
                    break;
                case "description":
                    description = reader.nextString();
                    break;
                case "tag":
                    tag = reader.nextString();
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.endObject();

        return new Path(name, description, tag);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == JSON_CONFIG) {
            try {
                InputStream stream = getContentResolver().openInputStream(data.getData());
                JsonReader reader = new JsonReader(new InputStreamReader(stream));

                reader.beginArray();
                while(reader.hasNext()) {
                    pathList.add(readPath(reader));
                }
                reader.endArray();


                LinearLayout mainLayout = findViewById(R.id.mainLayout);
                mainLayout.removeView(findViewById(R.id.tvLoadPath));

                    for (Path path: pathList) {
                        LinearLayout layoutCard = (LinearLayout) getLayoutInflater().inflate(R.layout.row_percorsi, null);

                        ((TextView) layoutCard.findViewById(R.id.cardTitle)).setText(path.getName());
                        ((TextView) layoutCard.findViewById(R.id.tvCardSecond)).setText(path.getDescription());
                        ((TextView) layoutCard.findViewById(R.id.tvCardSupport)).setText(path.getTag());

                        layoutCard.findViewById(R.id.card).setOnClickListener(v -> {
                            Toast.makeText(this, "Ciao sono il " + path.getName(), Toast.LENGTH_SHORT).show();
                        });

                        mainLayout.addView(layoutCard);
                    }

            } catch (IOException  e) {
                Toast.makeText(this, R.string.error_loading, Toast.LENGTH_LONG).show();
            }
        }
    }
}