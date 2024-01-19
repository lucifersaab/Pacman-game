package Pacman;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PopUp {
    public static void showCustomPopup(String message) {
        JFrame parentFrame = new JFrame("MESSAGE!");
        parentFrame.setSize(400, 300);
        parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parentFrame.setLocationRelativeTo(null);

        JDialog dialog = new JDialog(parentFrame, "MESSAGE!", true);
        dialog.setUndecorated(true); // Undecorate the dialog

        // Create a JPanel to hold the components
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK); // Set background color to black
        panel.setBorder(new LineBorder(Color.GREEN, 2)); // Set border color to green

        // Create a JLabel to display the message
        JLabel label = new JLabel("<html><div style='text-align: center; color: white;'>" + message + "</div></html>");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label, BorderLayout.CENTER);

        // Add a KeyListener to the dialog
        dialog.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    dialog.dispose(); // Close the dialog when spacebar is pressed
                }
            }
        });
        dialog.setFocusable(true); // Set the dialog focusable to receive key events

        // Set the panel as the content pane of the dialog
        dialog.setContentPane(panel);

        // Set the size and position of the dialog
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(parentFrame); // Center the dialog relative to the parent frame

        // Show the dialog
        dialog.setVisible(true);
    }
 }

