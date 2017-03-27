
package com.reyansh.audio.audioplayer.free.Common;


import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;


import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.ToxicBakery.viewpager.transforms.TabletTransformer;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedVignetteBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.reyansh.audio.audioplayer.free.Database.DataBaseHelper;
import com.reyansh.audio.audioplayer.free.GoogleAnalytics.AnalyticsTrackers;
import com.reyansh.audio.audioplayer.free.MusicService.MusicService;
import com.reyansh.audio.audioplayer.free.R;
import com.reyansh.audio.audioplayer.free.Utils.PreferencesUtility;
import com.reyansh.audio.audioplayer.free.Utils.ZoomOutPageTransformer;


/**
 * Created by REYANSH on 27/12/2015.
 */

public class CommonClass extends Application {
    boolean DEBUG = true;
    private Context mContext;
    DataBaseHelper dbHelper;
    String TAG = "CommonClass";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        dbHelper = new DataBaseHelper(mContext);
        initImageLoader();
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        if (DEBUG) Log.d(TAG, "onCreate");
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    private MusicService mService;
    private boolean mIsServiceRunning = false;

    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    public void setIsServiceRunning(boolean running) {
        mIsServiceRunning = running;
    }

    public void setService(MusicService service) {
        mService = service;
    }

    public MusicService getService() {
        return mService;
    }

    public PreferencesUtility getPreferencesUtility() {
        return PreferencesUtility.getInstance(mContext);
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new WeakMemoryCache()).defaultDisplayImageOptions(options).memoryCacheSizePercentage(13).build();
        ImageLoader.getInstance().init(config);
    }

   public DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_art)
            .showImageForEmptyUri(R.drawable.default_art)
            .showImageOnFail(R.drawable.default_art)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .displayer(new FadeInBitmapDisplayer(500))
            .handler(new Handler()).build();

    public DataBaseHelper getDBAccessHelper() {
        return DataBaseHelper.getDatabaseHelper(mContext);
    }


    public ViewPager.PageTransformer getTransformer() {
        if (getPreferencesUtility().getScrollView().equals("Default Transformer")) {
            return new DefaultTransformer();
        } else if (getPreferencesUtility().getScrollView().equals("Accordion Transformer")) {
            return new AccordionTransformer();
        } else if (getPreferencesUtility().getScrollView().equals("Rotate Down Transformer")) {
            return new RotateDownTransformer();
        } else if (getPreferencesUtility().getScrollView().equals("Rotate Up Transformer")) {
            return new RotateUpTransformer();
        } else if (getPreferencesUtility().getScrollView().equals("Scale In Out Transformer")) {
            return new ScaleInOutTransformer();
        } else if (getPreferencesUtility().getScrollView().equals("Stack Transformer")) {
            return new StackTransformer();
        } else if (getPreferencesUtility().getScrollView().equals("Tablet Transformer")) {
            return new TabletTransformer();
        } else if (getPreferencesUtility().getScrollView().equals("Zoom Out Slide Transformer")) {
            return new ZoomOutPageTransformer();
        } else {
            return new ZoomOutPageTransformer();
        }
    }

    public Typeface getStripTitleTypeFace() {
        return Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/julius-sans-one.ttf");
    }
}
/*
 * Copyright (C) 2012 jfrankie (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*

package com.mediaplayer.filter;

        import java.util.ArrayList;
        import java.util.List;

        import android.content.Context;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.BaseAdapter;
        import android.widget.Filter;
        import android.widget.Filterable;
        import android.widget.TextView;

public class PlanetAdapter extends ArrayAdapter<Planet> implements Filterable {

    private List<Planet> planetList;
    private Filter planetFilter;
    private List<Planet> origPlanetList;
    private Context context;

    public PlanetAdapter(List<Planet> planetList, Context ctx) {
        super(ctx, R.layout.img_row_layout, planetList);
        this.planetList = planetList;
        this.origPlanetList = planetList;
        this.context = ctx;
    }

    public int getCount() {
        return planetList.size();
    }

    public Planet getItem(int position) {
        return planetList.get(position);
    }

    public long getItemId(int position) {
        return planetList.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        PlanetHolder holder = new PlanetHolder();

        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.img_row_layout, null);
            // Now we can fill the layout with the right values
            TextView tv = (TextView) v.findViewById(R.id.name);
            TextView distView = (TextView) v.findViewById(R.id.dist);


            holder.planetNameView = tv;
            holder.distView = distView;

            v.setTag(holder);
        }
        else
            holder = (PlanetHolder) v.getTag();

        Planet p = planetList.get(position);
        holder.planetNameView.setText(p.getName());
        holder.distView.setText("" + p.getDistance());


        return v;
    }

    public void resetData() {
        planetList = origPlanetList;
        Log.d("pok","reset");
    }


	*/
/* *********************************
     * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************//*


    private static class PlanetHolder {
        public TextView planetNameView;
        public TextView distView;
    }

    @Override
    public Filter getFilter() {
        if (planetFilter == null)
            planetFilter = new PlanetFilter();
        Log.d("pok","getfilter");
        return planetFilter;
    }



    private class PlanetFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = origPlanetList;
                results.count = origPlanetList.size();
            }
            else {
                // We perform filtering operation
                List<Planet> nPlanetList = new ArrayList<Planet>();
                for (Planet p : planetList) {
                    if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nPlanetList.add(p);
                }

                results.values = nPlanetList;
                results.count = nPlanetList.size();


            }
            Log.d("pok","perform");
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            Log.d("pok","publish");
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                planetList = (List<Planet>) results.values;
                notifyDataSetChanged();
            }

        }

    }
}

*/
