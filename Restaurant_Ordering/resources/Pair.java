package resources;

/**
 * <h1> Pair Tool</h1>
 * The generic class for pairing any two object
 *
 * @author  Anh Pham
 * @version 0.4
 * @since   2017-03-24
 */
public class Pair<X,Y>
{
	// The two objects
	private X x;
	private Y y;
	
	/**
	 * Constructor
	 * @param x The first object
	 * @param y The second object
	 */
	public Pair(X x, Y y)
	{
		this.x = x;
		this.y = y;
	}
	
	/** 
	 * Method to get the first object
	 * @return X object x
	 */
	public X getFirst()
	{
		return x;
	}
	
	/**
	 * Method to get the second object
	 * @return Y object y
	 */
	public Y getSecond()
	{
		return y;
	}
	
	/** 
	 * Method to set the first object
	 * @param x Value to set for object x
	 */
	public void setFirst(X x)
	{
		this.x = x;
	}
	
	/** 
	 * Method to set the second object
	 * @param y Value to set for object y
	 */
	public void setSecond(Y y)
	{
		this.y = y;
	}
}
