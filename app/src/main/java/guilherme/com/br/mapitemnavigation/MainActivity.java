package guilherme.com.br.mapitemnavigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import guilherme.com.br.mapitemnavigation.POJO.Filial;
import guilherme.com.br.mapitemnavigation.POJO.Loja;
import guilherme.com.br.mapitemnavigation.ViewHolder.FilialListAdapter;

/*
#################################################################################
######   CREATED BY GUILHERME BORGES BASTOS                                 #####
######   FaceBook: https://www.facebook.com/guilherme.borgesbastos          #####
######   Linkedin: https://www.linkedin.com/in/guilhermeborgesbastos        #####
######   E-mail: Guilherme Borges Bastos                                    #####
#################################################################################
 */
public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int MY_PERMISSION_LOCATION = 10;
    public RecyclerView mRecyclerView;
    private List<Filial> mfiliais;
    private Boolean mapReady;
    private GoogleMap GMap;
    private static final String TAG = "MainActivity";
    private MapView mapView;

    /******************************************************************************************************************
     View Created (OnCreateView)
     *******************************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //verifica permissao
        // check permission
        permissionCheck();


        // GoogleMaps
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(MainActivity.this);

        buildGoogleApiClient();

    }

    private void init() {

        //lista
        mRecyclerView = (RecyclerView) findViewById(R.id.reclist);
        mRecyclerView.setHasFixedSize(true); // para performance

        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this); // é o quem vai hospedar os ViewHolder's
        llm.setOrientation(LinearLayoutManager.VERTICAL); // orientaçao
        mRecyclerView.setLayoutManager(llm);

        loadValuesFrag(mfiliais);


    }

    private void loadValuesFrag( List<Filial> _defaultList ) {

        //adaptador da lista
        FilialListAdapter ca = new FilialListAdapter(_defaultList, MainActivity.this);
        mRecyclerView.setAdapter(ca);

    }

   /******************************************************************************************************************
     Funcoes do Mapa
     Map Functions
     *******************************************************************************************************************/


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
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                LayoutInflater layoutInflater = MainActivity.this.getLayoutInflater();
                View info_balloon = layoutInflater.inflate(R.layout.info_window_layout, null);

                // get logo
                logo_info_balloon = (ImageView) info_balloon.findViewById(R.id.logo);

                changeBalloonLogo(logoPath);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                String title = arg0.getTitle();
                String snippet = arg0.getSnippet();

                // Getting reference to the TextView to set latitude
                TextView textTitle = (TextView) info_balloon.findViewById(R.id.title);

                // Getting reference to the TextView to set longitude
                TextView textSubtitle = (TextView) info_balloon.findViewById(R.id.subtitle);

                // Setting the latitude
                //textTitle.setText("Latitude:" + latLng.latitude);
                textTitle.setText(title);

                // Setting the longitude
                //textSubtitle.setText("Longitude:"+ latLng.longitude);

                String unidade = snippet.substring(0, snippet.indexOf("|"));

                textSubtitle.setText("Unidade " + unidade);

                // Returning the info_ballooniew containing InfoWindow contents
                return info_balloon;

            }
        });

    }

    private void changeBalloonLogo(String logoPath){
        if(logoPath != null) {
            Uri uri = Uri.parse(logoPath);
            Picasso.with(MainActivity.this).load(uri).into(logo_info_balloon);
        }
    }

    private void permissionCheck() {

        // location updates: at least 1 meter and 200millsecs change
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    private List<Filial> filiais;

    private void createData() {

        Double latitude = mCurrentLocation.getLatitude();
        Double longitude = mCurrentLocation.getLongitude();

        //atualiza-se o LatLng para que o ViewHolderFilial possa fazer o calculo de distâncias para cada filial
        //setLatitude(latitude);
        //setLongitude(longitude);

        // Aqui montamos as Lojas ( Hardcoded )
        // here create the stores
        /* ### STORE 1 ### */
        Loja store1 = new Loja();
        store1.setCategoria(Application.category1);

        // get current location and plus Float value to be showed dinamic on map
        // pega a localizaçao do usuário e adiciona um Float para ser mostrado dinamicamente no mapa
        store1.setLatitude((float) (latitude + 0.0009));
        store1.setLongitude((float) (longitude + 0.0005));
        //set store name
        // adiciona nome da loja
        store1.setNome_fantasia(Application.store1);
        // set store logo
        // adiciona logo da loja
        store1.setLogo(Application.store1LogoPath);


        /* ### STORE 2 ### */
        Loja store2 = new Loja();
        store2.setCategoria(Application.category2);
        // get current location and plus Float value to be showed dinamic on map
        // pega a localizaçao do usuário e adiciona um Float para ser mostrado dinamicamente no mapa
        store2.setLatitude((float) (latitude + 0.0003));
        store2.setLongitude((float) (longitude + 0.0009));
        //set store name
        // adiciona nome da loja
        store2.setNome_fantasia(Application.store2);
        // set store logo
        // adiciona logo da loja
        store2.setLogo(Application.store2LogoPath);


        /* ### STORE 3 ### */
        Loja store3 = new Loja();
        store3.setCategoria(Application.category3);
        // get current location and plus Float value to be showed dinamic on map
        // pega a localizaçao do usuário e adiciona um Float para ser mostrado dinamicamente no mapa
        store3.setLatitude((float) (latitude + 0.0003));
        store3.setLongitude((float) (longitude + 0.0009));
        //set store name
        // adiciona nome da loja
        store3.setNome_fantasia(Application.store3);
        // set store logo
        // adiciona logo da loja
        store3.setLogo(Application.store3LogoPath);



        // Aqui montamos as filiais ( Hardcoded )
        // here create the branches
        /* ### BRANCHE 1 ### */
        Filial branches1 = new Filial();
        branches1.setId(1);
        branches1.setUnidade(Application.unity + " " + Application.downTown);
                branches1.setCep("06542-955");
        branches1.setTelefone("(11) 5982-0892");
        branches1.setBairro("Centro");
        branches1.setNumero(624);
        branches1.setEndereco("Av Brasília");
        branches1.setCidade("Sao Paulo");
        branches1.setEstado("SP");
        branches1.setLoja(store1);
        branches1.setLatitude((float) (latitude - 0.0031));
        branches1.setLongitude((float) (longitude + 0.0019));

        /* ### BRANCHE 2 ### */
        Filial branches2 = new Filial();
        branches2.setId(2);
        branches2.setUnidade(Application.unity + " " + Application.downTown + "2");
        branches2.setCep("28942-955");
        branches2.setTelefone("(11) 5897-0892");
        branches2.setBairro("Centro");
        branches2.setNumero(78);
        branches2.setEndereco("Av Paulo Santo Jr.");
        branches2.setCidade("Sao Paulo");
        branches2.setEstado("SP");
        branches2.setLoja(store2);
        branches2.setLatitude((float) (latitude + 0.0043));
        branches2.setLongitude((float) (longitude - 0.0035));



        /* ### BRANCHE 5 ### */
        Filial branches5 = new Filial();
        branches5.setId(5);
        branches5.setUnidade(Application.unity + " " + Application.downTown + "5");
        branches5.setCep("58945-955");
        branches5.setTelefone("(11) 5897-0895");
        branches5.setBairro("Centro");
        branches5.setNumero(78);
        branches5.setEndereco("Av Pedro Paulo");
        branches5.setCidade("Sao Paulo");
        branches5.setEstado("SP");
        branches5.setLoja(store2);
        branches5.setLatitude((float) (latitude - 0.0043));
        branches5.setLongitude((float) (longitude - 0.0035));

        /* ### BRANCHE 3 ### */
        Filial branches3 = new Filial();
        branches3.setId(3);
        branches3.setUnidade(Application.unity + " " + Application.downTown + "3");
        branches3.setCep("38943-955");
        branches3.setTelefone("(11) 5478-0888");
        branches3.setBairro("Centro");
        branches3.setNumero(7854);
        branches3.setEndereco("Av Guilherme B. Bastos");
        branches3.setCidade("Sao Paulo");
        branches3.setEstado("SP");
        branches3.setLoja(store3);
        branches3.setLatitude((float) (latitude - 0.0024));
        branches3.setLongitude((float) (longitude + 0.0056));

        /* ### BRANCHE 4 ### */
        Filial branches4 = new Filial();
        branches4.setId(4);
        branches4.setUnidade(Application.unity + " " + Application.downTown + "4");
        branches4.setCep("58789-955");
        branches4.setTelefone("(11) 5874-0658");
        branches4.setBairro("Centro");
        branches4.setNumero(236);
        branches4.setEndereco("Av Rafael Bastos");
        branches4.setCidade("Sao Paulo");
        branches4.setEstado("SP");
        branches4.setLoja(store3);
        branches4.setLatitude((float) (latitude - 0.0039));
        branches4.setLongitude((float) (longitude + 0.0070));

        mfiliais = new ArrayList<Filial>();
        mfiliais.add(branches1);
        mfiliais.add(branches2);
        mfiliais.add(branches5);
        mfiliais.add(branches3);
        mfiliais.add(branches4);

    }



    private List<Marker> mMarkers;

    public void createMarkers(List<Filial> filiais) {

        mMarkers = new ArrayList<Marker>();

        for (int i = 0; i < filiais.size(); i++) {

            String nome_fantasia = filiais.get(i).getLoja().getNome_fantasia();
            String unidade = filiais.get(i).getUnidade() + "|" + filiais.get(i).getId();
            Float latitude = filiais.get(i).getLatitude();
            Float longitude = filiais.get(i).getLongitude();
            LatLng marker = new LatLng(latitude, longitude);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(marker);
            markerOptions.title(nome_fantasia);
            markerOptions.snippet(unidade);
            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_green);
            markerOptions.icon(markerIcon);
            Marker mMarker = GMap.addMarker(markerOptions);
            mMarkers.add(mMarker);

        }

    }

    private String logoPath = "";
    public Double userLat;
    public Double userLong;

    public void selectMarker(int id) {

        Marker marker = null;

        int _id = 0;
        for (int i = 0; i < mMarkers.size(); i++) {
            String itemInfo = mMarkers.get(i).getSnippet();
            itemInfo = itemInfo.substring(itemInfo.indexOf("|") + 1);
            _id = Integer.parseInt(itemInfo);
            if (_id == id) {
                marker = mMarkers.get(i);
                logoPath = mfiliais.get(i).getLoja().getLogo();
            }
        }

        Loja loja = null;

        if (marker == null){
            //abrir modal de add amigo
            int _id_loja = 0;
            for (int i = 0; i < mfiliais.size(); i++) {
                _id_loja = mfiliais.get(i).getId();
                if (_id_loja == id) {
                    loja = mfiliais.get(i).getLoja();
                }
            }

            return;

        }

        marker.showInfoWindow();

        Double latitude = marker.getPosition().latitude;
        Double longitude = marker.getPosition().longitude;

        //trace route usert -> filial
        userLat = mCurrentLocation.getLatitude();
        userLong = mCurrentLocation.getLongitude();

        LatLng origin = new LatLng(userLat, userLong);
        LatLng dest = new LatLng(latitude, longitude);

        // 0.002000 & 0.001200 to align the ballon
        // 0.002000 & 0.001200 para alinhar o balao de info da loja
        moveCamera(latitude  + 0.002, longitude + 0.0012);

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

        refreshRadioCircle(1000, latitude, longitude);

    }

    /*
    public void expandMap(){
        meuKloubbeBrowserFragment.expandMap();
    }
    */

    public void expandMap(Filial filial){
        //Open Intent Expand Map
        Intent mapViewIntent = new Intent(MainActivity.this, ExpandedMapActivity.class);
        mapViewIntent.putExtra("id_filial", filial.getId());
        mapViewIntent.putExtra("latitude", filial.getLatitude());
        mapViewIntent.putExtra("longitude", filial.getLongitude());
        mapViewIntent.putExtra("logo", filial.getLoja().getLogo());
        mapViewIntent.putExtra("unidade", filial.getUnidade());
        mapViewIntent.putExtra("nome_fantasia", filial.getLoja().getNome_fantasia());
        mapViewIntent.putExtra("endereco", filial.getEndereco() + ", " + filial.getNumero());
        mapViewIntent.putExtra("telefone", filial.getTelefone());
        mapViewIntent.putExtra("cep", filial.getCep());

        startActivity(mapViewIntent);
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
        if (!isGooglePlayServicesAvailable()) {
            Log.i("buildGoogleApiClient", "GooglePlayServices is not available");
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
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
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, MainActivity.this, 0).show();
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
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        // create data
        // cria as lojas e filiais
        createData();

        init();

        updateUI();
    }

    Circle mapCircle;

    private void refreshRadioCircle(int raio, Double lat, Double lng){

        //Draw Circle ---------------------------------------------------------------------
        if(mapCircle!=null){
            mapCircle.remove();
        }

        mapCircle = GMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(raio)
                .strokeWidth(3)
                .strokeColor(0x88159FDB)
                .fillColor(0x88d6f3ff));


    }

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
                .radius(600)
                .strokeWidth(1)//159fdb
                .strokeColor(0xFF159FDB)
                .fillColor(0x33159FDB));

        //Move Camera ---------------------------------------------------------------------
        moveCamera(lat, lng);

        //cria marcadores no Mapa
        createMarkers(mfiliais);

    }


    protected void stopLocationUpdates() {

        //TODO: check if Google API CLient is loaded

        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d(TAG, "Location update stopped .......................");
        }
    }

}
