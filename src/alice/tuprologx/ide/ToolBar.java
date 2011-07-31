package alice.tuprologx.ide;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.net.URL;

import alice.tuprolog.Prolog;


public class ToolBar extends JPanel
{
    
    private static final long serialVersionUID = 1L;

    /**
	 * The Prolog engine referenced by the toolbar.
	 * @uml.property  name="engine"
	 * @uml.associationEnd  
	 */
    private Prolog engine;

    /**
	 * @uml.property  name="libraryManager"
	 * @uml.associationEnd  
	 */
    private LibraryManager libraryManager;
    /**
	 * The Library dialog launched by the toolbar.
	 * @uml.property  name="libraryDialog"
	 * @uml.associationEnd  
	 */
    private LibraryDialogFrame libraryDialog;
    /**
	 * The debug area launched by the toolbar.
	 * @uml.property  name="debugArea"
	 * @uml.associationEnd  
	 */
    private DebugAreaFrame debugArea;
    /**
	 * The tuProlog config dialog launched by the toolbar.
	 * @uml.property  name="configFrame"
	 * @uml.associationEnd  
	 */
    private PrologConfigFrame configFrame;
    /**
	 * The About window launched by the toolbar.
	 * @uml.property  name="about"
	 * @uml.associationEnd  
	 */
    private AboutFrame about;

    /**
	 * The file manager launched by the toolbar.
	 * @uml.property  name="fileManager"
	 * @uml.associationEnd  
	 */
    private IOFileOperations fileManager;
    /**
	 * A message describing the status of the console.
	 * @uml.property  name="statusMessage"
	 */
    private String statusMessage;
    /**
	 * Used for components interested in changes of console's properties.
	 * @uml.property  name="propertyChangeSupport"
	 */
    private PropertyChangeSupport propertyChangeSupport;
    /**
	 * The IDE the toolbar belongs to, necessary to manage editor-related commands such as saving its content to the filesystem.
	 * @uml.property  name="ide"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private IDE ide;
    
    /**
	 * @uml.property  name="parent"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JFrame parent;
    
    /**
	 * @uml.property  name="bNew"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JButton bNew;
    /**
	 * @uml.property  name="bOpen"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JButton bOpen;
    /**
	 * @uml.property  name="bSave"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JButton bSave;
    /**
	 * @uml.property  name="bSaveAs"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JButton bSaveAs;
    /**
	 * @uml.property  name="bLibMan"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JButton bLibMan;
    /**
	 * @uml.property  name="bDebug"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JButton bDebug;
    /**
	 * @uml.property  name="bConfigure"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JButton bConfigure;
    /**
	 * @uml.property  name="bAbout"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JButton bAbout;

    /**
	 * @uml.property  name="fileIDE"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    protected FileIDE fileIDE;

    
    public ToolBar(IDE ide, JFrame parent)
    {
        this.parent = parent;
        this.ide = ide;
        propertyChangeSupport = new PropertyChangeSupport(this);
        initComponents();
        fileIDE=new FileIDE("",null);
    }

    private void initComponents()
    {
        bNew=new JButton();
        URL urlImage = getClass().getResource("img/New24.png");
        bNew.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(urlImage)));
        bNew.setToolTipText("New Theory");
        bNew.setPreferredSize(new Dimension(32,32));
        bNew.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                newTheory();
            }
        });
        bOpen=new JButton();
        urlImage = getClass().getResource("img/Open24.png");
        bOpen.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(urlImage)));
        bOpen.setToolTipText("Load Theory");
        bOpen.setPreferredSize(new Dimension(32,32));
        bOpen.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                loadTheory();
            }
        });
        bSave=new JButton();
        urlImage = getClass().getResource("img/Save24.png");
        bSave.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(urlImage)));
        bSave.setToolTipText("Save Theory");
        bSave.setPreferredSize(new Dimension(32,32));
        bSave.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                saveTheory();
            }
        });
        bSaveAs=new JButton();
        urlImage = getClass().getResource("img/SaveAs24.png");
        bSaveAs.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(urlImage)));
        bSaveAs.setToolTipText("Save Theory As...");
        bSaveAs.setPreferredSize(new Dimension(32,32));
        bSaveAs.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                saveTheoryAs();
            }
        });
        bLibMan=new JButton();
        urlImage = getClass().getResource("img/Library24.png");
        bLibMan.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(urlImage)));
        bLibMan.setToolTipText("Open Library Manager");
        bLibMan.setPreferredSize(new Dimension(32,32));
        bLibMan.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                openLibraryManager();
            }
        });
        bDebug=new JButton();
        urlImage = getClass().getResource("img/Debugger24.png");
        bDebug.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(urlImage)));
        bDebug.setToolTipText("Wiev Debug Information");
        bDebug.setPreferredSize(new Dimension(32,32));
        bDebug.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                viewDebugInformation();
            }
        });
        bConfigure=new JButton();
        urlImage = getClass().getResource("img/Configure24.png");
        bConfigure.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(urlImage)));
        bConfigure.setToolTipText("Configure tuProlog");
        bConfigure.setPreferredSize(new Dimension(32,32));
        bConfigure.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                configure();
            }
        });
        bAbout=new JButton();
        urlImage = getClass().getResource("img/About24.png");
        bAbout.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(urlImage)));
        bAbout.setToolTipText("About...");
        bAbout.setPreferredSize(new Dimension(32,32));
        bAbout.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                viewAboutInformation();
            }
        });

        


        JPanel bottonsPanel = new JPanel();
        bottonsPanel.add(bNew);
        bottonsPanel.add(bOpen);
        bottonsPanel.add(bSave);
        bottonsPanel.add(bSaveAs);
        bottonsPanel.add(bLibMan);
        bottonsPanel.add(bDebug);
        bottonsPanel.add(bConfigure);
        bottonsPanel.add(bAbout);

        
        setLayout(new BorderLayout());
        add(bottonsPanel,BorderLayout.WEST);
        add(new JSeparator(),BorderLayout.SOUTH);
    }

    /**
	 * Get the Prolog engine referenced by the toolbar.
	 * @return  The Prolog engine referenced by the toolbar.
	 * @uml.property  name="engine"
	 */
    public Prolog getEngine() {
        return engine;
    }

