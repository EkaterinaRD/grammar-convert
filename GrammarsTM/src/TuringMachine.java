import javafx.animation.Transition;

import java.io.*;
import java.util.LinkedHashSet;

public class TuringMachine {

    //класс движения ленты
    public enum Moving {
        LEFT,
        RIGHT
    }

    //класс, который описывает состояния
    public class State {
        public String id;
        public State(String id) {
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

    public State startState;                    //стартовое состояние q0
    public LinkedHashSet<State> states;         //множество состояний Q
    public LinkedHashSet<State> acceptingStates; //множество допускающих состояний F

    public LinkedHashSet<String> alphabet;       //алфавит

    //класс который  описывает одну функцию
    public class Transition {
        public State curentState;
        public String curentSymbol;
        public State newState;
        public String newSymbol;
        public Moving moving;

        public Transition(State curentState, String curentSymbol, State newState, String newSymbol, Moving moving) {
            this.curentState = curentState;
            this.curentSymbol = curentSymbol;
            this.newState = newState;
            this.newSymbol = newSymbol;
            this.moving = moving;
        }
    }

    public LinkedHashSet<Transition> function; //массив всех переходов


    public TuringMachine(File file) {
        try{
            readFromFile(file);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void  readFromFile(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);

        //читаем стартовый символ
        String line = reader.readLine();
        startState = new State(line);

        //читаем допускающие состояния
        line = reader.readLine();
        String[] acceting = line.split(" ");       //массив допускающих состояний
        acceptingStates = new LinkedHashSet<>();         //переносим в массив acceptingStates
        for (int i = 0; i < acceting.length; i++) {
            acceptingStates.add(new State(acceting[i]));
        }

        //читаем функции перехода Delta
        function = new LinkedHashSet<Transition>();
        states = new LinkedHashSet<State>();
        alphabet = new LinkedHashSet<String>();
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
