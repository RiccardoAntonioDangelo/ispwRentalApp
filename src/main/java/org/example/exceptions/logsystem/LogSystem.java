package org.example.exceptions.logsystem;

public class LogSystem {
    private static String log="";
    private LogSystem(){

    }
    public static void errorType(String message)
    {
        log+="\n"+message;
    }
    public  static String read()
    {
        return log;
    }
}
