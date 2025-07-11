package winter;

import winter.dispatcher.Dispatcher;

public class WinterMain {
    public static void main(String[] args){
        System.out.println("WinterFramework Start");

        Dispatcher dispatcher=new Dispatcher();

        dispatcher.dispatch("/hello");
        //hello from winter Framework!
        dispatcher.dispatch("/bye");
        //Goodbye from winter Framework!
        dispatcher.dispatch("/invalid");
        //4o4 Not Found
    }
}
