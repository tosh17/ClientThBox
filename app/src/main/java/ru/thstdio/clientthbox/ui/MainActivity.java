package ru.thstdio.clientthbox.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.List;

import ru.thstdio.clientthbox.R;
import ru.thstdio.clientthbox.bus.BusProvider;
import ru.thstdio.clientthbox.bus.event.DiskSizeEvent;
import ru.thstdio.clientthbox.bus.event.LogOutEvent;
import ru.thstdio.clientthbox.connect.ConnectThBox;
import ru.thstdio.clientthbox.fileutil.PDir;
import ru.thstdio.clientthbox.ui.fragment.FolderView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FolderView.FragmentCallback {

    TextView textSize;
    public static final String KEY_USER_NAME = "user_name";
    FragmentManager fm;
    private Menu menu;
    Toolbar toolbar;
    boolean isRootFolder = true;
    Fragment fragment;
    private int FILE_CODE = 1;
    private long currentFolder = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BusProvider.getInstance().register(this);
        onSupportNavigateUp();
        fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = FolderView.newInstance(this);
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
            setTitle(getIntent().getStringExtra(KEY_USER_NAME));

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        DrawerArrowDrawable searchToggle = new DrawerArrowDrawable(this);
        toggle.setHomeAsUpIndicator(searchToggle);
        toggle.syncState();
        drawer.addDrawerListener(toggle);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        TextView textLogin = (TextView) headerLayout.findViewById(R.id.txtLogin);
        textLogin.setText(getIntent().getStringExtra(KEY_USER_NAME));
        Intent intent = new Intent(this, ConnectThBox.class);
        intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_DISK_SIZE);
        startService(intent);
        textSize = (TextView) headerLayout.findViewById(R.id.txtSize);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRootFolder)
                    ((FolderView) fragment).setUpFolder();

                else {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START))
                        drawer.closeDrawer(GravityCompat.START);
                    else drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilePicker();
            }
        });
    }

    private void showFilePicker() {

        Intent i = new Intent(getApplicationContext(), FilePickerActivity.class);
        i.setPackage(getApplicationContext().getPackageName());
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, FILE_CODE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isRootFolder) super.onBackPressed();
            else ((FolderView) fragment).setUpFolder();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //   getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home && !isRootFolder) {
            ((FolderView) fragment).setUpFolder();
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_bin) {

        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, ConnectThBox.class);
            intent.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_LOGOUT);
            startService(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    @UiThread
    public void setFragmentTitle(PDir root) {
        isRootFolder = root.id == 0;
        currentFolder = root.id;
        if (isRootFolder) {
            toolbar.setNavigationIcon(R.drawable.ic_humburger);
            setTitle("");
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_back);
            setTitle(root.name);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> files = Utils.getSelectedFilesFromResult(intent);
            for (Uri uri : files) {

                File file = Utils.getFileForUri(uri);
                // Do something with the result...
                Log.d("Picker", file.toString());
                Intent intentToServer = new Intent(this, ConnectThBox.class);
                intentToServer.putExtra(ConnectThBox.KEY_COMMAND, ConnectThBox.COMMAND_UPLOAD_FILE);
                intentToServer.putExtra(ConnectThBox.KEY_FILE_URI, file);
                intentToServer.putExtra(ConnectThBox.KEY_FOLDER_ID, currentFolder);
                startService(intentToServer);
            }
        }
    }

    @Subscribe
    public void onLogOut(@NonNull LogOutEvent event) {
        SharedPreferences preff = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = preff.edit();
        edit.putString("UserName", "");
        edit.putString("UserPass", "");
        edit.commit();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        this.finish();
    }
    @Subscribe
    public void onDiskSize(@NonNull DiskSizeEvent event) {
        String str ="";
        int size=0;
        if(event.size<1024){
           size= (int) event.size;
            str=" Byte";
        }
        else if(event.size<1024*1024){
            size= (int) event.size/1024;
            str=" Kb";
        }
        else if(event.size<1024*1024*1024){
            size= (int) event.size/(1024*1024);
            str=" Mb";
        }
        textSize.setText(String.valueOf(size)+str);
    }
}
