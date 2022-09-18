package com.jannuzzi.ecultureexperience;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jannuzzi.ecultureexperience.data.JSONParser;
import com.jannuzzi.ecultureexperience.data.User;
import com.jannuzzi.ecultureexperience.data.UserRepository;
import com.jannuzzi.ecultureexperience.data.Path;
import com.jannuzzi.ecultureexperience.databinding.ActivityMainBinding;
import com.jannuzzi.ecultureexperience.ui.login.LoginActivity;
import com.jannuzzi.ecultureexperience.ui.qr.QrScanner;
import com.jannuzzi.ecultureexperience.ui.rate.RateActivity;
import com.jannuzzi.ecultureexperience.ui.route.RouteActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int JSON_CONFIG = 380;
    private static final int READ_PERMISSION = 208;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private List<String> idList = new ArrayList<>();
    private StorageReference storageRef;
    private NavigationView navigationView;
    private SearchView searchBar;
    private MenuItem searchBarItem;
    List<Path> pathList = new ArrayList<>();
    List<Integer> idPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view -> {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickFile();
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

        View headerView = navigationView.getHeaderView(0);
        // get user name and email textViews
        TextView email = headerView.findViewById(R.id.email);
        TextView name = headerView.findViewById(R.id.name);
        // set user name and email
        UserRepository.getInstance().getCurrentUser(msg -> {
            User user = (User) msg.obj;
            name.setText(user.name);
            email.setText(user.email);
            return true;
        });


        try {
            navigationView.getMenu().findItem(R.id.nav_home).setOnMenuItemClickListener(menuItem -> true);
            navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
                logout();
                finish();
                goToLogin();
                return true;
            });
            navigationView.getMenu().findItem(R.id.nav_qr).setOnMenuItemClickListener(menuItem -> {
                Intent openQr = new Intent(MainActivity.this, QrScanner.class);
                startActivity(openQr);
                return true;
            });
        } catch (Exception e) {
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
        searchBarItem = menu.findItem(R.id.action_search);
        searchBar = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                boolean found_something = false;

                if (newText.length() >= 1) {
                    for (Path path : pathList) {

                        String input = newText.toUpperCase(Locale.ROOT);
                        String name = path.getName().toUpperCase(Locale.ROOT);
                        String description = path.getDescription().toUpperCase(Locale.ROOT);
                        String tag = path.getTag().toUpperCase(Locale.ROOT);

                        if (name.contains(input) || description.contains(input) || tag.contains(input)) {
                            findViewById(idPaths.get(pathList.indexOf(path))).setVisibility(View.VISIBLE);
                            found_something = true;
                        } else {
                            findViewById(idPaths.get(pathList.indexOf(path))).setVisibility(View.GONE);
                        }
                    }

                    if (found_something) {
                        findViewById(R.id.tvNoResults).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.tvNoResults).setVisibility(View.VISIBLE);
                    }

                } else {
                    findViewById(R.id.tvNoResults).setVisibility(View.GONE);
                    for (Path path : pathList) {
                        View temp = findViewById(idPaths.get(pathList.indexOf(path)));
                        temp.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void displayPaths(List<Path> pathList) throws InterruptedException, IOException {
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.removeView(findViewById(R.id.tvLoadPath));

        for (Path path : pathList) {
            //LinearLayout layoutCard = (LinearLayout) getLayoutInflater().inflate(R.layout.row_percorsi, null);
            LinearLayout layoutCard = (LinearLayout) getLayoutInflater().inflate(R.layout.row_percorsi, null);


            ((TextView) layoutCard.findViewById(R.id.cardTitle)).setText(path.getName());
            ((TextView) layoutCard.findViewById(R.id.tvCardSecond)).setText(path.getDescription());
            ((TextView) layoutCard.findViewById(R.id.tvCardSupport)).setText(path.getTag());

            layoutCard.findViewById(R.id.rateButton).setOnClickListener(view -> {
                layoutCard.findViewById(R.id.rateButton).setEnabled(false);

                Bundle data = new Bundle();
                data.putString("name", path.getName());
                data.putString("description", path.getDescription());
                data.putString("imgPath", path.getImagePath());

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

            MaterialCardView card = (MaterialCardView) layoutCard.findViewById(R.id.card);
            card.setId(View.generateViewId());
            this.idPaths.add(card.getId());

            mainLayout.addView(layoutCard);
        }
        searchBarItem.setVisible(true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == JSON_CONFIG) {
            try {
                List<Path> pathListTemp = JSONParser.parsePath(getContentResolver().openInputStream(data.getData()), idList);
                if (pathListTemp != null) {
                    pathList.addAll(pathListTemp);
                    displayPaths(pathListTemp);
                } else {
                    Toast.makeText(this, R.string.file_already_loaded, Toast.LENGTH_LONG).show();
                }
            } catch (IOException | InterruptedException | IllegalStateException e) {
                Toast.makeText(this, R.string.error_loading, Toast.LENGTH_LONG).show();
            }
        }
    }


    void logout() {
        UserRepository.getInstance().logout();
        for (String file : getApplicationContext().fileList()) {
            getApplicationContext().deleteFile(file);
        }
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }


}