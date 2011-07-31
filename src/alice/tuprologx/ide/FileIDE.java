package alice.tuprologx.ide;

import java.io.File;

public class FileIDE
{
    /**
	 * @uml.property  name="fileName"
	 */
    private String fileName;
    /**
	 * @uml.property  name="filePath"
	 */
    private String filePath;
    /**
	 * @uml.property  name="content"
	 */
    private String content;

    public FileIDE(String content, String filePath)
    {
        this.content = content;
        this.fileName = null;
        this.filePath = null;
        if (filePath!=null)
        {
            int i=filePath.lastIndexOf(File.separator, filePath.length());
            this.filePath = filePath.substring(0,i+1);
            this.fileName = filePath.substring(i+1);
        }
    }

    /**
	 * @return
	 * @uml.property  name="fileName"
	 */
    public String getFileName()
    {
        return fileName;
    }
    /**
	 * @param fileName
	 * @uml.property  name="fileName"
	 */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    /**
	 * @return
	 * @uml.property  name="filePath"
	 */
    public String getFilePath()
    {
        return filePath;
    }
    /**
	 * @param filePath
	 * @uml.property  name="filePath"
	 */
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }
    /**
	 * @return
	 * @uml.property  name="content"
	 */
    public String getContent()
    {
        return content;
    }
    /**
	 * @param content
	 * @uml.property  name="content"
	 */
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getExtension()
    {
        String fileExtension = null;
        int i = getFileName().lastIndexOf('.');
        if (i > 0 && i < getFileName().length() - 1)
            fileExtension = getFileName().substring(i + 1).toLowerCase();
        return fileExtension;
    }
}
