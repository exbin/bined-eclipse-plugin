/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.utils.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Icon;
import org.exbin.framework.utils.UiUtils;

/**
 * Simple header panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WindowHeaderPanel extends javax.swing.JPanel {

    private BackgroundDecorationMode decorationMode = BackgroundDecorationMode.COLOR_BOTTOM_RIGHT_TRANSITION;
    private Color transitionColor = null;
    private Image decorationImage = null;
    private boolean darkMode = false;

    private final ImageObserver imageObserver = (Image img, int infoflags, int x, int y, int width, int height) -> true;

    public WindowHeaderPanel() {
        initComponents();
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        darkMode = UiUtils.isDarkUI();
        if (darkMode) {
            WindowHeaderPanel.this.setBackground(Color.BLACK);
            titleLabel.setForeground(Color.WHITE);
            descriptionTextArea.setForeground(Color.WHITE);
            transitionColor = new Color(35, 35, 55);
        } else {
            transitionColor = new Color(220, 220, 230);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        separator = new javax.swing.JSeparator();
        textPanel = new javax.swing.JPanel();
        descriptionTextArea = new javax.swing.JTextArea();
        titleLabel = new javax.swing.JLabel();
        iconLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());
        add(separator, java.awt.BorderLayout.SOUTH);

        textPanel.setMinimumSize(new java.awt.Dimension(16, 64));
        textPanel.setOpaque(false);

        descriptionTextArea.setEditable(false);
        descriptionTextArea.setColumns(20);
        descriptionTextArea.setForeground(java.awt.Color.black);
        descriptionTextArea.setText("Line 1\nLine 2");
        descriptionTextArea.setBorder(null);
        descriptionTextArea.setOpaque(false);

        titleLabel.setForeground(java.awt.Color.black);
        titleLabel.setText("Title");
        titleLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout textPanelLayout = new javax.swing.GroupLayout(textPanel);
        textPanel.setLayout(textPanelLayout);
        textPanelLayout.setHorizontalGroup(
            textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        textPanelLayout.setVerticalGroup(
            textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textPanelLayout.createSequentialGroup()
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descriptionTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        add(textPanel, java.awt.BorderLayout.CENTER);

        iconLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        iconLabel.setIconTextGap(0);
        iconLabel.setMinimumSize(new java.awt.Dimension(64, 64));
        iconLabel.setPreferredSize(new java.awt.Dimension(0, 64));
        add(iconLabel, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JLabel iconLabel;
    private javax.swing.JSeparator separator;
    private javax.swing.JPanel textPanel;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    @Nonnull
    public String getTitle() {
        return titleLabel.getText();
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    @Nonnull
    public String getDescription() {
        return descriptionTextArea.getText();
    }

    public void setDescription(String description) {
        descriptionTextArea.setText(description);
    }

    @Nullable
    public Icon getIcon() {
        return iconLabel.getIcon();
    }

    public void setIcon(Icon icon) {
        iconLabel.setIcon(icon);
        iconLabel.setPreferredSize(new Dimension(icon == null ? 0 : 64, 64));
    }

    @Nonnull
    public BackgroundDecorationMode getDecorationMode() {
        return decorationMode;
    }

    public void setDecorationMode(BackgroundDecorationMode decorationMode) {
        this.decorationMode = decorationMode;
        repaint();
    }

    @Nullable
    public Color getTransitionColor() {
        return transitionColor;
    }

    public void setTransitionColor(Color transitionColor) {
        this.transitionColor = transitionColor;
        repaint();
    }

    @Nullable
    public Image getDecorationImage() {
        return decorationImage;
    }

    public void setDecorationImage(Image decorationImage) {
        this.decorationImage = decorationImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (decorationMode) {
            case COLOR_RIGHT_HORIZONTAL_TRANSITION:
            case COLOR_TOP_RIGHT_TRANSITION:
            case COLOR_BOTTOM_RIGHT_TRANSITION: {
                Dimension size = getSize();
                int topOffset = decorationMode == BackgroundDecorationMode.COLOR_BOTTOM_RIGHT_TRANSITION ? size.height : 0;
                int bottomOffset = decorationMode == BackgroundDecorationMode.COLOR_TOP_RIGHT_TRANSITION ? size.height : 0;
                Color backgroundColor = getBackground();
                int redChange = transitionColor.getRed() - backgroundColor.getRed();
                int greenChange = transitionColor.getGreen() - backgroundColor.getGreen();
                int blueChange = transitionColor.getBlue() - backgroundColor.getBlue();
                for (int i = 0; i < 96; i++) {
                    Color color = new Color(
                            transitionColor.getRed() - redChange * i / 95,
                            transitionColor.getGreen() - greenChange * i / 95,
                            transitionColor.getBlue() - blueChange * i / 95);
                    g.setColor(color);
                    g.drawLine(size.width - i + topOffset, 0, size.width - i + bottomOffset, size.height);
                }
                break;
            }
            case TOP_RIGHT_IMAGE: {
                if (decorationImage != null) {
                    Dimension size = getSize();
                    int imageWidth = decorationImage.getWidth(imageObserver);
                    g.drawImage(decorationImage, size.width - imageWidth, 0, imageObserver);
                }
                break;
            }
            default:
                throw new IllegalStateException("Illegal decoration mode " + decorationMode.name());
        }
    }

    public enum BackgroundDecorationMode {
        PLAIN,
        COLOR_RIGHT_HORIZONTAL_TRANSITION,
        COLOR_TOP_RIGHT_TRANSITION,
        COLOR_BOTTOM_RIGHT_TRANSITION,
        TOP_RIGHT_IMAGE
    }

    /**
     * Interface for decoration provider.
     */
    @ParametersAreNonnullByDefault
    public interface WindowHeaderDecorationProvider {

        /**
         * Configures provided instance of header panel.
         *
         * @param windowHeaderPanel window header panel
         */
        void setHeaderDecoration(WindowHeaderPanel windowHeaderPanel);
    }
}
