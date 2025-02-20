package Main;
import java.util.Scanner;

public class App {
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws Exception {
        System.out.print("enter number procesess \n> ");
        int numProcesses = Integer.parseInt(scanner.nextLine());
        new Controller(numProcesses);
        scanner.close();
    }
}