package ru.olegslobodin.speedometer;

import android.app.FragmentManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView latituteField;
    TextView longitudeField;
    TextView speedField;
    TextView distanceField;

    double latitude_old = 0;
    double longitude_old = 0;
    long time_old = System.currentTimeMillis();
    long distance = 0;
    boolean first_measure = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            MyDialogFragment myDialogFragment = new MyDialogFragment();
            FragmentManager manager = getFragmentManager();
            myDialogFragment.show(manager, "dialog");
        }

        latituteField = (TextView) findViewById(R.id.textView1);
        longitudeField = (TextView) findViewById(R.id.textView2);
        speedField = (TextView) findViewById(R.id.textView3);
        distanceField = (TextView) findViewById(R.id.textView4);

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        locationManager.requestLocationUpdates(
                provider, 1000, 0, new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }

                    @Override
                    public void onLocationChanged(final Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        long time = System.currentTimeMillis();
                        latituteField.setText(String.valueOf(latitude));
                        longitudeField.setText(String.valueOf(longitude));
                        double f1 = latitude * Math.PI / 180;
                        double f2 = latitude_old * Math.PI / 180;
                        double dx = Math.abs(f1 - f2);
                        double dy = Math.abs(longitude - longitude_old) * Math.PI / 180;
                        double last_distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(dx / 2), 2) + Math.cos(f1) * Math.cos(f2) * Math.pow(Math.sin(dy / 2), 2))) * 6372795;
                        if (first_measure) {
                            first_measure = false;
                            last_distance = 0;
                        }
                        distance += last_distance;
                        speedField.setText(String.valueOf((int) (last_distance / (time - time_old) * 1000 * 3.6)));
                        distanceField.setText(String.valueOf((int) distance));

                        /*System.out.println("#######################################");
                        System.out.println("#######################################");
                        System.out.println("#######################################");
                        System.out.println("latitude = " + f1);
                        System.out.println("latitude_old = " + f2);
                        System.out.println("dx = " + dx);
                        System.out.println("dy = " + dy);
                        System.out.println("distance = " + distance);*/

                        latitude_old = latitude;
                        longitude_old = longitude;
                        time_old = time;
                    }
                });
    }

    public void clear_button_clicked(View view) {
        distance = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
