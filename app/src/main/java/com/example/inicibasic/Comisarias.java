package com.example.inicibasic;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.inicibasic.databinding.ActivityComisariasBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.PolyUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class Route {
    @SerializedName("overview_polyline")
    private OverviewPolyline overviewPolyline;

    public OverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }
}

class OverviewPolyline {
    @SerializedName("points")
    private String points;

    public String getPoints() {
        return points;
    }
}

public class Comisarias extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityComisariasBinding binding;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private LatLng origin;
    private final String apiKey = "AIzaSyCo6QNVPJ18ylCN-YxSgsbbcYhFrxytaSQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityComisariasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Obtén permiso para acceder a la ubicación del usuario
        getPermissionsLocation();

        // Obtén la ubicación actual del usuario
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getDeviceLocation();

        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);
    }

    // Pide permisos de localizacion
    private void getPermissionsLocation() {
        // Obtiene una instancia de FusedLocationProviderClient
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Solicita permiso al usuario para acceder a la ubicación del dispositivo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            // Si el permiso ha sido denegado, recuerda que lo active desde ajustes
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(Comisarias.this, "Permitir ubicación desde ajustes", Toast.LENGTH_SHORT).show();
            } else {
                // Si el permiso aún no ha sido concedido, solicita permiso al usuario
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                Toast.makeText(Comisarias.this, "Conceder permiso", Toast.LENGTH_SHORT).show();
            }

            // Obtiene la última ubicación conocida del dispositivo
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                // Si se ha obtenido una ubicación, muestra un marcador en el mapa en la ubicación actual
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Ubicación actual"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
                }
            });
        }
    }

    // Procesa la respuesta del usuario a la solicitud de permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // Si la solicitud se cancela, el array grantResults está vacío
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Comisarias.this, "El permiso se ha concedido", Toast.LENGTH_SHORT).show();
                buttonMyLocation();
            } else {
                Toast.makeText(Comisarias.this, "El permiso ha sido denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Habilita la capa de "My Location" (Mi ubicación) en el mapa para que el usuario pueda activar la ubicación
    private void buttonMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    //Obtener ubicacion del usuario
    private void getDeviceLocation() {
        try {
            boolean mLocationPermissionGranted = false;
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location mLastKnownLocation = task.getResult();
                            origin = new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                        } else {
                            Toast.makeText(Comisarias.this, "Current location is null. Using defaults.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Toast.makeText(Comisarias.this, "Exception: %s" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Evento clic en marcador
    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng destination = marker.getPosition();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");

        String orig = "origin=" + origin;
        String dest = "destination=" + destination;
        String key = "key=" + apiKey;
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + orig + "&" + dest + "&" + key;

        Request request = new Request.Builder().url(url).method("GET", body).build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // handle error
                Toast.makeText(Comisarias.this, "Fallo en el trazo de la ruta", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
                    JsonArray jsonArray = jsonObject.getAsJsonArray("routes");
                    Route route = gson.fromJson(jsonArray.get(0), Route.class);
                    final String encodedPoints = route.getOverviewPolyline().getPoints();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<LatLng> decodedPoints = PolyUtil.decode(encodedPoints);
                            PolylineOptions options = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
                            for (LatLng latLng : decodedPoints) {
                                options.add(latLng);
                            }
                            mMap.addPolyline(options);
                        }
                    });
                }
            }
        });

//        GoogleDirection.withServerKey(apiKey)
//                .from(origin)
//                .to(destination)
//                .transportMode(TransportMode.DRIVING)
//                .execute(new DirectionCallback() {
//                    @Override
//                    public void onDirectionSuccess(Direction direction, String rawBody) {
//                        if(direction.isOK()){
//                            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
//                            mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED));
//                        } else {
//                            Toast.makeText(Comisarias.this, "algo va mal", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onDirectionFailure(Throwable t) {
//                        Toast.makeText(Comisarias.this, "Fallo en el trazo de la ruta", Toast.LENGTH_SHORT).show();
//                    }
//                });
        return false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        LatLng madrid = new LatLng(40.416729, -3.703339);
        mMap.addMarker(new MarkerOptions().position(madrid).title("Marker in Madrid"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 10));

        // Add Zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        buttonMyLocation();
        searchPoliceStations();
    }


    private void searchPoliceStations() {
        // Crea una instancia de PlacesClient
        PlacesClient placesClient = Places.createClient(this);
        // Crea una sesión de búsqueda para comisarías
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        // Obtiene la ubicación actual del dispositivo
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                // Crea un objeto de FindAutocompletePredictionsRequest para establecer los parámetros de búsqueda
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery("comisaría")
                        .build();

                // Realiza la petición de búsqueda
                Task<FindAutocompletePredictionsResponse> placeResponse = placesClient.findAutocompletePredictions(request);
                placeResponse.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FindAutocompletePredictionsResponse response = task.getResult();
                        // Recorre la lista de lugares encontrados y agrega un marcador en el mapa para cada uno
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            String placeId = prediction.getPlaceId();
                            List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
                            FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, placeFields);
                            placesClient.fetchPlace(placeRequest).addOnSuccessListener(fetchPlaceResponse -> {
                                Place place = fetchPlaceResponse.getPlace();
                                LatLng latLng = place.getLatLng();
                                // Agrega un marcador en el mapa utilizando la posición del lugar
                                mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                            });
                        }
                    } else {
                        Toast.makeText(Comisarias.this, "Lugar no encontrado: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(Comisarias.this, "Ubicación actual desactivada", Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(Comisarias.this, "Buscando estaciones de policía", Toast.LENGTH_SHORT).show();
    }
}