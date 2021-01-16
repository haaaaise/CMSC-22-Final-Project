package bookClasses;

import javax.swing.*;
import java.text.BreakIterator;

/**
 * Class to make it easy to do output to the user
 * using JOptionPane
 * 
 * Copyright Georgia Institute of Technology 2004
 * @author Barb Ericson ericson@cc.gatech.edu
 */
@SuppressWarnings("unused")
public class SimpleOutput
{
  
  /**
   * Method to show a warning to a user
   * @param message the message to display
   */
  public static void showWarning(String message)
  {
    message = addNewLines(message);
    JOptionPane.showMessageDialog(null,message,"Warning Display",
                                  JOptionPane.WARNING_MESSAGE);
  }
  
  /**
   * Method to show an error to a user
   * @param message the message to display
   */
  public static void showError(String message)
  {
    message = addNewLines(message);
    JOptionPane.showMessageDialog(null,message,"Error Display",
                                  JOptionPane.ERROR_MESSAGE);
  }
  
  /**
   * Method to show information to the user
   * @param message the message to display
   */
  public static void showInformation(String message)
  {
    message = addNewLines(message);
    JOptionPane.showMessageDialog(null, message, "Information Display",
                                  JOptionPane.INFORMATION_MESSAGE);
  }
  
  /**
   * Method to add new line character if the message 
   * is too long
   * @param message the input message
   * @return the message with new lines added if needed
   */
  public static String addNewLines(String message)
  {
    BreakIterator boundary = 
      BreakIterator.getLineInstance();
    boundary.setText(message);
    int start = boundary.first();
    StringBuilder result = new StringBuilder();
    StringBuilder currLine = new StringBuilder();
    String temp;
    
    // loop till no more possible line breaks
    for (int end = boundary.next();
          end != BreakIterator.DONE;
          start = end, end = boundary.next()) 
    {
      // get string between start and end
      temp = message.substring(start,end);
      
      /* if adding that to the current line 
       * would make it too long then add current
       * to result followed by a newline and 
       * reset current 
       */
      if (temp.length() + currLine.length() > 100)
      {
        result.append(currLine).append("\n");
        currLine = new StringBuilder(temp);
      }
      // else add the segment to the current line
      else 
        currLine.append(temp);
    }
 
    // if no line breaks use the original message
    if (result.length() == 0)
      result = new StringBuilder(message);
    // else add any leftover parts
    else
      result.append(currLine);
    
    return result.toString();
  }
  
} // end of SimpleOutput class
