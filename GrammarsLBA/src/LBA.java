import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LBA extends TuringMachine {
    public String LeftMarker;
    public String RightMarker;
    public List<String> alphabetOfWord;

    public LBA(File file){
        super(file);
        LeftMarker = "c";
        RightMarker = "$";
        alphabetOfWord = new ArrayList<>();
        alphabetOfWord.add("1");
    }
}
