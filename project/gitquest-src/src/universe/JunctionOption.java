package universe;

import java.util.ArrayList;

public class JunctionOption implements AuthorContribution
{
	/*
	 * Class Constants: IMMUTABLE_VOTE_THRESHOLD - the number of upvotes required
	 *                                             to prevent the JunctionOption 
	 *                                             from being modified.
	 */
	final int IMMUTABLE_VOTE_THRESHOLD = 5;
	
	/*
	 * Members: mDestinationID - int id of the junction we are pointing to from
	 * 													 this option.
	 *          mText          - string describing what choice this represents.
	 *          mAuthors       - arraylist of strings referring to the authors of 
	 *                           this junction.
	 *          mVotes         - (signed) int of upvotes and downvotes on the 
	 *                           JunctionOption.
	 */
	int mDestinationID;
	String mText;
	ArrayList <String> mAuthors;
	int mVotes;
	
	/*
	 * (non-Javadoc)
	 * @see universe.AuthorContribution#getAuthors()
	 * Functions: getAuthors - returns a copy of the arraylist of authors.
	 *            getVotes   - returns the number of up/downvotes on this 
	 *                         JunctionOption.
	 *            bIsMutable - determines if the option is mutable by anyone.
	 *            addAuthor  - adds a new author to the JunctionOption if allowed.
	 *            upVote     - upvotes the JunctionOption.
	 *            downVote   - downvotes the JunctionOption.
	 */
	
	/**
	 * Purpose: A getter for the authors of this JunctionOption.
	 * Overriden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 * @return - an arraylist of strings. Each string is an author handle.
	 */
	@Override
	public ArrayList<String> getAuthors() {
		return new ArrayList<String> (mAuthors);
	}
	
	/**
	 * Purpose: a getter for the votes on this JunctionOption.
	 * Overridden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 * @return - an int containing the vote state of this JunctionOption.
	 */
	@Override
	public int getVotes() 
	{
		return mVotes;
	}
	
	/**
	 * Purpose: Determines if the JunctionOption is capable of being modified.
	 * Overridden from: AuthorContribution.
	 * @author dalt6282
	 * @version 0.0.1
	 * @return - whether or not an author can change the JunctionOption.
	 */
	@Override
	public boolean bIsMutable() 
	{
		return mVotes > IMMUTABLE_VOTE_THRESHOLD;
	}

	/**
	 * Purpose: Adds a new author to the JunctionOption. Should prevent this if  
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
	 * Purpose: Upvote this JunctionOption. JunctionOptions with a high upvote 
	 *          cannot be edited.
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
	 * Purpose: Downvote this Junction. JunctionOptions with a low upvote will be 
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
