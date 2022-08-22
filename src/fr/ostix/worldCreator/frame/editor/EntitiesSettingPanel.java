package fr.ostix.worldCreator.frame.editor;

import fr.ostix.worldCreator.entity.*;
import fr.ostix.worldCreator.frame.*;
import org.joml.*;
import org.joml.Math;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class EntitiesSettingPanel extends JPanel {
    private MainFrame frame;
    private List<Entity> entities;
    private float x, y, z;
    public EntitiesSettingPanel(List<Entity> entities, int width, int height, MainFrame frame) {
        this.entities = entities;
        this.frame = frame;
        this.setup();
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    private void setup() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        //this.setPreferredSize(new Dimension(width,height));
        gc.fill = 3;
        gc.gridy = 0;
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

    private void addDeleteButton(GridBagConstraints gc) {
        final JButton btn = new JButton("Supprimé l'entité");
        btn.setPreferredSize(new Dimension(100, 25));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                entities.forEach(frame.getWorld()::remove);
            }
        });
        this.add(btn, gc);
    }

    private void addSlider(GridBagConstraints gc, String variable) {
        float value = (float) 0;
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
    }



    private void setValue(float value, String variable) {
        switch (variable.toLowerCase()) {
            case "x":
                entities.forEach(entity -> entity.increasePosition(new Vector3f(x + value,0,0)));
                x = -value;
                break;
            case "y":
                entities.forEach(entity -> entity.increasePosition(new Vector3f(0,y + value,0)));
                y = -value;
                break;
            case "z":
                entities.forEach(entity -> entity.increasePosition(new Vector3f(0,0,z + value)));
                z = -value;
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
