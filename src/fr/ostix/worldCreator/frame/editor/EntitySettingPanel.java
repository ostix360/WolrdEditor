package fr.ostix.worldCreator.frame.editor;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.frame.*;
import org.joml.Math;
import org.joml.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class EntitySettingPanel extends JPanel {
    private MainFrame frame;
    private Entity entity;
    private Vector3f position;
    private float x, y, z;

    public EntitySettingPanel(Entity e, int width, int height, MainFrame frame) {
        this.entity = e;
        this.position = e.getPosition();
        this.frame = frame;
        this.setup();
        this.x = position.x();
        this.y = position.y();
        this.z = position.z();
    }

    private void setup() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        //this.setPreferredSize(new Dimension(width,height));
        gc.fill = 3;
        gc.gridx = 1;
        gc.gridy = 0;
        gc.gridwidth = 0;
        this.addRefreshButton(gc);
        gc.gridx = 0;
        gc.gridwidth = 1;
        this.addDeleteButton(gc);
        gc.gridy = 1;
        gc.gridwidth = 3;
        addSlider(gc, "X");
        gc.gridy = 2;
        addSlider(gc, "Y");
        gc.gridy = 3;
        addSlider(gc, "Z");

    }

    private void addRefreshButton(GridBagConstraints gc) {
        final JButton btn = new JButton("Rafraichire les collisions");
        btn.setPreferredSize(new Dimension(100, 25));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getWorld().refreshCollisions();
            }
        });
        this.add(btn, gc);
    }

    private void addDeleteButton(GridBagConstraints gc) {
        final JButton btn = new JButton("Supprimé l'entité");
        btn.setPreferredSize(new Dimension(100, 25));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getWorld().remove(entity, false);
            }
        });
        this.add(btn, gc);
    }

    private void addSlider(GridBagConstraints gc, String variable) {
        float value = (float) 0;
        switch (variable.toLowerCase()) {
            case "x":
                value = this.position.x();
                break;
            case "y":
                value = this.position.y();
                break;
            case "z":
                value = this.position.z();
                break;
            default:
                new IllegalArgumentException(variable + " is not define").printStackTrace();
        }
        String sValue = limitChars(String.valueOf(value), 3);
        SpinnerNumberModel model = new SpinnerNumberModel(Float.valueOf(sValue), null, null, 1);
        final JSpinner spinner = new JSpinner(model);
        spinner.setFont(MainFrame.SMALL_FONT);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
        spinner.setPreferredSize(new Dimension(55, 20));
        JPanel panel = new JPanel();
        //panel.setPreferredSize(new Dimension(this.getWidth(),70));
        this.add(panel, gc);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.fill = 3;
        gc2.gridx = 0;
        gc2.weightx = 1.0D;
        gc2.weighty = 5.0D;
        JLabel label = new JLabel(variable + ": ");
        label.setFont(MainFrame.SMALL_FONT);
        panel.add(label, gc2);
        gc2.gridx = 1;
        panel.add(spinner, gc2);
        spinner.addChangeListener(arg0 -> setValue((Float) spinner.getValue(), variable));
        gc2.gridx = 2;
        gc2.gridy = 0;
        panel.add(createSlider(variable + " :", 2, 0, variable), gc2);
    }


    private JPanel createSlider(String name, float maximum, float start, String variable) {
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
        final JFloatSlider slider = new JFloatSlider(0, 0.0F, maximum, reverseConvertValue(start));

        valueReading.setText(limitChars(Float.toString(convertScaleValue(slider.getActualValue())), 5));


        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                valueReading.setText(limitChars(Float.toString(slider.getActualValue()), 5));
                addValue(slider.getActualValue(), variable);
            }
        });
        panelSlider.add(slider, gc);
        slider.setPreferredSize(new Dimension(250, 20));
        gc.gridx = 2;
        gc.weightx = 1.0D;
        panelSlider.add(valueReading, gc);
        return panelSlider;
    }

    private void setValue(float value, String variable) {
        switch (variable.toLowerCase()) {
            case "x":
                this.position.x = value;
                x = value;
                break;
            case "y":
                this.position.y = value;
                y = value;
                break;
            case "z":
                this.position.z = value;
                z = value;
                break;
            default:
                new IllegalArgumentException(variable + " is not define").printStackTrace();
        }
    }

    private void addValue(float value, String variable) {
        switch (variable.toLowerCase()) {
            case "x":
                this.position.x = x + value;
                break;
            case "y":
                this.position.y = y + value;
                break;
            case "z":
                this.position.z = z + value;
                break;
            default:
                new IllegalArgumentException(variable + " is not define").printStackTrace();
        }
    }

    private String limitChars(String original, int limit) {
        if (original.length() <= limit) {
            return original;
        }
        return original.substring(0, 5);
    }


    private float convertScaleValue(float sliderValue) {
        float value = sliderValue * sliderValue;
        value *= 200.0F;
        return value;
    }

    private float reverseConvertValue(float reflectValue) {
        float value = reflectValue / 200.0F;
        return Math.sqrt(value);
    }
}
