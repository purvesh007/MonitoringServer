/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoringserver;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Purvesh
 * This thread will read every incoming request and assign it to respective polling frequency queue.
 * If there is no queue for the particular polling frequency, it will create a new queue.
 * 
 */
public class ReadRequest implements Runnable {

    Thread t; // Thread object for reading/extracting data from incoming user request
    Socket Read;  // Socket object for reading the incoming request
    public static HashMap<Integer, Queue<monitoringserver.UserQueueObject>> StoreUsersQueue; // Will Storing a queue for every polling frequency 
    public static HashMap<Integer, ReentrantLock> WorkerThreadLock = new HashMap<>();
    private static final Pattern IP_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public ReadRequest(Socket s) {

        this.Read = s;
        StoreUsersQueue = new HashMap<>();
        if (t == null) {
            t = new Thread(this, "thread3");
            t.start();
        }

    }

    @Override
    public void run() {
        
        // read the request
        BufferedReader in = null;
        try 
        {
            in = new BufferedReader(new InputStreamReader(Read.getInputStream(),"UTF-8"));
        }
        catch (IOException ex)
        {
            Logger.getLogger(ReadRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String inputLine;
        try {
            while (!(inputLine = in.readLine()).equals("")) {

                String[] tokens = inputLine.split(" ");   //Extract Content of the request
                
                // Check whether request from user is valid or not?
                isValid(tokens);   
                
                
                ServiceIdentifier s1 = new ServiceIdentifier(tokens[1], Integer.parseInt(tokens[2]));
                UserQueueObject UserObject = new UserQueueObject(Read, s1); 
                
                // Check's if the queue for the requested polling frequency exists
                if (StoreUsersQueue.containsKey(Integer.parseInt(tokens[3])))
                {
                    Queue<monitoringserver.UserQueueObject> obj = StoreUsersQueue.get(Integer.parseInt(tokens[3]));
                    ReentrantLock rl = WorkerThreadLock.get(Integer.parseInt(tokens[3]));
                    rl.lock();
                    obj.add(UserObject);
                    rl.unlock();
                }
                
                // It will create a new Queue for the requested polling frequency along with assining that queue to the new worker thread.
                else
                {
                    ReentrantLock QueueLock = new ReentrantLock();
                    WorkerThreadLock.put(Integer.parseInt(tokens[3]), QueueLock);
                    
                    //*** CRITICAL Section **********
                    QueueLock.lock();
                    Queue<monitoringserver.UserQueueObject> obj = new LinkedList<>();
                    obj.add(UserObject);
                    StoreUsersQueue.put(Integer.parseInt(tokens[3]), obj);
                    
                    
                    // Assign the queue to the new worker thread
                    WorkerThread w = new WorkerThread(Integer.parseInt(tokens[3]), StoreUsersQueue.get(Integer.parseInt(tokens[3])));

                    QueueLock.unlock();
                    //*** End of CRITICAL Section **********

                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ReadRequest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidRequestException i) {

            closeConnection(i.getMessage(), Read);

        }
        catch (Exception ex){
            //System.out.println(ex.getMessage());
        }
    }

    
    //Check whether request from user is valid or not?
    private void isValid(String[] tokens) throws InvalidRequestException {
        if (tokens.length == 4) {
            if (tokens[0].toUpperCase().equals("GET")) {
                if (IP_PATTERN.matcher(tokens[1]).matches()) {
                    int port = Integer.parseInt(tokens[2]);
                    if (port >= 0 && port <= 65535) {
                        int time = Integer.parseInt(tokens[3]);
                        if (time > 1 && time <= 3600) {

                        } else {
                            throw new InvalidRequestException("Invalid Time\n");
                        }
                    } else {
                        throw new InvalidRequestException("Invalid Port\n");
                    }
                } else {
                    throw new InvalidRequestException("Invalid IP Address\n");
                }
            } else {
                throw new InvalidRequestException("Invalid Request Type\n");
            }
        } else {
            throw new InvalidRequestException("Invalid Number of Arguments\n");
        }

    }

    //If a request is bad, it will close the connction with appropriare response
    private void closeConnection(String message, Socket Read) {
        try {
            OutputStream outToServer = this.Read.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeChars(message.toString());
            Read.close();
        } catch (Exception ex) {
            
        }

    }

}
