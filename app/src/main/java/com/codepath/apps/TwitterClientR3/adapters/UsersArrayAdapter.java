package com.codepath.apps.TwitterClientR3.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.TwitterClientR3.R;
import com.codepath.apps.TwitterClientR3.helpers.RoundedCornersTransformation;
import com.codepath.apps.TwitterClientR3.activities.TimelineActivity;
import com.codepath.apps.TwitterClientR3.models.User;

import java.util.List;

/**
 * Created by alex_ on 3/21/2017.
 */

//Taking the tweets object and turning them into views displayed in the list;
public class UsersArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private UserAdapterListener listener;
    // List of tweets
    private List<User> mUsers;
    // Store the context for easy access
    private Context mContext;

    public UsersArrayAdapter()
    {
        listener=null;
    }

    public UsersArrayAdapter(@NonNull Context context, @NonNull List<User> objects) {
        mUsers = objects;
        mContext = context;
        listener = null;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    // inflate xml layout and return  viewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v1 = inflater.inflate(R.layout.item_user, parent, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolder_simple(v1);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder_simple vh = (ViewHolder_simple) viewHolder;
        configureViewHolder_simple(vh, position);
    }

    private void configureViewHolder_simple(ViewHolder_simple vh, int position) {
        // Get the data model based on position
        User user = mUsers.get(position);
        fetchCommon(vh, user);
    }

    //populate main fields
    public void fetchCommon(ViewHolder_simple viewHolder, User user) {
        final  User user1 =user;
        viewHolder.tvBody.setText(user1.getTagLine());
        viewHolder.tvUsername.setText(user1.getScreenName());
        viewHolder.tvDisplayName.setText(user1.getName());

        if (viewHolder.ivProfileImage != null) {
            Glide.with(getContext()).load(user1.getProfileImageUrl())
                    .bitmapTransform(new RoundedCornersTransformation(getContext(), 15, 2))
                    .into(viewHolder.ivProfileImage);

            viewHolder.ivProfileImage.setClickable(true);
            viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*if (mContext instanceof TimelineActivity) {
                        ((TimelineActivity) mContext).lunchProfileActivity(user1);
                    }*/
                    if (listener != null) {
                        listener.showProfile(user1);
                    }
                }
            });
        }
    }

    public static class ViewHolder_simple extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvUsername;
        public TextView tvTimestamp;
        public TextView tvBody;
        public ImageView ivProfileImage;
        public TextView tvDisplayName;
        public TextView tvRetweeted;
        public RelativeLayout llLeftColumn;

        public TextView tvRetweet;
        public TextView tvLike;

        //Define constructor wichi accept entire row and find sub views
        public ViewHolder_simple(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvUsername = (TextView) itemView.findViewById(R.id.tvName);
            tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvDisplayName = (TextView) itemView.findViewById(R.id.tvDisplayName);
            tvRetweeted = (TextView) itemView.findViewById(R.id.tvRetweeted);

            llLeftColumn = (RelativeLayout) itemView.findViewById(R.id.llLeftColumn);

            tvRetweet = (TextView) itemView.findViewById(R.id.tvRetweet);
            tvLike = (TextView) itemView.findViewById(R.id.tvLike);
        }
    }

    public interface UserAdapterListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void showProfile(User user);
    }

    // Assign the listener implementing events interface that will receive the events
    public void setAdapterListener(UserAdapterListener listener) {
        this.listener = listener;
    }

}
