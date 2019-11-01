import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class Grammar {
    public abstract class GrammarElement{
        public String name;

        public GrammarElement(String name){
            this.name = name;
        }

        public String toString(){
            return name;
        }

        @Override
        public boolean equals(Object obj){
            return (name.equals(((GrammarElement)obj).name));
        }

        @Override
        public int hashCode(){
            return name.hashCode();
        }
    }

    protected class Terminal extends GrammarElement{
        public Terminal(String name){
            super(name);
        }
    }

    protected class NonTerminal extends GrammarElement{
        public NonTerminal(String name){
            super(name);
        }
    }

    protected class Rule{
        public List<GrammarElement> left;
        public List<GrammarElement> right;

        public Rule(List<GrammarElement> left, List<GrammarElement> right){
            this.left = left;
            this.right = right;
        }

        public Rule(GrammarElement left, GrammarElement right){
            this.left = new ArrayList<GrammarElement>();
            this.left.add(left);
            this.right = new ArrayList<GrammarElement>();
            this.right.add(right);
        }

        //public Rule(List<GrammarElement> left, GrammarElement right){
            //this.left = left;
            //this.right = new ArrayList<GrammarElement>();
            //this.right.add(right);
        //}

        public Rule(GrammarElement left, List<GrammarElement> right){
            this.left = new ArrayList<GrammarElement>();
            this.left.add(left);
            this.right = right;
        }

        public Rule(GrammarElement left1, GrammarElement left2, GrammarElement right1, GrammarElement right2){
            this.left = new ArrayList<GrammarElement>();
            this.left.add(left1);
            this.left.add(left2);
            this.right = new ArrayList<GrammarElement>();
            this.right.add(right1);
            this.right.add(right2);
        }

        public Rule(GrammarElement left1, GrammarElement right1, GrammarElement right2){
            this.left = new ArrayList<GrammarElement>();
            this.left.add(left1);
            this.right = new ArrayList<GrammarElement>();
            this.right.add(right1);
            this.right.add(right2);
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

    protected NonTerminal start;

    protected LinkedHashSet<NonTerminal> nonTerminals;

    protected LinkedHashSet<Terminal> terminals;

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

    public void generateWords(File file, int countOfWords){
        Generator generator = new Generator(file, countOfWords);
        generator.startGenerate();
        generator.writeResultToFile();
    }

    private class Generator{
        private File file;

        private LinkedHashSet<String> stringsForWriting;

        private int countOfWords;

        private List<String> usedCombinatios;

        private int maxLength = 25;

        public Generator(File file, int countOfWords){
            this.file = file;
            this.stringsForWriting = new LinkedHashSet<>();
            this.usedCombinatios = new LinkedList<>();
            this.countOfWords = countOfWords;
        }

        private void startGenerate(){
            for(Rule rule : rules){
                if((rule.left.size() == 1) && (rule.left.get(0).equals(start))){
                    generate(rule);
                }
            }
        }

        private void generate(Rule rule){

            Queue<List<GrammarElement>> queue = new LinkedList<>();

            queue.offer(rule.right);

            while((stringsForWriting.size() < countOfWords) && (queue.size() > 0)){

                List<GrammarElement> list = queue.poll();

                if(allSymbolsTerminal(list)){
                    createString(list);
                }

                for(Rule r : rules){
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

                            if((newList.size() < maxLength)&&(!useCombination(newList))){
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

        private boolean allSymbolsTerminal(List<GrammarElement> list){
            for(GrammarElement value : list){
                if(!terminals.contains(value)){
                    return false;
                }
            }
            return true;
        }


        private void createString(List<GrammarElement> list){
            StringBuilder builder = new StringBuilder();
            for(GrammarElement element : list){
                builder.append(element.name);
            }
            stringsForWriting.add(builder.toString());

        }

        private boolean useCombination(List<GrammarElement> combination){
            StringBuilder builder = new StringBuilder();
            for(GrammarElement element : combination){
                builder.append(element.name);
            }
            String str = builder.toString();
            if(usedCombinatios.contains(str)){
                return true;
            }
            usedCombinatios.add(str);
            return false;
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
