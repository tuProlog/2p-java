


test_swing(FileName):-
    java_object('javax.swing.JFileChooser',[],Dialog),
    Dialog <- showOpenDialog(_),
    Dialog <- getSelectedFile returns File,
    File <- getName returns FileName.



test_rmi(GoalText,Ok):-
    java_object('java.rmi.RMISecurityManager',[],Manager),
    class('java.lang.System') <- setSecurityManager(Manager as 'java.lang.SecurityManager'),
    class('java.rmi.Naming') <- lookup('prolog') returns Engine,
    Engine <- solve(GoalText) returns SolInfo,
    SolInfo <- isSuccess returns Ok,
    SolInfo <- getSubstitution returns Sub,
    stdout <- println(Sub).