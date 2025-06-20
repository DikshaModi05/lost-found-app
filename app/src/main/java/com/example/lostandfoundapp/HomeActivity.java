package com.example.lostandfoundapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import com.google.android.material.navigation.NavigationView;
//
//public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//
//    private DrawerLayout drawerLayout;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        // Toolbar setup
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        // Drawer Layout setup
//        drawerLayout = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        // Add menu icon functionality
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawerLayout, toolbar,
//                R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close
//        );
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.opt_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_search) {
//            startActivity(new Intent(this, SearchActivity.class));
//            return true;
//        } else if (id == R.id.action_notifications) {
//            startActivity(new Intent(this, NotificationsActivity.class));
//            return true;
//        } else if (id == R.id.action_profile) {
//            startActivity(new Intent(this, ProfileActivity.class));
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.nav_lost) {
//            startActivity(new Intent(this, LostItemsActivity.class));
//        } else if (id == R.id.nav_found) {
//            startActivity(new Intent(this, FoundItemsActivity.class));
//        } else if (id == R.id.nav_settings) {
//            startActivity(new Intent(this, SettingsActivity.class));
//        } else if (id == R.id.nav_logout) {
//            Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//}
//
//package com.example.lostandfoundapp;
//
//import static androidx.core.content.ContextCompat.startActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.squareup.picasso.Picasso;
//
//public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//
//    private DrawerLayout drawerLayout;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        // Toolbar setup
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        // Drawer Layout setup
//        drawerLayout = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        // Set up navigation drawer toggle
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawerLayout, toolbar,
//                R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close
//        );
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//
//
//        // Load user details in Navigation Header
//        View headerView = navigationView.getHeaderView(0);
//        TextView textViewName = headerView.findViewById(R.id.textViewName);
//        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
//        ImageView imageViewProfile = headerView.findViewById(R.id.imageViewProfile);
//
//        if (user != null) {
//            // ✅ Fetch Name & Email Correctly
//            textViewName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
//            textViewEmail.setText(user.getEmail());
//
//            // ✅ Load Profile Picture (if available)
//            if (user.getPhotoUrl() != null) {
//                Picasso.get().load(user.getPhotoUrl()).into(imageViewProfile);
//            } else {
//                imageViewProfile.setImageResource(R.drawable.user);  // Default profile image
//            }
//        }
//
//        FloatingActionButton fabAddLostItem = findViewById(R.id.fab_add_lost_item);
//        fabAddLostItem.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick (View v){
//                // Open AddLostItemActivity when FAB is clicked
//                Intent intent = new Intent(HomeActivity.this, AddLostItemActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.nav_home) {
//            startActivity(new Intent(this, HomeActivity.class));
//        } else if (item.getItemId() == R.id.nav_found) {
//            startActivity(new Intent(this, FoundItemsActivity.class));
//        } else if (item.getItemId() == R.id.nav_settings) {
//            startActivity(new Intent(this, SettingsActivity.class));
//        } else if (item.getItemId() == R.id.nav_logout) {
//            mAuth.signOut();
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        }
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.opt_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_search) {
//            startActivity(new Intent(this, SearchActivity.class));
//            return true;
//        } else if (id == R.id.action_notifications) {
//            startActivity(new Intent(this, NotificationsActivity.class));
//            return true;
//        } else if (id == R.id.action_profile) {
//            startActivity(new Intent(this, ProfileActivity.class));
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
//}




import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FloatingActionButton fabAdd, fabHistory, fabChat, fabMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Load default fragment (Home)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // FAB initialization
        fabAdd = findViewById(R.id.fab_add_lost_item);
        fabHistory = findViewById(R.id.fab_history);
        fabChat = findViewById(R.id.fab_chat);
        fabMap = findViewById(R.id.fab_map);

        fabAdd.setOnClickListener(view -> showReportDialog());

        fabHistory.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        fabChat.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        fabMap.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }

    private void showReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What do you want to report?")
                .setItems(new CharSequence[]{"Lost Item", "Found Item"}, (dialog, which) -> {
                    if (which == 0) {
                        startActivity(new Intent(HomeActivity.this, ReportLostItemActivity.class));
                    } else {
                        startActivity(new Intent(HomeActivity.this, ReportFoundItemActivity.class));
                    }
                });
        builder.create().show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.opt_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else if (id == R.id.action_notifications) {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        } else if (id == R.id.nav_lost) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new LostItemsFragment()).commit();
        } else if (id == R.id.nav_found) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FoundItemsFragment()).commit();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
