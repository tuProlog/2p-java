package alice.tuprologx.ide;

import javax.swing.JTable;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class PrologTable
    extends JTable
{
    /**
	 * @uml.property  name="isExpandedCellArray"
	 */
    private boolean[] isExpandedCellArray;
    /**
	 * @uml.property  name="isBorderedCellArray"
	 */
    private boolean[] isBorderedCellArray;

    public PrologTable()
    {
        super();
    }

    public PrologTable(Object[][] rowData, Object[] columnNames)
    {
        super(rowData,columnNames);
        initArrayisExpandedCellArray();
        initArrayisBorderedCellArray();
        prepareCellRenderer();
    }

    public PrologTable(Object[][] rowData, Object[] columnNames, boolean[] isBorderedCellArray)
    {
        super(rowData,columnNames);
        initArrayisExpandedCellArray();
        this.isBorderedCellArray=isBorderedCellArray;
        prepareCellRenderer();
    }

    public PrologTable(TableModel model)
    {
        super(model);
        initArrayisExpandedCellArray();
        initArrayisBorderedCellArray();
        prepareCellRenderer();
    }

    public PrologTable(TableModel model, boolean[] isBorderedCellArray)
    {
        super(model);
        initArrayisExpandedCellArray();
        this.isBorderedCellArray=isBorderedCellArray;
        prepareCellRenderer();
    }

    private void prepareCellRenderer()
    {
        //wrap text
        TableColumnModel columnModel = getColumnModel();
        TextAreaRenderer textAreaRenderer = new TextAreaRenderer(isExpandedCellArray,isBorderedCellArray);
        for (int i=0;i<columnModel.getColumnCount();i++)
        {
            columnModel.getColumn(i).setCellRenderer(textAreaRenderer);
        }    
    }

    private void initArrayisExpandedCellArray()
    {
        isExpandedCellArray = new boolean[getRowCount()];
        for (int j=0;j<getRowCount();j++)
        {
            isExpandedCellArray[j]=false;
        }
    }

    private void initArrayisBorderedCellArray()
    {
        isBorderedCellArray = new boolean[getRowCount()];
        for (int j=0;j<getRowCount();j++)
        {
            isBorderedCellArray[j]=false;
        }
    }

    public void changeRowStatus(int row)
    {
        isExpandedCellArray[row]=!isExpandedCellArray[row];
    }

    /**
	 * @param isExpandedCellArray
	 * @uml.property  name="isExpandedCellArray"
	 */
    public void setIsExpandedCellArray(boolean[] isExpandedCellArray)
    {
        this.isExpandedCellArray = isExpandedCellArray;
        prepareCellRenderer();
    }
    /**
	 * @return
	 * @uml.property  name="isExpandedCellArray"
	 */
    public boolean[] getIsExpandedCellArray()
    {
        return isExpandedCellArray;
    }

    /**
	 * @param isBorderedCellArray
	 * @uml.property  name="isBorderedCellArray"
	 */
    public void setIsBorderedCellArray(boolean[] isBorderedCellArray)
    {
        this.isBorderedCellArray = isBorderedCellArray;
        prepareCellRenderer();
    }
    /**
	 * @return
	 * @uml.property  name="isBorderedCellArray"
	 */
    public boolean[] getIsBorderedCellArray()
    {
        return isBorderedCellArray;
    }

    //cells are enabled, but not editable

    public boolean isCellEditable(int row, int column)
    {
        return false;
    }

}
