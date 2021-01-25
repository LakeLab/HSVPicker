package com.lakelab.hsvpickersample;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.lakelab.hsvpicker.ColorMediator;

public class MainActivityByJava extends AppCompatActivity implements ColorMediator.OnColorChangedListener {

    private final ColorMediator colorMediator = new ColorMediator();
    private MaterialCardView samplePanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorMediator.setAlphaSlider(findViewById(R.id.alpha_slider));
        colorMediator.setHueSlider(findViewById(R.id.hue_slider));
        colorMediator.setSatValPanel(findViewById(R.id.sat_val_panel));
        colorMediator.setOnColorChangedListener(this);
        samplePanel = findViewById(R.id.sample_panel);
        samplePanel.setCardBackgroundColor(colorMediator.getCurrentColor());
    }

    @Override
    public void onColorChanged(int color) {
        samplePanel.setCardBackgroundColor(color);
    }

    @Override
    public void onColorConfirmed(int color) {
        Toast.makeText(this, String.format("onColorConfirm : %s", "" + color), Toast.LENGTH_SHORT).show();
    }
}
