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
    private final List<Entity> entities;
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
        JPanel panel = new JPanel();
        JButton increase = new JButton("+");
        JButton decrease = new JButton("-");
        increase.setPreferredSize(new Dimension(50, 50));
        decrease.setPreferredSize(new Dimension(50, 50));
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
        panel.add(increase, gc2);
        increase.addActionListener(arg0 -> setValue(1, variable));
        gc2.gridx = 2;
        panel.add(decrease, gc2);
        decrease.addActionListener(arg0 -> setValue(-1, variable));
    }


    private void setValue(float value, String variable) {
        switch (variable.toLowerCase()) { //TODO : fix this
            case "x":
                final Vector3f v = new Vector3f(value, 0, 0);
                entities.forEach(entity -> entity.increasePosition(v));
                break;
            case "y":
                final Vector3f v2 = new Vector3f(0, value, 0);
                entities.forEach(entity -> entity.increasePosition(v2));
                break;
            case "z":
                final Vector3f v3 = new Vector3f(0, 0, value);
                entities.forEach(entity -> entity.increasePosition(v3));
                break;
            default:
                new IllegalArgumentException(variable + " is not define").printStackTrace();
        }
    }
}
