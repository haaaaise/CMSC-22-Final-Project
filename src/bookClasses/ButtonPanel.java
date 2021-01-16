package bookClasses;

import javax.swing.*;

/**
 * Class that holds the buttons for the movie player
 * @author Barb Ericson
 */
@SuppressWarnings("FieldCanBeLocal")
public class ButtonPanel extends JPanel
{
  //////////////// fields ////////////////////////
   /** list for the frame rate */
  private final JList<String> frameRateList;
  /** label for frame rate */
  private final JLabel frameRateLabel;
  private final JButton nextButton = new JButton("Next");
  private final JButton playButton = new JButton("Play Movie");
  private final JButton prevButton = new JButton("Prev");
  private final JButton delBeforeButton =
    new JButton("Delete All Previous");
  private final JButton delAfterButton =
    new JButton("Delete All After");
  private final JButton writeQuicktimeButton =
    new JButton("Write Quicktime");
  private final JButton writeAVIButton = new JButton("Write AVI");
  private final MoviePlayer moviePlayer;
  
  ///////////////// Constructors /////////////////
  
  /**
   * Constructor that doesn't take any parameters
   */
  public ButtonPanel(MoviePlayer player) 
  {
    this.moviePlayer = player;
    
    // add the previous and next buttons to this panel
    this.add(prevButton);
    this.add(nextButton);
    
    // set up the frame rate list
    frameRateLabel = new JLabel("Frames per Second: ");
    this.add(frameRateLabel);
    String[] rates = {"16","24","30"};
    frameRateList = new JList<>(rates);
    JScrollPane scrollPane = new JScrollPane(frameRateList);
    frameRateList.setSelectedIndex(0);
    frameRateList.setVisibleRowCount(1);
    frameRateList.setToolTipText("The number of frames per second in the movie");
    frameRateList.addListSelectionListener(e -> {
      String rateS = frameRateList.getSelectedValue();
      int rate = Integer.parseInt(rateS);
      moviePlayer.setFrameRate(rate);
    });
    this.add(scrollPane);
    
    this.add(playButton);
    this.add(delBeforeButton);
    this.add(delAfterButton);
    this.add(writeQuicktimeButton);
    this.add(writeAVIButton);
    
    // add the action listeners to the buttons
    nextButton.setToolTipText("Click to see the next frame");
    nextButton.addActionListener(e -> moviePlayer.showNext());
    prevButton.setToolTipText("Click to see the previous frame");
    prevButton.addActionListener(e -> moviePlayer.showPrevious());
    playButton.setToolTipText("Click to play the movie");
    playButton.addActionListener(e -> moviePlayer.playMovie());
    delBeforeButton.setToolTipText("Click to delete all frames before the current one");
    delBeforeButton.addActionListener(e -> moviePlayer.delAllBefore());
    delAfterButton.setToolTipText("Click to delete all frames after the current one");
    delAfterButton.addActionListener(e -> moviePlayer.delAllAfter());
    writeQuicktimeButton.setToolTipText("Click to write out a Quicktime movie from the frames");
    writeQuicktimeButton.addActionListener(e -> moviePlayer.writeQuicktime());
    writeAVIButton.setToolTipText("Click to write out an AVI movie from the frames");
    writeAVIButton.addActionListener(e -> moviePlayer.writeAVI());
  }
  
}
