package alice.tuprologx.ide;

public interface FontDimensionHandler {

    /**
     * Increment the font dimension of the IDE's editor
     */
    public void incFontDimension();

    /**
     * Increment the font dimension of the IDE's editor
     */
    public void decFontDimension();

    public int getFontDimension();

    public void setFontDimension(int dimension);


}
