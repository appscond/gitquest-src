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
}
