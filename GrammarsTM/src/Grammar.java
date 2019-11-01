import java.io.File;
import java.io.FileWriter;
import java.util.*;

public abstract class Grammar {

    //класс элемента грамматики
    public abstract class GrammarElement {
        public String name;

        public GrammarElement(String name) {
            this.name = name;
        }

        public String toString(){
            return name;
        }

        @Override
        public boolean equals(Object obj){
            //if (getClass() != obj.getClass())
            //return false;
            return (name.equals(((GrammarElement)obj).name));
        }

        @Override
        public int hashCode(){
            return name.hashCode();
        }
    }

    //класс терминала
    protected class Terminal extends GrammarElement {
        public Terminal(String name) {
            super(name);
        }
    }

    //класс нетерминала
    protected class NonTerminal extends GrammarElement{
        public NonTerminal(String name){
            super(name);
        }
    }

    //класс перехода
    protected class Rule{
        public List<GrammarElement> left;
        public List<GrammarElement> right;

        public Rule(GrammarElement left, List<GrammarElement> right) {
            this.left = new ArrayList<GrammarElement>();
            this.left.add(left);
            this.right = right;
        }

        public Rule(GrammarElement left, GrammarElement right){
            this.left = new ArrayList<GrammarElement>();
            this.left.add(left);
            this.right = new ArrayList<GrammarElement>();
            this.right.add(right);
        }

        public Rule(List<GrammarElement> left, List<GrammarElement> right){
            this.left = left;
            this.right = right;
        }

        public String toString(){
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < left.size(); i++){
                builder.append(left.get(i) + " ");
            }
            builder.append("-> ");
            for(int i = 0; i < right.size(); i++){
                builder.append(right.get(i) + " ");
            }

