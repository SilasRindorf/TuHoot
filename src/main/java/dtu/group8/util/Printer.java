package dtu.group8.util;

public class Printer {
    private static boolean log = true;
    private String defaultTAG = "";
    private PrintColor defaultPrintColor = PrintColor.ANSI_RESET;
    public Printer(){

    }

    public Printer(String defaultTAG, PrintColor defaultPrintColor){
        this.defaultTAG = defaultTAG + " ";
        this.defaultPrintColor = defaultPrintColor;
    }
    public void print(String TAG, String str, PrintColor color){
        if (log) {
            System.out.print(color.value + TAG + str + PrintColor.ANSI_RESET.value);
        }
    }
    public void print(String str, PrintColor color){
        print(defaultTAG,str,color);
    }

    public void print(String str){
        print(str, defaultPrintColor);
    }
    public void println(String TAG, String str, PrintColor color){
        if (log) {
            System.out.println(color.value + TAG + str + PrintColor.ANSI_RESET.value);
        }
    }
    public void println(String str, PrintColor color){
        println(defaultTAG,str,color);
    }

    public void println(String str){
        println(str, defaultPrintColor);
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public String getDefaultTAG() {
        return defaultTAG;
    }

    public void setDefaultTAG(String defaultTAG) {
        this.defaultTAG = defaultTAG + " ";
    }

    public PrintColor getDefaultPrintColor() {
        return defaultPrintColor;
    }

    public void setDefaultPrintColor(PrintColor defaultPrintColor) {
        this.defaultPrintColor = defaultPrintColor;
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
