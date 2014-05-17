package universe;

import java.util.HashSet;

public class Universe 
{
	/*
	 * Members: mJunctionList	- Contains the set of all Junctions in this Universe. 
	 * 			mInitialNode	- Provides the entryway into the graph structure.
	 */
	HashSet<Junction> mJunctionList = new HashSet<>();
	Junction mInitialNode;
}