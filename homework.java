import java.io.*;
import java.util.ArrayList;

public class homework {
    long timeLimit = 150;
    long startTime;

    public static void main(String[] args) throws IOException {
        String fileName = "input.txt";
        String line;
        File f = new File("output.txt");
        FileOutputStream fileOutputStream;
        fileOutputStream = new FileOutputStream(f);
        PrintWriter printWriter = new PrintWriter(fileOutputStream);
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        line = bufferedReader.readLine().trim();
        int numQueries = Integer.parseInt(line);
        ArrayList<Sentence> queries = new ArrayList<>();

        homework hw = new homework();

        for (int i = 0; i < numQueries; i++) {
            line = bufferedReader.readLine().trim();
            line = line.replaceAll("\\s+", "");
            Sentence s = hw.convertToSentences(line);
            queries.add(s);
        }

        line = bufferedReader.readLine().trim();
        int numOfSentences = Integer.parseInt(line);

        ArrayList<Sentence> KBSentences = new ArrayList<>();

        for (int i = 0; i < numOfSentences; i++) {
            line = bufferedReader.readLine().trim();
            line = line.replaceAll("\\s+", "");
            Sentence s = hw.convertToSentences(line);
            KBSentences.add(s);
        }


        bufferedReader.close();

        try{
            for (Sentence s : queries) {
                Sentence negatedQ = negateQuery(s);
                KnowledgeBase KB = new KnowledgeBase(KBSentences);
                boolean result = hw.fol_resolution(KB, negatedQ);
                if (result){
                    printWriter.write("TRUE");
                    System.out.println("TRUE");
                }

                else{
                    printWriter.write("FALSE");
                    System.out.println("FALSE");
                }

                printWriter.write("\n");
            }
            printWriter.flush();
            fileOutputStream.close();
            printWriter.close();
        }
        catch(Exception E){
            System.out.println("Exception found " + E.getMessage());
            printWriter.write("FALSE");
            printWriter.flush();
            fileOutputStream.close();
            printWriter.close();
        }

    }

    private static Sentence negateQuery(Sentence s) {
        boolean negatedValue = s.predicates.get(0).isNegated;
        s.predicates.get(0).isNegated = !negatedValue;
        return s;
    }

    boolean fol_resolution(KnowledgeBase kb, Sentence query) {
        startTime = System.currentTimeMillis();
        StandardizeVariables standardizeVariables = new StandardizeVariables(kb.sentences);
        ArrayList<Sentence> clauses = new ArrayList<>(kb.sentences);
        clauses.add(query);

        ArrayList<Sentence> newSentences = new ArrayList<>();
        while (((System.currentTimeMillis() - startTime) / 1000) < timeLimit) {
            for (int i = 0; i < clauses.size() - 1 && (((System.currentTimeMillis() - startTime) / 1000) < timeLimit); i++) {
                for (int j = i + 1; j < clauses.size() && (((System.currentTimeMillis() - startTime) / 1000) < timeLimit); j++) {
                    Sentence resolvent = resolve_sentences(clauses.get(i), clauses.get(j));
                    if (resolvent.predicates.size() == 0)
                        return true;
                    //if(resolvent.predicates.size() < (Math.max(clauses.get(i).predicates.size(), clauses.get(j).predicates.size()))){
                    if (resolvent.predicates.size() < (clauses.get(i).predicates.size() + clauses.get(j).predicates.size())) {
                        resolvent.removeDuplicatePredicates();
                        newSentences.add(resolvent);
                    }
                }
            }

            ArrayList<Sentence> newSentencesIdentified = findNewSentences(clauses, newSentences);

            if (((System.currentTimeMillis() - startTime) / 1000) >= timeLimit)
                return false;

            if (newSentencesIdentified.size() == 0) {
                return false;
            }
            clauses.addAll(newSentencesIdentified);
        }
        return false;
    }

    ArrayList<Sentence> findNewSentences(ArrayList<Sentence> clauses, ArrayList<Sentence> newSentences) {
        ArrayList<Sentence> newSentencesIdentified = new ArrayList<>();

        for (int i = 0; i < newSentences.size() && (((System.currentTimeMillis() - startTime) / 1000) < timeLimit); i++) {
            Sentence s = newSentences.get(i);
            boolean newSentence = true;

            for (Sentence clause : clauses) {
                if (s.equals(clause)) {
                    newSentence = false;
                }
            }

            for (Sentence newIdentified : newSentencesIdentified) {
                if (s.equals(newIdentified)) {
                    newSentence = false;
                }
            }

            if (newSentence) {
                newSentencesIdentified.add(s);
            }
        }
        return newSentencesIdentified;
    }

