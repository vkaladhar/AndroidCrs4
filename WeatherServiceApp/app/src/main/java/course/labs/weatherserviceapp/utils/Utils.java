package course.labs.weatherserviceapp.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import course.labs.weatherserviceapp.aidl.WeatherData;
import course.labs.weatherserviceapp.jsonweather.WeatherJSONParser;
import course.labs.weatherserviceapp.jsonweather.JsonWeather;

/**
 * @class WeatherDownloadUtils
 *
 * @brief Handles the actual downloading of Weather information from
 *        the Weather web service.
 */
public class Utils {
    /**
     * Logging tag used by the debugger. 
     */
    private final static String TAG = Utils.class.getCanonicalName();

    /** 
     * URL to the Weather web service.
     */
    private final static String sWeather_Web_Service_URL =
        "http://api.openweathermap.org/data/2.5/weather?q=";

    /**
     * Obtain the Weather information.
     * 
     * @return The information that responds to your current weather search.
     */
    public static List<WeatherData> getResults(final String weatherIn) {

        // Create a List that will return the WeatherData obtained
        // from the Weather Service web service.

        final List<WeatherData> returnList =
            new ArrayList<WeatherData>();
            
        // A List of JsonWeather objects.
        List<JsonWeather> jsonWeathers = null;

        try {
            // Append the location to create the full URL.
            final URL url =
                new URL(sWeather_Web_Service_URL
                        + weatherIn);

            // Opens a connection to the Weather Service.
            HttpURLConnection urlConnection =
                (HttpURLConnection) url.openConnection();
            
            // Sends the GET request and reads the Json results.
            try (InputStream in =
                 new BufferedInputStream(urlConnection.getInputStream())) {
                 // Create the parser.
                 final WeatherJSONParser parser = new WeatherJSONParser();

                // Parse the Json results and create JsonWeather data
                // objects.
                jsonWeathers = parser.parseJsonStream(in);
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonWeathers != null && jsonWeathers.size() > 0) {
            // Convert the JsonWeather data objects to our WeatherData
            // object, which can be passed between processes.
            for (JsonWeather jsonWeather : jsonWeathers)
                //Done UnComments and adjust the code
                returnList.add(new WeatherData(jsonWeather.getName(),
                        jsonWeather.getWind().getSpeed(),
                        jsonWeather.getWind().getDeg(),
                        jsonWeather.getMain().getTemp(),
                        jsonWeather.getMain().getHumidity(),
                        jsonWeather.getSys().getSunrise(),
                        jsonWeather.getSys().getSunset()));

             // Return the List of WeatherData.
             return returnList;
        }  else
            return null;
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
           (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                       message,
                       Toast.LENGTH_SHORT).show();
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    }


    public static void main(String args[]){
        System.out.println(getResults("Tustin,CA"));

    }
}
