import java.util.ArrayList;

public class StandardizeVariables{
    ArrayList<String> variables = new ArrayList<>();
    int sentenceCounter = 1;

    StandardizeVariables(ArrayList<Sentence> sentenceArrayList){
        for(Sentence s: sentenceArrayList){
            for(Predicate predicate: s.predicates){
                for(int k=0; k< predicate.argumentList.size();k++){
                    String var = predicate.argumentList.get(k);
                    if(Character.isLowerCase(var.charAt(0))){
                        String newVar = var.charAt(0) + Integer.toString(sentenceCounter);
                        predicate.argumentList.set(k,newVar);
                        variables.add(newVar);
                    }
                }
            }
            ++sentenceCounter;
        }
    }
}