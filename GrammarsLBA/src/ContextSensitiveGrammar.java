import javafx.animation.Transition;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class ContextSensitiveGrammar extends Grammar{

    public ContextSensitiveGrammar(LBA lba){
        convertFromLBA(lba);
        System.out.println(rules.size());

        LinkedHashSet<GrammarElement> generatingElements = getGeneratingElements();
        LinkedHashSet<GrammarElement> reachableNonTerminals = getReachableNonTerminals();
        LinkedHashSet<Rule> correctedRules = new LinkedHashSet<Rule>();
        for(Rule rule : rules) {
            boolean onlyReachableAndGenerating = true;
            for(GrammarElement value : rule.left){
                if (!reachableNonTerminals.contains(value) || !generatingElements.contains(value)) {
                    onlyReachableAndGenerating = false;
                    break;
                }
            }

            for(GrammarElement value : rule.right){
                if (!reachableNonTerminals.contains(value) || !generatingElements.contains(value)) {
                    onlyReachableAndGenerating = false;
                    break;
                }
            }

            if (onlyReachableAndGenerating) {
                correctedRules.add(rule);
                for(GrammarElement value : rule.right) {
                    nonTerminals.add((NonTerminal)value);
                }
                for(GrammarElement value : rule.left) {
                    nonTerminals.add((NonTerminal)value);
                }
            }
        }
        rules = correctedRules;
    }

    public void convertFromLBA(LBA lba) {
        terminals = new LinkedHashSet<Terminal>();
        rules = new LinkedHashSet<Rule>();
        nonTerminals = new LinkedHashSet<NonTerminal>();

        String leftMarker = lba.LeftMarker;
        String rightMarker = lba.RightMarker;
        for (String terminal : lba.alphabetOfWord) {
            terminals.add(new Terminal(terminal));
        }
        start = new NonTerminal("A1");

        for(Terminal a : terminals)
        {
            // A1 → [q0, ¢, a, a, $]
            String str = String.format("[%s,%s,%s,%s,%s]", lba.startState.id, leftMarker, a.name, a.name, rightMarker);
            rules.add(new Rule(new NonTerminal("A1"), new NonTerminal(str)));

            // (4.1) A1 →[q0, ¢, a, a]A2;
            str = String.format("[%s,%s,%s,%s]", lba.startState.id, leftMarker, a.name, a.name);
            rules.add(new Rule(new NonTerminal("A1"), new NonTerminal(str), new NonTerminal("A2")));

            // (4.2) A2 →[a, a]A2 ;
            str = String.format("[%s,%s]", a.name, a.name);
            rules.add(new Rule(new NonTerminal("A2"), new NonTerminal(str), new NonTerminal("A2")));

            // (4.3) A2 →[a, a, $];
            str = String.format("[%s,%s,%s]", a.name, a.name, rightMarker);
            rules.add(new Rule(new NonTerminal("A2"), new NonTerminal(str)));

            for(String X : lba.alphabet)
            {
                for(TuringMachine.State q : lba.acceptingStates)
                {
                    // (3.1) [q, ¢, X, a, $] → a;
                    str = String.format("[%s,%s,%s,%s,%s]", q.id, leftMarker, X, a.name, rightMarker);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(a.name)));

                    // (3.2) [¢, q, X, a, $] → a;
                    str = String.format("[%s,%s,%s,%s,%s]", leftMarker, q.id, X, a.name, rightMarker);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(a.name)));

                    // (3.3) [¢, X, a, q, $] → a;
                    str = String.format("[%s,%s,%s,%s,%s]", leftMarker, X, a.name, q.id, rightMarker);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(a.name)));

                    // (8.1) [q, ¢, X, a] → a;
                    str = String.format("[%s,%s,%s,%s]", q.id, leftMarker, X, a.name);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(a.name)));

                    // (8.2) [¢, q, X, a] → a;
                    str = String.format("[%s,%s,%s,%s]", leftMarker, q.id, X, a.name);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(a.name)));

                    // (8.3) [q, X, a] → a;
                    str = String.format("[%s,%s,%s]", q.id, X, a.name);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(a.name)));

                    // (8.4) [q, X, a, $] → a;
                    str = String.format("[%s,%s,%s,%s]", q.id, X, a.name, rightMarker);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(a.name)));

                    // (8.5) [X, a, q, $] → a;
                    str = String.format("[%s,%s,%s,%s]", X, a.name, q.id, rightMarker);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(a.name)));
                }

                for(Terminal b : terminals)
                {
                    // (9.1) a[X, b] → ab;
                    str = String.format("[%s,%s]", X, b.name);
                    rules.add(new Rule(new NonTerminal(a.name), new NonTerminal(str), new NonTerminal(a.name), new NonTerminal(b.name)));

                    // (9.2) a[X, b, $] → ab;
                    str = String.format("[%s,%s,%s]", X, b.name, rightMarker);
                    rules.add(new Rule(new NonTerminal(a.name), new NonTerminal(str), new NonTerminal(a.name), new NonTerminal(b.name)));

                    // (9.3) [X, a]b → ab;
                    str = String.format("[%s,%s]", X, a.name);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(b.name),
                            new NonTerminal(a.name), new NonTerminal(b.name)));

                    // (9.4) [¢, X, a]b → ab;
                    str = String.format("[%s,%s,%s]", leftMarker, X, a.name);
                    rules.add(new Rule(new NonTerminal(str), new NonTerminal(b.name),
                            new NonTerminal(a.name), new NonTerminal(b.name)));
                }
            }
        }

        for(TuringMachine.Transition t : lba.function)
        {
            // q ∈ Q\F
            if (!lba.acceptingStates.contains(t.currentState))
            {
                for(Terminal a : terminals)
                {
                    if (t.moving.equals(TuringMachine.Moving.RIGHT))
                    {
                        // (p, ¢, R)∈δ(q,¢)
                        if (t.currentSymbol.equals(leftMarker)
                                && t.newSymbol.equals(leftMarker))
                        {
                            for(String X : lba.alphabet)
                            {
                                // (2.1) [q, ¢, X, a, $] → [¢, p, X, a, $], если ( p, ¢, R)∈ δ(q, ¢);
                                String str1 = String.format("[%s,%s,%s,%s]",
                                        t.currentState.id, leftMarker, X, a.name, rightMarker);
                                String str2 = String.format("[%s,%s,%s,%s]",
                                        leftMarker, t.newState.id, X, a.name, rightMarker);
                                rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2)));

                                // (5.1) [q, ¢, X, a] → [ ¢, p, X, a], если ( p, ¢, R)∈ δ(q, ¢);
                                str1 = String.format("[%s,%s,%s,%s]",
                                        t.currentState.id, leftMarker, X, a.name);
                                str2 = String.format("[%s,%s,%s,%s]",
                                        leftMarker, t.newState.id, X, a.name);
                                rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2)));
                            }
                        }
                        else
                        {
                            // (2.3) [¢, q, X, a, $] → [¢, Y, a, p, $], если ( p, Y, R)∈ δ(q, X );
                            String str1 = String.format("[%s,%s,%s,%s,%s]",
                                    leftMarker, t.currentState.id, t.currentSymbol, a.name, rightMarker);
                            String str2 = String.format("[%s,%s,%s,%s,%s]",
                                    leftMarker, t.newSymbol, a.name, t.newState.id, rightMarker);
                            rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2)));

                            // (7.1) [q, X, a, $] → [ Y, a, p, $], если ( p, Y, R)∈ δ(q, X );
                            str1 = String.format("[%s,%s,%s,%s]",
                                    t.currentState.id, t.currentSymbol, a.name, rightMarker);
                            str2 = String.format("[%s,%s,%s,%s]",
                                    t.newSymbol, a.name, t.newState.id, rightMarker);
                            rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2)));

                            for(Terminal b : terminals)
                            {
                                for(String Z : lba.alphabet)
                                {
                                    // (5.3) [¢, q, X, a] [Z, b]→ [¢, Y, a] [p, Z, b], если (p, Y, R)∈ δ(q, X );
                                    str1 = String.format("[%s,%s,%s,%s]",
                                            leftMarker, t.currentState.id, t.currentSymbol, a.name);
                                    str2 = String.format("[%s,%s]",
                                            Z, b.name);
                                    String str3 = String.format("[%s,%s,%s]",
                                            leftMarker, t.newSymbol, a.name);
                                    String str4 = String.format("[%s,%s,%s]",
                                            t.newState.id, Z, b.name);
                                    rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2),
                                            new NonTerminal(str3), new NonTerminal(str4)));

                                    // (5.4) [¢,q,X,a] [Z,b,$]→ [¢,Y a] [p,Z,b,$], если (p, Y, R)∈δ(q,X);
                                    str1 = String.format("[%s,%s,%s,%s]",
                                            leftMarker, t.currentState.id, t.currentSymbol, a.name);
                                    str2 = String.format("[%s,%s,%s]",
                                            Z, b.name,rightMarker);
                                    str3 = String.format("[%s,%s,%s]",
                                            leftMarker, t.newSymbol, a.name);
                                    str4 = String.format("[%s,%s,%s,%s]",
                                            t.newState.id, Z, b.name,rightMarker);
                                    rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2),
                                            new NonTerminal(str3), new NonTerminal(str4)));

                                    // (6.1) [q, X, a] [Z, b] → [Y, a][ p, Z, b], если ( p, Y, R)∈ δ(q, X );
                                    str1 = String.format("[%s,%s,%s]",
                                            t.currentState.id, t.currentSymbol, a.name);
                                    str2 = String.format("[%s,%s]",
                                            Z, b.name);
                                    str3 = String.format("[%s,%s]",
                                            t.newSymbol, a.name);
                                    str4 = String.format("[%s,%s,%s]",
                                            t.newState.id, Z, b.name);
                                    rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2),
                                            new NonTerminal(str3), new NonTerminal(str4)));

                                    // (6.3) [q, X, a] [Z, b, $]→ [Y, a] [p, Z, b, $], если (p, Y, R)∈ δ(q, X );
                                    str1 = String.format("[%s,%s,%s]",
                                            t.currentState.id, t.currentSymbol, a.name);
                                    str2 = String.format("[%s,%s,%s]",
                                            Z, b.name, rightMarker);
                                    str3 = String.format("[%s,%s]",
                                            t.newSymbol, a.name);
                                    str4 = String.format("[%s,%s,%s,%s]",
                                            t.newState.id, Z, b.name, rightMarker);
                                    rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2),
                                            new NonTerminal(str3), new NonTerminal(str4)));
                                }
                            }
                        }
                    }
                    else
                    {
                        // (p, $, L)∈δ(q,$)
                        if (t.currentSymbol.equals(rightMarker)
                                && t.newSymbol.equals(rightMarker))
                        {
                            for(String X : lba.alphabet)
                            {
                                // (2.4) [¢, X, a, q, $] → [¢, p, X, a, $], если ( p, $, L)∈ δ(q, $);
                                String str1 = String.format("[%s,%s,%s,%s,%s]",
                                        leftMarker, X, a.name, t.currentState.id, rightMarker);
                                String str2 = String.format("[%s,%s,%s,%s,%s]",
                                        leftMarker, t.newState.id, X, a.name, rightMarker);
                                rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2)));

                                // (7.2) [X, a, q, $] → [p, X, a, $], если ( p, $, L)∈ δ(q, $);
                                str1 = String.format("[%s,%s,%s,%s]",
                                        X, a.name, t.currentState.id, rightMarker);
                                str2 = String.format("[%s,%s,%s,%s]",
                                        t.newState.id, X, a.name, rightMarker);
                                rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2)));
                            }
                        }
                        else
                        {
                            // (2.2) [¢, q, X, a, $] → [p, ¢, Y, a, $], если ( p, Y, L)∈ δ(q, X );
                            String str1 = String.format("[%s,%s,%s,%s,%s]",
                                    leftMarker, t.currentState.id, t.currentSymbol, a.name, rightMarker);
                            String str2 = String.format("[%s,%s,%s,%s,%s]",
                                    t.newState.id, leftMarker, t.newSymbol, a.name, rightMarker);
                            rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2)));

                            // (5.2) [¢, q, X, a] → [p, ¢, Y, a], если ( p, Y, L)∈ δ(q, X );
                            str1 = String.format("[%s,%s,%s,%s]",
                                    leftMarker, t.currentState.id, t.currentSymbol, a.name);
                            str2 = String.format("[%s,%s,%s,%s]",
                                    t.newState.id, leftMarker, t.newSymbol, a.name);
                            rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2)));

                            for(Terminal b : terminals)
                            {
                                for(String Z : lba.alphabet)
                                {
                                    // (6.2) [Z, b] [q, X, a] → [p, Z, b] [Y, a], если ( p, Y, L)∈ δ(q, X );
                                    str1 = String.format("[%s,%s]",
                                            Z, b.name);
                                    str2 = String.format("[%s,%s,%s]",
                                            t.currentState.id, t.currentSymbol, a.name);
                                    String str3 = String.format("[%s,%s,%s]",
                                            t.newState.id, Z, b.name);
                                    String str4 = String.format("[%s,%s]",
                                            t.newSymbol, a.name);
                                    rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2),
                                            new NonTerminal(str3), new NonTerminal(str4)));

                                    // (6.4) [¢, Z, b] [q, X,a] → [¢,p,Z,b] [Y, a], если (p, Y, L)∈δ(q,X)
                                    str1 = String.format("[%s,%s,%s]",
                                            leftMarker, Z, b.name);
                                    str2 = String.format("[%s,%s,%s]",
                                            t.currentState.id, t.currentSymbol, a.name);
                                    str3 = String.format("[%s,%s,%s,%s]",
                                            leftMarker, t.newState.id, Z, b.name);
                                    str4 = String.format("[%s,%s]",
                                            t.newSymbol, a.name);
                                    rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2),
                                            new NonTerminal(str3), new NonTerminal(str4)));

                                    // (7.3) [Z, b] [q, X, a, $] → [p, Z, b] [Y, a, $], если ( p, Y, L)∈ δ(q, X );
                                    str1 = String.format("[%s,%s]",
                                            Z, b.name);
                                    str2 = String.format("[%s,%s,%s,%s]",
                                            t.currentState.id, t.currentSymbol, a.name, rightMarker);
                                    str3 = String.format("[%s,%s,%s]",
                                            t.newState.id, Z, b.name);
                                    str4 = String.format("[%s,%s,%s]",
                                            t.newSymbol, a.name, rightMarker);
                                    rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2),
                                            new NonTerminal(str3), new NonTerminal(str4)));

                                    // (7.4) [¢, Z, b] [q, X,a,$] → [¢,p,Z,b] [Y, a,$], если (p, Y, L)∈δ(q,X);
                                    str1 = String.format("[%s,%s,%s]",
                                            leftMarker, Z, b.name);
                                    str2 = String.format("[%s,%s,%s,%s]",
                                            t.currentState.id, t.currentSymbol, a.name, rightMarker);
                                    str3 = String.format("[%s,%s,%s,%s]",
                                            leftMarker, t.newState.id, Z, b.name);
                                    str4 = String.format("[%s,%s,%s]",
                                            t.newSymbol, a.name, rightMarker);
                                    rules.add(new Rule(new NonTerminal(str1), new NonTerminal(str2),
                                            new NonTerminal(str3), new NonTerminal(str4)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private LinkedHashSet<GrammarElement> getGeneratingElements() {
        LinkedHashSet<GrammarElement> generatingElements = new LinkedHashSet<>();

        for(Rule rule : rules) {
            boolean inRightPartOnlyTerminals = true;
            for(GrammarElement value : rule.right) {
                if (!terminals.contains(value)) {
                    inRightPartOnlyTerminals = false;
                    break;
                }
            }

            if (inRightPartOnlyTerminals) {
                for(GrammarElement value : rule.left) {
                    if (!generatingElements.contains(value)) {
                        generatingElements.add(value);
                    }
                }
            }
        }

        for(GrammarElement value : terminals) {
            if (!generatingElements.contains(value)) {
                generatingElements.add(value);
            }
        }

        boolean find = true;

        while (find) {
            find = false;
            for(Rule rule : rules) {
                boolean inRightPartOnlyGenerating = true;
                for(GrammarElement value : rule.right) {
                    if (!generatingElements.contains(value)) {
                        inRightPartOnlyGenerating = false;
                        break;
                    }
                }

                if (inRightPartOnlyGenerating) {
                    for(GrammarElement value : rule.left) {
                        if (!generatingElements.contains(value)) {
                            generatingElements.add(value);
                            find = true;
                        }
                    }
                }
            }
        }
        return generatingElements;
    }

    private LinkedHashSet<GrammarElement> getReachableNonTerminals() {
        LinkedHashSet<GrammarElement> reachableNonTerminals = new LinkedHashSet<>();
        reachableNonTerminals.add(start);
        boolean find = true;

        while (find) {
            find = false;
            for(Rule rule : rules) {
                boolean inLeftPartOnlyReachable = true;
                for(GrammarElement value : rule.left) {
                    if (!reachableNonTerminals.contains(value)) {
                        inLeftPartOnlyReachable = false;
                        break;
                    }
                }

                if (inLeftPartOnlyReachable) {
                    for(GrammarElement value : rule.right) {
                        if (!reachableNonTerminals.contains(value)) {
                            reachableNonTerminals.add(value);
                            find = true;
                        }
                    }
                }
            }
        }
        return reachableNonTerminals;
    }
}

