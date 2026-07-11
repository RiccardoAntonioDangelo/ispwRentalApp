package org.example.model.dao.file.utility;


import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Registro avanzato con supporto per alias manuali e risalita gerarchica.
 */
public final class ManualTypeRegistry extends TypeRegistry {

    private final Map<Class<?>, String> manualAliases = new HashMap<>();

    public ManualTypeRegistry(Path rootPath) {super(rootPath);}
    public void addAlias(Class<?> clazz) {
        if (clazz == null) return;
        this.manualAliases.put(clazz, this.aliasOf(clazz));
        super.register(clazz,this.aliasOf(clazz));
    }
    public String getAlias(Class<?> clazz) {
        if (clazz == null || clazz.equals(Object.class)) return null;

        String alias = this.manualAliases.get(clazz);
        if (alias != null) return alias;

        alias = getAlias(clazz.getSuperclass());
        if (alias != null) {this.manualAliases.put(clazz, alias);return alias;}

        return this.aliasOf(clazz);
    }
    public String aliasOf(Class<?> clazz) {
        if (clazz == null) return null;
        return clazz.getSimpleName().toLowerCase();
    }


    @Override
    public boolean isRegistered(Class<?> clazz) {
        return super.isFolder(getAlias(clazz)) ;
    }
    @Override
    public String folderOf(Class<?> clazz) {
        return super.folderOf(super.classOf(getAlias(clazz))  )  ;
    }



}