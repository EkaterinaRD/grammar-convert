import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ConvectorUD extends Grammar {

    public ConvectorUD(TuringMachine tm) {
        convectorFromTm(tm);
        System.out.println(rules.size());
    }

    public void convectorFromTm(TuringMachine tm) {
        terminals = new LinkedHashSet<Terminal>();
        terminals.add(new Terminal("1"));

        nonTerminals = new LinkedHashSet<NonTerminal>();
        rules = new LinkedHashSet<Rule>();

        start = new NonTerminal("A1");

        //(1) A1 ->  (e, B) q0 A2
        List<GrammarElement> rt1 = new ArrayList<>();
        String str0 = String.format("(e,B)");
        rt1.add(new NonTerminal(str0));
        rt1.add(new NonTerminal(tm.startState.id));
        rt1.add(new NonTerminal("A2"));
        rules.add(new Rule(new NonTerminal("A1"), rt1));

        //(2) A2 -> (a,a) A2
        for (Terminal a : terminals) {
            String str = String.format("(%s,%s)", a.name, a.name);
            List<GrammarElement> rt = new ArrayList<>();
            rt.add(new NonTerminal(str));
            rt.add(new NonTerminal("A2"));

            rules.add(new Rule(new NonTerminal("A2"), rt));

        }

        //(3) A2 -> A3
        rules.add(new Rule(new NonTerminal("A2"), new NonTerminal("A3")));

        //(4) A3 -> (e,B) A3
        String str = String.format("(e,B)");
        List<GrammarElement> rt2 = new ArrayList<>();
        rt2.add(new NonTerminal(str));
        rt2.add(new NonTerminal("A3"));
        rules.add(new Rule(new NonTerminal("A3"), rt2));

        //(5) A3 -> e
        rules.add(new Rule(new NonTerminal("A3"), new NonTerminal("e")));

        //(6) q(a,A) -> (a,M)p
        for (TuringMachine.Transition fun : tm.function) {

            if (fun.moving == TuringMachine.Moving.RIGHT){
                //a =/=e
                for (Terminal a : terminals) {
                    List<GrammarElement> left = new ArrayList<>();
                    left.add(new NonTerminal(fun.curentState.id));
                    String strL = String.format("(%s,%s)", a.name, fun.curentSymbol);
                    left.add(new NonTerminal(strL));

                    List<GrammarElement> right = new ArrayList<>();
                    String strR = String.format("(%s,%s)", a.name, fun.newSymbol);
                    right.add(new NonTerminal(strR));
                    right.add(new NonTerminal(fun.newState.id));

                    rules.add(new Rule(left, right));
                }

                //a ==e
                List<GrammarElement> left = new ArrayList<>();
                left.add(new NonTerminal(fun.curentState.id));
                String strL = String.format("(e,%s)", fun.curentSymbol);
                left.add(new NonTerminal(strL));

                List<GrammarElement> right = new ArrayList<>();
                String strR = String.format("(e,%s)", fun.newSymbol);
                right.add(new NonTerminal(strR));
                right.add(new NonTerminal(fun.newState.id));

                rules.add(new Rule(left, right));
            }

        }

        //(7)(b,C)q(a,A) -> p(b,C)(a,M)
        for (TuringMachine.Transition fun : tm.function) {
            if (fun.moving == TuringMachine.Moving.LEFT) {
                for (String C : tm.alphabet) {
                    //a=/=e
                    for (Terminal a : terminals) {
                        //b=/=e
                        for (Terminal b : terminals) {
                            List<GrammarElement> lf = new ArrayList<>();
                            String strL1 = String.format("(%s,%s)", b.name, C);
                            lf.add(new NonTerminal(strL1));
                            lf.add(new NonTerminal(fun.curentState.id));
                            String strL2 = String.format("(%s,%s)", a.name, fun.curentSymbol);
                            lf.add(new NonTerminal(strL2));

                            List<GrammarElement> rt = new ArrayList<>();
                            rt.add(new NonTerminal(fun.newState.id));
                            String strR1 = String.format("(%s,%s)", b.name, C);
                            rt.add(new NonTerminal(strR1));
                            String strR2 = String.format("(%s,%s)", a.name, fun.newSymbol);
                            rt.add(new NonTerminal(strR2));

                            rules.add(new Rule(lf, rt));
                        }

                        //b==e
                        List<GrammarElement> lf = new ArrayList<>();
                        String strL1 = String.format("(e,%s)",  C);
                        lf.add(new NonTerminal(strL1));
                        lf.add(new NonTerminal(fun.curentState.id));
                        String strL2 = String.format("(%s,%s)", a.name, fun.curentSymbol);
                        lf.add(new NonTerminal(strL2));

                        List<GrammarElement> rt = new ArrayList<>();
                        rt.add(new NonTerminal(fun.newState.id));
                        String strR1 = String.format("(e,%s)", C);
                        rt.add(new NonTerminal(strR1));
                        String strR2 = String.format("(%s,%s)", a.name, fun.newSymbol);
                        rt.add(new NonTerminal(strR2));

                        rules.add(new Rule(lf, rt));
                    }

                    //a==e
                    for (Terminal b : terminals) {
                        List<GrammarElement> lf = new ArrayList<>();
                        String strL1 = String.format("(%s,%s)", b.name, C);
                        lf.add(new NonTerminal(strL1));
                        lf.add(new NonTerminal(fun.curentState.id));
                        String strL2 = String.format("(e,%s)", fun.curentSymbol);
                        lf.add(new NonTerminal(strL2));

                        List<GrammarElement> rt = new ArrayList<>();
                        rt.add(new NonTerminal(fun.newState.id));
                        String strR1 = String.format("(%s,%s)", b.name, C);
                        rt.add(new NonTerminal(strR1));
                        String strR2 = String.format("(e,%s)", fun.newSymbol);
                        rt.add(new NonTerminal(strR2));

                        rules.add(new Rule(lf, rt));
                    }

                    //a=b=e
                    List<GrammarElement> lf = new ArrayList<>();
                    String strL1 = String.format("(e,%s)", C);
                    lf.add(new NonTerminal(strL1));
                    lf.add(new NonTerminal(fun.curentState.id));
                    String strL2 = String.format("(e,%s)", fun.curentSymbol);
                    lf.add(new NonTerminal(strL2));

                    List<GrammarElement> rt = new ArrayList<>();
                    rt.add(new NonTerminal(fun.newState.id));
                    String strR1 = String.format("(e,%s)", C);
                    rt.add(new NonTerminal(strR1));
                    String strR2 = String.format("(e,%s)", fun.newSymbol);
                    rt.add(new NonTerminal(strR2));

                    rules.add(new Rule(lf, rt));
                }
            }
        }

        //(8) (a,C)q -> qaq
        for (TuringMachine.State st : tm.acceptingStates) {
            for (String C : tm.alphabet) {
                for (Terminal a : terminals) {

                    List<GrammarElement> lf = new ArrayList<>();
                    String strL = String.format("(%s,%s)", a.name, C);
                    lf.add(new NonTerminal(strL));
                    lf.add(new NonTerminal(st.id));

                    List<GrammarElement> rt = new ArrayList<>();
                    rt.add(new NonTerminal(st.id));
                    rt.add(new NonTerminal(a.name));
                    rt.add(new NonTerminal(st.id));

                    rules.add(new Rule(lf, rt));
                }

                List<GrammarElement> lf = new ArrayList<>();
                String strL = String.format("(e,%s)", C);
                lf.add(new NonTerminal(strL));
                lf.add(new NonTerminal(st.id));

                List<GrammarElement> rt = new ArrayList<>();
                rt.add(new NonTerminal(st.id));
                rt.add(new NonTerminal("e"));
                rt.add(new NonTerminal(st.id));

                rules.add(new Rule(lf, rt));
            }
        }

        //(9) q(a,C) -> qaq
        for (TuringMachine.State st : tm.acceptingStates) {
            for (String C : tm.alphabet) {

                for (Terminal a : terminals) {

                    List<GrammarElement> lf = new ArrayList<>();
                    lf.add(new NonTerminal(st.id));
                    String strL = String.format("(%s,%s)", a.name, C);
                    lf.add(new NonTerminal(strL));

                    List<GrammarElement> rt = new ArrayList<>();
                    rt.add(new NonTerminal(st.id));
                    rt.add(new NonTerminal(a.name));
                    rt.add(new NonTerminal(st.id));

                    rules.add(new Rule(lf, rt));
                }

                List<GrammarElement> lf = new ArrayList<>();
                lf.add(new NonTerminal(st.id));
                String strL = String.format("(e,%s)", C);
                lf.add(new NonTerminal(strL));

                List<GrammarElement> rt = new ArrayList<>();
                rt.add(new NonTerminal(st.id));
                rt.add(new NonTerminal("e"));
                rt.add(new NonTerminal(st.id));

                rules.add(new Rule(lf, rt));
            }
        }

        //(10) q->e
        /*for (TuringMachine.State st : tm.acceptingStates) {
            rules.add(new Rule(new NonTerminal(st.id), new NonTerminal("e")));
        }*/
    }

}
