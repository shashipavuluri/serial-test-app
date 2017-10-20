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



public class TestSerial485 implements SerialPortEventListener {

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
        try
        {
            byte[] buffer = new byte[32];
            int len = inputStream.read(buffer);
            System.out.println("Length of read data: " + len);
            if (len > 0)
            {
              System.out.println(" byte Data read =>>> " + Arrays.toString(buffer));
              //Java byte is from -128 - 127
              //unsigned byte is 0 -255
              //Converting any -ve bytes to +ve
              System.out.print("[");
              for(int i=0;i<buffer.length;i++) {
                Byte b = new Byte(buffer[i]);
                System.out.print(b.intValue() < 0 ? 256 + b.intValue() : b.intValue());
                System.out.print(",");
              }
              System.out.println("]");
              //doing exactly what python app is doing ???? need to check if they are correct.
              Byte b = new Byte(buffer[7]);
              int humid_high = b.intValue()/10;
              b = new Byte(buffer[9]);
              int temp_high = ((b/10)*(9/5)) + 32;
              System.out.println(" Temperature : "+ temp_high + " Humidity : " + humid_high);
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
  
  public void writeData(byte[] byteArray) {
    try {
      
      System.out.println("Serial Write data " + Arrays.toString(byteArray));
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
