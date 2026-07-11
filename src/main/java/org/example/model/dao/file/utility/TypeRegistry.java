// TypeRegistry.java
package org.example.model.dao.file.utility;

import org.example.model.services.EntityI;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class TypeRegistry {

    private static final String JSON_EXT = ".json";

    private final Map<Class<?>, String> classToFolder = new LinkedHashMap<>();
    private final Map<String, Class<?>> folderToClass = new LinkedHashMap<>();
    private final Path rootPath;

    public TypeRegistry(Path rootPath) {
        this.rootPath = rootPath;
    }

    public void register(Class<?> clazz, String folder) {
        if (!EntityI.class.isAssignableFrom(clazz))
            throw new IllegalArgumentException(clazz.getSimpleName() + " must implement EntityI");
        classToFolder.put(clazz, folder);
        folderToClass.put(folder, clazz);
    }

    public boolean isRegistered(Class<?> clazz) {
        return classToFolder.containsKey(clazz);
    }
    public boolean isFolder(String folder) {
        return folderToClass.containsKey(folder);
    }

    public String folderOf(Class<?> clazz) {
        String folder = classToFolder.get(clazz);
        if (folder == null)
            throw new IllegalArgumentException("No folder registered for: " + clazz.getSimpleName());
        return folder;
    }
    public Class<?> classOf(String folder) {
        Class<?> clazz = folderToClass.get(folder);
        if (clazz == null)
            throw new IllegalArgumentException("No class registered for folder: " + folder);
        return clazz;
    }


    /** Percorso canonico del file di un'entità dato folder e id. */
    public Path entityPath(String folder, String id) {
        return rootPath.resolve(folder).resolve(id + JSON_EXT);
    }
    public String entityPathStr(String folder, String id) {return folder+"/"+id + JSON_EXT;}
    public String entityPathStr(Class<?> clazz, String id) {return entityPathStr(folderOf(clazz),id);}
    public String entityPathStr(EntityI<?> entityI) {return entityPathStr(entityI.getClass(),entityI.getId().toString());}

    public Path resolvePath(String relativePath) {return rootPath.resolve(relativePath);}
    public Path rootPath() {
        return rootPath;
    }

}