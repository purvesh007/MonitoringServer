package monitoringserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Purvesh
 *  This Monitor Class/(Thread 1) will listen on the port for the incoming user request for registering the service and store it in a Queue
 */
public class Monitor implements Runnable {

    Thread t; // Thread for incoming request
    ServerSocket MyMonitor;  // Object of ServerSocket to listen to connections
    public static ReentrantLock QueueLock; // For Locking the main Queue
    public static Queue<Socket> incomingRequestQueue; // To store incoming requests
    int portNumber;
    int NumberOfClients;  //maximum number of clients it can served at a time

   
    
    /**
     * Thread start the monitor and listen & stores the incoming requests
     * @param portNumber is the port for listening on server
     * @param NumberOfClients is the maximum number of clients a monitor can
     * handle at a given point
     */
    public Monitor(int portNumber, int NumberOfClients) {
        QueueLock = new ReentrantLock();
        incomingRequestQueue = new LinkedList();
        this.portNumber = portNumber;
        this.NumberOfClients = NumberOfClients;
        
        if (t == null) {
            t = new Thread(this, "thread1");
            t.start();
        }
    }

    
    @Override
    public void run() {
        try {
            MyMonitor = new ServerSocket(portNumber);
            //System.out.println("Thread 1 started");
            while (true) {
                
                Socket socket = MyMonitor.accept();
                // **** CRITICAL Section ****
                QueueLock.lock();
                incomingRequestQueue.add(socket);
                QueueLock.unlock();
                // ****  END of CRITICAL Section
            }
        } catch (IOException e) {
            //System.out.println(e);
        }
    }

}
