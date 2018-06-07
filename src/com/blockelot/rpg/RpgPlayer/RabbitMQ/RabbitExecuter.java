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
abstract public class RabbitExecuter {
    public abstract RabbitMessagePayload Execute(RabbitMessagePayload payload);
}
