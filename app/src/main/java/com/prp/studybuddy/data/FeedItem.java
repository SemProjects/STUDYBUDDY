package com.prp.studybuddy.data;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class FeedItem extends Activity {
	private int id;
	String ids,Name,ques;
	ImageButton imgReply;
	private String name,field, status, image, profilePic, timeStamp, url;

	public FeedItem() {
	}

	public FeedItem(int id, String name,String field, String image, String status,
					String profilePic, String timeStamp, String url) {
		super();
		this.id = id;
		this.name = name;
		this.field = field;
		this.image = image;
		this.status = status;
		this.profilePic = profilePic;
		this.timeStamp = timeStamp;
		this.url = url;
	/*	imgReply = (ImageButton) findViewById(R.id.reply);
		imgReply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent iv = new Intent(FeedItem.this, Reply.class);
				startActivity(iv);
			}
		});*/
		abc(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getImge() {
		return image;
	}

	public void setImge(String image) {
		this.image = image;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	void abc(int id) {
		final ImageButton imgButton;
       ids=Integer.toString(id);

	}
	class BookMarkTask extends AsyncTask<String,Void,String> {

		String add_info_url;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(getApplicationContext(),"OnPreExeute",Toast.LENGTH_LONG).show();
			add_info_url="https://studybudy.000webhostapp.com/NewInsert.php";
		}

		@Override
		protected String doInBackground(String... params) {
			String flag;
			String e,id;
			e=params[0];
			id=params[1];

			try{
				flag="true";
				URL url = new URL(add_info_url);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				OutputStream os = httpURLConnection.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os,"UTF-8");
				BufferedWriter bfw = new BufferedWriter(osw);
				String data_string = URLEncoder.encode("Email","UTF-8")+"="+URLEncoder.encode(e,"UTF-8")+"&"+
						URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");

				//String data_string="INSERT INTO  Login Table (Name,Email,Password) VALUES ('$n','$email','$pass');";
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
				Toast.makeText(getApplicationContext(),"Exception",Toast.LENGTH_LONG).show();
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
			Toast.makeText(getApplicationContext(),aVoid,Toast.LENGTH_LONG).show();
		}
	}
}

