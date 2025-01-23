import java.util.*;

public class InputHandler {
    private Scanner in;
    private static InputHandler instance;
    private InputHandler(){
        in = new Scanner(System.in);
    }

    public static InputHandler getInstance(){
        if(instance==null) instance = new InputHandler();
        return instance;
    }

    public void clearConsole(){
        System.out.print("\033\143");
    }
    public void printInputChar(){
        System.out.print("> ");
    }
    public void close(){
        in.close();
    }
    public String nextLine(){
        printInputChar();
        return in.nextLine();
    }
    public String nextNonEmptyLine(){
        String out;
        do{
            printInputChar();
            out = in.nextLine();
        }while(out.equals(""));
        return out;
    }
    public boolean confirmUserProceed(){
        System.out.println("\nEnter K to continue");
        String out;
        printInputChar();
        do{
            out = in.nextLine();
        } while(!(out.toLowerCase().equals("k")));
        clearConsole();
        return true;
    }
    public int nextInt(){
        int out;
        while(true){
            try {
                printInputChar();
                out = Integer.parseInt(in.nextLine());
                if(out < 0){
                    System.out.println("You must enter a positive integer.");
                    continue;
                } else {
                    return out;
                }
            } catch (NumberFormatException e) {
                System.out.println("You must enter an integer.");
            } catch (Exception e){
                System.out.println("Something went wrong.");
            }
        }
    }
    public float nextPositiveFloat(){
        float out;
        while(true) {
            try {
                printInputChar();
                out = Float.parseFloat(in.nextLine());
                if(out <= 0){
                    System.out.println("You must enter a non-negative value.");
                } else {
                    return out;
                }
            } catch (Exception e){
                System.out.println("You must enter a value.");
            }
        }
    }
}
