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
public class PlayerWorldResponse {
    private String PlayerId;
    public String getPlayerId(){
        return PlayerId;
    }
    public void setPlayerId(String id){
        PlayerId = id;
    }
    
    private String WorldName;
    public String getWorldName(){
        return WorldName;
    
}
    public void setWorldName(String w){
        WorldName = w;
    }

  
}