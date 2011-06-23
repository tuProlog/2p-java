package alice.tuprologx.ide;

public interface Console
{
    public boolean hasOpenAlternatives();
    public void enableTheoryCommands(boolean flag);
    public void getNextSolution();
    public void acceptSolution();
    public void stopEngine();
    public String getGoal();
}