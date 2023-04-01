package fr.ostix.worldCreator.frame;

import fr.ostix.worldCreator.entity.camera.Camera;
import org.joml.Math;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ViewportSettings extends JPanel {

    public ViewportSettings(int width, int height) {
        setBorder(BorderFactory.createTitledBorder("Parametre du view port"));
        setPreferredSize(new Dimension(width, height));
        super.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = 1;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;
        this.addSpeedSlider(gc);
    }

    // Function with a slider to change camera speed
    public void addSpeedSlider(GridBagConstraints gc) {
        this.add(createSlider("Camera speed", 2000, Camera.RUN_SPEED),gc);
    }

    private JPanel createSlider(String name, float maximum, float start) {
        JPanel panelSlider = new JPanel();
        panelSlider.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = 1;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1.0D;
        gc.weighty = 1.0D;

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(MainFrame.SMALL_FONT);
        nameLabel.setPreferredSize(new Dimension(25, 20));
        panelSlider.add(nameLabel, gc);
        gc.weightx = 1.0D;
        gc.gridx = 1;
        final JLabel valueReading = new JLabel();
        valueReading.setPreferredSize(new Dimension(50, 20));
        valueReading.setFont(MainFrame.SMALL_FONT);
        final JFloatSlider slider = new JFloatSlider(0, 20.0F, maximum, start);

        valueReading.setText(limitChars(Float.toString(slider.getActualValue()), 5));


        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                valueReading.setText(limitChars(Float.toString(slider.getActualValue()), 5));
                Camera.RUN_SPEED = slider.getActualValue();
            }
        });
        panelSlider.add(slider, gc);
        slider.setPreferredSize(new Dimension(350, 20));
        gc.gridx = 2;
        gc.weightx = 1.0D;
        panelSlider.add(valueReading, gc);
        return panelSlider;
    }

  private String limitChars(String original, int limit) {
        if (original.length() <= limit) {
            return original;
        }
        return original.substring(0, limit);
    }

}
