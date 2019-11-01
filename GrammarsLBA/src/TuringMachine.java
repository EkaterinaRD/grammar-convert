import java.io.*;
//import java.util.ArrayList;
import java.util.LinkedHashSet;
//import java.util.LinkedList;
import java.util.List;

public class TuringMachine {

    public enum Moving {
        LEFT,
        RIGHT
    }

    public class State{

        public String id;

        public State(String id){
            this.id = id;
        }

        @Override
        public boolean equals(Object obj){
            if (getClass() != obj.getClass())
                return false;
            return (id.equals(((State)obj).id));
        }

        @Override
        public int hashCode(){
            return id.hashCode();
        }
    }

    public class Transition{

        public State currentState;

        public State newState;

        public String currentSymbol;

        public String newSymbol;

        public Moving moving;

        public Transition(State currentState, String currentSymbol, State newState, String newSymbol, Moving moving){
            this.currentState = currentState;
            this.currentSymbol = currentSymbol;
            this.newState = newState;
            this.newSymbol = newSymbol;
            this.moving = moving;
        }
    }

    public LinkedHashSet<State> states;

    public LinkedHashSet<String> alphabet;

    public State startState;

    public LinkedHashSet<State> acceptingStates;

    public LinkedHashSet<Transition> function;

    public TuringMachine(LinkedHashSet<State> states, LinkedHashSet<String> alphabet, State startState,
                         LinkedHashSet<State> acceptingStates, LinkedHashSet<Transition> function){
        this.states = states;
        this.alphabet = alphabet;
        this.startState = startState;
        this.acceptingStates = acceptingStates;
        this.function = function;
    }

    public TuringMachine(File file){
        try{
            readFromFile(file);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void readFromFile(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);

        String line = reader.readLine();
        startState = new State(line);

        line = reader.readLine();
        String[] accepting = line.split(" ");
        acceptingStates = new LinkedHashSet<>();
        for(int i = 0; i < accepting.length; i++){
            acceptingStates.add(new State(accepting[i]));
        }

        function = new LinkedHashSet<Transition>();
        states = new LinkedHashSet<State>();
        alphabet = new LinkedHashSet<String>();
        // from read to write move
        line = reader.readLine();
        while (line != null) {
            String[] symbols = line.split(",");
            State from = new State(symbols[0]);
            String currentSymb = symbols[1];
            State to = new State(symbols[2]);
            String newSymb = symbols[3];
            Moving mov = (symbols[4].equals("l")) ? Moving.LEFT : Moving.RIGHT;

            function.add(new Transition(from, currentSymb, to, newSymb, mov));
            alphabet.add(currentSymb);
            alphabet.add(newSymb);
            states.add(from);
            states.add(to);

            line = reader.readLine();
        }
        fr.close();
    }
}
