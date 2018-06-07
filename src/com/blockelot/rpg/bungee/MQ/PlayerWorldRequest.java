/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockelot.rpg.bungee.MQ;

/**
 *
 * @author geev
 */
public class PlayerWorldRequest extends RequestBase{

    private String PlayerId;

    public String getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(String id) {
        PlayerId = id;
    }

    @Override
    public void Execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
