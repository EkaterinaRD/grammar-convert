import java.io.File;

public class MainClass {

    public static void main(String[] args) {

        File fileTM = new File("tm.txt");
        TuringMachine tm = new TuringMachine(fileTM);

        File fileUD = new File("ud.txt");
        ConvectorUD ud = new ConvectorUD(tm);
        ud.printToFile(fileUD);

        File fileWords = new File("words.txt");
        System.out.println("start");
        ud.generateWords(fileWords, 7);
        System.out.println("finish! result in file \"words.txt\"");
    }
}
