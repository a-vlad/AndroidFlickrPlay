package com.ufo.androidphotoviewer.flickr;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;



/**
 * Threaded task used to retrieve a list of all new 
 * Flickr images and parse out JSON reply.
 * 
 * @author Vlad
 *
 */
class FlickrServiceATask extends AsyncTask<Void, FlickrResultSet, FlickrResultSet> 
{
	private static final int CONNECTION_ESTABLISH_TIMEOUT = 3000;	// 3 seconds
	private static final int CONNECTION_READ_TIMEOUT = 3000;	// 3 seconds	
	
	//RESPONSE PARAMETER FLICKR RESULTSET JSONy
	private static final String JSON_RESULT_TITLE = "title";
	private static final String JSON_RESULT_LINK = "link";
	private static final String JSON_RESULT_DESCRIPTION = "description";
	private static final String JSON_RESULT_MODIFIED = "modified";
	private static final String JSON_RESULT_ITEMS = "items";	
	
	//RESPONSE PARAMETER FLICKR IMAGE JSON
	private static final String JSON_IMG_TITLE = "title";
	private static final String JSON_IMG_LINK = "link";
	private static final String JSON_IMG_MEDIA = "media";	// Sublist
	private static final String JSON_IMG_MEDIA_URL = "m";	// Member of media sublist list
	private static final String JSON_IMG_DESCRIPTION = "description";
	private static final String JSON_IMG_PUBLISHED = "published";
	private static final String JSON_IMG_AUTHOR = "author";
	private static final String JSON_IMG_AUTHOR_ID = "author_id";
	private static final String JSON_IMG_TAGS = "tags";

	private FlickrQueryCriteria criteria;
	private Context context;	
	
	
	
	//CONSTRUCTOR
	public FlickrServiceATask(FlickrQueryCriteria criteria, Context ctx)
	{
		this.criteria = criteria;
		this.context = ctx;
	}
	
	

	@Override
    protected FlickrResultSet doInBackground(final Void... unused)
    {	
		FlickrResultSet result = new FlickrResultSet();
		result.setQueryCrtieria(this.criteria);
		ArrayList<FlickrImage> images = new ArrayList<FlickrImage>();	
		
 	    InputStream in = null;  
 	    HttpURLConnection connection;
 	   
		try{
			// Setup connection
	 	    URL serviceURL = new URL(this.criteria.getRequestUrl());
	 	    connection = (HttpURLConnection) serviceURL.openConnection();
     	    connection.setConnectTimeout(CONNECTION_ESTABLISH_TIMEOUT);
     	    connection.setReadTimeout(CONNECTION_READ_TIMEOUT);
     	    connection.setRequestProperty("Content-Type",  "application/json");	
     	    connection.setRequestMethod("GET");
     	    connection.setUseCaches(false);
     	    connection.setDoInput(true);	//Sets for GET request
     	    connection.setChunkedStreamingMode(0);

     	    // Get JSON reply from service
	        in = new BufferedInputStream(connection.getInputStream());
	        StringBuilder sb = new StringBuilder();
	        while (in.available() > 0) 
	        {
	        	sb.append((char)in.read());
	        }
	        
	        // Get result meta-data
	        JSONObject responseObj = new JSONObject(sb.toString());
	        result.setTitle(responseObj.getString(JSON_RESULT_TITLE));
	        result.setLink(responseObj.getString(JSON_RESULT_LINK));
	        result.setDescription(responseObj.getString(JSON_RESULT_DESCRIPTION));
	        result.setModified(parseJsonDate(responseObj.getString(JSON_RESULT_MODIFIED)));
	        
	        // Get list of images
	        JSONArray jsonImageList = new JSONArray(responseObj.getString(JSON_RESULT_ITEMS));          
	        for (int i = 0; i< jsonImageList.length(); i++)
	        {
	        	JSONObject tmpJsonImg = jsonImageList.getJSONObject(i);
	        	FlickrImage tmpImg = new FlickrImage();
	        	
	        	tmpImg.setTitle(tmpJsonImg.getString(JSON_IMG_TITLE));
	        	tmpImg.setAlbumLink(tmpJsonImg.getString(JSON_IMG_LINK));
	        	tmpImg.setDescriptionHTML(tmpJsonImg.getString(JSON_IMG_DESCRIPTION));
	        	tmpImg.setPublishDate(parseJsonDate(tmpJsonImg.getString(JSON_IMG_PUBLISHED)));
	        	tmpImg.setAuthor(tmpJsonImg.getString(JSON_IMG_AUTHOR));
	        	tmpImg.setAuthorID(tmpJsonImg.getString(JSON_IMG_AUTHOR_ID));
 	        	tmpImg.setTags(tmpJsonImg.getString(JSON_IMG_TAGS));
	        	JSONObject tmpJsonImgUrl = tmpJsonImg.getJSONObject(JSON_IMG_MEDIA);
	        	tmpImg.setImageURL(tmpJsonImgUrl.getString(JSON_IMG_MEDIA_URL));
	        	
	        	images.add(tmpImg);
	        }
	        
	        result.setImages(images);	        
	        
	        in.close();
	        connection.disconnect();
	        
        } catch (java.net.SocketException ex) {
        	System.out.println("FlickrServiceATask: Socket Exception: " + ex.getMessage());
        	ex.printStackTrace();
        } catch (Exception ex) {
        	System.out.println("FlickrServiceATask: Exception");
        	ex.printStackTrace();
        }

   		return result;		     
    }
    
    @Override
    protected void onPostExecute(FlickrResultSet result) 
    {
    	//Broadcast message to subscribed activities (FlickrQueryEngine)
    	if ((result != null) && (result.getImages() != null) && (result.getImages().size() > 0)) {
    		Intent broadcastIntent = new Intent(FlickrQueryEngine.GOT_RESPONSE);
    		broadcastIntent.putExtra(FlickrQueryEngine.INTENT_RESULT_NAME, result);
    		context.sendBroadcast(broadcastIntent);
    	} else {
    		Intent broadcastIntent = new Intent(FlickrQueryEngine.RESPONSE_FAIL);
    		context.sendBroadcast(broadcastIntent);
    	}
    }
    
    
    /**
     * Convert ISO8601 date to java.util.date
     * 
     * @param string date
     * @return java.util.date
     */
    private static Date parseJsonDate(String jsonDate)
    {
    	Date date = null;
    	
    	try {
    		SimpleDateFormat formatter;
    		formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
    		date = formatter.parse(jsonDate);
    	} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	return date;    	
    }
    
}



