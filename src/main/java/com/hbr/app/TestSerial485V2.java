package com.hbr.app;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;



public class TestSerial485V2 implements SerialPortEventListener {

  private SerialPort serialPort = null;
  private InputStream inputStream = null;
  private OutputStream outputStream = null;
  private boolean isConnected = false;
  final static int SPACE_ASCII = 32;
  final static int DASH_ASCII = 45;
  final static int NEW_LINE_ASCII = 10;
  
  public void scanPorts()
  {
      System.out.println("Searching for ports on this device ...");
      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
      while (ports.hasMoreElements())
      {
          CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();         
          if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
          {         
              String portType = getPortTypeName(curPort.getPortType());
              System.out.println("Serial port Found: " + curPort.getName() + " " + curPort.toString());
              System.out.println("------Serial port type: " + portType);             
          }
          else {
              System.out.println("Non-Serial port Found: " + curPort.getName() + " " + curPort.toString());
          }
      }
  }
  
  private static String getPortTypeName ( int portType )
  {
      switch ( portType )
      {
          case CommPortIdentifier.PORT_I2C:
              return "I2C";
          case CommPortIdentifier.PORT_PARALLEL:
              return "Parallel";
          case CommPortIdentifier.PORT_RAW:
              return "Raw";
          case CommPortIdentifier.PORT_RS485:
              return "RS485";
          case CommPortIdentifier.PORT_SERIAL:
              return "Serial";
          default:
              return "unknown type";
      }
  }
 
  public void connect(String selectedPort)
  {
      System.out.println("Trying to establish connection to Port " + selectedPort);
      CommPortIdentifier portIdentifier;
      CommPort commPort;
      try {
        portIdentifier = CommPortIdentifier.getPortIdentifier(selectedPort);
        System.out.println("port type is : " + getPortTypeName(portIdentifier.getPortType()));
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
               System.out.println("Serialport param 9600/8bits/stopbits-1/parity-none");
               serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
               
               System.out.println("Initialized the serial comm port of type " + getPortTypeName(portIdentifier.getPortType()));
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
      byte[] buffer = new byte[1024];
      int len = -1;
      try
      {
          System.out.println("Serial485 reader starting to read data....");   
          while ( ( len = this.inputStream.read(buffer)) > -1 )
          {
              System.out.print(new String(buffer,0,len));
          }
          System.out.println("Serial reader Done reading the data with length : " + len);
      }
      catch ( IOException e )
      {
        System.out.print("caught an exception " + e.getMessage());
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
  
  public void writeData(byte[] byteArray) {
    try {
      
      System.out.println("Serial Write data " + Arrays.toString(byteArray));
      outputStream.flush();
      outputStream.write(byteArray);
      outputStream.flush();
    } catch (IOException e) {
      System.out.println("Write failed due to " + e.getMessage());
    }
  }
  
  
  public boolean isConnected()
  {
      return isConnected;
  }

}

