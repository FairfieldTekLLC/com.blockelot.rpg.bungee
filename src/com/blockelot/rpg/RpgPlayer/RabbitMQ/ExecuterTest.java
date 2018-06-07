/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockelot.rpg.RpgPlayer.RabbitMQ;

/**
 *
 * @author geev
 */
public class ExecuterTest extends RabbitExecuter {

    @Override
    public RabbitMessagePayload Execute(RabbitMessagePayload payload) {
        
        System.out.print(payload.getType());
        System.out.print("Recieved Message: " + payload.getData());
        return payload;
        
    }
    
}
