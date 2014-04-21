package universe;

import java.util.Vector;

/**
 * @author dalt6282
 * @version 0.0.1
 *
 */
public class Junction implements AuthorContribution
{
	/*
	 * Class Constants: IMMUTABLE_VOTE_THRESHOLD - the number of upvotes required
	 *                                             to prevent the Junction from
	 *                                             being modified.
	 */
	final int IMMUTABLE_VOTE_THRESHOLD = 5;
	/*
	 * Members: mID            - int that is extracted from the Universe at  
	 *                           construction time. If there is a git conflict, 
	 *                           this will at least need to be recomputed.
	 *          mTitle         - string representing a quick way for players to  
	 *                           refer to this junction.
	 *          mText          - string describing what happens at the junction.
	 *          mPlayerOptions - vector of the junctions available to the player
	 *                           from this junction.
	 *          mAuthors       - vector of strings referring to the authors of 
	 *                           this junction.
	 *          mVotes         - (signed) int of upvotes and downvotes on the 
	 *                           junction.
	 */
	int mID;
	String mTitle;
	String mText;
	Vector <JunctionOption> mPlayerOptions;
	Vector <String> mAuthors;
	int mVotes;
	
	/*
	 * (non-Javadoc)
	 * @see universe.AuthorContribution#getAuthors()
	 * Functions: getAuthors - returns a copy of the vector of authors.
	 *            getVotes   - returns the number of up/downvotes on this 
	 *                         junction.
	 */
	
	/**
	 * Purpose: A getter for the authors of this Junction.
	 * Overriden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 * @return - a vector of strings. Each string is an author handle.
	 */
	@Override
	public Vector<String> getAuthors() {
		return new Vector<String> (mAuthors);
	}
	
	/**
	 * Purpose: a getter for the votes on this Junction.
	 * Overridden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 * @return - an int containing the vote state of this Junction.
	 */
	@Override
	public int getVotes() 
	{
		return mVotes;
	}
	
	/**
	 * Purpose: Determines if the junction is capable of being modified.
	 * Overridden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 * @return - whether or not an author can change the junction.
	 */
	@Override
	public boolean bIsMutable() 
	{
		return mVotes > IMMUTABLE_VOTE_THRESHOLD;
	}
}
