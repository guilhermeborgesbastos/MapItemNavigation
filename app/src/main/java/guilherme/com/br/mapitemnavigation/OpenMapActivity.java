package guilherme.com.br.mapitemnavigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import guilherme.com.br.mapitemnavigation.POJO.Branche;
import guilherme.com.br.mapitemnavigation.gps.DirectionsJSONParser;


public class OpenMapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID_FILIAL = "id_filial";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String LOGO = "logo";
    private static final String UNIDADE = "unidade";
    private static final String NOME_FANTASIA = "nome_fantasia";
    private static final String TELEFONE = "telefone";
    private static final String ENDERECO = "endereco";
    private static final String CEP = "cep";
    private static final String TAG = "OpenMapActivity";
    private int id_filial;
    public float latitude;
    public float longitude;
    private String logoPath;
    private String unidade;
    private String nome_fantasia;
    private String telefone;
    private String endereco;
    private String cep;
    private GoogleMap GMap;
    private MapView mapView;
    private Boolean mapReady;


    /***********************************************************************************************
     * Fragment LifeCycle Methods
     **********************************************************************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.expanded_map_activity);

        permissionCheck();

        // GoogleMaps
        mapView = (MapView) findViewById(R.id.expanded_map);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(OpenMapActivity.this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            id_filial = extras.getInt(ID_FILIAL);
            latitude = extras.getFloat(LATITUDE);
            longitude = extras.getFloat(LONGITUDE);
            logoPath = extras.getString(LOGO);
            unidade  = extras.getString(UNIDADE);
            nome_fantasia  = extras.getString(NOME_FANTASIA);
            telefone  = extras.getString(TELEFONE);
            endereco  = extras.getString(ENDERECO);
            cep  = extras.getString(CEP);
        }


        setTitle("Voltar");

        /*
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //supportFinishAfterTransition();
                finish();
            }
        });*/

        //setSubtitle("By: Guilherme Borges Bastos");

        init();
    }

    private void init() {
        buildGoogleApiClient();
    }


    /***********************************************************************************************
     * Map Methods
     ************* **********************************************************************************/

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private ImageView logo_info_balloon;

    @Override
    public void onMapReady(GoogleMap map) {
        GMap = map;
        mapReady = true;
        if (ActivityCompat.checkSelfPermission(OpenMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OpenMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GMap.setMyLocationEnabled(true);
        //moveCamera(-23.543628, -46.658199);
        //GMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        createMarker();

        GMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                LayoutInflater layoutInflater = OpenMapActivity.this.getLayoutInflater();
                View info_balloon = layoutInflater.inflate(R.layout.info_window_layout, null);

                // get logo
                logo_info_balloon = (ImageView) info_balloon.findViewById(R.id.logo);

                changeBalloonLogo(logoPath);

                // Getting reference to the TextView to set latitude
                TextView textTitle = (TextView) info_balloon.findViewById(R.id.title);

                // Getting reference to the TextView to set longitude
                TextView textSubtitle = (TextView) info_balloon.findViewById(R.id.subtitle);
                TextView textTelefone = (TextView) info_balloon.findViewById(R.id.telefone);
                TextView textEndereco = (TextView) info_balloon.findViewById(R.id.endereco);
                TextView textCep = (TextView) info_balloon.findViewById(R.id.cep);

                // Setting the latitude
                //textTitle.setText("Latitude:" + latLng.latitude);
                textTitle.setText(nome_fantasia);
                textTelefone.setText(telefone);
                textEndereco.setText(endereco);
                textCep.setText("CEP: " + cep);

                textSubtitle.setText("Unidade " + unidade);

                // Returning the info_ballooniew containing InfoWindow contents
                return info_balloon;

            }
        });

    }

    private void changeBalloonLogo(String logoPath){
        if(logoPath != null) {
            Uri uri = Uri.parse(logoPath);
            Picasso.with(OpenMapActivity.this).load(uri).into(logo_info_balloon);
        }
    }

    private void permissionCheck() {

        // location updates: at least 1 meter and 200millsecs change
        if (ActivityCompat.checkSelfPermission(OpenMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OpenMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OpenMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    private List<Marker> mMarkers;

    public void createMarker() {

        mMarkers = new ArrayList<Marker>();

        LatLng marker = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(marker);
        markerOptions.title(nome_fantasia);
        markerOptions.snippet(unidade);
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        markerOptions.icon(markerIcon);
        Marker mMarker = GMap.addMarker(markerOptions);
        mMarkers.add(mMarker);


    }

    private List<Branche> mfiliais;

    public void selectMarker() {

        Marker marker = mMarkers.get(0);

        marker.showInfoWindow();

        //trace route usert -> branche
        Double userLat = mCurrentLocation.getLatitude();
        Double userLong = mCurrentLocation.getLongitude();

        LatLng origin = new LatLng(userLat, userLong);
        LatLng dest = new LatLng(latitude, longitude);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

        moveCamera(latitude  + 0.002, longitude + 0.0012); // 0.002000 & 0.001200 para alinhar o balao de info da loja

    }

    private void moveCamera(Double latitude, Double longitude) {
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))
                        .bearing(45)
                        .tilt(0)
                        .zoom(15)
                        .build();

        GMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);

    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /******************************************************************************************************************

     Android Location Fused Provider
     http://javapapers.com/android/android-location-fused-provider/

     *******************************************************************************************************************/

    private GoogleApiClient mGoogleApiClient;
    private static final long INTERVAL = 1000 * 30;
    private static final long FASTEST_INTERVAL = 1000 * 30;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String mLastUpdateTime;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(OpenMapActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(OpenMapActivity.this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, OpenMapActivity.this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    private LocationListener locationListener;

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(OpenMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OpenMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    Circle mapCircle;

    Boolean firstTime = true;

    private void updateUI() {

        if(!firstTime){
            return;
        }


        if (mCurrentLocation ==null) return;
        Double lat = mCurrentLocation.getLatitude();
        Double lng = mCurrentLocation.getLongitude();

        //Log ---------------------------------------------------------------------
        Log.i(TAG, "At Time: " + mLastUpdateTime + "\n" +
                "Latitude: " + lat + "\n" +
                "Longitude: " + lng + "\n" +
                "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                "Provider: " + mCurrentLocation.getProvider());


        //Draw Circle ---------------------------------------------------------------------
        if(mapCircle!=null){
            mapCircle.remove();
        }

        mapCircle = GMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(400)
                .strokeWidth(1)
                .strokeColor(0xFF00FF00)
                .fillColor(0x3300FF00));

        //Move Camera ---------------------------------------------------------------------
        moveCamera(lat, lng);

        selectMarker();
    }


    protected void stopLocationUpdates() {

        //TODO: check if Google API CLient is loaded

        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            Log.d(TAG, "Location update stopped .......................");
        }
    }


    public void searchMode(){

    }

    /*
    ####################### GUILHERME BORGES BASTOS #########################
    ##   CALCULA A MELHOR ROTA ENTRE O USUÄRIO E A FILIAL NO MAPA     #######
    #########################################################################
    */
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);

            }
            data = sb.toString();
            br.close();


        }catch(Exception e){
            Log.d("Exc. while down url", e.toString());

        }finally{
            iStream.close();
            urlConnection.disconnect();

        }
        return data;

    }

    public void setfiliais(List<Branche> mfiliais) {
        this.mfiliais = mfiliais;
    }

    // Fetches data from url passed
    public class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    public Polyline polylineFinal;

    /** A class to parse the Google Places in JSON format */
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }


        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            if(polylineFinal != null){
                polylineFinal.remove();
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            polylineFinal = GMap.addPolyline(lineOptions);

            //da Zoom a onde a pessoa está
            //findHer(friendPoint);

        }
    }


}
