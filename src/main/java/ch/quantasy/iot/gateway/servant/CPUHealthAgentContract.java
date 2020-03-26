/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant;

import ch.quantasy.mdservice.message.Message;
import ch.quantasy.mdsmqtt.gateway.client.contract.AyamlServiceContract;
import java.util.Map;

/**
 *
 * @author reto
 */
public class CPUHealthAgentContract extends AyamlServiceContract{

    public CPUHealthAgentContract(String rootContext, String baseClass, String instance) {
        super(rootContext, baseClass, instance);
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        //none
    }
    
}
