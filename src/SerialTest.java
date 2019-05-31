/*
 * High-level instructions and the original version of this code:
 * https://playground.arduino.cc/Interfacing/Java/
 * 
 * Demo video: https://www.youtube.com/watch?v=43Vdpz1YmdU
 * 
 * Download RXTX code here: http://fizzed.com/oss/rxtx-for-java.
 * 
 * Add the following VM argument in the run configuration (replace path as appropriate):
 * -Djava.library.path="C:\path\to\rxtx"
 * 
 * See here for javaDoc for a library with a similar interface:
 * https://docs.oracle.com/cd/E17802_01/products/products/javacomm/reference/api/javax/comm/package-summary.html
 */
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

// RXTX
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

public class SerialTest implements SerialPortEventListener {
  
  SerialPort port;
  private BufferedReader input;
  private BufferedWriter output;
  private static final int DATA_RATE = 9600;
  private static final int PORT_OPEN_BLOCKING_TIME = 2000; // milliseconds
  
  public void initialize(String portName) {
    
    // If Raspberry Pi doesn't work, add this line (see https://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186)
//    System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
    
    try {
      
      CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier(portName);
      
      String appName = this.getClass().getName();
      port = (SerialPort) portID.open(appName, PORT_OPEN_BLOCKING_TIME);
      port.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
      port.addEventListener(this);
      port.notifyOnDataAvailable(true);

      input = new BufferedReader(new InputStreamReader(port.getInputStream()));
      output = new BufferedWriter(new OutputStreamWriter(port.getOutputStream()));
      
   // TODO: Update this for two-way communications. See:
   // http://rxtx.qbang.org/wiki/index.php/Two_way_communcation_with_the_serial_port
//      output.write("Hey!");
      
    } catch (NoSuchPortException e) {
      System.err.println("Could not find COM port.");
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }

  /** Handle an event on the serial port. */
  public synchronized void serialEvent(SerialPortEvent event) {
    
    switch(event.getEventType()) {
    
      case SerialPortEvent.DATA_AVAILABLE:
        try {
          System.out.println(input.readLine());
        } catch (Exception e) {
          System.err.println(e.toString());
        }      
        break;
  
//      case SerialPortEvent.BI:
//        System.out.println("BI");
//      case SerialPortEvent.OE:
//        System.out.println("OE");
//      case SerialPortEvent.FE:
//        System.out.println("FE");
//      case SerialPortEvent.PE:
//        System.out.println("PE");
//      case SerialPortEvent.CD:
//        System.out.println("CD");
//      case SerialPortEvent.CTS:
//        System.out.println("CTS");
//      case SerialPortEvent.DSR:
//        System.out.println("DSR");
//      case SerialPortEvent.RI:
//        System.out.println("RI");
//      case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
//        System.out.println("OUTPUT_BUFFER_EMPTY");
//        break;
        
      default:
        System.out.println("Unrecognized event type");     
    }
  }

//  /** This should be called when you stop using the port. This will prevent port locking on platforms like Linux. */
//  public synchronized void close() {
//    if (port != null) {
//      port.removeEventListener();
//      port.close();
//    }
//  }

  public static void main(String[] args) {
    
    SerialTest main = new SerialTest();
    main.initialize("COM10"); // for Windows
    // Other alternatives:
    //  "/dev/tty.usbserial-A9007UX1" for Mac OS X
    //  "/dev/ttyUSB0" for Linux
    //  "/dev/ttyACM0" for Raspberry Pi
    
    Thread thread = new Thread() { public void run() {} };    
    thread.start();    
    System.out.println("Started.");
  }
}
