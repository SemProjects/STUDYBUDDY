package com.prp.studybuddy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.prp.studybuddy.adapter.FeedListAdapter;
import com.prp.studybuddy.app.AppController;
import com.prp.studybuddy.data.FeedItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class global extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = global.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "https://studybudy.000webhostapp.com/create_json.php";
    private SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Toast.makeText(getActivity(), "In global", Toast.LENGTH_SHORT).show();
        View x = inflater.inflate(R.layout.fragment_global, container, false);
        listView = (ListView) x.findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);


        //floating button
       /* FloatingActionButton fab = (FloatingActionButton) x.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), postquestion.class);
                startActivity(i);
            }
        });
        */

        swipeRefreshLayout = (SwipeRefreshLayout) x.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
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
        AppController.getInstance().addToRequestQueue(jsonReq);

        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }


    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONObject response) {
        try {
            feedItems.clear();
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));
                item.setField(feedObj.getString("field"));


                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);
                SharedPreferences sp= this.getActivity().getSharedPreferences("PREFERENCE",Context.MODE_PRIVATE);
                String user=sp.getString("name","null");
                String srch=sp.getString("search","null");
                String flg=sp.getString("flag","0");
                String draweritem=sp.getString("drawerselected","0");
                String personname=sp.getString("person","User");
                Log.d("draweritem",draweritem);
                Log.d("Flag",flg);
                Log.d("User in my questions",user);

                if(flg=="1")//For search
                {
                    if (srch != "null") {
                        if ((feedObj.getString("field").contains(srch)) || (feedObj.getString("name").contains(srch)) || (feedObj.getString("status").contains(srch)))
                        {
                            feedItems.add(item);
                        }
                    }

                }
                if(flg=="0") //for all in drawer
                {
                    feedItems.add(item);
                }

                if(flg=="3")//For each field
                {
                    if (draweritem != "0") {
                        if (draweritem.contains(feedObj.getString("field"))) {
                            feedItems.add(item);
                        }

                    }
                }

                if(flg=="4")//for My Questions
                {
                    if (user != "null") {

                        if (feedObj.getString("name").contains(user))
                        {
                            Log.d("User in post questions",feedObj.getString("name"));
                            feedItems.add(item);
                        }
                    }


                }
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
