/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoringserver;

import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author Purvesh
 * This is used to store the service request
 */
public class ServiceIdentifier {
    String IP;
    int Port;
    int TimeInterval;

    /**
     * 
     * @param IP address for the service to be check
     * @param Port number for the service to be check
     */
    public ServiceIdentifier(String IP, int Port) {
        this.IP = IP;
        this.Port = Port;
    }
    
    

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int Port) {
        this.Port = Port;
    }

    
    
    
}
