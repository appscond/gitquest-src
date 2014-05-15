package gitio;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;

//TODO: improve comment style to match that of dalt6282.
//TODO: actually deal with exceptions.

/**This class provides a simple interface for using common low-level git commands.*/
public class GitInterface {
	/**stores the directory of the local clone of the git branch. */
	private String localPath=System.getProperty("user.home")+"/gitquest/gitquest-core";
	/**stores the directory of the remote clone of the git branch.*/
	private String remotePath="https://github.com/appscond/gitquest-core";
	private Repository localRepo;
	private Git git;
	/**public member can be accessed to set ssh-keys used for apache git server identification.*/
	public final SSHInterface ssh = new SSHInterface();
	/**Like GitInterface(String localPath), but uses the default local path.*/
	public GitInterface() throws IOException{
		this(null);
	}
	/**Creates a new handler for a git repository at the given location. The repository does not need to exist yet;
	 *  createNewRepository() can be called afterward.*/
	public GitInterface(String localPath) throws IOException{
		if (localPath!=null)
			if (localPath.length()>0)
				this.localPath=localPath;
		localRepo = new FileRepository(this.localPath + "/.git");
		git = new Git(localRepo);
	}
	/**Sets the URI to the remote repository. This method can be called multiple times to overwrite the remote path
	 * without problems.*/
    public void setRemoteRepositoryPath(String remotePath){
    	this.remotePath=remotePath;
    }
    /**Sets the path to the local repository. This method can be called multiple times to overwrite the remote path
	 * without problems, although cloneFromRemote() or createNewRepository() should be called immediately afterward.*/
    public void setLocalRepositoryPath(String localPath){
    	this.localPath=localPath;
    }
	public File getLocalRepositoryPath() {
		return new File(localPath);
	}
    /**Returns true iff the local repository (as specifiedexists.*/
    public boolean getExistsRepository(){
    	//Use jgit's local repo detection system.
    	boolean detected = RepositoryCache.FileKey.isGitRepository(new File(localPath), FS.DETECTED);
    	//If that returns false, it's still possible a partially-constructed git repo exists:
    	if (new File(localPath + "/.git").exists())
    		detected=true;
    	return detected;
    }
    /**Creates a new repository at the location specified at construction.*/
    public void createNewRepository(){
    	try {
			git=Git.init().setDirectory(new File(localPath)).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
    }
    /**Stages all files in the local git repository and commits them with the given metadata.*/
    public void stageAndCommit(String author,String email,String message){
    	try {
			git.add().addFilepattern(".").call();
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
    	try {
			git.commit().setMessage(message).setAuthor(author, email).setCommitter(author, email).call();
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (NoMessageException e) {
			e.printStackTrace();
		} catch (UnmergedPathsException e) {
			e.printStackTrace();
		} catch (ConcurrentRefUpdateException e) {
			e.printStackTrace();
		} catch (WrongRepositoryStateException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
    }
    /**Creates a new repository at the location specified at construction by cloning from the
     * remote URI specified in setRemotePath.*/
    public void cloneFromRemote() throws IOException{
    	try {
			git=Git.cloneRepository().setURI(remotePath).setDirectory(new File(localPath)).call();
    	} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
    }
    public void pullFromRemote() throws IOException{
		try {
			git.pull().call();
		} catch (WrongRepositoryStateException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		} catch (DetachedHeadException e) {
			e.printStackTrace();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (CanceledException e) {
			e.printStackTrace();
		} catch (RefNotFoundException e) {
			e.printStackTrace();
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
    }
    public void pushToRemote() throws IOException{
    	//TODO: this method most of all needs to deal with exceptions.
    	try {
			git.push().setRemote(remotePath).call();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			//e.g. not authorized.
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
    }
}
