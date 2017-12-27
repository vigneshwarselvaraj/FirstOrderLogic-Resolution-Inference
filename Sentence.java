import java.util.ArrayList;

public class Sentence
{
    ArrayList<Predicate> predicates = new ArrayList<>();

    Sentence(){

    }

    Sentence(ArrayList<Predicate> predicates)
    {
        this.predicates = predicates;

    }

    Sentence(Sentence s){
        for(Predicate p:s.predicates){
            Predicate p2 = new Predicate(p);
            this.predicates.add(p2);
        }
    }

    void addPredicate( Predicate p)
    {
        predicates.add(p);
    }

    boolean equals(Sentence s2){
        if(this.predicates.size() == s2.predicates.size()){
            int size = this.predicates.size();
            int matched = 0;
            for(Predicate p1:this.predicates){
                for(Predicate p2:s2.predicates){
                    if(p1.equals(p2)){
                        matched++;
                        break;
                    }
                }
            }
            if(matched == size)
                return true;
        }
        return false;
    }

    void removeDuplicatePredicates() {
        int position = checkForDuplicates();
        while(position != -1){
            this.predicates.remove(position);
            position = checkForDuplicates();
        }
    }

    int checkForDuplicates(){
        for(int i = 0; i < this.predicates.size() - 1; i++) {
            for (int j = i + 1; j < this.predicates.size(); j++) {
                if (this.predicates.get(i).equalsAll(this.predicates.get(j))) {
                    return j;
                }
            }
        }
        return -1;
    }
}