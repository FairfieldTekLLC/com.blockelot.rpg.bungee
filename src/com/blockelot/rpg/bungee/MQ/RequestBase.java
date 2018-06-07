/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockelot.rpg.bungee.MQ;

import com.blockelot.rpg.RpgPlayer.RabbitMQ.RabbitMessagePayload;

/**
 *
 * @author geev
 */
abstract public class RequestBase {
    public abstract void Execute();
}
