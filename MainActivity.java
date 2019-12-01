package com.example.a422cassignment5;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    String address = "";
    List<Address> addresses;
    String API = "85de102c367b27067002a154bcef70cd";  //Dark Sky API
    double latitude, longitude;
    long epochSec;

    Location current;
    TextView addressTxt, updated_atTxt, tempTxt, windTxt, pressureTxt, humidityTxt, precipationTxt,
            hour1Txt, hour2Txt, hour3Txt, hour4Txt, hour5Txt, temp1Txt, temp2Txt, temp3Txt,
            temp4Txt, temp5Txt,temp48Txt, day1Txt, day2Txt, day3Txt, day4Txt, day5Txt, day6Txt, day7Txt,
            tempDay1HighTxt, tempDay2HighTxt, tempDay3HighTxt, tempDay4HighTxt, tempDay5HighTxt,
            tempDay6HighTxt, tempDay7HighTxt, tempDay1LowTxt, tempDay2LowTxt, tempDay3LowTxt,
            tempDay4LowTxt, tempDay5LowTxt,
            tempDay6LowTxt, tempDay7LowTxt,
            requestedDayTxt, requestedDayTempTxt;
    EditText userInput;
    Button submitButton;
    ImageView precipIcon;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        tempTxt = findViewById(R.id.temp);
        windTxt = findViewById(R.id.wind);
        precipationTxt = findViewById(R.id.precipation);
        pressureTxt = findViewById(R.id.pressure);
        humidityTxt = findViewById(R.id.humidity);
        hour1Txt = findViewById(R.id.hour1); hour2Txt = findViewById(R.id.hour2);
        hour3Txt = findViewById(R.id.hour3); hour4Txt = findViewById(R.id.hour4);
        hour5Txt = findViewById(R.id.hour5);
        temp1Txt = findViewById(R.id.temp1); temp2Txt = findViewById(R.id.temp2);
        temp3Txt = findViewById(R.id.temp3); temp4Txt = findViewById(R.id.temp4);
        temp5Txt = findViewById(R.id.temp5); temp48Txt = findViewById(R.id.temp48);
        day1Txt = findViewById(R.id.day1); day2Txt = findViewById(R.id.day2);
        day3Txt = findViewById(R.id.day3); day4Txt = findViewById(R.id.day4);
        day5Txt = findViewById(R.id.day5); day6Txt = findViewById(R.id.day6);
        day7Txt = findViewById(R.id.day7);
        tempDay1HighTxt = findViewById(R.id.tempDay1High); tempDay2HighTxt = findViewById(R.id.tempDay2High);
        tempDay3HighTxt = findViewById(R.id.tempDay3High); tempDay4HighTxt = findViewById(R.id.tempDay4High);
        tempDay5HighTxt = findViewById(R.id.tempDay5High); tempDay6HighTxt = findViewById(R.id.tempDay6High);
        tempDay7HighTxt = findViewById(R.id.tempDay7High);
        tempDay1LowTxt = findViewById(R.id.tempDay1Low); tempDay2LowTxt = findViewById(R.id.tempDay2Low);
        tempDay3LowTxt = findViewById(R.id.tempDay3Low); tempDay4LowTxt = findViewById(R.id.tempDay4Low);
        tempDay5LowTxt = findViewById(R.id.tempDay5Low); tempDay6LowTxt = findViewById(R.id.tempDay6Low);
        tempDay7LowTxt = findViewById(R.id.tempDay7Low);
        precipIcon = findViewById(R.id.precipicon);

        userInput = findViewById(R.id.userinput);
        submitButton = findViewById(R.id.submitbutton);
        requestedDayTxt = findViewById(R.id.requestedDay);
        requestedDayTempTxt = findViewById(R.id.requestedDayTemp);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }else{
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null) {
                        current = location;
                        latitude = current.getLatitude();
                        longitude = current.getLongitude();
                        try {
                            Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                            if (addresses.size() > 0) {
                                address = addresses.get(0).getLocality();// + ", " +  addresses.get(0).getCountryName();
                            }
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
                        new weatherTask().execute();
                        submitButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                //display.setText("Name: " + lname.getText().toString() + ", " + fname.getText().toString());
                                Calendar realDate = Calendar.getInstance();

                                Log.d("Debugging Android", String.valueOf(Integer.parseInt(userInput.getText().toString().substring(3,5))));
                                Log.d("Debugging Android", String.valueOf(Integer.parseInt(userInput.getText().toString().substring(0,2))));
                                Log.d("Debugging Android", String.valueOf(Integer.parseInt(userInput.getText().toString().substring(6,10))));
                                Log.d("Debugging Android", String.valueOf(Integer.parseInt(userInput.getText().toString().substring(11,13))));
                                Log.d("Debugging Android", String.valueOf(Integer.parseInt(userInput.getText().toString().substring(14))));


                                realDate.set(Calendar.MONTH, Integer.parseInt(userInput.getText().toString().substring(3,5)) - 1);
                                realDate.set(Calendar.DATE, Integer.parseInt(userInput.getText().toString().substring(0,2)));
                                realDate.set(Calendar.YEAR, Integer.parseInt(userInput.getText().toString().substring(6,10)));
                                realDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(userInput.getText().toString().substring(11,13)));
                                realDate.set(Calendar.MINUTE, Integer.parseInt(userInput.getText().toString().substring(14)));

                                Date completeDate = realDate.getTime();
                                epochSec = completeDate.getTime()/1000;
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                String strDate = dateFormat.format(completeDate);
                                Context context = getApplicationContext();
                                String text = String.valueOf(epochSec);
                                int duration = Toast.LENGTH_LONG;

                                Log.d("Debugging Android", strDate);
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                Log.d("Debugging Android", text);
                                new buttonTask().execute();
                            }
                        });
                    }
                }
            });
        }
    }

    class buttonTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /* Showing the ProgressBar, Making the main design GONE */
            //findViewById(R.id.loader).setVisibility(View.VISIBLE);
            //findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }
        protected String doInBackground(String... args) {
            StringBuilder sb = new StringBuilder();
            try {
                String baseURL = "https://api.darksky.net/forecast/85de102c367b27067002a154bcef70cd/";
                baseURL += latitude+","+longitude+","+epochSec;
                URL url = new URL(baseURL);
                BufferedReader in;
                in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            try {JSONObject obj = new JSONObject(result);

                Long updatedAt = Long.valueOf(obj.getJSONObject("currently").getString("time"));
                String requestedDay = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String requestedDaytemp = obj.getJSONObject("currently").getString("temperature");
                int decimal = requestedDaytemp.indexOf(".");
                requestedDaytemp = requestedDaytemp.substring(0,decimal + 2) + "°F";
                requestedDayTxt.setText(requestedDay);
                requestedDayTempTxt.setText(requestedDaytemp);

                findViewById(R.id.requestLayout).setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }



    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
            findViewById(R.id.requestLayout).setVisibility(View.GONE);
        }
        protected String doInBackground(String... args) {
            StringBuilder sb = new StringBuilder();
            try {
                String baseURL = "https://api.darksky.net/forecast/85de102c367b27067002a154bcef70cd/";
                baseURL += latitude+","+longitude;
                URL url = new URL(baseURL);
                BufferedReader in;
                in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            try {

                addressTxt.setText(address);

                JSONObject obj = new JSONObject(result);
                String temp = obj.getJSONObject("currently").getString("temperature");
                int decimal = temp.indexOf(".");
                temp = temp.substring(0,decimal + 2) + "°F";
                tempTxt.setText(temp);

                String humidity = obj.getJSONObject("currently").getString("humidity");
                int currentHumidity = Integer.parseInt(humidity.replace(".", ""));
                humidity = currentHumidity + "%";
                humidityTxt.setText(humidity);

                String windSpeed = obj.getJSONObject("currently").getString("windSpeed");
                windSpeed = windSpeed.substring(0, 4) + " MPH";
                windTxt.setText(windSpeed);

                String precipType = "";
                String precipation = obj.getJSONObject("currently").getString("precipProbability");
                int currentChance = Integer.parseInt(precipation.replace(".", ""));
                if(currentChance > 0){
                    precipType +=  obj.getJSONObject("currently").getString("precipType") + ": ";
                }
                precipation = precipType + currentChance + "%";
                precipationTxt.setText(precipation);

                if(precipType.equals("rain")){
                    precipIcon.setImageDrawable(getResources().getDrawable(R.drawable.rain));
                }
                else{
                    precipIcon.setImageDrawable(getResources().getDrawable(R.drawable.snowrain));
                }

                Long updatedAt = Long.valueOf(obj.getJSONObject("currently").getString("time"));
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                updated_atTxt.setText(updatedAtText);

                String pressure = obj.getJSONObject("currently").getString("pressure").replace(".", "");
                pressure = pressure.substring(0, pressure.length() - 1);
                pressure += " mb";
                pressureTxt.setText(pressure);

                JSONArray arr = obj.getJSONObject("hourly").getJSONArray("data");
                Long hour1Time = Long.valueOf(arr.getJSONObject(0).getString("time"));
                String hour1 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(hour1Time * 1000));
                hour1Txt.setText(hour1);
                String temp1 = arr.getJSONObject(0).getString("temperature");
                temp1Txt.setText(temp1);
                Long hour2Time = Long.valueOf(arr.getJSONObject(1).getString("time"));
                String hour2 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(hour2Time * 1000));
                hour2Txt.setText(hour2);
                String temp2 = arr.getJSONObject(1).getString("temperature");
                temp2Txt.setText(temp2);
                Long hour3Time = Long.valueOf(arr.getJSONObject(2).getString("time"));
                String hour3 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(hour3Time * 1000));
                hour3Txt.setText(hour3);
                String temp3 = arr.getJSONObject(2).getString("temperature");
                temp3Txt.setText(temp3);
                Long hour4Time = Long.valueOf(arr.getJSONObject(3).getString("time"));
                String hour4 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(hour4Time * 1000));
                hour4Txt.setText(hour4);
                String temp4 = arr.getJSONObject(3).getString("temperature");
                temp4Txt.setText(temp4);
                Long hour5Time = Long.valueOf(arr.getJSONObject(4).getString("time"));
                String hour5 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(hour5Time * 1000));
                hour5Txt.setText(hour5);
                String temp5 = arr.getJSONObject(4).getString("temperature");
                temp5Txt.setText(temp5);
                float sum = 0;
                for(int i = 0; i < 48; i++){
                    sum += Float.valueOf(arr.getJSONObject(i).getString("temperature"));
                }
                sum /= 48;
                String temp48 = String.valueOf(sum);
                int sum48Index = temp48.indexOf(".");
                temp48 = temp48.substring(0, sum48Index + 2);
                temp48Txt.setText(temp48);


                JSONArray arr2 = obj.getJSONObject("daily").getJSONArray("data");


                Long day1Time = Long.valueOf(arr2.getJSONObject(0).getString("time"));
                String day1 = new SimpleDateFormat("dd/MM", Locale.ENGLISH).format(new Date(day1Time * 1000));
                String tempDay1High = "High: " + arr2.getJSONObject(0).getString("temperatureMax");
                String tempDay1Low = "Low: " + arr2.getJSONObject(0).getString("temperatureMin");
                tempDay1HighTxt.setText(tempDay1High);
                tempDay1LowTxt.setText(tempDay1Low);
                day1Txt.setText(day1);

                Long day2Time = Long.valueOf(arr2.getJSONObject(1).getString("time"));
                String day2 = new SimpleDateFormat("dd/MM", Locale.ENGLISH).format(new Date(day2Time * 1000));
                String tempDay2High = "High: " + arr2.getJSONObject(1).getString("temperatureMax");
                String tempDay2Low = "Low: " + arr2.getJSONObject(1).getString("temperatureMin");
                tempDay2HighTxt.setText(tempDay2High);
                tempDay2LowTxt.setText(tempDay2Low);
                day2Txt.setText(day2);

                Long day3Time = Long.valueOf(arr2.getJSONObject(2).getString("time"));
                String day3 = new SimpleDateFormat("dd/MM", Locale.ENGLISH).format(new Date(day3Time * 1000));
                String tempDay3High = "High: " + arr2.getJSONObject(2).getString("temperatureMax");
                String tempDay3Low = "Low: " + arr2.getJSONObject(2).getString("temperatureMin");
                tempDay3HighTxt.setText(tempDay3High);
                tempDay3LowTxt.setText(tempDay3Low);
                day3Txt.setText(day3);

                Long day4Time = Long.valueOf(arr2.getJSONObject(3).getString("time"));
                String day4 = new SimpleDateFormat("dd/MM", Locale.ENGLISH).format(new Date(day4Time * 1000));
                String tempDay4High = "High: " + arr2.getJSONObject(3).getString("temperatureMax");
                String tempDay4Low = "Low: " + arr2.getJSONObject(3).getString("temperatureMin");
                tempDay4HighTxt.setText(tempDay4High);
                tempDay4LowTxt.setText(tempDay4Low);
                day4Txt.setText(day4);

                Long day5Time = Long.valueOf(arr2.getJSONObject(4).getString("time"));
                String day5 = new SimpleDateFormat("dd/MM", Locale.ENGLISH).format(new Date(day5Time * 1000));
                String tempDay5High = "High: " + arr2.getJSONObject(4).getString("temperatureMax");
                String tempDay5Low = "Low: " + arr2.getJSONObject(4).getString("temperatureMin");
                tempDay5HighTxt.setText(tempDay5High);
                tempDay5LowTxt.setText(tempDay5Low);
                day5Txt.setText(day5);

                Long day6Time = Long.valueOf(arr2.getJSONObject(5).getString("time"));
                String day6 = new SimpleDateFormat("dd/MM", Locale.ENGLISH).format(new Date(day6Time * 1000));
                String tempDay6High = "High: " + arr2.getJSONObject(5).getString("temperatureMax");
                String tempDay6Low = "Low: " + arr2.getJSONObject(5).getString("temperatureMin");
                tempDay6HighTxt.setText(tempDay6High);
                tempDay6LowTxt.setText(tempDay6Low);
                day6Txt.setText(day6);

                Long day7Time = Long.valueOf(arr2.getJSONObject(6).getString("time"));
                String day7 = new SimpleDateFormat("dd/MM", Locale.ENGLISH).format(new Date(day7Time * 1000));
                String tempDay7High = "High: " + arr2.getJSONObject(6).getString("temperatureMax");
                String tempDay7Low = "Low: " + arr2.getJSONObject(6).getString("temperatureMin");
                tempDay7HighTxt.setText(tempDay7High);
                tempDay7LowTxt.setText(tempDay7Low);
                day7Txt.setText(day7);


                /**JSONObject jsonObj = new JSONObject(result);
                Log.d("ADebugTag", "Value: " + result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }
}