    /**
	 * Set the Prolog engine referenced by the toolbar.
	 * @param engine  an <code>alice.tuprolog.Prolog</code> engine.
	 * @uml.property  name="engine"
	 */
    public void setEngine(Prolog engine) {
        this.engine = engine;
        debugArea = new DebugAreaFrame();
        libraryDialog = new LibraryDialogFrame(libraryManager,parent);
        libraryDialog.setFileManager(new JavaIOManager(libraryDialog));
        libraryDialog.setFileManagerType("preferences");
        this.engine.addSpyListener(debugArea);
        this.engine.addWarningListener(debugArea);
        this.engine.addLibraryListener(libraryDialog);
        //this.engine.addPropertyChangeListener(debugArea);
    }

    /**
	 * Get the library manager 
	 * @return  the current library manager of the toolbar
	 * @uml.property  name="libraryManager"
	 */
    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    /**
	 * Set the library manager 
	 * @param libraryManager  a libraryManager to associate to the libraryDialog
	 * @uml.property  name="libraryManager"
	 */
    public void setLibraryManager(LibraryManager libraryManager)
    {
        this.libraryManager = libraryManager;
    }

    /**
     * Set the tuProlog config frame
     *
     * @param libraryManager a libraryManager to associate to the libraryDialog 
     */
    public void setPrologConfig(PrologConfigFrame configFrame)
    {
        this.configFrame = configFrame;
    }

