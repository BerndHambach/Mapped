package com.example.mapped.ui.home;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapped.Adapter;
import com.example.mapped.CreateHangoutActivity;
import com.example.mapped.FilterMapActivity;
import com.example.mapped.Place;
import com.example.mapped.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private final int PERMISSION_REQUEST_CODE = 123;
    private final static int LOCATION_REQUEST_CODE = 1000;

    private final int MY_REQUEST_CODE = 50;
    Boolean locationIsSet;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private AppBarConfiguration mAppBarConfiguration;
    private MapView map;

    private MapEventsReceiver mapEventsReceiver;

    private IMapController mapController;
    //  private final int PERMISSION_REQUEST_CODE = ;
    LocationManager locationManager;
    private MyLocationNewOverlay mLocationOverlay;
    public GeoPoint standort;
    private Context ctx;
    private Marker markerstandort;

    private FirebaseDatabase mfirebaseDatabase;
    private DatabaseReference mdatabaseReference;
    private DatabaseReference categoriesdbr;

    public List<Place> placesList;
    public List<Place> allPlacesList;


    public FloatingActionButton kategoriesbutton;
    public ImageView imageMarker;
    public String markerImageUri;
    public ArrayList<Place> placesArrayList;
    public ArrayList<String> categoriesArrayList;
    public RecyclerView rvMarkerInfo;

    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;

    FloatingActionButton btnFilter;
    AlertDialog chooseCategorieAlertDialog;

    String selectedCategorie;

    Calendar calendar;
    //public Date today;
    Date choosenDate;
    public String selectedDate;
    public String today;
    public int sizeOverlays;
    //public String[] categoriesList;
    public String categorie;

    public String[] categoriesList = {"Alles im meiner Umgebung", "Sport", "Verteiler", "Nachtleben"};

    public String categoriefilter, datefilter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        checkGPS();
        map = (MapView) root.findViewById(R.id.mapView);
        showMap(map);
        navigateToCurrentLocation();


        placesList = new ArrayList<Place>();
        placesArrayList = new ArrayList<Place>();
        allPlacesList = new ArrayList<Place>();

        categoriesArrayList = new ArrayList<String>();

        selectedCategorie = new String("Alles in meiner Umgebung");
        calendar = Calendar.getInstance();

        categorie = new String();

       // calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        today = (dayOfMonth + "/" + (month + 1) + "/" + year);
        selectedDate = today;

        btnFilter = root.findViewById(R.id.fab);

        // rvMarkerInfo = (RecyclerView) root.findViewById(R.id.rvInfoMarker);
        recyclerView = (RecyclerView) root.findViewById(R.id.rvInfoMarker);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(root.getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        MapEventsOverlay mapevents = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // tiMarker.setVisibility(View.INVISIBLE);
                //    descMarker.setVisibility(View.INVISIBLE);
                //linearLayoutMarker.setVisibility(View.INVISIBLE);
                return false;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                Intent intent = new Intent(getContext(), CreateHangoutActivity.class);
                intent.putExtra("latitude", p.getLatitude());
                intent.putExtra("longitude", p.getLongitude());
                startActivity(intent);
                return true;

            }
        });
        map.getOverlayManager().add(mapevents);
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mdatabaseReference = mfirebaseDatabase.getReference("Places");

        /*categoriesdbr = mfirebaseDatabase.getReference("Categories");
        categoriesdbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.getValue() != null) {

                        categorie = snapshot.getValue().toString();
                        categoriesArrayList.add(categorie);
                       // categoriesList.
                        Toast.makeText(getView().getContext(), "categorieslist befüllt", Toast.LENGTH_SHORT).show();
                }
                    Toast.makeText(getView().getContext(), "categorieslist befüllt", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        addAllPlacesToMap(selectedCategorie, selectedDate);


       btnFilter.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getContext(), FilterMapActivity.class);
               startActivityForResult(intent, MY_REQUEST_CODE);

               /*AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
               builder.setTitle("Filter");

               final View filterLayout = getLayoutInflater().inflate(R.layout.filter_layout, null);
               builder.setView(filterLayout);

               // add a button
               builder.setPositiveButton("OK", (dialog, which) -> {
                   // send data from the AlertDialog to the Activity
                   //EditText editText = customLayout.findViewById(R.id.editText);
                   //sendDialogDataToActivity(editText.getText().toString());
               });
               // create and show the alert dialog
               AlertDialog dialog = builder.create();
               dialog.show();*/
           }
       });
       /*btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final String[] kategorieItems = {"Alles in meiner Nähe", "Sport", "Nachtleben", "Verteiler"};

                AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
                builder.setTitle("Wähle eine Kategorie");
                builder.setSingleChoiceItems(categoriesList, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        switch(item)
                        {
                            case 0:
                                selectedCategorie = categoriesList[0];
                                break;
                            case 1:
                                // Your code when 2nd  option seletced
                                selectedCategorie = categoriesList[1];
                                break;
                            case 2:
                                // Your code when 3rd option seletced
                                selectedCategorie = categoriesList[2];
                                break;
                            case 3:
                                // Your code when 3rd option seletced
                                selectedCategorie = categoriesList[3];
                                break;

                        }
                        String mselectedCategorie = selectedCategorie;
                        chooseCategorieAlertDialog.dismiss();
                        sizeOverlays = map.getOverlayManager().overlays().size();

                        for (int a = 2; a < sizeOverlays; a++)
                        {
                            map.getOverlayManager().remove(2);
                        }
                        //sizeOverlays = 0;
                        addAllPlacesToMap(selectedCategorie, selectedDate);
                    }
                });
                chooseCategorieAlertDialog = builder.create();
                chooseCategorieAlertDialog.show();

            }

       });*/
        return root;
    }

    /*private void showFilterMapDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = getView().findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.filter_layout, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MY_REQUEST_CODE) {
                if (data != null) {
                    categoriefilter = data.getStringExtra("CategorieFilter");
                    datefilter = data.getStringExtra("DateFilter");
                    sizeOverlays = map.getOverlayManager().overlays().size();

                    for (int a = 2; a < sizeOverlays; a++)
                    {
                        map.getOverlayManager().remove(2);
                    }
                    addAllPlacesToMap(categoriefilter, datefilter);

                }
            }
        }
    }

    private void addAllPlacesToMap(String filterByCategorie, String filterByDate) {
       //map.getOverlayManager().remove(0);
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null && snapshot.getValue() != null) {

                    for (DataSnapshot placeSnapshot : snapshot.getChildren()) {

                        Place place = placeSnapshot.getValue(Place.class);

                        placesArrayList.add(place);
                    }
                    //fillAllPlacesList();

                    allPlacesToMap(filterByCategorie, filterByDate);

                    adapter = new Adapter(getView().getContext(), placesArrayList);
                    // Setting Adapter to RecyclerView
                    recyclerView.setAdapter(adapter);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void allPlacesToMap(String categorieFilter, String dateFilter) {

        for (int i = 0; i < placesArrayList.size(); i++) {

            if (placesArrayList.get(i).getDate().equals(dateFilter) && categorieFilter.equals("Alles in meiner Umgebung")) {
                Marker marker = new Marker(map);
                GeoPoint markerGeopoint = new GeoPoint(placesArrayList.get(i).getLatitude(), placesArrayList.get(i).getLongitude());
                marker.setPosition(markerGeopoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                if (placesArrayList.get(i).getCategorie().equals("Sport"))
                    marker.setIcon(getView().getContext().getResources().getDrawable(R.drawable.sport));
                if (placesArrayList.get(i).getCategorie().equals("Nachtleben"))
                    marker.setIcon(getView().getContext().getResources().getDrawable(R.drawable.nachtleben));
                if (placesArrayList.get(i).getCategorie().equals("Verteiler"))
                    marker.setIcon(getView().getContext().getResources().getDrawable(R.drawable.tausch));
                // marker.setIcon(this.getResources().getDrawable(R.drawable.ic_your_location));
                map.getOverlays().add(marker);

                marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        Toast.makeText(getView().getContext(), "Marker clicked", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
            }

            else if (placesArrayList.get(i).getDate().equals(dateFilter) && placesArrayList.get(i).getCategorie().equals(categorieFilter))
            {
                Marker marker = new Marker(map);
                GeoPoint markerGeopoint = new GeoPoint(placesArrayList.get(i).getLatitude(), placesArrayList.get(i).getLongitude());
                marker.setPosition(markerGeopoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                if (placesArrayList.get(i).getCategorie().equals("Sport"))
                    marker.setIcon(getView().getContext().getResources().getDrawable(R.drawable.sport));
                if (placesArrayList.get(i).getCategorie().equals("Nachtleben"))
                    marker.setIcon(getView().getContext().getResources().getDrawable(R.drawable.nachtleben));
                if (placesArrayList.get(i).getCategorie().equals("Verteiler"))
                    marker.setIcon(getView().getContext().getResources().getDrawable(R.drawable.tausch));
                // marker.setIcon(this.getResources().getDrawable(R.drawable.ic_your_location));
                map.getOverlays().add(marker);

                marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        Toast.makeText(getView().getContext(), "Marker clicked", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
            }
        }
    }
    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
      //  String stringtitle = new String (placesList.get(0).toString());
       // String stringtitle = new String (allPlacesList.get(0).toString());
        Toast.makeText(getView().getContext(), "onViewCreated", Toast.LENGTH_SHORT).show();

    }
    public void checkGPS() {
        //Check GPS
        LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // check permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
        else return;
    }
    private void navigateToCurrentLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //mFusedLocationProviderClient.getLastLocation();
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this::onLocationReceived);

    }
    protected void onLocationReceived(Location location) {
        //    String locationText = location.getLatitude() + " | " + location.getLongitude();
        //   GeoPoint standort = new GeoPoint(location.getLatitude(), location.getLongitude());
        //String locationText = location.getLatitude() + " | " + location.getLongitude();

        if (location != null) {
            // Toast.makeText(getApplicationContext(),"location", Toast.LENGTH_SHORT).show();
            standort = new GeoPoint(location.getLatitude(), location.getLongitude());
            map.getController().setCenter(standort);
            Marker marker = new Marker(map);
            marker.setPosition(standort);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(this.getResources().getDrawable(R.drawable.ort));

            //GeoPoint standort = new GeoPoint (location.getLatitude(), location.getLongitude());
            map.getOverlays().add(marker);


        } else {
            //Toast.makeText(getApplicationContext(),"location nicht gefunden", Toast.LENGTH_SHORT).show();
        }
    }
    protected void showMap(MapView map) {

        Configuration.getInstance().setUserAgentValue(getActivity().getPackageName());
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(18.0);
    }
    private void fillAllPlacesList() {
        for (int i = 0; i < placesList.size(); i++) {

            String markerTitle = new String(String.valueOf(placesList.get(i)));

            String[] arrSplit_2 = markerTitle.split(", ");

            String title = arrSplit_2[7];
            String mTitle = title.replace("title=", "");

            String description = arrSplit_2[4];
            String mDescription = description.replace("description=", "");

            String latitude = arrSplit_2[3];
            String mlatitude = latitude.replace("latitude=", "");
            Double doublelatitude = Double.parseDouble(mlatitude);

            String longitude = arrSplit_2[8];
            String mlongitude = longitude.replace("longitude=", "");
            String finallongitude = mlongitude.replace("}", "");
            Double doublelongitude = Double.parseDouble(finallongitude);

            String imageUri = arrSplit_2[2];
            String mimageUri = imageUri.replace("imageUrl=", "");
            Uri uri = Uri.parse(mimageUri);

            String startTime = arrSplit_2[5];
            String mstartTime = startTime.replace("startTime=", "");

            String endTime = arrSplit_2[6];
            String mendTime = endTime.replace("endTime=", "");

            String date = arrSplit_2[0];
            String mdate = date.replace("{date=", "");

            String categorie = arrSplit_2[1];
            String mcategorie = categorie.replace("categorie=", "");

           // Place place = new Place(mTitle, mDescription, doublelatitude, doublelongitude, mcategorie, mimageUri, mstartTime, mendTime, mdate);
          //  placesArrayList.add(place);

        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // binding = null;
    }
}