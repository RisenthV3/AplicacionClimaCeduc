package com.example.climapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private MapView mapView;
    private double lat;
    private double lon;

    public void conseguirClima(double lat, double lon) {

        String ubicacion = lat + "," + lon;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.tomorrow.io/v4/weather/realtime?location="+ ubicacion +"&apikey=vaAbPzXyXuKIezj47XlHG0PQ6lR2aX7Y";


        // Solicitar la información en forma de String
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject respuesta = new JSONObject(response);
                            double temperatura = respuesta.getJSONObject("data").getJSONObject("values").getDouble("temperature");
                            double viento = respuesta.getJSONObject("data").getJSONObject("values").getDouble("windSpeed");

                            Toast.makeText(MainActivity.this,"Temperatura actual: " + temperatura + "° C",Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this,"Velocidad del viento actual: " + viento + " km/h",Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "That didn't work!");
            }
        });

        // Añadir la solicitud a una "queue"
        queue.add(stringRequest);


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configuration.getInstance().setUserAgentValue("myApp");
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        Context context = this;

        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));


        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        mapView.getController().setZoom(18.0); // Establece el nivel de zoom inicial

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Obtiene las coordenadas de la ubicación actual.
                lat = location.getLatitude();
                lon = location.getLongitude();

                // Centra el mapa en la ubicación actual.
                mapView.getController().setCenter(new GeoPoint(lat, lon));

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no tienes permiso, solicítalo al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Solicita actualizaciones de ubicación.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        Button darClima = findViewById(R.id.consultButton);
        darClima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                conseguirClima(lat,lon);



            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }
}