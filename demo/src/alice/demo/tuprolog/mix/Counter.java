
public class Counter {
  private int value;
  public  String name;

  public Counter(){}
  public Counter(String n){ name=n; }

  public void setValue(int val){ value=val; }
  public int  getValue() { return value; }
  public void inc()      { value++; }
  public void setName(String s) {
      name = s;
  }

  static public String getVersion() { return "1.0"; }
}


