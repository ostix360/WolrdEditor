package fr.ostix.worldCreator.frame;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;

public class VectorPanel extends JPanel {
    private JFormattedTextField x;
    private JFormattedTextField y;
    private JFormattedTextField z;

    public VectorPanel(int width, int height, String name, float x, float y, float z) {
        setPreferredSize(new Dimension(width, height));
        setLayout(new GridBagLayout());
        setupName(name);
        setUpInputs(x,y,z);
    }

    private void setupName(String name) {
        JLabel label = new JLabel(name);
        label.setFont(MainFrame.SMALL_FONT);
        add(label,getGC(0,0));
    }

    private GridBagConstraints getGC(int x, int y) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = 1;
        gc.gridx = x;
        gc.gridy = y;
        gc.weightx = 1;
        gc.weighty = 1;
        return gc;
    }
    private void setUpLabel(String name, int gcX) {
        JLabel label = new JLabel(name);
        label.setFont(MainFrame.SMALL_FONT);
        add(label, getGC(gcX, 0));
    }

    private JFormattedTextField setUpValueInput(float value, int gcX) {
        JFormattedTextField field = createTextField(4);
        field.setText("0,0");
        add(field, getGC(gcX, 0));
        return field;
    }
    private JFormattedTextField createTextField(int columns) {
        NumberFormat floatFormat = NumberFormat.getNumberInstance();
        floatFormat.setMinimumFractionDigits(1);
        floatFormat.setMaximumFractionDigits(5);
        NumberFormatter numberFormatter = new NumberFormatter(floatFormat);
        numberFormatter.setValueClass(Float.class);
        numberFormatter.setAllowsInvalid(false);
        JFormattedTextField text = new JFormattedTextField(numberFormatter);
        text.setColumns(columns);
        text.setFont(MainFrame.SMALL_FONT);
        text.setHorizontalAlignment(0);
        return text;
    }

    public void addXFieldListener(DocumentListener listener) {
        this.x.getDocument().addDocumentListener(listener);
    }

    public void addYFieldListener(DocumentListener listener) {
        this.y.getDocument().addDocumentListener(listener);
    }

    public void addZFieldListener(DocumentListener listener) {
        this.z.getDocument().addDocumentListener(listener);
    }

    private void setUpInputs(float x, float y, float z) {
        setUpLabel(" x:", 1);
        this.x = setUpValueInput(x, 2);
        setUpLabel(" y:", 3);
        this.y = setUpValueInput(y, 4);
        setUpLabel(" z:", 5);
        this.z = setUpValueInput(z, 6);
    }
    public void addTotalListener(DocumentListener listener) {
        this.x.getDocument().addDocumentListener(listener);
        this.y.getDocument().addDocumentListener(listener);
        this.z.getDocument().addDocumentListener(listener);
    }


    public JFormattedTextField getXField() {
        return this.x;
    }

    public JFormattedTextField getYField() {
        return this.y;
    }

    public JFormattedTextField getZField() {
        return this.z;
    }

    public float getXValue() {
        String value = this.x.getText().replaceAll(",", "");
        if (value.equals("")) {
            return 0.0F;
        }
        return Float.parseFloat(value);
    }
    public float getYValue() {
        String value = this.y.getText().replaceAll(",", "");
        if (value.equals("")) {
            return 0.0F;
        }
        return Float.parseFloat(value);
    }
    public float getZValue() {
        String value = this.z.getText().replaceAll(",", "");
        if (value.equals("")) {
            return 0.0F;
        }
        return Float.parseFloat(value);
    }



}
