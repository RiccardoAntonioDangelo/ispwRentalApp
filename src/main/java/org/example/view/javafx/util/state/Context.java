package org.example.view.javafx.util.state;


public abstract class Context {
    protected ConcreteState currentState;

    protected Context(ConcreteState initialState) {setState(initialState);}
    public void setState(ConcreteState state) {this.currentState = state;if (state != null) state.setContext(this);}
    public ConcreteState getCurrentState(){return currentState;}
}
