import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // maybe a hash generator class -> add a "Generate random hash" button in GUI
        int input = 0;

        switch(input) {
            case 0:
                seqGUI.GUI();
                break;
            case 1:
                parGUI.GUI();
                break;
            case 2:
                // CUDA
                break;
            case 3:
                Result.showResult("69.7s", "Banana");
                break;
            default:

        }

    }
}