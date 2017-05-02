/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoringserver;

import monitoringserver.ServiceIdentifier;
import java.net.Socket;
/**
 *
 * @author Purvesh
 * For storing the client socket and the request that is the service IP address and the service port
 * 
 */
public class UserQueueObject {
    Socket s;
    monitoringserver.ServiceIdentifier Service;

    public UserQueueObject(Socket s, ServiceIdentifier Service) {
        this.s = s;
        this.Service = Service;
    }
    
    
}