    /**
	 * Get the toolbar status as a <code>java.lang.String</code> message.
	 * @return  the current status of the toolbar as a <code>java.lang.String</code>  message.
	 * @uml.property  name="statusMessage"
	 */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
	 * Set the toolbar status.
	 * @param message  The message describing the new status of the toolbar.
	 * @uml.property  name="statusMessage"
	 */
    public void setStatusMessage(String message) {
        String oldStatusMessage = getStatusMessage();
        statusMessage = message;
        propertyChangeSupport.firePropertyChange("StatusMessage", oldStatusMessage, statusMessage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
	 * Set the file manager referenced by the toolbar for use in Input/Output tasks.
	 * @param fileManager  The file manager we want the toolbar to use.
	 * @uml.property  name="fileManager"
	 */
    public void setFileManager(IOFileOperations fileManager) {
        this.fileManager = fileManager;
    }

    public void setFileManagerType(String type)
    {
        this.fileManager.setTypeFileFilter(type);
    }

    /**
	 * Get the file manager used by the toolbar.
	 * @return  The file manager used by the toolbar.
	 * @uml.property  name="fileManager"
	 */
    public IOFileOperations getFileManager() {
        return fileManager;
    }

    /**
     * Enable or disable theory-related buttons.
     *
     * @param flag true if the buttons have to be enabled, false otherwise.
     */
    protected void enableTheoryCommands(boolean flag)
    {
        bNew.setEnabled(flag);
        bOpen.setEnabled(flag);
        bSave.setEnabled(flag);
        bSaveAs.setEnabled(flag);
        bLibMan.setEnabled(flag);
    }
  
    /**
     * Reset the engine's theory to a new blank theory.
     */
    public void newTheory() {
        engine.clearTheory();
        ide.newTheory();
        setStatusMessage("Ready (new theory accepted).");
//        theoryIDE=new TheoryIDE(null,null);
    }

    /**
     * Load the engine with a theory previously stored in a file.
     */
    public void loadTheory() {
        setStatusMessage("Load Theory...");
        try {
            fileIDE = fileManager.loadFile();
            if (fileIDE.getFilePath() != null) {
                
                /** this isn't correct with the tabbed theory visualization*/
                //engine.setTheory(theory);

                //ide.setEditorContent(theoryIDE.getTheory().toString());
                ide.loadTheory();
            }
            setStatusMessage("Ready.");
        } catch (Exception e) {
            setStatusMessage("Error consulting theory.");
        }
    }

    /**
     * Save the engine's current theory to a default file.
     */
    public void saveTheory() {
        FileIDE oldValue = fileIDE;
        try {
            fileIDE.setContent(ide.getEditorContent());
            fileIDE=fileManager.saveFile(fileIDE);
            if (fileIDE.getFilePath() != null)
            {
                setStatusMessage("Theory saved to " + fileIDE.getFileName() + ".");
                ide.saveTheory();
            }
            else
                setStatusMessage("Ready.");
        } catch (Exception e){
            setStatusMessage("Error saving theory.");
            fileIDE = oldValue;
        }
    }

    /**
     * Save the engine's current theory to a file chosen by the user.
     */
    public void saveTheoryAs() {
        setStatusMessage("Save Theory As...");
        FileIDE oldValue = fileIDE;
        try {
            fileIDE.setContent(ide.getEditorContent());
            fileIDE = fileManager.saveFileAs(fileIDE);
            if (fileIDE.getFilePath() != null)
            {
                setStatusMessage("Theory saved to " + fileIDE.getFileName() + ".");
                ide.saveTheory();
            }
            else
                setStatusMessage("Ready.");
        } catch (Exception e) {
            setStatusMessage("Error saving theory.");
            fileIDE = oldValue;
        }
    }

    /**
     * Opens the Library Manager dialog.
     */
    public void openLibraryManager() {
        parent.setEnabled(false);
        libraryDialog.setVisible(true);
        libraryDialog.setStatusMessage("Ready.");
    }

    /**
     * Show an instance of the debug area referenced by the toolbar in order to
     * display debug informations.
     */
    public void viewDebugInformation() {
        debugArea.setVisible(true);
    }

    public void configure()
    {
        parent.setEnabled(false);
        configFrame.setVisible(true);
        configFrame.reload();
    }

    /**
     * Display an About dialog with information on the system and its crafters.
     */
    public void viewAboutInformation() {
        about = new AboutFrame(parent);
        parent.setEnabled(false);
        about.pack();
        about.setVisible(true);
    }


    /**
	 * @param fileIDE
	 * @uml.property  name="fileIDE"
	 */
    public void setFileIDE(FileIDE fileIDE)
    {
        this.fileIDE = fileIDE;
    }
    /**
	 * @return
	 * @uml.property  name="fileIDE"
	 */
    public FileIDE getFileIDE()
    {
        return fileIDE;
    }
}
