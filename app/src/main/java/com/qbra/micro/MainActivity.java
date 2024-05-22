package com.qbra.micro;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView temperatureTextView;
    private TextView humidityTextView;
    private EditText temperatureEditText;
    private EditText humidityEditText;
    private Button refreshButton;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        temperatureEditText = findViewById(R.id.temperatureEditText);
        humidityEditText = findViewById(R.id.humidityEditText);
        refreshButton = findViewById(R.id.refreshButton);
        updateButton = findViewById(R.id.updateButton);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchSensorData().execute();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTemperature = temperatureEditText.getText().toString();
                String newHumidity = humidityEditText.getText().toString();
                new UpdateSensorData().execute(newTemperature, newHumidity);
            }
        });
    }

    private class FetchSensorData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://your_arduino_ip/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                // Basit bir şekilde yanıtı bölerek sıcaklık ve nemi gösterin
                String[] data = response.split("<br>");
                if (data.length == 2) {
                    temperatureTextView.setText(data[0].replace("Sıcaklık: ", "").replace(" °C", ""));
                    humidityTextView.setText(data[1].replace("Nem: ", "").replace(" %", ""));
                }
            }
        }
    }

    private class UpdateSensorData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String newTemperature = params[0];
                String newHumidity = params[1];
                URL url = new URL("http://your_arduino_ip/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                String postData = "temp=" + newTemperature + "&hum=" + newHumidity;
                OutputStream os = urlConnection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                // Basit bir şekilde yanıtı bölerek sıcaklık ve nemi gösterin
                String[] data = response.split("<br>");
                if (data.length == 2) {
                    temperatureTextView.setText(data[0].replace("Sıcaklık: ", "").replace(" °C", ""));
                    humidityTextView.setText(data[1].replace("Nem: ", "").replace(" %", ""));
                }
            }
        }
    }
}
