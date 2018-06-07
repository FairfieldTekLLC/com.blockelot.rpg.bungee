/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockelot.rpg.RpgPlayer.RabbitMQ;
import com.google.gson.Gson;
/**
 *
 * @author geev
 */
public final class RabbitMessagePayload {
    private String Type;
    private String Data;
    
    public RabbitMessagePayload(){
        
    }
    
    public RabbitMessagePayload(Object o){
        setType(o.getClass().getSimpleName());
         Gson gson = new Gson();
         setData( gson.toJson(o));
    }
    
    public String getData(){
        return Data;
    }
    public void setData(String data)
    {
        Data=data;
    }
    
    public String getType(){
        return Type;
    }
    public void setType(String t){
        Type = t;
    }
    
}
