 package com.prp.studybuddy;

            import android.content.Context;
            import android.net.ConnectivityManager;
            import android.net.NetworkInfo;
            import android.os.Bundle;
            import android.support.design.widget.FloatingActionButton;
            import android.support.v4.app.Fragment;
            import android.support.v4.widget.SwipeRefreshLayout;
            import android.view.LayoutInflater;
            import android.view.View;
            import android.view.ViewGroup;
            import android.widget.ListView;
            import android.widget.TextView;

            import org.json.JSONArray;
            import org.json.JSONException;
            import org.json.JSONObject;

            import java.util.ArrayList;
            import java.util.List;
            import android.content.Context;
            import android.content.Intent;
            import android.net.ConnectivityManager;
            import android.net.NetworkInfo;
            import android.os.Bundle;
            import android.support.design.widget.FloatingActionButton;
            import android.support.v4.app.Fragment;
            import android.support.v4.widget.SwipeRefreshLayout;
            import android.view.LayoutInflater;
            import android.view.View;
            import android.view.ViewGroup;
            import android.widget.ListView;
            import android.widget.TextView;

            import com.android.volley.Request;
            import com.prp.studybuddy.adapter.wall_adapter;
            import com.prp.studybuddy.data.wall_feed;

            import java.util.ArrayList;
            import java.util.List;

            import org.json.JSONArray;
            import org.json.JSONException;
            import org.json.JSONObject;

            import com.android.volley.Response;
            import com.android.volley.VolleyError;
            import com.android.volley.VolleyLog;
            import com.android.volley.toolbox.JsonObjectRequest;


            import static com.prp.studybuddy.app.AppController.getInstance;

public class wall extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private  wall_adapter listAdapter;
    private List<wall_feed> wall_feeds;
    private String URL_FEED = "http://api.androidhive.info/feed/feed.json";
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.fragment_wall, container, false);
        listView = (ListView) x.findViewById(R.id.list);
        wall_feeds = new ArrayList<wall_feed>();

        listAdapter = new wall_adapter( getActivity(), wall_feeds);
        listView.setAdapter(listAdapter);


        swipeRefreshLayout = (SwipeRefreshLayout) x.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        callCache();
                                    }
                                }
        );
        callCache();


        return x;


    }
    private void callCache() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                URL_FEED, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJsonFeed(response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        getInstance().addToRequestQueue(jsonReq);

        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                wall_feed item = new wall_feed();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);

                wall_feeds.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        callCache();
    }

}
