package com.ufo.androidphotoviewer.flickr;

import android.content.Context;



/**
 * Use interfaces to generate Flickr Image ResultSet based on QueryCriteria
 * 
 * In future QueryCriteria can be extended to provide different search criteria 
 * which the engine will use to generate different ResultSet of images.
 * 
 * @author vlad
 *
 */
public class FlickrQueryEngine 
{
	// Broadcast messages to subscribe to for result of asynchronis query
	public static String GETTING_RESPONSE = "getting_response";
	public static String RESPONSE_FAIL = "response_fail";
	public static String GOT_RESPONSE = "response_done";
	public static String INTENT_RESULT_NAME = "result";	// Intent extra name of FlickrResultSet
	
	private FlickrServiceATask asyncTask = null; 
	private Context context;
	
	
	
	// Constructor
	public FlickrQueryEngine(Context c)
	{
		this.context = c;
	}
	
	public void AsynchronisQuery(FlickrQueryCriteria criteria)
	{
		try {
			if (asyncTask!=null) asyncTask.cancel(true);
			asyncTask = new FlickrServiceATask(criteria, context);	
			asyncTask.execute();
		} catch (Exception e) {
			System.out.println("FlickrQueryEngine.AsynchronisQuery() Exception");
			e.printStackTrace();
		}
	}
	
	
	
	public FlickrResultSet SynchronisQuery(FlickrQueryCriteria criteria)
	{
		FlickrResultSet result = null;
		try {
			if (asyncTask!=null) asyncTask.cancel(true);
			asyncTask = new FlickrServiceATask(criteria, context);	
			result = asyncTask.execute().get();
		} catch (Exception e) {
			System.out.println("FlickrQueryEngine.SynchronisQuery() Exception");
			e.printStackTrace();
		}
		return result;
	}
}
