package com.example.rpgplatform.MagicRoom;

import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class WarriorPool<T extends Entity> {
    private List<T> pool;

    public WarriorPool(int size, Class<T> entityType){
        pool = new ArrayList<>(size);
        for(int i=0; i<size; i++){
            try{
                pool.add(entityType.newInstance());
            }catch (InstantiationException | IllegalAccessException e){
                throw new RuntimeException("Failed");
            }
        }
    }
    public T getHitbox(){
        if (!pool.isEmpty()){
            return pool.removeFirst();
        }
        try{
            T entity = (T) pool.getFirst().getClass().newInstance();
            return entity;
        }catch (InstantiationException | IllegalAccessException e){
            System.out.println("errorrrr");
        }
        return null;
    }



}
