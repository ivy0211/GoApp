package student.inti.goapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerMaps extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location LastLocation;
    LocationRequest locationRequest;

    private Button logOut, settings, order;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference, driver, driverLocation, driversRef;
    private LatLng pickupLocation;
    private int radius = 1;

    private Boolean driverFound = false, requestType = false;
    private String driverID, customerID;
    Marker driverMaker, pickupMaker;
    GeoQuery geoQuery;

    private ValueEventListener listener;

    private TextView txtName, txtPhone, txtCarName;
    private CircleImageView profilePic;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customer Requests");
        driver = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        driverLocation = FirebaseDatabase.getInstance().getReference().child("Drivers Working");


        logOut = (Button) findViewById(R.id.logout_customer_btn);
        settings = (Button) findViewById(R.id.settings_customer_btn);
        order = (Button) findViewById(R.id.call_a_car_button);

        txtName = findViewById(R.id.name_driver);
        txtPhone = findViewById(R.id.phone_driver);
        txtCarName = findViewById(R.id.car_name_driver);
        profilePic = findViewById(R.id.profile_image_driver);
        relativeLayout = findViewById(R.id.rel1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerMaps.this, Settings.class);
                intent.putExtra("type", "Customers");
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                LogOutUser();
            }
        });


        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestType) {
                    requestType = false;
                    geoQuery.removeAllListeners();
                    driverLocation.removeEventListener(listener);

                    if (driverFound != null) {
                        driversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverID).child("CustomerRideID");
                        driversRef.removeValue();
                        driverID = null;
                    }
                    driverFound = false;
                    radius = 1;
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    GeoFire geoFire = new GeoFire(databaseReference);
                    geoFire.removeLocation(customerId);

                    if (pickupMaker != null) {
                        pickupMaker.remove();
                    }
                    if (driverMaker != null) {
                        driverMaker.remove();
                    }

                    order.setText("ORDER REQUEST");
                    relativeLayout.setVisibility(View.GONE);
                } else {
                    requestType = true;

                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    GeoFire geoFire = new GeoFire(databaseReference);
                    geoFire.setLocation(customerId, new GeoLocation(LastLocation.getLatitude(), LastLocation.getLongitude()));

                    pickupLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                    pickupMaker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

                    order.setText("GETTING YOUR DRIVER");
                    getClosetDriverCab();
                }
            }
        });
    }


    private void getClosetDriverCab() {
        GeoFire geoFire = new GeoFire(driver);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestType) {
                    driverFound = true;
                    driverID = key;

                    driversRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverID);
                    HashMap driversMap = new HashMap();
                    driversMap.put("CustomerRideID", customerID);
                    driversRef.updateChildren(driversMap);

                    //Show driver location on customerMapActivity
                    GettingDriverLocation();
                    order.setText("LOOKING FOR DRIVER LOCATION");
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound) {
                    radius = radius + 1;
                    getClosetDriverCab();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    //and then we get to the driver location - to tell customer where is the driver
    private void GettingDriverLocation() {
        listener = driverLocation.child(driverID).child("l")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && requestType) {
                            List<Object> driverLocationMap = (List<Object>) dataSnapshot.getValue();
                            double LocationLat = 0;
                            double LocationLng = 0;
                            order.setText("DRIVER FOUND");


                            relativeLayout.setVisibility(View.VISIBLE);
                            getAssignedDriverInformation();


                            if (driverLocationMap.get(0) != null) {
                                LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                            }
                            if (driverLocationMap.get(1) != null) {
                                LocationLng = Double.parseDouble(driverLocationMap.get(1).toString());
                            }

                            //adding marker - to pointing where driver is - using this lat lng
                            LatLng DriverLatLng = new LatLng(LocationLat, LocationLng);
                            if (driverMaker != null) {
                                driverMaker.remove();
                            }


                            Location location1 = new Location("");
                            location1.setLatitude(pickupLocation.latitude);
                            location1.setLongitude(pickupLocation.longitude);

                            Location location2 = new Location("");
                            location2.setLatitude(DriverLatLng.latitude);
                            location2.setLongitude(DriverLatLng.longitude);

                            float Distance = location1.distanceTo(location2);

                            if (Distance < 90) {
                                order.setText("DRIVER RECEIVED");
                            } else {
                                order.setText("DRIVER FOUND: " + String.valueOf(Distance));
                            }

                            driverMaker = mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("YOUR DRIVER HAS ARRIVE").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // now let set user location enable
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //

            return;
        }
        //it will handle the refreshment of the location
        //if we dont call it we will get location only once
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //getting the updated location
        LastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }


    //create this method -- for useing apis
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    public void LogOutUser() {
        Intent intent = new Intent(CustomerMaps.this, Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    private void getAssignedDriverInformation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    String riderName = dataSnapshot.child("name").getValue().toString();
                    String riderPhoneNum = dataSnapshot.child("phone").getValue().toString();
                    String riderCar = dataSnapshot.child("car").getValue().toString();

                    txtName.setText(riderName);
                    txtPhone.setText(riderPhoneNum);
                    txtCarName.setText(riderCar);

                    if (dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}