            return builder.toString();
        }
    }

    protected NonTerminal start; //стартовый нетерминал
    protected LinkedHashSet<NonTerminal> nonTerminals; //нетерминалы
    protected LinkedHashSet<Terminal> terminals; //терминалы
    protected LinkedHashSet<Rule> rules;

    public void printToFile(File file){
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(start.toString() + System.getProperty("line.separator"));
            for (Rule rule : rules) {
                fw.write(rule.toString() + System.getProperty("line.separator"));
            }
            fw.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public void generateWords(File file, int countOfWords) {
        Generator generator = new Generator(file,countOfWords);
        generator.startGenerate();
        generator.writeResultToFile();
    }

    private class Generator {
        private File file;
        private int countOfWords;
        private LinkedHashSet<String> stringsForWriting;
        private List<String> usedCombinations;

        private int cw = 0;

        public Generator(File file, int countOfWords) {
            this.file = file;
            this.countOfWords = countOfWords;
            this.stringsForWriting = new LinkedHashSet<>();
            this.usedCombinations = new LinkedList<>();
        }

        public void startGenerate() {
            for (Rule rule : rules) {
                if ((rule.left.size() == 1) && (rule.left.get(0).equals(start))) {
                    generate1(rule);
                }
            }
        }


        private void generate1(Rule rule) {
            //r1=A2->(1,1)A2; r2=A2->A3; r3=A3->(e,B)A3; r4=A3->e
            Rule r1 = null, r2=null,r3=null,r4=null;
            for (Rule r : rules) {
                if (r.left.toString().equals("[A2]")) {
                    if (r.right.toString().equals("[(1,1), A2]")) {
                        r1 = r;
                    }
                    else {
                        r2 = r;
                    }
                }
                if (r.left.toString().equals("[A3]")) {
                    if (r.right.toString().equals("[(e,B), A3]")) {
                        r3 = r;
                    }
                    else {
                        r4 = r;
                    }
                }
            }

            List<List<GrammarElement>> numbers = new ArrayList<>();

            int num = 1;
            while (cw < countOfWords) {
                List<GrammarElement> number= new ArrayList<>();
                number.addAll(rule.right);

                int n = num;
                while (n > 0) {
                    int startInd = 0, ind;
                    ind = containsList(r1.left, number, startInd);
                    List<GrammarElement> newList = new ArrayList<>();
                    for (int i = 0; i < ind; i++)
                    {
                        newList.add(number.get(i));
                    }
                    for (int i = 0; i < r1.right.size(); i++)
                    {
                        newList.add(r1.right.get(i));
                    }
                    for (int i = ind + r1.left.size(); i < number.size(); i++)
                    {
                        newList.add(number.get(i));
                    }
                    number = newList;
                    n--;
                }
                int startInd = 0, ind = containsList(r2.left, number, startInd);
                number.remove(ind);
                number.add(r2.right.get(0));

                ind = containsList(r3.left, number, startInd);
                number.remove(ind);
                number.add(r3.right.get(0));
                number.add(r3.right.get(1));

                ind = containsList(r4.left, number, startInd);
                number.remove(ind);
                number.add(r4.right.get(0));

                checkPrime(number);
                num++;
            }

        }

        private void checkPrime(List<GrammarElement> number) {
            Queue<List<GrammarElement>> queue = new LinkedList<>();
            queue.offer(number);

            while (true) {
                if(queue.isEmpty()) {
                    break;
                }
                List<GrammarElement> list = queue.poll();
                if(isPrime(list)){
                    createString(list);
                    break;
                }
                for (Rule r : rules) {
                    int startInd = 0;
                    int ind = 0;
                    while(startInd < list.size() - r.left.size() + 1) {
                        ind = containsList(r.left, list, startInd);
                        if (ind > -1) {
                            List<GrammarElement> newList = new ArrayList<>();

                            for (int i = 0; i < ind; i++)
                            {
                                newList.add(list.get(i));
                            }

                            for (int i = 0; i < r.right.size(); i++)
                            {
                                newList.add(r.right.get(i));
                            }

                            for (int i = ind + r.left.size(); i < list.size(); i++)
                            {
                                newList.add(list.get(i));
                            }

                            if((!useCombination(newList))){
                                queue.offer(newList);
                            }
                            startInd = ind + 1;
                        }

                        else{
                            break;
                        }
                    }
                }
            }
        }

        private boolean isPrime(List<GrammarElement> list) {

            for (GrammarElement element : list) {
                if(!element.name.equals("1") && !element.name.equals("e") && !element.name.equals("isPrime")) {
                    return false;
                }
            }
            cw++;
            return true;

        }

        private boolean compareListsOfSymbols(List<GrammarElement> list1, List<GrammarElement> list2, int ind){
            for(int i = 0; i < list1.size(); i++){
                if(!list1.get(i).equals(list2.get(ind + i))){
                    return false;
                }
            }
            return true;
        }

        private int containsList(List<GrammarElement> list1, List<GrammarElement> list2, int startInd){
            for(int i = startInd; i < list2.size() - list1.size() + 1; i++){
                if(compareListsOfSymbols(list1, list2, i)){
                    return i;
                }
            }
            return -1;
        }

        private boolean useCombination(List<GrammarElement> combination){

            int k = combination.size();
            if (combination.get(k-2).name.equals("(e,B)") && combination.get(k-3).name.equals("(e,B)")) {
                return true;
            }

            StringBuilder builder = new StringBuilder();
            for(GrammarElement element : combination){
                builder.append(element.name);
            }
            String str = builder.toString();
            if(usedCombinations.contains(str)){
                return true;
            }

            usedCombinations.add(str);
            return false;
        }

        private void createString(List<GrammarElement> list) {
            StringBuilder builder = new StringBuilder();
            for(GrammarElement element : list){
                if (element.name.equals("1")) {
                    builder.append(element.name);
                }
            }
            if (!stringsForWriting.contains(builder.toString())) {
                stringsForWriting.add(builder.toString());
            }


        }

        public void writeResultToFile(){
            try {
                FileWriter fw = new FileWriter(file);
                for (String str : stringsForWriting) {
                    fw.write(str + System.getProperty("line.separator"));
                }
                fw.close();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

    }
}
