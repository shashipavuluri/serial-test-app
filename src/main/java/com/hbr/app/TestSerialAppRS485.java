package com.hbr.app;


import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

public class TestSerialAppRS485 {
  
  private static TestSerial485 tserial = null;
  public static void main ( String[] args )
  {
      System.out.println("Starting sensor application : TestSerialApp(RS232 RS485)");
      
      
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          System.out.println("stopping TestSerialApp");
          if (tserial != null) {
            System.out.println("Sending shutdown commands and waiting for data...");
            String initStr = "*11\rdemov1\r~~~G\rdemov1\r";
            byte [] barray = initStr.getBytes();
            System.out.println("+++++++++  Sending command ++++++++ "  + Arrays.toString(barray));
            tserial.writeData(barray);
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
        
        System.out.println("Initializing TestSerial Object");
        tserial = new TestSerial485();
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
        System.out.println("Sending commands and waiting for data...");
        String initStr = "*11\r-SHLAF\r~~~O\r-SHLAF\r";
        byte [] barray = initStr.getBytes();
        tserial.writeData(barray);
        
        int address = 1;
        byte [] command = { (byte)0x55, (byte)0xAA, (byte)address, (byte) 0x00, (byte) 0x21,(byte) 0x21 };
        Thread.sleep(2000);
        while(true) {
          System.out.println("+++++++++  Sending command ++++++++ "  + Arrays.toString(command));
          tserial.writeData(command);
          Thread.sleep(10000);
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
