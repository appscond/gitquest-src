package universe;

import java.util.ArrayList;

public interface AuthorContribution 
{
	/*
	 * (non-Javadoc)
	 * @see universe.AuthorContribution#getAuthors()
	 * Functions: getAuthors - returns a copy of the arraylist of authors.
	 *            getVotes   - returns the number of up/downvotes on this
	 *                         contribution.
	 *            bIsMutable - determines if the contribution can be edited by 
	 *                         anyone.
	 */
	
	/* Getters */
	ArrayList <String> getAuthors ();
	int getVotes ();
	boolean bIsMutable ();

	/* Setters */
	void addAuthor (String author);
	void upVote ();
	void downVote ();
}
