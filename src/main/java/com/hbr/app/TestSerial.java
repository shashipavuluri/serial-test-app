package com.hbr.app;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;


public class TestSerial implements SerialPortEventListener {

  private SerialPort serialPort = null;
  private InputStream inputStream = null;
  private OutputStream outputStream = null;
  private boolean isConnected = false;
  final static int SPACE_ASCII = 32;
  final static int DASH_ASCII = 45;
  final static int NEW_LINE_ASCII = 10;
  private String readData = null;
  
  public void scanPorts()
  {
      System.out.println("Searching for ports on this device ...");
      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
      while (ports.hasMoreElements())
      {
          CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();
          if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
          {
              System.out.println("Serial port Found: " + curPort.getName() + " " + curPort.toString());
          }
          else {
              System.out.println("Non-Serial port Found: " + curPort.getName() + " " + curPort.toString());
          }
      }
  }
 
  public void connect(String selectedPort)
  {
      System.out.println("Trying to establish connection to Port " + selectedPort);
      CommPortIdentifier portIdentifier;
      CommPort commPort;
      try {
        portIdentifier = CommPortIdentifier.getPortIdentifier(selectedPort);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
           System.out.println("Port is currently free.....");
           commPort = portIdentifier.open(this.getClass().getName(),2000);
           serialPort = (SerialPort)commPort;
           if ( commPort instanceof SerialPort)
           {
               SerialPort serialPort = (SerialPort) commPort;
               serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
               System.out.println("Initialized the serial comm port");
               isConnected = true;
           }
           else
           {
               System.out.println("Not a serial to connect...");
           }
        }
      } catch (Exception e) {
        System.out.println("Connect Caught an exception : " + e.getMessage());
      }
      
  }
  
  public boolean initInputStream()
  {
      try {
          inputStream = serialPort.getInputStream();
          outputStream = serialPort.getOutputStream();
          return true;
      }
      catch (IOException e) {
        System.out.println("initInputStream stream Caught an exception : " + e.getMessage());
          return false;
      }
  }

  public void initSerialPortListener()
  {
      try
      {
          serialPort.addEventListener(this);
          serialPort.notifyOnDataAvailable(true);
      }
      catch (Exception e)
      {
        System.out.println("Failed to init listener : " + e.getMessage());
      }
  }

  @Override
  public void serialEvent(SerialPortEvent evt) {
    System.out.println("Encountered event... " + evt.getEventType());
    if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
    {
        try
        {
            byte singleData = (byte)inputStream.read();
            if (singleData != NEW_LINE_ASCII)
            {
                readData = new String(new byte[] {singleData});
                System.out.println("Data read : " + readData);
            }
            else
            {
              System.out.println("Got newline\n ");
            }
        }
        catch (Exception e)
        {
            System.out.println("Error reading Data : " + e.getMessage());
        }
    }
  }
  
  public void disconnect()
  {
      try
      {
          serialPort.removeEventListener();
          serialPort.close();
          inputStream.close();
          outputStream.close();
          isConnected = false;
          System.out.println("Input/Output streams Disconnected ");
      }
      catch (Exception e)
      {
          System.out.println("Disconnect of "+ serialPort.getName() + " failed due to " + e.getMessage());
      }
  }
  
  
  public boolean isConnected()
  {
      return isConnected;
  }

}
