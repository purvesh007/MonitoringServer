/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoringserver;

import java.net.Socket;
import java.util.*;
import monitoringserver.ServiceIdentifier;
import monitoringserver.UserQueueObject;
import java.io.*;
import java.net.InetSocketAddress;

/**
 *
 * @author Purvesh
 * This Thread will check status of the services and will respond back the status to the corresponding user by it's polling frequency
 * For every polling frequency(Queue) there is a new worker thread
 * 
 */
public class WorkerThread implements Runnable {

    Thread t;
    Queue<monitoringserver.UserQueueObject> StoreclientQueue; // To store the requests
    HashMap<monitoringserver.ServiceIdentifier, Boolean> ServiceCalls; //Store status of the services
    int TimeInterval;

    public WorkerThread(int TimeInterval, Queue<monitoringserver.UserQueueObject> StoreUsersQueue) {

        this.TimeInterval = TimeInterval;
        this.StoreclientQueue = ReadRequest.StoreUsersQueue.get(TimeInterval);
        this.ServiceCalls = new HashMap<>();

        if (t == null) {
            t = new Thread(this, "thread4");
            t.start();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (this.ServiceCalls.size() > 0)
                {
                    Iterator it = ServiceCalls.entrySet().iterator();
                    while (it.hasNext())
                    {
                        Map.Entry pair = (Map.Entry) it.next();
                        checkServiceStatus((ServiceIdentifier) pair.getKey());  //Function to check status of the service
                    }

                    it = StoreclientQueue.iterator();
                    while (it.hasNext())
                    {
                        monitoringserver.UserQueueObject temp = (monitoringserver.UserQueueObject) it.next();
                        sendServiceStatus(temp);  //Function to send status of the service back to the user
                    }

                } else if (this.StoreclientQueue.size() > 0)
                {
                    Iterator it = this.StoreclientQueue.iterator();
                    while (it.hasNext())
                    {
                        monitoringserver.UserQueueObject temp = ((monitoringserver.UserQueueObject) it.next());
                        if (!ServiceCalls.containsKey(temp.Service))
                        {
                            ServiceCalls.put(temp.Service, Boolean.FALSE);
                        }
                    }
                }
                
                // Put thread in sleep for the  polling frequency time interval
                Thread.currentThread().sleep(this.TimeInterval * 1000); 

            } catch (Exception e) {
                //System.out.println(e);
            }
        }
    }
    
    //Function to check status of the service
    private void checkServiceStatus(monitoringserver.ServiceIdentifier key) throws IOException {
        Socket s = new Socket();
        try {
            s.connect(new InetSocketAddress(key.IP, key.Port), 1200);  //2nd argument is timeout time to get back repsonse from the server/service
            if (s.isConnected()) {
                ServiceCalls.put(key, Boolean.TRUE);
                s.close();
            } else {
                ServiceCalls.put(key, Boolean.FALSE);
            }

        } catch (Exception ex) {
            //System.out.println(ex.getMessage());
        }

    }

    //Function to send status of the service back to the user
    private void sendServiceStatus(UserQueueObject userQueueObject) throws IOException {
        try {
            OutputStream outToServer = userQueueObject.s.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);       
            Boolean status = ServiceCalls.get(userQueueObject.Service);
            if (status) {
                out.writeChars(userQueueObject.Service.IP.toString() + " is Up at " + userQueueObject.Service.Port + "\n");
            } else {
                out.writeChars(userQueueObject.Service.IP.toString() + " is Down \n");
            }
        } catch (Exception ex) {
            //System.out.println(ex.getMessage());
        }

    }

}
