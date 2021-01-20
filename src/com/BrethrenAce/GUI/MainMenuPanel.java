package com.BrethrenAce.GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Main menu JPanel that displays game main menu.
 *
 * Author: Brethren de la Gente
 * Date: 2021-01-04
 */
@SuppressWarnings("unused")
public class MainMenuPanel extends JPanel {

  /** Background image directory */
  private final static String BG_DIR_PATH = "art/bg/";

  /** Main menu options buttons */
  private final JButton start, howToPlay, quit;

  /** Background image */
  private BufferedImage backgroundImage;

  /**
   * No argument constructor that creates the main menu JPanel.
   */
  public MainMenuPanel() {
    // Load background image.
    try {
      backgroundImage = ImageIO.read(MainMenuPanel.class.getResource("/images/test.jpg"));
    } catch (final IOException e) {
      e.printStackTrace();
    }
    this.setLayout(new GridBagLayout());
    this.setBackground(Color.GRAY);
    this.setVisible(true);

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // Add main menu label
    final JLabel title = new JLabel("World at War(Blitz)");
    title.setFont(new Font("Dialog", Font.BOLD, 70));
    title.setForeground(new Color(120, 24, 24));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    this.add(title, gbc);

    // Add main menu buttons
    start = new JButton("PLAY");
    /** Main menu JButton size */
    Dimension MAIN_MENU_BUTTON_SIZE = new Dimension(200, 25);
    start.setPreferredSize(MAIN_MENU_BUTTON_SIZE);
    /** Main menu JButton font */
    Font MAIN_MENU_FONT = new Font("Monospaced", Font.BOLD, 16);
    start.setFont(MAIN_MENU_FONT);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    this.add(start, gbc);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridheight = 1;
    howToPlay = new JButton("INSTRUCTIONS");
    howToPlay.setPreferredSize(MAIN_MENU_BUTTON_SIZE);
    howToPlay.setFont(MAIN_MENU_FONT);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridheight = 1;
    this.add(howToPlay, gbc);
    quit = new JButton("QUIT");
    quit.setPreferredSize(MAIN_MENU_BUTTON_SIZE);
    quit.setFont(MAIN_MENU_FONT);
    quit.setForeground(new Color(196, 106, 37));
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridheight = 1;
    this.add(quit, gbc);
    final JLabel author = new JLabel("BETA VERSION; IMPROVEMENTS NEEDED");
    author.setFont(new Font("Dialog", Font.BOLD, 24));
    author.setForeground(new Color(132, 205, 53));
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridheight = 5;
    this.add(author, gbc);

  }

  /**
   * Paints main menu JPanel background with image.
   */
  @Override
  protected void paintComponent(final Graphics g) {
    super.paintComponent(g);
    g.drawImage(backgroundImage, 0, 0, null);
  }

  /**
   * Gets the main menu start button.
   * @return JButton start field.
   */
  public JButton getStartBtn() {
    return this.start;
  }

  /**
   * Gets the main menu how to play button.
   * @return JButton howToPlay field.
   */
  public JButton getHowToPlayBtn() {
    return this.howToPlay;
  }

  /**
   * Gets the main menu quit button.
   * @return JButton quit field.
   */
  public JButton getQuitBtn() {
    return this.quit;
  }

} // MainMenuPanel
