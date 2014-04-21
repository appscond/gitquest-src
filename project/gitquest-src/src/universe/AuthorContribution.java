package universe;

import java.util.ArrayList;

public interface AuthorContribution 
{
	ArrayList <String> getAuthors ();
	int getVotes ();
	boolean bIsMutable ();
}
