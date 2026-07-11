package org.example.view.javafx.util.state;

public abstract class  ConcreteState implements State {
    private Context context=null;
    public Context getContext() {return context;}
    @Override
    public void setContext(Context ctx){this.context=ctx;}

}
