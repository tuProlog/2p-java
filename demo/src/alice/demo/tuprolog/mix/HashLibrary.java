import alice.tuprolog.*;
import java.util.*;

public class HashLibrary extends Library {

  private HashMap dicts=new HashMap();
  private int counter = 0;
  
  public boolean new_dict_1(Struct goal){
    Term idDict=goal.getTerm(0);
    HashMap map=new HashMap();
    String dict_name="dict"+counter++;
    dicts.put(dict_name,map);
    return unify(idDict,new Struct(dict_name));
  }

  public boolean dict_put_3(Struct goal){
    String dictRef=goal.getTerm(0).toString();
    HashMap dict=(HashMap)dicts.get(dictRef);
    String key=goal.getTerm(1).toString();
    dict.put(key,goal.getTerm(2));
    return true;
  }

  public boolean dict_get_3(Struct goal){
    String dictRef=goal.getTerm(0).toString();
    HashMap dict=(HashMap)dicts.get(dictRef);
    String key=goal.getTerm(1).toString();
    Term result=(Term)dict.get(key);
    return unify(goal.getTerm(2),result);
  }

  public boolean dict_remove_2(Struct goal){
    String dictRef=goal.getTerm(0).toString();
    HashMap dict=(HashMap)dicts.get(dictRef);
    String key=goal.getTerm(1).toString();
    dict.remove(key);
    return true;
  }
}






