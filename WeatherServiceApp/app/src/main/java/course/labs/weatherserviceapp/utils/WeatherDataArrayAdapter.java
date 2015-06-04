package course.labs.weatherserviceapp.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import  course.labs.weatherserviceapp.R;
import  course.labs.weatherserviceapp.aidl.WeatherData;

/**
 * Custom ArrayAdapter for the WeatherData class, which makes each row
 * of the ListView have a more complex layout than just a single
 * textview (which is the default for ListViews).
 */
public class WeatherDataArrayAdapter extends ArrayAdapter<WeatherData> {

    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Construtor that declares which layout file is used as the
     * layout for each row.
     */
    public WeatherDataArrayAdapter(Context context) {
        super(context, R.layout.weather_data_row);
    }

    /**
     * Construtor that declares which layout file is used as the
     * layout for each row.
     */
    public WeatherDataArrayAdapter(Context context,
                                   List<WeatherData> objects) {
        super(context, R.layout.weather_data_row, objects);
        Log.d(TAG, "Objects Size:"+objects.size());
    }

    /**
     * Method used by the ListView to "get" the "view" for each row of
     * data in the ListView.
     * 
     * @param position
     *            The position of the item within the adapter's data set of the
     *            item whose view we want. convertView The old view to reuse, if
     *            possible. Note: You should check that this view is non-null
     *            and of an appropriate type before using. If it is not possible
     *            to convert this view to display the correct data, this method
     *            can create a new view. Heterogeneous lists can specify their
     *            number of view types, so that this View is always of the right
     *            type (see getViewTypeCount() and getItemViewType(int)).
     * @param parent
     *            The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        //WeatherHolder holder = null;
        WeatherData data = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.weather_data_row,
                                                                    parent,
                                                                    false);
            //holder = new WeatherHolder();
            //holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
           // holder.txtValue = (TextView) convertView.findViewById(R.id.txtValue);
            //convertView.setTag(holder);

        } else
        {
            //holder = (WeatherHolder)convertView.getTag();
        }


        //holder.txtTitle.setText("Name:");
       // holder.txtValue.setText(data.mName);



        TextView weatherName =
            (TextView) convertView.findViewById(R.id.weather_name);
        Log.d(TAG,"weatherName:"+weatherName.getText());

        TextView weatherSpeed =
            (TextView) convertView.findViewById(R.id.weather_speed);
        Log.d(TAG,"weatherSpeed:"+weatherSpeed.getText());

        TextView weatherDeg =
            (TextView) convertView.findViewById(R.id.weather_deg);
        Log.d(TAG,"weatherDeg:"+weatherDeg.getText());

        TextView weatherTemp =
                (TextView) convertView.findViewById(R.id.weather_temp);
        Log.d(TAG,"weatherTemp:"+weatherTemp.getText());

        TextView weatherHumidity =
                (TextView) convertView.findViewById(R.id.weather_humidity);
        Log.d(TAG,"weatherHumidity:"+weatherHumidity.getText());

        TextView weatherSunrise =
                (TextView) convertView.findViewById(R.id.weather_sunrise);
        Log.d(TAG,"weatherSunrise:"+weatherSunrise.getText());

        TextView weatherSunset =
                (TextView) convertView.findViewById(R.id.weather_sunset);
        Log.d(TAG,"weatherSunset:"+weatherSunset.getText());

       // TODO UnComments and adjust the code
       weatherName.setText("Name: "+data.mName);
        weatherSpeed.setText("Speed: "+data.mSpeed);
        weatherDeg.setText("Degree: "+data.mDeg);
        weatherTemp.setText("Temp: "+data.mTemp);
        weatherHumidity.setText("Humidity: "+data.mHumidity);
        weatherSunrise.setText("Sunrise: "+data.mSunrise);
        weatherSunset.setText("Sunset: "+data.mSunset);

        return convertView;
    }

    static class WeatherHolder
    {
        TextView txtTitle;
        TextView txtValue;
    }
}
