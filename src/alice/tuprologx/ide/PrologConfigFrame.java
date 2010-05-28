package alice.tuprologx.ide;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.*;

public class PrologConfigFrame
	extends GenericFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int millsStopEngine;
	private int selectDisplayModality;
	private PropertyChangeSupport propertyChangeSupport;
	private JRadioButton inColumnsRadioButton;
	private JRadioButton inRowsRadioButton;
	private JRadioButton inRowsVariableSeparatedRadioButton;
	private JTextField millsStopEngineTextField;
	private JTextField fontDimensionTextField;

	private FontDimensionHandler fontDimensionHandler;

	public PrologConfigFrame(JFrame mainWindow,FontDimensionHandler fontDimensionHandler)
	{
		super("Configure Console", mainWindow, 275, 135, true, true);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.fontDimensionHandler = fontDimensionHandler;
		initComponents();
	}


	private void initComponents()
	{
		Container c=this.getContentPane();
		setLayout(new BorderLayout());
		JPanel fontDimensionPanel = new JPanel();
		JPanel optionPanel = new JPanel();
		JPanel okCancelPanel = new JPanel();
		c.add(fontDimensionPanel ,BorderLayout.NORTH);
		c.add(optionPanel,BorderLayout.CENTER);
		c.add(okCancelPanel,BorderLayout.SOUTH);

		JLabel fontDimensionLabel = new JLabel(" Font dimension");
		JButton bDec = new JButton("<");
		bDec.setToolTipText("Reduce Font Dimension");
		bDec.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	decFontDimension();
            }
        });

		JButton bInc = new JButton(">");
		bInc.setToolTipText("Enlarge Font Dimension");
		bInc.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	incFontDimension();
            }
        });

		fontDimensionTextField = new JTextField();
		fontDimensionTextField.setText(""+fontDimensionHandler.getFontDimension());
		fontDimensionTextField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	setFontDimension();
            }
        });
		
		fontDimensionPanel.setLayout(new BorderLayout());
		fontDimensionPanel.add(fontDimensionLabel,BorderLayout.WEST);
		JPanel fontDimensionHandlingPanel = new JPanel();
		fontDimensionPanel.add(fontDimensionHandlingPanel,BorderLayout.EAST);
		fontDimensionHandlingPanel.add(bDec);
		fontDimensionHandlingPanel.add(fontDimensionTextField);
		fontDimensionHandlingPanel.add(bInc);
		
		JLabel selectDisplayModalityLabel = new JLabel(" Select the display modality for query Solve solutions:");
		inColumnsRadioButton = new JRadioButton("Variables in columns");
		inRowsRadioButton = new JRadioButton("Variables in rows");
		inRowsVariableSeparatedRadioButton = new JRadioButton("Variables in rows with separated variable");
		ButtonGroup group = new ButtonGroup();
		group.add(inColumnsRadioButton);
		group.add(inRowsRadioButton);
		group.add(inRowsVariableSeparatedRadioButton);
		JLabel millsStopEngineLabel1 = new JLabel(" tuProlog will stop solving after  ");
		millsStopEngineTextField = new JTextField(""+millsStopEngine);
		millsStopEngineTextField.setPreferredSize(new Dimension(50,20));
		millsStopEngineTextField.setHorizontalAlignment(JTextField.RIGHT);
		JLabel millsStopEngineLabel2 = new JLabel(" ms ");		

		optionPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		optionPanel.add(selectDisplayModalityLabel,constraints);
		constraints.gridy++;
		optionPanel.add(inColumnsRadioButton,constraints);
		constraints.gridy++;
		optionPanel.add(inRowsRadioButton,constraints);
		constraints.gridy++;
		optionPanel.add(inRowsVariableSeparatedRadioButton,constraints);
		constraints.gridy++;
		constraints.gridy++;
		optionPanel.add(millsStopEngineLabel1,constraints);
		constraints.gridx++;
		optionPanel.add(millsStopEngineTextField,constraints);
		constraints.gridx++;
		optionPanel.add(millsStopEngineLabel2,constraints);
/*		constraints.gridy = 4;
		constraints.gridx = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
		optionPanel.add(new JSeparator(),constraints);
		constraints.gridy = 7;
		optionPanel.add(new JSeparator(),constraints);*/

		JButton ok = new JButton("OK");
		ok.setPreferredSize(new Dimension(80,32));
		ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	ok();
            }
        });
		
		JButton cancel = new JButton("Cancel");
		cancel.setPreferredSize(new Dimension(80,32));
		cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
            	cancel();
            }
        });

		okCancelPanel.add(ok);
		okCancelPanel.add(cancel);
		
		reload();
		pack();
	}

	public void reload()
	{
		if (selectDisplayModality==0)
			inColumnsRadioButton.setSelected(true);
		if (selectDisplayModality==1)
			inRowsRadioButton.setSelected(true);
		if (selectDisplayModality==2)
			inRowsVariableSeparatedRadioButton.setSelected(true);
		millsStopEngineTextField.setText(""+millsStopEngine);
	}

	public void ok()
	{
		setFontDimension();
		
		if (inColumnsRadioButton.isSelected())
			setSelectDisplayModality(0);
		if (inRowsRadioButton.isSelected())
			setSelectDisplayModality(1);
		if (inRowsVariableSeparatedRadioButton.isSelected())
			setSelectDisplayModality(2);

		boolean close=true;
		try
		{
			setMillsStopEngine(Integer.parseInt(millsStopEngineTextField.getText()));
		}
		catch (NumberFormatException e)
		{
			close=false;
		}
		if (close)
			onClose();
	}
	public void cancel()
	{
		onClose();
	}

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public int getMillsStopEngine()
    {
    	return millsStopEngine;
    }
    public void setMillsStopEngine(int newValue)
    {
    	int oldValue = millsStopEngine;
    	millsStopEngine = newValue;
    	propertyChangeSupport.firePropertyChange("millsStopEngine", oldValue, newValue);
    }
    public int getSelectDisplayModality()
    {
    	return selectDisplayModality;
    }
    public void setSelectDisplayModality(int newValue)
    {
    	int oldValue = selectDisplayModality;
    	selectDisplayModality = newValue;
    	propertyChangeSupport.firePropertyChange("selectDisplayModality", oldValue, newValue);
    }

    private void decFontDimension()
    {
    	if ((Integer.parseInt( fontDimensionTextField.getText() ))!=1)
    	{
	    	fontDimensionHandler.decFontDimension();
			fontDimensionTextField.setText(""+fontDimensionHandler.getFontDimension());
    	}
    }
    private void setFontDimension()
    {
    	int dim;
    	try
    	{
    		dim = Integer.parseInt( fontDimensionTextField.getText() );
    	}
    	catch (NumberFormatException e)
    	{
    		dim = fontDimensionHandler.getFontDimension();
    	}
    	if (dim<1)
    	{
        	fontDimensionHandler.setFontDimension(1);
    		fontDimensionTextField.setText(""+fontDimensionHandler.getFontDimension());
    	}
    	else if (dim>99)
    	{
        	fontDimensionHandler.setFontDimension(99);
        	fontDimensionTextField.setText(""+fontDimensionHandler.getFontDimension());
    	}
    	else
    		fontDimensionHandler.setFontDimension(dim);
    }
    private void incFontDimension()
    {
    	if ((Integer.parseInt( fontDimensionTextField.getText() ))!=99)
    	{
	    	fontDimensionHandler.incFontDimension();
			fontDimensionTextField.setText(""+fontDimensionHandler.getFontDimension());
    	}
    }

}
