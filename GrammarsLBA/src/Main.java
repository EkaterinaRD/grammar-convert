import java.io.File;

public class Main {
    public static void main(String[] args) {
        File fileLBA = new File("lba.txt");
        LBA lba = new LBA(fileLBA);
        ContextSensitiveGrammar cs = new ContextSensitiveGrammar(lba);
        File fileCS = new File("cs.txt");
        cs.printToFile(fileCS);
        File fileWords = new File("words.txt");
        System.out.println("start gen");
        cs.generateWords(fileWords, 7);
    }
}
