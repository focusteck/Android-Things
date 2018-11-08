package com.ft.firebase;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

/**
 * Skeleton of the main Android Things activity. Implement your device's logic
 * in this class.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 *
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private PeripheralManagerService pms;
    private Gpio redPin;
    private Gpio greenPin;
    private Gpio bluePin;


    private DatabaseReference databaseRef;
    private ValueEventListener veListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            RGBColor color = dataSnapshot.getValue(RGBColor.class);
            Log.d(TAG, "Red ["+color.getRed()+"]");
            Log.d(TAG, "Green ["+color.getGreen()+"]");
            Log.d(TAG, "Blue ["+color.getBlue()+"]");

            // update pins
            updatePin(redPin, color.getRed());
            updatePin(greenPin, color.getGreen());
            updatePin(bluePin, color.getBlue());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.addValueEventListener(veListener);
        pms = new PeripheralManagerService();
        initPin();
    }

    private void initPin() {
        try {
            redPin = pms.openGpio("BCM26");
            redPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            greenPin = pms.openGpio("BCM6");
            greenPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            bluePin = pms.openGpio("BCM5");
            bluePin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);


        }
        catch (IOException ioe) {
            Log.e(TAG, "Unable to open pins");
        }
    }

    private void updatePin(Gpio pin, int value) {
        try {
            pin.setValue( value > 0 ? false : true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
