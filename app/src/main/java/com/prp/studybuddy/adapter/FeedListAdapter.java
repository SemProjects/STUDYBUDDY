package com.prp.studybuddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.prp.studybuddy.MainActivity;
import com.prp.studybuddy.R;
import com.prp.studybuddy.Reply;
import com.prp.studybuddy.app.AppController;
import com.prp.studybuddy.data.FeedItem;

import java.util.List;

//Created by Parag on 19-10-2016.

public class FeedListAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<FeedItem> feedItems;
	private ImageButton imgButton,imgReply;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
		this.activity = activity;
		this.feedItems = feedItems;

	}

	@Override
	public int getCount() {
		return feedItems.size();
	}

	@Override
	public Object getItem(int location) {
		return feedItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.feed_item, null);

			/*imgButton =(ImageButton)convertView.findViewById(R.id.bookmark);
			imgButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int x=v.getId();
					if(x==R.id.bookmark)
					{
						imgButton.setImageResource(R.drawable.afterbookmark);
						Toast.makeText(activity, "Bookmarkclicked", Toast.LENGTH_SHORT).show();
						SharedPreferences sp = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
						String Email=sp.getString("Email","");



					}
				}
			});*/

		}

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView field = (TextView) convertView.findViewById(R.id.field);
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);
		TextView statusMsg = (TextView) convertView
				.findViewById(R.id.txtStatusMsg);
		TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
		NetworkImageView profilePic = (NetworkImageView) convertView
				.findViewById(R.id.profilePic);


		FeedItem item = feedItems.get(position);

		name.setText(item.getName());

		field.setText(item.getField());

		// Converting timestamp into x ago format
		CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
				Long.parseLong(item.getTimeStamp()),
				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
		timestamp.setText(timeAgo);

		// Chcek for empty status message
		if (!TextUtils.isEmpty(item.getStatus())) {
			statusMsg.setText(item.getStatus());
			statusMsg.setVisibility(View.VISIBLE);
		} else {
			// status is empty, remove from view
			statusMsg.setVisibility(View.GONE);
		}

		// Checking for null feed url
		if (item.getUrl() != null) {
			url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
					+ item.getUrl() + "</a> "));

			// Making url clickable
			url.setMovementMethod(LinkMovementMethod.getInstance());
			url.setVisibility(View.VISIBLE);
		} else {
			// url is null, remove from the view
			url.setVisibility(View.GONE);
		}

		// user profile pic
		profilePic.setImageUrl(item.getProfilePic(), imageLoader);

		FeedItem itemm = feedItems.get(position);
		final int p = position;
		final String Name=item.getName();
		final String ques=item.getStatus();
		System.out.println("GetView"+p+"::"+Name);
		imgReply = (ImageButton)convertView.findViewById(R.id.reply);
		imgReply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("getView"+p+"::"+Name);
				Intent iv = new Intent(v.getContext(), Reply.class);
				iv.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				iv.putExtra("Name",Name);
				iv.putExtra("Que",ques);
				activity.startActivity(iv);
			}
		});


		return convertView;
	}

}
