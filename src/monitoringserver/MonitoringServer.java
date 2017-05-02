/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoringserver;

/**
 *
 * @author Purvesh
 */
public class MonitoringServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.err.println("Starting the Monitoring Server");
        Monitor m = new Monitor(8080, 100);
        AssignRequest a = new AssignRequest();
    }
    
}
