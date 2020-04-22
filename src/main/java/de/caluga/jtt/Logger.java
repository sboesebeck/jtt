package de.caluga.jtt;

import jdk.jfr.StackTrace;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private String name;

    public Logger(Class cls){name=cls.getName();}

    public void info(String txt){
        doLog("info",txt);
    }



    private void doLog(String level,String text){
        SimpleDateFormat df=new SimpleDateFormat("yyyy-mm-dd hh:MM:ss.S");
        var stacktrace=new RuntimeException().getStackTrace();
        var out=System.out;
        out.print(df.format(new Date()));
        out.print(" - ");
        out.print(level);
        out.print(": ");
        out.print(stacktrace[2].getClassName()+"."+stacktrace[2].getMethodName()+"(");
        out.print(stacktrace[2].getFileName()+":"+stacktrace[2].getLineNumber());
        out.print("): ");
        out.println(text);
    }
}
