package UI.Main;
import UI.Ui;

public class main {
    public static void main(String[] args){
        runUi(args);
    }

    public static void runUi(String[] args){
        Ui ui = new Ui(args);
        ui.run();
    }
}
