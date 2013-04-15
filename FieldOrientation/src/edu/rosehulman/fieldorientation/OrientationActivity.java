package edu.rosehulman.fieldorientation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OrientationActivity extends Activity implements FieldOrientationListener {

  /** Field orientation instance that gives field heading via sensors. */
  private FieldOrientation mFieldOrientation;

  /** Text views that will be updated. */
  private TextView mHeadingTextView, mCounterTextView,  mAzimuthTextView, mPitchTextView,
  mRollTextView, mFieldBearingTextView;

  /** Counter for the number of updates received. */
  private long mUpdatesCounter = 0;
  
  /** Force the screen and CPU to high power mode always. */
  private PowerManager.WakeLock mWakeLock;
  
  @SuppressWarnings("deprecation")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_orientation);
    
    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "Unused TAG");
    
    mHeadingTextView = (TextView) findViewById(R.id.heading_textview);
    mCounterTextView = (TextView) findViewById(R.id.counter_textview);
    mAzimuthTextView = (TextView) findViewById(R.id.azimuth_textview);
    mPitchTextView = (TextView) findViewById(R.id.pitch_textview);
    mRollTextView = (TextView) findViewById(R.id.roll_textview);
    mFieldBearingTextView = (TextView) findViewById(R.id.field_bearing_textview);

    mFieldOrientation = new FieldOrientation(this);
    Button setNeg30Button = (Button) findViewById(R.id.set_neg_30);
    setNeg30Button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mFieldOrientation.setCurrentFieldHeading(-30);
      }
    });
    Button set0Button = (Button) findViewById(R.id.set_0);
    set0Button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mFieldOrientation.setCurrentFieldHeading(0);
      }
    });
    Button set30Button = (Button) findViewById(R.id.set_30);
    set30Button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mFieldOrientation.setCurrentFieldHeading(30);
      }
    });
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    mFieldOrientation.registerListener(this);
    mWakeLock.acquire();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    mFieldOrientation.unregisterListener();
    mWakeLock.release();
  }

  @Override
  public void onSensorChanged(double fieldHeading, float[] orientationValues) {
    mUpdatesCounter++;
    mCounterTextView.setText("" + mUpdatesCounter);
    mHeadingTextView.setText(String.format(" %.1fº", fieldHeading));
    mAzimuthTextView.setText(String.format(" %.1fº", orientationValues[0]));
    mPitchTextView.setText(String.format(" %.1fº", orientationValues[1]));
    mRollTextView.setText(String.format(" %.1fº", orientationValues[2]));
    mFieldBearingTextView.setText(
        String.format(" %.1fº", mFieldOrientation.getFieldBearing()));
  }
}
