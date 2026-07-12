package org.example.model.dao.filejson;

import org.example.model.services.EntityI;


import java.io.IOException;
import java.nio.file.Path;

public class SmartJsonSerializer {

    public SmartJsonSerializer(Path baseDataFolder) {
    }

    public void registerAlias(Class<?> clazz, String alias) {
        // Vuoto
    }

    public synchronized void set(EntityI<?> entity) throws IOException {
        // Vuoto
    }


    public synchronized <T> T get(String id, Class<T> targetClass) throws IOException {
        if("".equals(id+targetClass))
         throw new IOException(id+targetClass);
        return null;
    }

}