    Sentence resolve_sentences(Sentence firstSentence, Sentence secondSentence) {
        Sentence copiedFirstSentence = new Sentence(firstSentence);
        Sentence copiedSecondSentence = new Sentence(secondSentence);
        ArrayList<Predicate> predFirstSent = new ArrayList<>(copiedFirstSentence.predicates);
        ArrayList<Predicate> predSecondSent = new ArrayList<>(copiedSecondSentence.predicates);

        boolean isEqual = false;

        for (int i = 0; i < predFirstSent.size(); i++)
            for (int j = 0; j < predSecondSent.size(); j++) {
                if (predFirstSent.get(i).predicateName.equals(predSecondSent.get(j).predicateName)
                        && ((predFirstSent.get(i).isNegated != predSecondSent.get(j).isNegated))) {
                    isEqual = true;

                    if (predFirstSent.get(i).argumentList.size() == predSecondSent.get(j).argumentList.size()) {
                        for (int l = 0; l < predFirstSent.get(i).argumentList.size(); l++) {
                            String argument1 = predFirstSent.get(i).argumentList.get(l);
                            String argument2 = predSecondSent.get(j).argumentList.get(l);

                            String unified = unify(argument1, argument2);

                            if (unified != null) {
                                if (!argument1.equals(unified)) {
                                    for (Predicate p : predFirstSent) {
                                        for (int m = 0; m < p.argumentList.size(); m++) {
                                            if (argument1.equals(p.argumentList.get(m)))
                                                p.argumentList.set(m, unified);
                                        }
                                    }
                                    for (Predicate p : predSecondSent) {
                                        for (int m = 0; m < p.argumentList.size(); m++) {
                                            if (argument1.equals(p.argumentList.get(m)))
                                                p.argumentList.set(m, unified);
                                        }
                                    }
                                    argument1 = unified;
                                    argument2 = unified;
                                }

                                if (!argument2.equals(unified)) {
                                    for (Predicate p : predSecondSent) {
                                        for (int m = 0; m < p.argumentList.size(); m++) {
                                            if (argument2.equals(p.argumentList.get(m)))
                                                p.argumentList.set(m, unified);
                                        }
                                    }
                                    for (Predicate p : predFirstSent) {
                                        for (int m = 0; m < p.argumentList.size(); m++) {
                                            if (argument2.equals(p.argumentList.get(m)))
                                                p.argumentList.set(m, unified);
                                        }
                                    }
                                    argument1 = unified;
                                    argument2 = unified;
                                }
                            }

                            if (!argument1.equals(argument2))
                                isEqual = false;
                        }
                    }
                }
                if (isEqual && (predFirstSent.get(i).isNegated != predSecondSent.get(j).isNegated))
                    return resolve(predFirstSent, predSecondSent, i, j);
                isEqual = false;
            }
        return resolve(firstSentence.predicates, secondSentence.predicates, -1, -1);
    }

    String unify(String str1, String str2) {
        if (isVar(str1)) {
            if (isVar(str2))
                return str1;
            else
                return str2;
        } else {
            if (isVar(str2)) {
                return str1;
            } else if (str1.equals(str2))
                return str1;
            else
                return null;
        }
    }

    boolean isVar(String s) {
        return Character.isLowerCase(s.charAt(0));
    }

    Sentence resolve(ArrayList<Predicate> firstSentence, ArrayList<Predicate> secondSentence, int firstIndex, int secondIndex) {
        Sentence resolvedSentence = new Sentence();

        for (int i = 0; i < firstSentence.size(); i++) {
            if (i != firstIndex)
                resolvedSentence.addPredicate(firstSentence.get(i));
        }
        for (int j = 0; j < secondSentence.size(); j++) {
            if (j != secondIndex)
                resolvedSentence.addPredicate(secondSentence.get(j));
        }
        return resolvedSentence;
    }


    private Sentence convertToSentences(String str) {
        Sentence s;
        Predicate p = new Predicate();
        ArrayList<Predicate> predicateArrayList = new ArrayList<Predicate>();
        char charArray[] = str.toCharArray();
        int n = charArray.length;

        for (int i = 0; i < n; i++) {
            ArrayList<String> argumentsList = new ArrayList<>();
            if (charArray[i] == '~') {
                p.isNegated = true;
                i++;
            }

            if (i - 1 >= 0 && charArray[i] == '~' && charArray[i - 1] == '~') {
                p.isNegated = false;
                i++;
            }

            if (charArray[i] >= 'A' && charArray[i] <= 'Z') {
                StringBuilder string = new StringBuilder();
                while (i < n && charArray[i] != '(') {
                    string.append(charArray[i]);
                    i++;
                }
                p.predicateName = string.toString();
            }

            if (charArray[i] == '(') {
                i++;
                while (charArray[i] != ')') {
                    while (charArray[i] >= 'a' && charArray[i] <= 'z') {
                        String var;
                        StringBuilder name = new StringBuilder();
                        while (i < n && charArray[i] != ',' && charArray[i] != ')') {
                            name.append(charArray[i]);
                            i++;
                        }
                        var = name.toString();
                        argumentsList.add(var);
                    }

                    if (charArray[i] == ',') {
                        i++;
                        continue;
                    }

                    if (charArray[i] == ')') {
                        predicateArrayList.add(new Predicate(p.predicateName, argumentsList, p.isNegated));
                        p = new Predicate();
                        continue;
                    }

                    while (charArray[i] >= 'A' && charArray[i] <= 'Z') {
                        StringBuilder constant = new StringBuilder();
                        while (i < n && charArray[i] != ',' && charArray[i] != ')') {
                            constant.append(charArray[i]);
                            i++;
                        }
                        argumentsList.add(constant.toString());
                    }

                    if (charArray[i] == ',') {
                        i++;
                        continue;
                    }

                    if (charArray[i] == ')') {
                        predicateArrayList.add(new Predicate(p.predicateName, argumentsList, p.isNegated));
                        p = new Predicate();
                        continue;

                    }
                }
            }

            if (i + 1 < n && charArray[i + 1] == '|') {
                i++;
                continue;
            }

            if (i + 1 < n && charArray[i + 1] == ')') {
                i++;
                continue;
            }
        }
        s = new Sentence(predicateArrayList);
        return s;
    }
}