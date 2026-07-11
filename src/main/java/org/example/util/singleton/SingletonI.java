package org.example.util.singleton;
import java.util.function.Supplier;

/**
 * interfaccia marcatore per identificare le classi gestite come Singleton.
 */
public interface SingletonI{
     static <T extends SingletonI> T getOrCreateSingleton(Class<T> clazz, Supplier<T> factory) {return SingletonService.getOrCreate(clazz,factory);}

     static <T extends SingletonI> T getSingleton(Class<T> clazz) {return SingletonService.get(clazz);}
     static <T extends SingletonI> void registerSingleton(Class<T> clazz, T instance) {SingletonService.register(clazz,instance);}
     static <T extends SingletonI> void registerSingleton(T instance) {SingletonService.register((Class<T>) instance.getClass(), instance);}
}