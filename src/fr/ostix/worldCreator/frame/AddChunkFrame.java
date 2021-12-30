package fr.ostix.worldCreator.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddChunkFrame {
    private JDialog frame;
    private final MainFrame mainFrame;
    private int x = 10;
    private int z = 10;
    private int xCoords = 0;
    private int zCoords = 0;

    public AddChunkFrame(MainFrame frame) {
        this.mainFrame = frame;
        setup();
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = 1;
        gc.gridx = 0;
        gc.gridy = 0;
        JPanel panel = new JPanel();
        this.frame.add(panel,gc);
        addSpinner(panel);
        gc.gridy = 1;
        panel = new JPanel();
        this.frame.add(panel,gc);
        addCoordsSpinner(panel);
        gc.gridy = 2;
        addButton(gc);
        this.frame.setVisible(true);
    }

    private void addButton(GridBagConstraints gc) {
        JButton confirm = new JButton("Valider");
        confirm.setFont(new Font("Segoe UI", 1, 15));
        confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
               mainFrame.notifyNewChunk(x, z,xCoords,zCoords);
               frame.dispose();
            }
        });
        this.frame.add(confirm, gc);
    }

    private void setup() {
        this.frame = new JDialog();
        this.frame.setAlwaysOnTop(true);
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        this.frame.setSize(250, 200);
        this.frame.setResizable(true);
        this.frame.setLocationRelativeTo(null);
        this.frame.setLayout(new GridBagLayout());
    }

    private void addSpinner( JPanel settingsPanel) {
        SpinnerNumberModel model = new SpinnerNumberModel(10, 10, 100, 10);
        final JSpinner spinner = new JSpinner(model);
        spinner.setFont(MainFrame.SMALL_FONT);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
        spinner.setPreferredSize(new Dimension(30, 20));
        SpinnerNumberModel model2 = new SpinnerNumberModel(10, 10, 100, 10);
        final JSpinner spinner2 = new JSpinner(model2);
        spinner2.setFont(MainFrame.SMALL_FONT);
        ((JSpinner.DefaultEditor) spinner2.getEditor()).getTextField().setEditable(false);
        spinner2.setPreferredSize(new Dimension(30, 20));
        JPanel panel = new JPanel();
        settingsPanel.add(panel);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.fill = 1;
        gc2.gridx = 0;
        gc2.weightx = 1.0D;
        gc2.weighty = 1.0D;
        JLabel label = new JLabel("Chunks: \t");
        label.setFont(MainFrame.SMALL_FONT);
        panel.add(label, gc2);

        gc2.gridx = 1;
        label = new JLabel("x: ");
        label.setFont(MainFrame.SMALL_FONT);
        panel.add(label, gc2);
        gc2.gridx = 2;

        panel.add(spinner, gc2);
        spinner.addChangeListener(arg0 -> x = (Integer) spinner.getValue());

        gc2.gridx = 3;
        label = new JLabel("z: ");
        label.setFont(MainFrame.SMALL_FONT);
        panel.add(label, gc2);

        gc2.gridx = 4;
        panel.add(spinner2, gc2);
        spinner2.addChangeListener(arg0 -> z = (Integer) spinner2.getValue());
    }

    private void addCoordsSpinner(JPanel settingsPanel) {
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 100, 1);
        final JSpinner spinner = new JSpinner(model);
        spinner.setFont(MainFrame.SMALL_FONT);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
        spinner.setPreferredSize(new Dimension(30, 20));


        SpinnerNumberModel model2 = new SpinnerNumberModel(0, 0, 100, 1);
        final JSpinner spinner2 = new JSpinner(model2);
        spinner2.setFont(MainFrame.SMALL_FONT);
        ((JSpinner.DefaultEditor) spinner2.getEditor()).getTextField().setEditable(false);
        spinner2.setPreferredSize(new Dimension(30, 20));


        JPanel panel = new JPanel();
        settingsPanel.add(panel);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.fill = 1;
        gc2.gridx = 0;
        gc2.weightx = 1.0D;
        gc2.weighty = 1.0D;
        JLabel label = new JLabel("Coords: \t");
        label.setFont(MainFrame.SMALL_FONT);
        panel.add(label, gc2);

        gc2.gridx = 1;
        label = new JLabel("x: ");
        label.setFont(MainFrame.SMALL_FONT);
        panel.add(label, gc2);

        gc2.gridx = 2;
        panel.add(spinner, gc2);
        spinner.addChangeListener(arg0 -> xCoords = (Integer) spinner.getValue());

        gc2.gridx = 3;
        label = new JLabel("z: ");
        label.setFont(MainFrame.SMALL_FONT);
        panel.add(label, gc2);

        gc2.gridx = 4;
        panel.add(spinner2, gc2);
        spinner2.addChangeListener(arg0 -> zCoords = (Integer) spinner2.getValue());
    }


}
