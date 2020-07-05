package com.cepheuen.elegantnumberbuttonsample;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    public ElegantNumberButton btn1;
    public ElegantNumberButton btn2;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.number_button);
        btn2 = findViewById(R.id.number_button2);

        textView = findViewById(R.id.text_view);

        btn1.setRange(1, 5);
        btn1.setOnClickListener((ElegantNumberButton.OnClickListener) view -> {
            String number = btn1.getNumber();
            textView.setText(number);
            btn1.setNumber(number);
        });
        btn1.setOnValueChangeListener((view, oldValue, newValue) -> {
            Log.d(TAG, String.format("oldValue: %f   newValue: %f", oldValue, newValue));
        });

        btn2.setOnClickListener((ElegantNumberButton.OnClickListener) view -> {
            String number = btn2.getNumber();
            textView.setText(number);
            btn2.setNumber(number);
        });
        btn2.setOnValueChangeListener((view, oldValue, newValue) -> {
            Log.d(TAG, String.format("oldValue: %f   newValue: %f", oldValue, newValue));
        });
    }
}
