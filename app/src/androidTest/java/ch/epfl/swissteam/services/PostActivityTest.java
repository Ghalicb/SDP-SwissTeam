package ch.epfl.swissteam.services;

import android.content.Intent;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.epfl.swissteam.services.models.Post;
import ch.epfl.swissteam.services.models.User;
import ch.epfl.swissteam.services.providers.DBUtility;
import ch.epfl.swissteam.services.providers.GoogleSignInSingleton;
import ch.epfl.swissteam.services.view.activities.PostActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.swissteam.services.view.builders.PostAdapter.POST_TAG;
import static ch.epfl.swissteam.services.TestUtils.personalClick;
import static ch.epfl.swissteam.services.TestUtils.sleep;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

public class PostActivityTest extends SocializeTest<PostActivity>{

    private Post post;
    private User user;

    public PostActivityTest(){
        setTestRule(PostActivity.class);
    }

    @Override
    public void initialize() {
        TestUtils.addTestPost();
        user = TestUtils.getTestUser();
        post = TestUtils.getTestPost();
        GoogleSignInSingleton.putUniqueID(user.getGoogleId_());
        post.addToDB(DBUtility.get().getDb_());
        user.addToDB(DBUtility.get().getDb_());
        sleep(400);
    }

    @Test
    public void infoAboutPostCorrespondToTheGivenPost(){
        onView(withId(R.id.textview_postactivity_title)).check(matches(withText(post.getTitle_())));
        onView(withId(R.id.textview_postactivity_body)).check(matches(withText(post.getBody_())));
        onView(withId(R.id.textview_postactivity_date)).check(matches(withText(
                (new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)).format(new Date(post.getTimestamp_()).getTime()))));
        onView(withId(R.id.textview_postactivity_username)).check(matches(withText(user.getName_())));
    }

    @Test
    public void theLocationOfThePostOnTheMapCorrespondToThePost(){
        Marker marker = testRule_.getActivity().getMarker();
        testRule_.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(marker != null){
                LatLng markerPos = marker.getPosition();
                assertEquals(markerPos.latitude, post.getLatitude_(), 0.01);
                assertEquals(markerPos.longitude, post.getLongitude_(), 0.01);
                assertEquals(marker.getTitle(), post.getTitle_());}
            }
        });

    }

    @Test
    public void canClickOnlyOnceOnToDoButton(){
        onView(withId(R.id.button_postactivity_todo)).perform(personalClick());
        onView(withId(R.id.button_postactivity_todo)).check(matches(not(isClickable())));
    }

    @Test
    public void canClickOnlToDoButtonAndItAppearsInTodoListFragment(){
        onView(withId(R.id.button_postactivity_todo)).perform(personalClick());

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.button_maindrawer_todoList));

        onView(withId(R.id.recyclerview_todofragment)).check(matches((hasDescendant(withText(post.getTitle_())))));
        onView(withId(R.id.recyclerview_todofragment)).check(matches((hasDescendant(withText(post.getBody_())))));
    }

    @Override
    public Intent getActivityIntent(){
        Intent intent = new Intent();
        intent.putExtra(POST_TAG, post);
        return intent;
    }

}
