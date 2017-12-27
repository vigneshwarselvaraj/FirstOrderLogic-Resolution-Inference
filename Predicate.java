import java.util.ArrayList;

public class Predicate {
        String predicateName;
        ArrayList<String> argumentList = new ArrayList<>();
        boolean isNegated;

        Predicate(){

        }

        Predicate(String predicateName, ArrayList<String> argumentList, boolean isNegated)
        {
            this.predicateName = predicateName;
            this.isNegated = isNegated;
            this.argumentList = argumentList;
        }

        Predicate(Predicate p)
        {
            this.predicateName = p.predicateName;
            this.isNegated = p.isNegated;
            for(String s : p.argumentList)
            {
                this.argumentList.add(new String(s));
            }
        }

        boolean equals(Predicate p2){
            if(this.predicateName.equals(p2.predicateName) && this.argumentList.size() == p2.argumentList.size() && this.isNegated == p2.isNegated){
                int size = this.argumentList.size();
                for(int i=0; i<size; i++){
                        if(!(this.argumentList.get(i).equals(p2.argumentList.get(i))))
                            return false;
                }
                return true;
            }
            else
                return false;
        }

    boolean equalsAll(Predicate p2){
        if(this.predicateName.equals(p2.predicateName) && this.argumentList.size() == p2.argumentList.size() && this.isNegated == p2.isNegated){
            int size = this.argumentList.size();
            for(int i=0; i<size; i++){
                if(!(this.argumentList.get(i).equals(p2.argumentList.get(i)))){
                    return false;
                }
            }
            return true;
        }
        else
            return false;
    }
}
