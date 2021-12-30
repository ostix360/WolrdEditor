//package fr.entityCreator.frame;
//
//
//import fr.entityCreator.entity.camera.Camera;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//
//public class ExtraPreviewOptions extends JPanel {
//    private Camera camera;
//
//
//    public ExtraPreviewOptions(int width, int height, Camera camera) {
//        this.camera = camera;
//        setPreferredSize(new java.awt.Dimension(width, height));
//        super.setLayout(new java.awt.GridBagLayout());
//        addGuideShowOption();
//        addDistancePreviewButton();
//        addNormalCameraButton();
//    }
//
//    private void addGuideShowOption() {
//        GridBagConstraints gc = getGC(0);
//        JCheckBox showGuideBox = new JCheckBox("Show Radius");
//        showGuideBox.setFont(MainFrame.SMALL_FONT);
//        showGuideBox.setSelected(MainApp.sphere.isShown());
//        showGuideBox.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                MainApp.sphere.showEntity(showGuideBox.isSelected());
//            }
//        });
//        add(showGuideBox, gc);
//
//    }
//
//    private GridBagConstraints getGC(int y) {
//        GridBagConstraints gc = new GridBagConstraints();
//        gc.anchor = 17;
//        gc.gridx = 0;
//        gc.gridy = y;
//        gc.weightx = 1.0D;
//        gc.weighty = 1.0D;
//        return gc;
//    }
//
//    private void addDistancePreviewButton() {
//        GridBagConstraints gc = getGC(1);
//        JButton preview = new JButton("Visibility Preview");
//        preview.setFont(MainFrame.SMALL_FONT);
//        preview.setSelected(MainApp.sphere.isShown());
//        preview.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                System.out.println(MainApp.currentEntity.getEntity().getVisibleRange());
//
//                ExtraPreviewOptions.this.camera.setDistanceFromPlayer(MainApp.currentEntity.getEntity().getVisibleRange());
//
//            }
//        });
//        add(preview, gc);
//
//    }
//
//
//    private void addNormalCameraButton() {
//        GridBagConstraints gc = getGC(2);
//        JButton preview = new JButton("Reset Camera");
//        preview.setFont(MainFrame.SMALL_FONT);
//        preview.setSelected(MainApp.sphere.isShown());
//        preview.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                System.out.println(MainApp.currentEntity.getEntity().getVisibleRange());
//
//                ExtraPreviewOptions.this.camera.setDistanceFromPlayer(40.0F);
//
//            }
//        });
//        add(preview, gc);
//    }
//}