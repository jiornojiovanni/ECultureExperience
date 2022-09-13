package com.jannuzzi.ecultureexperience;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
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

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jannuzzi.ecultureexperience.data.LoginDataSource;
import com.jannuzzi.ecultureexperience.data.LoginRepository;
import com.jannuzzi.ecultureexperience.data.Path;
import com.jannuzzi.ecultureexperience.databinding.ActivityMainBinding;
import com.jannuzzi.ecultureexperience.ui.login.LoginActivity;
import com.jannuzzi.ecultureexperience.ui.rate.RateActivity;
import com.jannuzzi.ecultureexperience.ui.route.RouteActivity;

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
    private List<String> idList = new ArrayList<>();
    private StorageReference storageRef;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view -> {
            if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickFile();
                //findViewById(R.id.action_search).setVisibility(View.VISIBLE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_PERMISSION);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_logout, R.id.nav_qr, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        try {
            navigationView.getMenu().findItem( R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
                logout();
                finish();
                goToLogin();
                return true;
            });
        }
        catch(Exception e){
            Log.w("NAVBAR", e);
        }

        storageRef = FirebaseStorage.getInstance().getReference();

    }

    private void pickFile() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("text/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, JSON_CONFIG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.succesful_permission, Toast.LENGTH_SHORT).show();
                pickFile();
            } else {
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
        String name = "";
        String description = "";
        String tag = "";
        String imagePath = "";
        String path = "";

        reader.beginObject();
        while(reader.hasNext()) {
            String token = reader.nextName();
            if ("name".equals(token)) {
                name = reader.nextString();
            } else if ("description".equals(token)) {
                description = reader.nextString();
            } else if ("tag".equals(token)) {
                tag = reader.nextString();
            } else if ("imagePath".equals(token)) {
                imagePath = reader.nextString();
            } else if ("path".equals(token)) {
                path = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Path(name, description, tag, imagePath, path);
    }

    private List<Path> parsePathJson(Intent data) throws IOException {
        List<Path> pathList = new ArrayList<>();
        InputStream stream = getContentResolver().openInputStream(data.getData());
        JsonReader reader = new JsonReader(new InputStreamReader(stream));
        reader.beginObject();
        if("id".equals(reader.nextName())) {
            String id = reader.nextString();
            if(id.equals("")) {
                throw new IOException();
            }
            for (String idPath:
                 idList) {
                if(idPath.equals(id)) {
                    return null;
                }
            }
            idList.add(id);
        }
        reader.nextName();
        reader.beginArray();
        while(reader.hasNext()) {
            pathList.add(readPath(reader));
        }
        reader.endArray();
        reader.endObject();
        return pathList;
    }

    private void displayPaths(List<Path> pathList) throws InterruptedException, IOException {
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.removeView(findViewById(R.id.tvLoadPath));

        for (Path path: pathList) {
            LinearLayout layoutCard = (LinearLayout) getLayoutInflater().inflate(R.layout.row_percorsi, null);

            ((TextView) layoutCard.findViewById(R.id.cardTitle)).setText(path.getName());
            ((TextView) layoutCard.findViewById(R.id.tvCardSecond)).setText(path.getDescription());
            ((TextView) layoutCard.findViewById(R.id.tvCardSupport)).setText(path.getTag());

            layoutCard.findViewById(R.id.rateButton).setOnClickListener(view -> {
                layoutCard.findViewById(R.id.rateButton).setEnabled(false);

                Bundle data = new Bundle();
                data.putString("name", path.getName());
                data.putString("description", path.getDescription());

                Intent intent = new Intent(this, RateActivity.class);
                intent.putExtras(data);

                startActivity(intent);
            });

            layoutCard.findViewById(R.id.card).setOnClickListener(view -> {
                Bundle data = new Bundle();
                data.putString("pathFile", path.getPath());
                Intent intent = new Intent(this, RouteActivity.class);
                intent.putExtras(data);

                startActivity(intent);
            });

            StorageReference imgPath = storageRef.child(path.getImagePath());
            imgPath.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ((ShapeableImageView) layoutCard.findViewById(R.id.ivCard)).setImageBitmap(bmp);
            });

            mainLayout.addView(layoutCard);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == JSON_CONFIG) {
            try {
                List<Path> pathList = parsePathJson(data);
                if(pathList != null) {
                    displayPaths(pathList);
                } else {
                    Toast.makeText(this, R.string.file_already_loaded, Toast.LENGTH_LONG).show();
                }
            } catch (IOException | InterruptedException | IllegalStateException e) {
                Toast.makeText(this, R.string.error_loading, Toast.LENGTH_LONG).show();
            }
        }
    }


    void logout(){
        LoginRepository.getInstance(new LoginDataSource()).logout();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }


}