package org.example.model.entity.actors.factory;


import org.example.model.entity.actors.User;
import org.example.util.singleton.SingletonI;

public class ActorsManager implements ActorAbstractFactory,SingletonI {
    private final ActorAbstractFactory activeFactory;

    static {ActorsManager.initialize(ActorEnumFactory.STRATEGY);}
    // Costruttore privato: riceve la factory scelta (Simple o Strategy)
    private ActorsManager(ActorEnumFactory type) {this.activeFactory = ActorFactory.getFactory(type);}

    public static void initialize(ActorEnumFactory type) {SingletonI.registerSingleton(new ActorsManager(type));}
    public static ActorsManager getInstance() {return SingletonI.getSingleton(ActorsManager.class);}

    // --- FACTORY PER ENTITÀ (Delegata alla activeFactory) ---

    public ActorAbstractFactory getActorFactory() {return activeFactory;}

    @Override
    public User createClient(String email, String password) {return getActorFactory().createClient(email,  password);}

    @Override
    public User createOwner(String email, String password) {return getActorFactory().createOwner(email,  password);}
    public static User create(String email, String password, String role) {
        return switch (ActorEnum.fromValue(role)) {
            case CLIENT -> getInstance().createClient(email, password);
            case OWNER -> getInstance().createOwner(email, password);
            case USER -> new User(email, password); // Utente base senza privilegi
        };
    }
}