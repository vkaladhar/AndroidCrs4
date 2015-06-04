package course.labs.weatherserviceapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import course.labs.weatherserviceapp.operations.WeatherOps;
import course.labs.weatherserviceapp.operations.WeatherOpsImpl;
import course.labs.weatherserviceapp.utils.RetainedFragmentManager;

/**
 * The main Activity that prompts the user for Weathers to expand via
 * various implementations of WeatherServiceSync and
 * WeatherServiceAsync and view via the results.  Extends
 * LifecycleLoggingActivity so its lifecycle hook methods are logged
 * automatically.
 */
public class MainActivity extends LifecycleLoggingActivity {
    /**
     * Used to retain the ImageOps state between runtime configuration
     * changes.
     */
    protected final RetainedFragmentManager mRetainedFragmentManager = 
        new RetainedFragmentManager(this.getFragmentManager(),
                                    TAG);

    /**
     * Provides weather-related operations.
     */
    private WeatherOps mWeatherOps;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., runtime
     * configuration changes.
     *
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Create the WeatherOps object one time.
        //is this necessary - Vis
        mWeatherOps = new WeatherOpsImpl(this);

        // Handle any configuration change.
        handleConfigurationChanges();
    }

    /**
     * Hook method called by Android when this Activity is
     * destroyed.
     */
    @Override
    protected void onDestroy() {
        // Unbind from the Service.
        mWeatherOps.unbindService();

        // Always call super class for necessary operations when an
        // Activity is destroyed.
        super.onDestroy();
    }

    /**
     * Handle hardware reconfigurations, such as rotating the display.
     */
    protected void handleConfigurationChanges() {
        // If this method returns true then this is the first time the
        // Activity has been created.
        if (mRetainedFragmentManager.firstTimeIn()) {
            Log.d(TAG,
                  "First time onCreate() call");

            // Create the WeatherOps object one time.  The "true"
            // parameter instructs WeatherOps to use the
            // DownloadWeatherBoundService.
            mWeatherOps = new WeatherOpsImpl(this);

            // Store the WeatherOps into the RetainedFragmentManager.
            mRetainedFragmentManager.put("WEATHER_OPS_STATE",
                    mWeatherOps);
            
            // Initiate the service binding protocol (which may be a
            // no-op, depending on which type of DownloadImages*Service is
            // used).
            mWeatherOps.bindService();
        } else {
            // The RetainedFragmentManager was previously initialized,
            // which means that a runtime configuration change occured.

            Log.d(TAG,
                  "Second or subsequent onCreate() call");

            // Obtain the WeatherOps object from the
            // RetainedFragmentManager.
            mWeatherOps =
                mRetainedFragmentManager.get("WEATHER_OPS_STATE");

            // This check shouldn't be necessary under normal
            // circumtances, but it's better to lose state than to
            // crash!
            if (mWeatherOps == null) {
                // Create the WeatherOps object one time.  The "true"
                // parameter instructs WeatherOps to use the
                // DownloadImagesBoundService.
                mWeatherOps = new WeatherOpsImpl(this);

                // Store the WeatherOps into the RetainedFragmentManager.
                mRetainedFragmentManager.put("ACRONYM_OPS_STATE",
                        mWeatherOps);

                // Initiate the service binding protocol (which may be
                // a no-op, depending on which type of
                // DownloadImages*Service is used).
                mWeatherOps.bindService();
            } else
                // Inform it that the runtime configuration change has
                // completed.
                mWeatherOps.onConfigurationChange(this);
        }
    }

    /*
     * Initiate the synchronous weather lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void getWeatherSync(View v) {
        mWeatherOps.getWeatherSync(v);
    }

    /*
     * Initiate the asynchronous weather lookup when the user presses
     * the "Look Up Async" button.
     */
    public void getWeatherAsync(View v) {
        mWeatherOps.getWeatherAsync(v);
    }
}
