package ch.epfl.swissteam.services.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.MapStyleOptions;
import com.squareup.picasso.Picasso;

import ch.epfl.swissteam.services.R;
import ch.epfl.swissteam.services.providers.SettingsDBUtility;
import ch.epfl.swissteam.services.providers.DBUtility;
import ch.epfl.swissteam.services.providers.GoogleSignInSingleton;
import ch.epfl.swissteam.services.utils.ActivityUtils;

/**
 * Activity which contains the navigation drawer.
 * Every activity that needs the drawer menu must extend this class.
 *
 * @author Julie Giunta
 */
public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public static final String NAVIGATION_TAG = "NAV_DRAWER_CLICKED";
    public static final String CANCEL = "Cancel";
    public static final String MAIN = "Main";
    public static final String BACK = "Back";

    private DBUtility util_ = DBUtility.get();

    private DrawerLayout drawer_;
    private Toolbar toolbar_;
    protected ActionBarDrawerToggle toggle_;
    private String toggleButton_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int darkmode = SettingsDBUtility.retrieveDarkMode(this);
        if (darkmode == 1){
            getTheme().applyStyle(R.style.DarkMode, true);
        } else {
            getTheme().applyStyle(R.style.AppTheme, true);
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * Create a NavigationDrawerActivity
     * @param toggleButton a string that tells which toggle button you want for
     *                     the menu (MAIN : normal hamburger menu,
     *                     CANCEL : cross image and disables button to draw the menu,
     *                     BACK : arrow image and disables button tu draw the menu)
     *
     */
    protected void onCreateDrawer(String toggleButton) {
        this.toggleButton_ = toggleButton;
        toolbar_ = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_);
        toolbar_.setOnClickListener(v -> {
            ActivityUtils.hideKeyboard(this);
        });

        drawer_ = findViewById(R.id.drawer_layout);

        setUpToggle();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //if(toggleButton.equals(CANCEL)){
            /*
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            */

        //}


        TextView navHeaderName = findViewById(R.id.nav_header_name);
    }

    /**
     * Set up the action bar
     */
    private void setUpToggle(){
        Activity thisActivity = this;
        toggle_ = new ActionBarDrawerToggle(
                thisActivity, drawer_, toolbar_, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset){
                ActivityUtils.hideKeyboard(thisActivity);
            }
        };
        drawer_.addDrawerListener(toggle_);
        toggle_.syncState();

        if(!toggleButton_.equals(MAIN)){
            toggle_.setDrawerIndicatorEnabled(false);
            toggle_.setToolbarNavigationClickListener(view -> {
                finish();
            });
        }

        switch(toggleButton_){
            case (CANCEL) :
                toggle_.setHomeAsUpIndicator(R.drawable.ic_toggle_cancel);
                break;
            case (BACK) :
                toggle_.setHomeAsUpIndicator(R.drawable.ic_toggle_backarrow);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            drawer_.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if(toggleButton_.equals(CANCEL)){
            menu.setGroupEnabled(R.id.group_cancel, true);
            menu.setGroupVisible(R.id.group_cancel, true);
        }

        setNavUserName();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        /*
         //noinspection SimplifiableIfStatement
         if (id == R.id.action_settings) {
         return true;
         }
         */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        startActivity(new Intent(this, MainActivity.class).putExtra(NAVIGATION_TAG, id));
        drawer_.closeDrawer(GravityCompat.START);
        finish();

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        int dark = SettingsDBUtility.retrieveDarkMode(this);
        if (dark == 1){
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_night));
        }
    }

    /**
     * Set the user name and email in the nav
     */
    private void setNavUserName() {
        util_.getUser(GoogleSignInSingleton.get().getClientUniqueID(), user -> {
            if(user != null){
                ((TextView) findViewById(R.id.nav_header_name)).setText(user.getName_());
                ((TextView) findViewById(R.id.nav_header_email)).setText(user.getEmail_());
                Picasso.get().load(user.getImageUrl_()).into((ImageView)findViewById(R.id.nav_header_profileimage));}

        });
    }
    protected boolean isDrawerOpen(){
        return drawer_.isDrawerOpen(GravityCompat.START);
    }

}