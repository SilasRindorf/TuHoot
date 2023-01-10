package dtu.group8.util;

public class Printer {
    private static Printer instance;
    public boolean log = true;
    private Printer(){

    }
    public static Printer getInstance(){
        if (instance == null){
            instance = new Printer();
        }
        return instance;
    }


    public void print(String TAG, String str, PrintColor color){
        if (log) {
            System.out.println(color.value + TAG + ": " + str + PrintColor.ANSI_RESET.value);
        }
    }
    public void print(String str, PrintColor color){
        print("",str,color);
    }

    public void print(String str){
        print(str, PrintColor.WHITE);
    }

    public enum PrintColor {
        ANSI_RESET( "\u001B[0m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),

        CYAN("\u001B[36m"),
        WHITE("\u001B[37m");

        public final String value;
        PrintColor(String value){
            this.value = value;
        }
    }

}
