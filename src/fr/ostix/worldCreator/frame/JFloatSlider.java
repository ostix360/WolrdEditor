package fr.ostix.worldCreator.frame;

import javax.swing.*;

public class JFloatSlider extends JSlider {

    private float min;
    private float scale;

    public JFloatSlider(int orientation, float min, float max, float start) {
        super(orientation, 0, 200, (int) (200.0F * ((start - min) / (max - min))));
        this.min = min;
        this.scale = (max - min);
    }

    public float getActualValue() {
        return super.getValue() / 200.0F * this.scale + this.min;
    }
}
