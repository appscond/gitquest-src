package universe;

import java.util.ArrayList;

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
	 *          mPlayerOptions - arraylist of the junctions available to the 
	 *                           player from this junction.
	 *          mAuthors       - arraylist of strings referring to the authors of 
	 *                           this junction.
	 *          mVotes         - (signed) int of upvotes and downvotes on the 
	 *                           junction.
	 */
	int mID;
	String mTitle;
	String mText;
	ArrayList <JunctionOption> mPlayerOptions;
	ArrayList <String> mAuthors;
	int mVotes;
	
	/*
	 * (non-Javadoc)
	 * @see universe.AuthorContribution#getAuthors()
	 * Functions: getAuthors - returns a copy of the arraylist of authors.
	 *            getVotes   - returns the number of up/downvotes on this 
	 *                         junction.
	 *            bIsMutable - determines if the Junction can be edited by anyone.
	 *            addAuthor  - adds a new author to the Junction if permitted.
	 *            upVote     - upvotes the Junction.
	 *            downVote   - downvotes the Junction.
	 */
	
	/**
	 * Purpose: A getter for the authors of this Junction.
	 * Overriden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 * @return - a arraylist of strings. Each string is an author handle.
	 */
	@Override
	public ArrayList<String> getAuthors() {
		return new ArrayList<String> (mAuthors);
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
	 * Purpose: Determines if the Junction is capable of being modified.
	 * Overridden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 * @return - whether or not an author can change the Junction.
	 */
	@Override
	public boolean bIsMutable() 
	{
		return mVotes > IMMUTABLE_VOTE_THRESHOLD;
	}

	/**
	 * Purpose: Adds a new author to the Junction. Should prevent this if  
	 * 					bIsMutable is false.
	 * Overridden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 * @param author - the author to be added.
	 */
	@Override
	public void addAuthor(String author) 
	{
		if (!bIsMutable())
		{
			mAuthors.add (author);
		}
	}

	/**
	 * Purpose: Upvote this Junction. Junctions with a high upvote cannot be 
	 *          edited.
	 * Overriden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 */
	@Override
	public void upVote() 
	{
		mVotes++;
	}

	/**
	 * Purpose: Downvote this Junction. Junctions with a low upvote will be 
	 * 					purged.
	 * Overriden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 */
	@Override
	public void downVote() 
	{
		mVotes--;
	}
}
