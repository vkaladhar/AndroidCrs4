package course.labs.weatherserviceapp.operations;

import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import course.labs.weatherserviceapp.R;
import course.labs.weatherserviceapp.activities.MainActivity;
import course.labs.weatherserviceapp.aidl.WeatherCall;
import course.labs.weatherserviceapp.aidl.WeatherData;
import course.labs.weatherserviceapp.aidl.WeatherRequest;
import course.labs.weatherserviceapp.aidl.WeatherResults;
import course.labs.weatherserviceapp.services.WeatherServiceAsync;
import course.labs.weatherserviceapp.services.WeatherServiceSync;
import course.labs.weatherserviceapp.utils.WeatherDataArrayAdapter;
import course.labs.weatherserviceapp.utils.GenericServiceConnection;
import course.labs.weatherserviceapp.utils.Utils;

/**
 * This class implements all the weather-related operations defined in
 * the WeatherOps interface.
 */
public class WeatherOpsImpl implements WeatherOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Using HashMap for Caching
     */
    protected static HashMap<String,List<WeatherData>> cachedWeatherMap=null;


    /**
     * Store previous request time
     */
    static Calendar oldReqTime = null;

    /**
     * Time difference in seconds
     */
    static final int TIME_DIFF = 30;

    /**
     * Used to enable garbage collection.
     */
    protected WeakReference<MainActivity> mActivity;
    	
    /**
     * The ListView that will display the results to the user.
     */
    protected WeakReference<ListView> mListView;

    /**
     * Weather entered by the user.
     */
    protected WeakReference<EditText> mEditText;

    /**
     * List of results to display (if any).
     */
    protected List<WeatherData> mResults;

    /**
     * A custom ArrayAdapter used to display the list of WeatherData
     * objects.
     */
    protected WeakReference<WeatherDataArrayAdapter> mAdapter;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the WeatherServiceSync Service using bindService().
     */
    private GenericServiceConnection<WeatherCall> mServiceConnectionSync;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the WeatherServiceAsync Service using bindService().
     */
    private GenericServiceConnection<WeatherRequest> mServiceConnectionAsync;

    /**
     * Constructor initializes the fields.
     */
    public WeatherOpsImpl(MainActivity activity) {
        // Initialize the WeakReference.
        mActivity = new WeakReference<>(activity);

        // Finish the initialization steps.
        initializeViewFields();
        initializeNonViewFields();
    }

    /**
     * Initialize the View fields, which are all stored as
     * WeakReferences to enable garbage collection.
     */
    private void initializeViewFields() {
        // Get references to the UI components.
        mActivity.get().setContentView(R.layout.main_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = new WeakReference<>
            ((EditText) mActivity.get().findViewById(R.id.editText1));

        // Store the ListView for displaying the results entered.
        mListView = new WeakReference<>
            ((ListView) mActivity.get().findViewById(R.id.listView1));

        // Create a local instance of our custom Adapter for our
        // ListView.
        mAdapter = new WeakReference<>
            (new WeatherDataArrayAdapter(mActivity.get()));

        // Set the adapter to the ListView.
        mListView.get().setAdapter(mAdapter.get());

        // Display results if any (due to runtime configuration change).
        if (mResults != null)
            displayResults(mResults);
    }

    /**
     * (Re)initialize the non-view fields (e.g.,
     * GenericServiceConnection objects).
     */
    private void initializeNonViewFields() {
        mServiceConnectionSync = 
            new GenericServiceConnection<WeatherCall>(WeatherCall.class);

        mServiceConnectionAsync =
            new GenericServiceConnection<WeatherRequest>(WeatherRequest.class);
    }

    /**
     * Initiate the service binding protocol.
     */
    @Override
    public void bindService() {
        Log.d(TAG, "calling bindService()");

        // Launch the Weather Bound Services if they aren't already
        // running via a call to bindService(), which binds this
        // activity to the WeatherService* if they aren't already
        // bound.
        cachedWeatherMap = new HashMap<String,List<WeatherData>>();
        if (mServiceConnectionSync.getInterface() == null) 
            mActivity.get().getApplicationContext().bindService
                (WeatherServiceSync.makeIntent(mActivity.get()),
                 mServiceConnectionSync,
                 Context.BIND_AUTO_CREATE);

        if (mServiceConnectionAsync.getInterface() == null) 
            mActivity.get().getApplicationContext().bindService
                (WeatherServiceAsync.makeIntent(mActivity.get()),
                 mServiceConnectionAsync,
                 Context.BIND_AUTO_CREATE);
    }

    /**
     * Initiate the service unbinding protocol.
     */
    @Override
    public void unbindService() {
        if (mActivity.get().isChangingConfigurations()) 
            Log.d(TAG,
                  "just a configuration change - unbindService() not called");
        else {
            Log.d(TAG,
                  "calling unbindService()");

            // Unbind the Async Service if it is connected.
            if (mServiceConnectionAsync.getInterface() != null)
                mActivity.get().getApplicationContext().unbindService
                    (mServiceConnectionAsync);

            // Unbind the Sync Service if it is connected.
            if (mServiceConnectionSync.getInterface() != null)
                mActivity.get().getApplicationContext().unbindService
                    (mServiceConnectionSync);
            //nullify the cache
            cachedWeatherMap = null;
        }

    }

    /**
     * Called after a runtime configuration change occurs to finish
     * the initialization steps.
     */
    public void onConfigurationChange(MainActivity activity) {
        Log.d(TAG,
              "onConfigurationChange() called");

        // Reset the mActivity WeakReference.
        mActivity = new WeakReference<>(activity);

        // (Re)initialize all the View fields.
        initializeViewFields();
    }

    /*
     * Initiate the asynchronous weather lookup when the user presses
     * the "Look Up Async" button.
     */
    public void getWeatherAsync(View v) {
        WeatherRequest weatherRequest = 
            mServiceConnectionAsync.getInterface();

        //Use Caching
        //get the current time will be useful for caching
        Calendar nowReqTime = Calendar.getInstance();

        if(oldReqTime != null && timeDiff(oldReqTime, nowReqTime) < TIME_DIFF) {
            Log.d(TAG, "Getting Weather Data From the Cache in ASync");
            final String weather =
                    mEditText.get().getText().toString();
            mActivity.get().runOnUiThread(new Runnable() {
                public void run() {

                    displayResults(cachedWeatherMap.get(weather));
                }
            });

        }else {
            Log.d(TAG, "Getting Weather Data From the Server in ASync");
            oldReqTime = nowReqTime;
            if (weatherRequest != null) {
                // Get the weather entered by the user.
                final String weather =
                        mEditText.get().getText().toString();

                resetDisplay();

                try {
                    // Invoke a one-way AIDL call, which does not block
                    // the client.  The results are returned via the
                    // sendResults() method of the mWeatherResults
                    // callback object, which runs in a Thread from the
                    // Thread pool managed by the Binder framework.


                    weatherRequest.getCurrentWeather(weather,
                            mWeatherResults);
                } catch (RemoteException e) {
                    Log.e(TAG,
                            "RemoteException:"
                                    + e.getMessage());
                }
            } else {
                Log.d(TAG, "weatherRequest was null.");
            }
        }
    }

    /*
     * Initiate the synchronous weather lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void getWeatherSync(View v) {
        final WeatherCall weatherCall = 
            mServiceConnectionSync.getInterface();

        //Use Caching
        //get the current time will be useful for caching
        Calendar nowReqTime = Calendar.getInstance();

        if(oldReqTime != null && timeDiff(oldReqTime, nowReqTime) < TIME_DIFF) {
            Log.d(TAG, "Getting Weather Data From Caching in Sync");
            mActivity.get().runOnUiThread(new Runnable() {
                final String weather =
                        mEditText.get().getText().toString();
                public void run() {
                    displayResults(cachedWeatherMap.get(weather));
                }
            });

        }else {
            Log.d(TAG, "Getting Weather Data From the Server in Sync");
            oldReqTime = nowReqTime;
            if (weatherCall != null) {
                // Get the weather entered by the user.
                final String weather =
                        mEditText.get().getText().toString();

                resetDisplay();

                // Use an anonymous AsyncTask to download the Weather data
                // in a separate thread and then display any results in
                // the UI thread.
                new AsyncTask<String, Void, List<WeatherData>>() {
                    /**
                     * Weather we're trying to expand.
                     */
                    private String mWeather;

                    /**
                     * Retrieve the expanded weather results via a
                     * synchronous two-way method call, which runs in a
                     * background thread to avoid blocking the UI thread.
                     */
                    protected List<WeatherData> doInBackground(String... weathers) {
                        try {
                            mWeather = weathers[0];
                            Log.d(TAG, "input weather:" + mWeather);
                            //Look into Cache and return if it matches by City, CA combination if
                            //system time passes X amount of period

                            //return from getFromCache(mWeather)
                            return weatherCall.getCurrentWeather(mWeather);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    /**
                     * Display the results in the UI Thread.
                     */
                    protected void onPostExecute(List<WeatherData> weatherDataList) {
                        if (weatherDataList.size() > 0) {
                            if(cachedWeatherMap.isEmpty() || cachedWeatherMap.containsKey(mWeather)) {
                                cachedWeatherMap.put(mWeather, weatherDataList);
                            }
                            displayResults(weatherDataList);
                        }
                        else
                            Utils.showToast(mActivity.get(),
                                    "no weather details for "
                                            + mWeather
                                            + " found");
                    }
                    // Execute the AsyncTask to expand the weather without
                    // blocking the caller.
                }.execute(weather);
            } else {
                Log.d(TAG, "mWeatherCall was null.");
            }
        }
    }

    /**
     * The implementation of the WeatherResults AIDL Interface, which
     * will be passed to the Weather Web service using the
     * WeatherRequest.getWeather() method.
     * 
     * This implementation of WeatherResults.Stub plays the role of
     * Invoker in the Broker Pattern since it dispatches the upcall to
     * sendResults().
     */
    private WeatherResults.Stub mWeatherResults = new WeatherResults.Stub() {
            /**
             * This method is invoked by the WeatherServiceAsync to
             * return the results back to the WeatherActivity.
             */
            @Override
            public void sendResults(final List<WeatherData> weatherDataList)
                throws RemoteException {
                //used for caching
                final String weather =
                        mEditText.get().getText().toString();
                // Since the Android Binder framework dispatches this
                // method in a background Thread we need to explicitly
                // post a runnable containing the results to the UI
                // Thread, where it's displayed.
                mActivity.get().runOnUiThread(new Runnable() {
                        public void run() {
                            if(cachedWeatherMap.isEmpty() || cachedWeatherMap.containsKey(weather))
                                cachedWeatherMap.put(weather, weatherDataList);
                            displayResults(weatherDataList);
                        }
                    });
            }

            /**
             * This method is invoked by the WeatherServiceAsync to
             * return error results back to the WeatherActivity.
             */
            @Override
            public void sendError(final String reason)
                throws RemoteException {
                // Since the Android Binder framework dispatches this
                // method in a background Thread we need to explicitly
                // post a runnable containing the results to the UI
                // Thread, where it's displayed.
                mActivity.get().runOnUiThread(new Runnable() {
                        public void run() {
                            Utils.showToast(mActivity.get(),
                                            reason);
                        }
                    });
            }
	};

    /**
     * Display the results to the screen.
     * 
     * @param results
     *            List of Results to be displayed.
     */
    private void displayResults(List<WeatherData> results) {
        mResults = results;

        // Set/change data set.
        mAdapter.get().clear();
        mAdapter.get().addAll(mResults);
        mAdapter.get().notifyDataSetChanged();
    }

    /**
     * Reset the display prior to attempting to expand a new weather.
     */
    private void resetDisplay() {
        Utils.hideKeyboard(mActivity.get(),
                           mEditText.get().getWindowToken());
        mResults = null;
        mAdapter.get().clear();
        mAdapter.get().notifyDataSetChanged();
    }

    private int timeDiff(Calendar oldTime, Calendar newTime){
        Log.d(TAG, "Old Time:"+oldTime+" Seconds:"+oldTime.get(Calendar.SECOND));
        Log.d(TAG, "New Time:"+newTime+" Seconds:"+newTime.get(Calendar.SECOND));
        int timeDiffInSec = 0;
        if(newTime.get(Calendar.SECOND) < oldTime.get(Calendar.SECOND))
            timeDiffInSec = (60+newTime.get(Calendar.SECOND)) - oldTime.get(Calendar.SECOND);
        else
            timeDiffInSec = newTime.get(Calendar.SECOND) - oldTime.get(Calendar.SECOND);

        Log.d(TAG, "Time Diff:"+timeDiffInSec );
        return timeDiffInSec;
    }
}
