/**
 * Cluster.java
 * 
 * KIT107 Assignment 2 -- Cluster Implementation
 * 
 * @author Muhtasim Nahiyan 
 * @version	<< 14/09/2026>>
 */

public class Cluster implements ClusterInterface
{
    // instance variables
    protected String bundleName;        // candidate this cluster belongs to
    protected double weightedCount;     // total weight of ballots
    protected int rawCount;             // number of ballots

    protected Ballot[] ballots;   
    protected int numBallots;           
   	protected final int INITIAL_SIZE = 10;


	/**
	 * Constructor
	 * 
     * @param candidate String -- the name of the candidate whose votes 
     *                  this bundle (cluster) is for
     * 
	 * Precondition: The String is defined and unique
	 * Postcondition: The new instance will have its instance variable(s)
     *                  initialised to indicate an empty cluster.
	 * Informally: Initialise the cluster of ballots.
	 */
    public Cluster(String candidate)
    {
        bundleName = candidate;
        weightedCount = 0.0;
        rawCount = 0;

        ballots = new Ballot[INITIAL_SIZE];
        numBallots = 0;
    }

	/**
	 * isEmpty()
	 * 
	 * @return boolean -- whether the cluster is empty
	 * 
	 * Precondition: None
	 * Postcondition: True is returned if the Cluster is empty; false is
     *                  returned otherwise.
	 * Informally: Check whether the Cluster is empty.
	 */
    public boolean isEmpty()  // this loop checks if the cluster is empty by checking if the number of ballots is zero
    {
        return (numBallots == 0);
    }

 	/**
	 * getFirstBallot()
	 * 
	 * @return Ballot -- the first ballot paper in the cluster
	 * 
	 * Precondition: None
	 * Postcondition: the first ballot in the cluster is returned if the
     *                  cluster is non-empty; null is returned otherwise.
	 * Informally: Get the first ballot paper in the cluster.
	 */
    public Ballot getFirstBallot() // this loop returns the first ballot in the cluster if it is not empty, otherwise it returns null
	{
		if (isEmpty())
		{
			return null;
		}
		return ballots[0];
	}

 	/**
	 * getRawCount()
	 * 
	 * @return int -- the raw count of ballot papers in the cluster
	 * 
	 * Precondition: None
	 * Postcondition: the raw count of ballots in the cluster is returned.
	 * Informally: Get the count of ballots in the cluster.
	 */
   public int getRawCount() // this returns the raw count of ballots in the cluster
    {
        return rawCount;
    }

 	/**
	 * getWeightedCount()
	 * 
	 * @return double -- the weighted count of ballot papers in the cluster
	 * 
	 * Precondition: None
	 * Postcondition: the weighted count of ballots in the cluster is
     *                  returned.
	 * Informally: Get the weighted count of ballots in the cluster.
	 */
    public double getWeightedCount()
    {
        return weightedCount;
    }

 	/**
	 * getBundleName()
	 * 
	 * @return String -- the name of the candidate that is the recipient
     *                  of this cluster of ballots
	 * 
	 * Precondition: None
	 * Postcondition: the name of the bundle is returned.
	 * Informally: Get the cluster's candidate name.
	 */
    public String getBundleName()
    {
        return bundleName;
    }
	
	private void ensureCapacity()
    {
        if (numBallots == ballots.length)
        {
            Ballot[] temp = new Ballot[ballots.length * 2];

            for (int i = 0; i < ballots.length; i++)
            {
                temp[i] = ballots[i];
            }

            ballots = temp;
        }
    }
    /**
	 * addBallotToCluster()
	 * 
	 * @param votes Ballot -- the ballot paper to add to this cluster
	 * 
	 * Precondition: The given Ballot parameter has been constructed.
	 * Postcondition: The given Ballot has been added to the Cluster of
     *                  ballot papers ordered by descending preference.
	 * Informally: Add a ballot paper to the Cluster.
	 */
     
	public void addBallotToCluster(Ballot votes)
    {
        if (votes == null)
        {
            return;
        }

        ensureCapacity();

        int insertPos = numBallots;
        int newChoice = votes.getChoice();

        // find correct position (descending order)
        for (int i = 0; i < numBallots; i++)
        {
            if (ballots[i].getChoice() < newChoice)
            {
                insertPos = i;
                break;
            }
        }

        // shift right
        for (int i = numBallots; i > insertPos; i--)
        {
            ballots[i] = ballots[i - 1];
        }

        // insert ballot
        ballots[insertPos] = votes;

        numBallots++;
        rawCount++;
        weightedCount += votes.getWeight();
    }
 	/**
	 * votesFor()
	 * 
	 * @param candidate String -- the candidate to count the votes of
     * @param preference int -- the preference to count the votes for
     * 
     * @return int -- the count of votes for the given candidate of the
     *                  given preference
	 * 
	 * Precondition: None
	 * Postcondition: the first ballot in the cluster is returned if the
     *                  cluster is non-empty; null is returned otherwise.
	 * Informally: Get the first ballot paper in the cluster.
	 */
     public int votesFor(String candidate, int preference) // this part counts the number of votes for a given candidate at a given preference level in the cluster
    {
        int matches;
        int i;
        String[] prefs;

        matches = 0;

        for (i = 0; i < rawCount; i++)
        {
            prefs = ballots[i].getVotes();

            if (preference < prefs.length)
            {
                if (prefs[preference].equalsIgnoreCase(candidate))
                {
                    matches++;
                }
            }
        }

        return matches;
    }

 	/**
	 * transfer()
	 * 
     * @param residiual double -- the residual weight to be allocated to
     *                  the ballot being transferred
     * 
	 * @return Ballot -- the ballot removed from the current cluster
     *                  which is to be moved to another cluster with the
     *                  given weight
	 * 
	 * Precondition: None
	 * Postcondition: the first ballot in the cluster is removed, the
     *                  selection is updated to the next preference,
     *                  the weight is altered if the residual is not
     *                  full weight, and then the ballot is returned.
     *                  null is returned if the cluster is empty.
	 * Informally: Prepare the first ballot of the cluster to be
     *                  moved to the cluster of its next preference,
     *                  and remove it from this cluster.
	 */
    public Ballot transfer(double residual)
    {
        if (isEmpty())
        {
            return null;
        }

        Ballot b = ballots[0];

        // shift left
        for (int i = 1; i < numBallots; i++)
        {
            ballots[i - 1] = ballots[i];
        }
        ballots[numBallots - 1] = null;
        numBallots--;

        weightedCount -= b.getWeight();

        b.setWeight(b.getWeight() * residual);

        b.update();

        return b;
    }


	/**
	 * toString()
	 * 
	 * @return String -- printable form of the Cluster of ballots
	 * 
	 * Precondition: None
	 * Postcondition: A printable (String) form of the ballot data is
     *                  returned.  If there are no ballot papers then ""
     *                  is returned.
	 * Informally: Convert the Cluster of ballot data to a multi-line
     *                  String.
	 */
    public String toString()
    {
        if (isEmpty())
        {
            return "";
        }

        String r = bundleName + " (" + numBallots + " ballots)\n";

        for (int i = 0; i < numBallots; i++)
        {
            r += ballots[i].toString() + "\n\n";
        }

        return r;
    }
}