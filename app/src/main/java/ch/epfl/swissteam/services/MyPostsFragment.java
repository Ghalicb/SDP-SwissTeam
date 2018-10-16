package ch.epfl.swissteam.services;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.helper.ItemTouchHelper.Callback;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;

/**
 * MyPostsFragment, a fragment that display to the currently logged in user
 * his posts. Depending on his interaction with his posts, sends him to
 * {@link MyPostEdit}.
 */
public class MyPostsFragment extends Fragment {

    private String clientUniqueID_;
    private RecyclerView.Adapter mAdapter_;
    private List<Post> mPosts_ = new ArrayList<>();

    public MyPostsFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new {@link MyPostsFragment}.
     *
     * @return new instance of <code>MyPostsFragment</code>
     */
    public static MyPostsFragment newInstance() {
        MyPostsFragment fragment = new MyPostsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_my_posts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_my_posts, container, false);

        clientUniqueID_ = GoogleSignInSingleton.get().getClientUniqueID();
        loadAndShowPostsFromUser();

        //setup recyclerview for posts
        RecyclerView mRecyclerView_ = frag.findViewById(R.id.recyclerview_mypostsfragment);

        if (mRecyclerView_ != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
            mRecyclerView_.setLayoutManager(layoutManager);

            mAdapter_ = new PostAdapter(mPosts_);
            mRecyclerView_.setAdapter(mAdapter_);
        }

        return frag;
    }

    /**
     * Load the posts from a user and display them in the recycler view
     */
    private void loadAndShowPostsFromUser() {
        DBUtility.get().getUsersPosts(clientUniqueID_, (posts) -> {
            mPosts_.clear();
            mPosts_.addAll(posts);
            mAdapter_.notifyDataSetChanged();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAndShowPostsFromUser();
    }

}