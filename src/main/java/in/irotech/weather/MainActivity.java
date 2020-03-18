package in.irotech.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {
    TextView longnitude;
    TextView latitude;
    TextView city;
    RelativeLayout top_view;
    TextView temp;
    TextView tempH;
    TextView tempL;
    TextView wSpeed;
    TextView wDeg;
    TextView pressure;
    TextView humidity;
    TextView sunrise;
    TextView sunset;
    TextView country;
    TextView climate;
    TextView visibility;
    View viewOfTextMode;
    View viewOfEditMode;
    private boolean onLocation;
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        top_view=findViewById(R.id.topMost);
        viewOfTextMode=getLayoutInflater().inflate(R.layout.text_viewtop,null);
        city=viewOfTextMode.findViewById(R.id.city);
        LinearLayout topLeft=viewOfTextMode.findViewById(R.id.topLeftTextMode);
        top_view.addView(viewOfTextMode);

        gestureDetector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener());

        topLeft.setOnTouchListener(onTochListen);

        longnitude=findViewById(R.id.longitude);
        latitude=findViewById(R.id.latitude);
        temp=findViewById(R.id.temprature);
        tempH=findViewById(R.id.maxTemp);
        tempL=findViewById(R.id.minTemp);
        wSpeed=findViewById(R.id.speed);
        wDeg=findViewById(R.id.deg);
        pressure=findViewById(R.id.pressure);
        humidity=findViewById(R.id.humidity);
        sunrise=findViewById(R.id.sunrise);
        sunset=findViewById(R.id.sunset);
        country=findViewById(R.id.country);
        climate=findViewById(R.id.climate);
        visibility=findViewById(R.id.visibility);
        if(getIntent().getExtras()!=null){
            onLocation=getIntent().getExtras().getBoolean("onLocation");
            city.setText(getIntent().getStringExtra("city"));
            climate.setText(getIntent().getStringExtra("climate"));
            temp.setText(getIntent().getStringExtra("temp"));
            tempH.setText(getIntent().getStringExtra("tempH"));
            tempL.setText(getIntent().getStringExtra("tempL"));
            wSpeed.setText(getIntent().getStringExtra("windSpeed"));
            wDeg.setText(getIntent().getStringExtra("windDeg"));
            pressure.setText(getIntent().getStringExtra("pressure"));
            humidity.setText(getIntent().getStringExtra("humidity"));
            sunrise.setText(getIntent().getStringExtra("sunrise"));
            sunset.setText(getIntent().getStringExtra("sunset"));
            country.setText(getIntent().getStringExtra("country"));
            longnitude.setText(getIntent().getStringExtra("longnitude"));
            visibility.setText(getIntent().getStringExtra("visibility"));
            latitude.setText(getIntent().getStringExtra("latitude"));

            ImageView pinedLocation=viewOfTextMode.findViewById(R.id.onLocationCoordinates);
            ImageView goForLocationTextMode=viewOfTextMode.findViewById(R.id.goForLocationTextMode);
            if(onLocation){
                pinedLocation.setVisibility(View.VISIBLE);
            }else {
                pinedLocation.setVisibility(View.GONE);
                goForLocationTextMode.setVisibility(View.VISIBLE);
                goForLocationTextMode.setOnClickListener(searchForLoaction);
            }
        }
    }
    Button onSubmitCity;
    EditText cityName;
    View.OnTouchListener onTochListen=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            viewOfEditMode=getLayoutInflater().inflate(R.layout.edit_viewtop,null);
            top_view.removeView(viewOfTextMode);
            onSubmitCity=viewOfEditMode.findViewById(R.id.citySubmit);
            cityName=viewOfEditMode.findViewById(R.id.cityText);
            top_view.addView(viewOfEditMode);

            ImageView goToLocationImage=viewOfEditMode.findViewById(R.id.goForLocationEditMode);
            if(!onLocation){
                goToLocationImage.setVisibility(View.VISIBLE);
            }
            goToLocationImage.setOnClickListener(searchForLoaction);

            onSubmitCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String city=cityName.getText().toString();
                    if(!city.equals("")){
                        Intent intent=new Intent(MainActivity.this,progress_bar.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("cityName",city);
                        bundle.putString("methord","getDataByCity");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
            return true;
        }
    };
    View.OnClickListener searchForLoaction=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(MainActivity.this,progress_bar.class);
            startActivity(intent);
        }
    };
}
