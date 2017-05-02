package monitoringserver;

import java.net.Socket;
import monitoringserver.Monitor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Purvesh
 * This AssignRequest class/(Thread 2) will check the RequestQueue and Will pass this request/socket to another thread 
 * to read the data from the socket/request
 * 
 */
public class AssignRequest implements Runnable {
    Thread t; // Thread for retreving the request
       
    public AssignRequest(){
                
        if (t == null) {
            t = new Thread(this, "thread2");
            t.start();
        }
    }
    
     /**
     * The thread which assign the requests to worker threads
     */
    @Override
    public void run() {
        //System.out.println("Thread 2 started");
        while (true) {

            // **** CRITICAL Section ****
            Monitor.QueueLock.lock();
            if (Monitor.incomingRequestQueue.size() > 0) {
                Socket newRequest = Monitor.incomingRequestQueue.remove();
                System.out.println("New request received for IP - "+ newRequest.getInetAddress().getHostAddress()+" and port number - " + newRequest.getPort() );
                
                //Pass this request socket to another thread to read request content and then move forward
                monitoringserver.ReadRequest r = new ReadRequest(newRequest);
            }
            Monitor.QueueLock.unlock();
            // ****  END of CRITICAL Section

        }

    }

}
