package ch.epfl.swissteam.services;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Activity to modify the profile in the database
 * It shows the user infos and they can be edited
 *
 * @Author Samuel Chassot
 */
public class ProfileSettings extends AppCompatActivity {

    private String imageUrl_; //TODO: Allow user to change picture in his profile.
    private ArrayList<Categories> userCapabilities_ = new ArrayList<>();
    private RecyclerView recycler;
    private User oldUser_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        Button saveButton = (Button) findViewById(R.id.button_profilesettings_save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.button_profilesettings_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();

            }
        });

        String uniqueID = GoogleSignInSingleton.get().getClientUniqueID();
        loadAndShowUser(uniqueID);

        recycler = findViewById(R.id.recyclerview_profilesettings_categories);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        //don't want the keyboad automatically opens when activity starts
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * Save the modification done by the user
     */
    private void save() {
        String name = ((TextView) findViewById(R.id.edittext_profilesettings_name)).getText().toString();
        String uniqueID = GoogleSignInSingleton.get().getClientUniqueID();
        String email = ((TextView) findViewById(R.id.textview_profilesettings_email)).getText().toString();
        String descr = ((TextView) findViewById(R.id.edittext_profilesettings_description)).getText().toString();
        User updatedUser = new User(uniqueID, name, email, descr, userCapabilities_, imageUrl_, oldUser_.getRating_(),
                oldUser_.getLatitude_(), oldUser_.getLongitude_());


        ArrayList<Categories> categoriesThatHaveBeenRemoved = oldUser_.getCategories_();
        categoriesThatHaveBeenRemoved.removeAll(userCapabilities_);

        for (Categories c : categoriesThatHaveBeenRemoved){
            DBUtility.get().getCategory(c, (cat)->{
                cat.removeUser(uniqueID);
                cat.addToDB(DBUtility.get().getDb_());
            });
        }


        updatedUser.addToDB(DBUtility.get().getDb_());
        finish();
    }

    private void cancel() {
        this.finish();
    }

    /**
     * TODO : Explain
     *
     * @param cat
     * @param checked
     */
    public void updateUserCapabilities(Categories cat, boolean checked) {
        if (checked) {
            //add category to the user's list
            if (!userCapabilities_.contains(cat)) {
                userCapabilities_.add(cat);
            }
        } else {
            //remove it from user's list
            if (userCapabilities_.contains(cat)) {
                userCapabilities_.remove(cat);
            }
        }
    }


    private void loadAndShowUser(String clientUniqueID) {
        DBUtility.get().getUser(clientUniqueID, (user) -> {
            TextView nameView = (TextView) findViewById(R.id.edittext_profilesettings_name);
            nameView.setText(user.getName_());

            TextView emailView = (TextView) findViewById(R.id.textview_profilesettings_email);
            emailView.setText(user.getEmail_());

            TextView descrView = (TextView) findViewById(R.id.edittext_profilesettings_description);
            descrView.setText(user.getDescription_());

            oldUser_ = user;

            Picasso.get().load(user.getImageUrl_()).into((ImageView)findViewById(R.id.imageview_profilesettings_picture));
            imageUrl_ = user.getImageUrl_();
            userCapabilities_.clear();
            userCapabilities_.addAll(user.getCategories_());
            
            recycler.setAdapter(new CategoriesAdapterProfileSettings(Categories.realCategories(), userCapabilities_));

        });
    }


}
