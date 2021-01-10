package Main;
import UI.Ui;

import java.io.IOException;
import java.net.ConnectException;

public class main {
    public static void main(String[] args){
        runUi(args);
    }

    public static void runUi(String[] args){
        Ui ui = null;
        try {
            ui = new Ui(args);
            ui.run();

        } catch (IOException e) {
            System.out.println("there is no connection!\nGoodbye");
        }

    }
}
