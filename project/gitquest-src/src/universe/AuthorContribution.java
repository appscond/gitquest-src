package universe;

import java.util.Vector;

public interface AuthorContribution 
{
	Vector <String> getAuthors ();
	int getVotes ();
	boolean bIsMutable ();
}
