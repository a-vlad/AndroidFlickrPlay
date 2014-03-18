package com.ufo.androidphotoviewer;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.Shader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ufo.androidphotoviewer.flickr.*;
import com.ufo.androidphotoviewer.flickr.FlickrQueryCriteria.Stream;
import com.squareup.picasso.*;


/**
 * Main activity controller
 * 
 * @author vlad
 */
public class MainActivity extends Activity
{		
	private LinearLayout galleryView;
	private HorizontalScrollView horizontalScrollView;
	private ViewPager imagePager;
	
	private FlickrQueryEngine flickrConnect;
	private DataUpdateReceiver dataUpdateReceiver;
	private FlickrResultSet results = null;
	
	
	/**
	 * Listens for broadcast messages from the service
	 * and updates the GUI accordingly.
	 */
	private class DataUpdateReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	    	if (intent.getAction().equals(FlickrQueryEngine.GOT_RESPONSE)) {

	    		// Get attached ResultSet object
	    		results = (FlickrResultSet)intent.getSerializableExtra(FlickrQueryEngine.INTENT_RESULT_NAME);
	    		processUpdatedResultSet();
	    		
	    	} else if (intent.getAction().equals(FlickrQueryEngine.RESPONSE_FAIL)) {
	    		
	    		String labelStr = getResources().getString(R.string.flickr_error);
	    		Toast.makeText(context, labelStr, Toast.LENGTH_LONG).show();
	    	}
	    }
	}
	
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		galleryView = (LinearLayout) findViewById(R.id.mygallery);
        imagePager = (ViewPager) findViewById(R.id.img_pager);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_img_scroll);
        horizontalScrollView.setFadingEdgeLength(50);
        horizontalScrollView.setVerticalFadingEdgeEnabled(true);
        horizontalScrollView.setVerticalScrollBarEnabled(false);
        flickrConnect = new FlickrQueryEngine(this);
        flickrConnect.AsynchronisQuery(new FlickrQueryCriteria(Stream.PUBLIC1));
        String labelStr = getResources().getString(R.string.refreshing);
        Toast.makeText(this, labelStr, Toast.LENGTH_SHORT).show();
	}

	
	@Override
	public void onResume() {	
		super.onResume();
		// REGISTER SERVICE BROADCAST RECEIVER
		if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FlickrQueryEngine.GOT_RESPONSE);
		intentFilter.addAction(FlickrQueryEngine.RESPONSE_FAIL);
		registerReceiver(dataUpdateReceiver, intentFilter);
	}

	
	@Override
    protected void onPause() {
        super.onPause();
		if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	} 
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Action with ID action_refresh was selected
			case R.id.action_refresh:
				// Clear all and request new results asynchronisly
				galleryView.removeAllViews();
				imagePager.removeAllViews();
				flickrConnect.AsynchronisQuery(new FlickrQueryCriteria(Stream.PUBLIC1));
				String labelStr = getResources().getString(R.string.refreshing);
				Toast.makeText(this, labelStr, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
		}

		return true;
	} 
	
	
	
	/**
	 * Reloads the latest articles
	 */
	private void processUpdatedResultSet()
	{
		Picasso.with(this).setDebugging(true);
		
		for (int i=0; i<results.getImages().size(); i++)
		{
			FlickrImage flickrImg = results.getImages().get(i);
			
		    ImageView thumbView = new ImageView(this);
		    thumbView.setAdjustViewBounds(true);
		    thumbView.setFocusable(true);
		    // Load image
		    String url = flickrImg.getHDImageUrl();		
		    Picasso.with(this).load(url).transform(new RoundedCornersTransformation())
		    							.placeholder(R.drawable.placeholder)
		    							.error(R.drawable.warning)
		    							.into(thumbView);
		    thumbView.setTag((Integer)i);	// Hold index value
		    
		    // On click function
		    thumbView.setOnClickListener(new OnClickListener()
		    {
		    	@Override
    	    	public void onClick(View v) {
		    		imagePager.setCurrentItem((Integer)v.getTag(), true);
    	    	}
		    });
		    
		    galleryView.addView(thumbView);
		}
		
		// Set up image swipe ViewPager
		FullScreenImageAdapter pageAdapter = new FullScreenImageAdapter(this, results);
		imagePager.setAdapter(pageAdapter);
		imagePager.setOnPageChangeListener(new OnPageChangeListener() 
		{
			@Override
			public void onPageSelected(int arg0) 
			{
				horizontalScrollView.smoothScrollTo(galleryView.getChildAt(arg0).getLeft(), 0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				//Unused
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				//Unused
			}
		});
	}
	
	
	
	
	/**
	 * Apply rounded corner transformation to Picasso handled image
	 * 
	 * @author Vlad
	 */
	private class RoundedCornersTransformation implements Transformation 
	{
		private final int RADIUS = 10;
		private final int MARGIN = 5; 
		
		@Override 
		public String key() { return "roundCorners()"; }
		  
		@Override
		public Bitmap transform(final Bitmap source) 
		{
			final Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
		  
			Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			canvas.drawRoundRect(new RectF(MARGIN, MARGIN, source.getWidth() - MARGIN, source.getHeight() - MARGIN), RADIUS, RADIUS, paint);
		  
			if (source != output) {
				source.recycle();
			}
		  
			return output;
		}
	}
	
	
	
	/**
	 * Paging image adapter class
	 * 
	 * @author vlad
	 *
	 */
	private class FullScreenImageAdapter extends PagerAdapter {
		 
	    private Context context;
	    private FlickrResultSet imageResults;
	 
	    // constructor
	    public FullScreenImageAdapter(Context c, FlickrResultSet results) {
	        this.context = c;
	        this.imageResults = results;
	    }
	    
	    @Override
	    public int getCount() {
	        if ((this.imageResults != null) 
	        		&& (this.imageResults.getImages() != null)){
	        	return this.imageResults.getImages().size();
	        } else {
	        	return 0;
	        }
	    }
	 
	    @Override
	    public Object instantiateItem(ViewGroup container, int position) 
	    {
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View viewLayout = inflater.inflate(R.layout.image_pager_view, container, false);
	        
	        ImageView imgDisplay = (ImageView) viewLayout.findViewById(R.id.page_image);
	        
	        // Load image
	        String url = imageResults.getImages().get(position).getHDImageUrl();		
		    Picasso.with(context).load(url).transform(new RoundedCornersTransformation())
		    							   .placeholder(R.drawable.placeholder)
		    							   .error(R.drawable.warning)
		    							   .into(imgDisplay);	        
	        ((ViewPager) container).addView(viewLayout);
	  
	        return viewLayout;
	    }
	    
	    
	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        ((ViewPager) container).removeView((RelativeLayout) object);
	    }

	    @Override
	    public boolean isViewFromObject(View view, Object object) {
	        return view == ((RelativeLayout) object);
	    }

	}
	
}


