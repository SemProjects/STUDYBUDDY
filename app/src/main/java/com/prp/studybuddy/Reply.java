package com.prp.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class Reply extends AppCompatActivity implements View.OnClickListener {

    TextView n,q;
    EditText reply;
    Button btnreply;
    ListView replylist;
    String que,name,respons;
    String replyy;
    void init()
    {
        n=(TextView)findViewById(R.id.textView2);
        q=(TextView)findViewById(R.id.textView);
        replylist = (ListView)findViewById(R.id.replylist);

        n.setText("-by "+name);
        q.setText(que);
        reply=(EditText)findViewById(R.id.reply);
        btnreply=(Button)findViewById(R.id.btnsubmit);
        btnreply.setOnClickListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent rcv = getIntent();
        name = rcv.getStringExtra("Name");
        que = rcv.getStringExtra("Que");
        setContentView(R.layout.activity_reply);
        init();
        ReplyListTask rlt = new ReplyListTask();
        rlt.execute(que);

    }

    @Override
    public void onClick(View v) {
        String data="";
        SharedPreferences sp2 =getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String defname = "User Name";
        name=sp2.getString("name",defname);
        data=data+replyy+name+" : "+reply.getText().toString().trim()+"::";
        ReplyTask rt = new ReplyTask();
        rt.execute(data,que);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reply.setText("");
        ReplyListTask rlt2 = new ReplyListTask();
        rlt2.execute(que);

    }

    class ReplyTask extends AsyncTask<String,Void,String> {

        String add_info_url;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            add_info_url="https://studybudy.000webhostapp.com/reply.php";
        }

        @Override
        protected String doInBackground(String... params) {
            String flag;
            String n,q;
            n=params[0];
            q=params[1];

            try{
                flag="true";
                URL url = new URL(add_info_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os,"UTF-8");
                BufferedWriter bfw = new BufferedWriter(osw);
                String data_string = URLEncoder.encode("Reply","UTF-8")+"="+URLEncoder.encode(n,"UTF-8")+"&"+
                        URLEncoder.encode("status","UTF-8")+"="+URLEncoder.encode(q,"UTF-8");

                bfw.write(data_string);
                bfw.flush();
                bfw.close();
                osw.close();
                os.close();
                InputStream is =httpURLConnection.getInputStream();
                is.close();
                httpURLConnection.disconnect();
                return "One row added";
            }catch (Exception et)
            {
                flag=et.toString();
                et.printStackTrace();
            }
            return flag;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    public void ParseJson()
    {
        try{
            JSONObject job= new JSONObject(respons);
            JSONArray array= job.getJSONArray("result");

            for(int i=0;i<array.length();i++)
            {
                JSONObject obj = array.getJSONObject(i);
                if(que.compareTo(obj.getString("Question"))==0)
                {
                    replyy=obj.getString("Reply");
                    if(replyy.equals("null"))
                        replyy="";
                    ArrayList<String> items = new ArrayList<String>(Arrays.asList(replyy.split("::")));
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1,
                            items);
                    replylist.setAdapter(adapter);
                    break;
                }
                else
                {}
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    class ReplyListTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try{

                URL url = new URL("http://studybudy.000webhostapp.com/replylist.php");
                HttpURLConnection urlConnection =  (HttpURLConnection)url.openConnection();
                InputStream ios = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(ios);
                BufferedReader br = new BufferedReader(reader);
                StringBuilder builder = new StringBuilder();
                String line = br.readLine();
                while(line!=null)
                {
                    builder.append(line);
                    line=br.readLine();
                }
                respons = builder.toString();

            }catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ParseJson();

        }
    }

}
