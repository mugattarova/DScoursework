import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public abstract class Client{

    public Client(){}

    public static void clearConsole(){
        System.out.print("\033\143");
    }
    
    public static void printInputChar(){
        System.out.print("> ");
    }
}