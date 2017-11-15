package com.hbr.app;

import java.util.Enumeration;
import java.util.Properties;

public class TestSerial485AppV2 {
  
  private static TestSerial485V2 tserial = null;
  public static void main ( String[] args )
  {
      System.out.println("Starting sensor application : TestSerialApp(RS232 RS485v2)");
      
      
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          System.out.println("stopping TestSerialApp");
          if (tserial != null) {
           
            tserial.disconnect();
            System.out.println("msg='Disconnected the serial port");
          }
          System.out.println("Shutting down");
        }
      });
      
      
      try
      {
        Properties systemProperties = System.getProperties();
        Enumeration enuProp = systemProperties.propertyNames();
        System.out.println("Current System properties: ");
        while (enuProp.hasMoreElements()) {
            String propertyName = (String) enuProp.nextElement();
            String propertyValue = systemProperties.getProperty(propertyName);
            System.out.println(propertyName + ": " + propertyValue);
        }
        
        System.out.println("Test loading the RXTXSerial library with libname: rxtxSerial");
        
        System.loadLibrary("rxtxSerial");
        System.out.println("Done RXTXSerial library with libname: rxtxSerial");
        System.out.println("Serial Ports to connect from environment variable(Defined in package.yaml) " + System.getenv("HOST_DEV1"));
        
        System.out.println("Initializing TestSerial Object RS485V2");
        tserial = new TestSerial485V2();
        System.out.println("Search for ports");
        tserial.scanPorts();
        System.out.println("Connecting to: " + System.getenv("HOST_DEV1"));
        tserial.connect(System.getenv("HOST_DEV1"));
        if (tserial.isConnected() == true)
        {
            if (tserial.initInputStream() == true)
            {
                tserial.initSerialPortListener();
            }
        }
        
        while(true) {
          Thread.sleep(5000);
        }
       
      }
      catch ( Exception e )
      {
        System.out.println("failed due to  " + e.getMessage());
          e.printStackTrace();
      }
 
      System.out.println("Exiting the application");
  }
}

