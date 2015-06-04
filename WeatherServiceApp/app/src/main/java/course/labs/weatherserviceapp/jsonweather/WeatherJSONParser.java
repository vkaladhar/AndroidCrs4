package course.labs.weatherserviceapp.jsonweather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of JsonWeather objects that contain this data.
 */
public class WeatherJSONParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();

    /**
     * Parse the @a inputStream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonStream(InputStream inputStream)
        throws IOException {
        Log.d(TAG, "In parseJsonStream ----------");
        // TODO -- you fill in here.
        // Create a JsonReader for the inputStream.

       try (JsonReader reader =
                     new JsonReader(new InputStreamReader(inputStream,
                             "UTF-8"))) {
            // Log.d(TAG, "Parsing the results returned as an array");

            // Handle the array returned from the Acronym Service.
            return parseJsonWeatherArray(reader);
        }
        //return null;
    }

    /**
     * Parse a single Json stream and convert it into a JsonWeather
     * object.
     */
    public JsonWeather parseJsonStreamSingle(JsonReader reader)
        throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG, "In parseJsonStreamSingle ----------");
        return parseJsonWeather(reader);
    }

    /**
     * Parse a Json stream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonWeatherArray(JsonReader reader)
        throws IOException {

        // TODO -- you fill in here.
        Log.d(TAG, "In parseJsonWeatherArray **********: "+reader);

       // reader.beginObject();

        try {
            List<JsonWeather> weathers = new ArrayList<JsonWeather>();

            //while (reader.hasNext())
             weathers.add(parseJsonWeather(reader));

            return weathers;
        } finally {
           // reader.endObject();
        }
    }

    /**
     * Parse a Json stream and return a JsonWeather object.
     */
    public JsonWeather parseJsonWeather(JsonReader reader) 
        throws IOException {

        Log.d(TAG, "In parseJsonWeather ----------:"+ reader);

        reader.beginObject();

        JsonWeather weather = new JsonWeather();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                Log.d(TAG,"Json Name:"+name );

                switch (name) {
                    case JsonWeather.name_JSON:
                        weather.setName(reader.nextString());
                        //Log.d(TAG, "JsonWeather.name_JSON " + reader.nextString());
                        break;
                    case JsonWeather.wind_JSON:
                        weather.setWind(parseWind(reader));
                        //Log.d(TAG, "JsonWeather.wind_JSON " + acronym.getFreq());
                        break;
                    case JsonWeather.main_JSON:
                        weather.setMain(parseMain(reader));
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case JsonWeather.sys_JSON:
                        weather.setSys(parseSys(reader));
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case JsonWeather.weather_JSON:
                        if (reader.peek() == JsonToken.BEGIN_ARRAY)
                            weather.setWeather(parseWeathers(reader));
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
//                    case JsonWeather.base_JSON:
//                        reader.skipValue();
//                        // Log.d(TAG, "ignoring " + name);
//                        break;
//                    case JsonWeather.cod_JSON:
//                        reader.skipValue();
//                        // Log.d(TAG, "ignoring " + name);
//                        break;
//                    case JsonWeather.dt_JSON:
//                        reader.skipValue();
//                        // Log.d(TAG, "ignoring " + name);
//                        break;
//                    case JsonWeather.id_JSON:
//                        reader.skipValue();
//                        // Log.d(TAG, "ignoring " + name);
//                        break;
                    default:
                        reader.skipValue();
                         Log.d(TAG, "In parseJsonWeather ignoring: " + name);
                        break;
                }

            }
        } finally {
            reader.endObject();
        }
        return weather;
    }


    /**
     * Parse a Json stream and return a List of Weather objects.
     */
    public List<Weather> parseWeathers(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG, "In parseWeathers ******** ");
        reader.beginArray();

        try {
            List<Weather> weatherInJson = new ArrayList<Weather>();
            if (reader.peek() == JsonToken.END_ARRAY)
                return null;

            while (reader.hasNext())
                weatherInJson.add(parseWeather(reader));

            return weatherInJson;
        } finally {
            reader.endArray();
        }

    }

    /**
     * Parse a Json stream and return a Weather object.
     */
    public Weather parseWeather(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG, "In parseWeather --------");
        reader.beginObject();

        Weather weatherInJson = new Weather();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                Log.d(TAG,"Name:"+name );

                switch (name) {
                    case Weather.description_JSON:
                        weatherInJson.setDescription(reader.nextString());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case Weather.id_JSON:
                        weatherInJson.setId(reader.nextLong());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case Weather.main_JSON:
                        weatherInJson.setMain(reader.nextString());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    default:
                        reader.skipValue();
                        Log.d(TAG, "In parseWeather ignoring " + name);
                        break;
                }

            }
        } finally {
            reader.endObject();
        }
        Log.d(TAG, "End parseWeather --------");
        return weatherInJson;
    }
    
    /**
     * Parse a Json stream and return a Main Object.
     */
    public Main parseMain(JsonReader reader) 
        throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG, "In parseMain --------");
        reader.beginObject();

        //JsonWeather weather = new JsonWeather();
        Main jsonMain = new Main();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                Log.d(TAG,"Name:"+name );

                switch (name) {
                    case Main.temp_JSON:
                        jsonMain.setTemp(reader.nextDouble());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case Main.humidity_JSON:
                        jsonMain.setHumidity(reader.nextLong());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case Main.pressure_JSON:
                        jsonMain.setPressure(reader.nextDouble());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    default:
                        reader.skipValue();
                        Log.d(TAG, "In parseMain ignoring " + name);
                        break;
                }

            }
        } finally {
            reader.endObject();
        }
        Log.d(TAG, "End parseMain --------");
        return jsonMain;
    }

    /**
     * Parse a Json stream and return a Wind Object.
     */
    public Wind parseWind(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        reader.beginObject();
        Log.d(TAG, "In parseWind --------");

        //JsonWeather weather = new JsonWeather();
        //Wind jsonWind = weather.getWind();
        Wind jsonWind = new Wind();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                Log.d(TAG,"Name:"+name );

                switch (name) {
                    case Wind.speed_JSON:
                        jsonWind.setSpeed(reader.nextDouble());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case Wind.deg_JSON:
                        jsonWind.setDeg(reader.nextDouble());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    default:
                        reader.skipValue();
                        Log.d(TAG, "In parseWind ignoring " + name);
                        break;
                }

            }
        } finally {
            reader.endObject();
        }
        Log.d(TAG, "Before Return " + jsonWind.toString());
        return jsonWind;
    }

    /**
     * Parse a Json stream and return a Sys Object.
     */
    public Sys parseSys(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG, "In parseSys --------:");
        reader.beginObject();

        //JsonWeather weather = new JsonWeather();
        Sys jsonSys = new Sys();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                Log.d(TAG,"Name:"+name );

                switch (name) {
                    case Sys.sunrise_JSON:
                        jsonSys.setSunrise(reader.nextLong());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case Sys.sunset_JSON:
                        jsonSys.setSunset(reader.nextLong());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case Sys.message_JSON:
                        jsonSys.setMessage(reader.nextDouble());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    case Sys.country_JSON:
                        jsonSys.setCountry(reader.nextString());
                        // Log.d(TAG, "reading since " + acronym.getSince());
                        break;
                    default:
                        reader.skipValue();
                        Log.d(TAG, "In parseSys ignoring: " + name);
                        break;
                }

            }
        } finally {
            reader.endObject();
        }
        Log.d(TAG, "End In parseSys --------:");
        return jsonSys;
    }
}
