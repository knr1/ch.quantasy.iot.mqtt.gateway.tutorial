/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.cpuLoad;

import ch.quantasy.mqtt.gateway.client.ClientContract;

/**
 *
 * @author reto
 */
public class CPULoadServiceContract extends ClientContract {

    private final String CPU;
    private final String LOAD;
    private final String CALLBACK_PERIOD;
    public final String INTENT_CPU_LOAD_CALLBACK_PERIOD;
    public final String STATUS_CPU_LOAD_CALLBACK_PERIOD;
    public final String EVENT_CPU_LOAD;

    public CPULoadServiceContract(String instanceID) {
        super("Tutorial", "CPULoad", instanceID);

        CPU="cpu";
        LOAD="load";
        CALLBACK_PERIOD="callbackPeriod";
        INTENT_CPU_LOAD_CALLBACK_PERIOD = INTENT + "/"+CPU+"/"+LOAD+"/"+CALLBACK_PERIOD;
        STATUS_CPU_LOAD_CALLBACK_PERIOD = STATUS + "/"+CPU+"/"+LOAD+"/"+CALLBACK_PERIOD;
        EVENT_CPU_LOAD=EVENT +"/"+CPU+"/"+LOAD;
    }

}
