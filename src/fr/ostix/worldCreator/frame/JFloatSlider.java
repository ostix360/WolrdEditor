package fr.ostix.worldCreator.frame;

import javax.swing.*;

public class JFloatSlider extends JSlider {

    private float min;
    private float scale;

    public JFloatSlider(int orientation, float min, float max, float start) {
        super(orientation, (int) min, (int) max, (int) start);
        this.min = min;
        this.scale = (max - min);
    }

    public float getActualValue() {
        return super.getValue();
    }
}
