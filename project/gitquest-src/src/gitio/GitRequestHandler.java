package gitio;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
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
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.util.FS;

//TODO: improve comment style to match that of dalt6282.
//TODO: actually deal with exceptions.

/**
 * This class provides a simple interface for using common low-level git
 * commands.
 */
public class GitRequestHandler {
	/** stores the directory of the local clone of the git branch. */
	private String localPath = System.getProperty("user.home")
			+ "/gitquest/gitquest-core";
	/** stores the directory of the remote clone of the git branch. */
	private String remoteSSHPath = "git@github.com:appscond/gitquest-core";
	private String remoteHTTPSPath = "https://github.com/appscond/gitquest-core";
	private Repository localRepo;
	private Git git;
	/** used to set ssh-keys used for apache git server identification. */
	private final SSHInterface ssh = new SSHInterface();
	/**
	 * If this is true, authentication will use the ssh key (either
	 * userSpecifiedSSHKeyPath or defaultAnonymousSSHKeyPath)
	 */
	private boolean useSSH = true;
	/**
	 * Stores the path to the default key used for anonymous SSH authentication
	 * to the remote git repository. This field can be modified (though no
	 * methods allow this openly at the moment), but it's better to change
	 * UserSpecifiedSSHKey via the setSSHKey() method.
	 */
	private File defaultAnonymousSSHKeyPath;
	/**
	 * Stores the path to the key used for SSH authentication to the remote git
	 * repository, as specified by the user in setSSHKey().
	 */
	private File userSpecifiedSSHKeyPath = null;

	/** Like GitInterface(String localPath), but uses the default local path. */
	public GitRequestHandler() throws IOException {
		this(null);
	}

	/**
	 * Creates a new handler for a git repository at the given location. The
	 * repository does not need to exist yet; createNewRepository() can be
	 * called afterward.
	 * 
	 * @author NaOH
	 * @version 0.0.3
	 * @param localPath
	 *            is a (preferably absolute) path to the (not yet necessarily
	 *            existent) local repository.
	 */
	public GitRequestHandler(String localPath) throws IOException {
		if (localPath != null)
			if (localPath.length() > 0)
				this.localPath = localPath;
		localRepo = new FileRepository(getLocalRepositoryPath() + "/.git");
		defaultAnonymousSSHKeyPath = new File(getLocalRepositoryPath()
				+ "/.ssh/id_anon");
		git = new Git(localRepo);
	}

	/**
	 * Sets the URI to the remote repository as used for identification. This
	 * method can be called multiple times to overwrite the remote path without
	 * problems. <br>
	 * <br>
	 * If ssh is true, this GitInterface object will use ssh authentication when
	 * connecting with the remote repository. A default ssh-key may be found in
	 * the gitquest repository, and will be used iff
	 * useDefaultAnonymousSSHKey(true) is called. setSSHKey() can be called to
	 * provide a different key. If no key is specified by either of the above
	 * methods, ssh authentication will still occur, but no key will be
	 * provided. This will fail if the server demands SSH authentication.
	 * remotePath should be of the form *@*:pathToRemote, e.g.
	 * ssh@example.com:user/repo, or else an IllegalArgumentException will be
	 * thrown. <br>
	 * <br>
	 * If ssh is false, this GitInterface object will use https authentication
	 * when connecting with the remote repository. Unless the repository allows
	 * anonymous commits, a username and password will have to be specified with
	 * setCredentials(). remotePath should be of the form https://*, or else an
	 * IllegalArgumentException will be thrown.
	 * 
	 * @author NaOH
	 * @version 0.0.1
	 * @param remotePath
	 *            The URI for the remote repository, in either ssh or https
	 *            format.
	 * @param ssh
	 *            specifies whether to use ssh (true) or https (false)
	 *            connections.
	 */
	public void setRemoteRepositoryPath(String remotePath, boolean ssh)
			throws IllegalArgumentException {
		remotePath = remotePath.trim();
		if (ssh) {
			if (remotePath.matches("\\A.+@.+(:.+)?\\Z")) {
				useSSH = true;
				this.remoteSSHPath = remotePath;
			} else
				throw new IllegalArgumentException();
		} else {
			if (remotePath.matches("https://.+")) {
				useSSH = false;
				this.remoteHTTPSPath = remotePath;
			} else
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Sets the path to the local repository. This method can be called multiple
	 * times to overwrite the remote path without problems, although
	 * cloneFromRemote() or createNewRepository() should be called immediately
	 * afterward.
	 * 
	 * @author NaOH
	 * @version 0.0.1
	 * @param localPath
	 *            is the (preferably absolute) path the local repository. Note
	 *            that this directory doesn't have to exist yet.
	 */
	public void setLocalRepositoryPath(String localPath) {
		this.localPath = localPath;
	}

	public File getLocalRepositoryPath() {
		return new File(localPath);
	}

	/**
	 * Returns true iff the local repository (as specified in
	 * setLocalRepositoryPath() or in the constructor) exists.
	 * 
	 * @author NaOH
	 * @version 0.0.1
	 */
	public boolean getExistsRepository() {
		// Use jgit's local repo detection system.
		boolean detected = RepositoryCache.FileKey.isGitRepository(new File(
				localPath), FS.DETECTED);
		// If that returns false, it's still possible a partially-constructed
		// git repo exists:
		if (new File(localPath + "/.git").exists())
			detected = true;
		return detected;
	}

	/**
	 * Creates a new repository at the location specified at construction. This
	 * is equivalent to calling git init on the command line. Creates the path
	 * to the local repository, if necessary.
	 * 
	 * @author NaOH
	 * @version 0.0.1
	 */
	public void createNewRepository() {
		try {
			git = Git.init().setDirectory(new File(localPath)).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stages all files in the local git repository and commits them with the
	 * given metadata.
	 * 
	 * @author NaOH
	 * @version 0.0.2
	 * @param author
	 *            The alias of the author for the commit.
	 * @param email
	 *            must be a valid email, and preferably a useful one.
	 * @param message
	 *            is the commit message. An empty message will cause the commit
	 *            to fail.
	 */
	public void stageAndCommit(String author, String email, String message) {
		// TODO tackle the daunting number of exceptions in this method.
		try {
			git.add().addFilepattern(".").call();
		} catch (NoFilepatternException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		try {
			git.commit().setMessage(message).setAuthor(author, email)
					.setCommitter(author, email).call();
			return;
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
		// undo git.add if commit fails:
		try {
			git.reset().call();
		} catch (CheckoutConflictException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new repository at the location specified at construction by
	 * cloning from the remote URI specified in setRemotePath. Creates the path
	 * to the local repository, if necessary.
	 * 
	 * @author NaOH
	 * @version 0.0.1
	 */
	public void cloneFromRemote() throws IOException {
		// TODO: handle password-protected remote https repositories.
		try {
			git = Git.cloneRepository().setURI(getRemotePath())
					.setDirectory(new File(localPath)).call();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Downloads the latest updates to the remote git repository.
	 * 
	 * @author NaOH
	 * @version 0.0.5
	 */
	public void pullFromRemote() throws IOException {
		// TODO: handle password-protected remote https repositories.
		try {
			// TODO: technically, this should occur in
			// setRemoteRepositoryPath(), i.e. when a new
			// remote repository is set, and not on-the-fly before a pull.
			// However, there are some irritating edge-cases that I don't want
			// to deal with just yet.
			String path = getRemotePath();
			StoredConfig config = git.getRepository().getConfig();
			config.setString("remote", "origin", "url", path);
			try {
				config.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	/**
	 * Uploads the latest local updates to the remote git repository. Must be
	 * called after stageAndCommit().
	 * 
	 * @author NaOH
	 * @version 0.0.1
	 */
	public void pushToRemote() throws IOException {
		try {
			git.push().setRemote(getRemotePath()).call();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			// e.g. not authorized.
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets the SSH Key to be used for authentication when connecting to the
	 * remote using SSH.
	 * 
	 * @author NaOH
	 * @version 0.0.2
	 * @param pathToKey
	 *            The path to the file containing the desired SSH key.
	 */
	public void setSSHKey(File pathToKey) {
		ssh.setID(userSpecifiedSSHKeyPath = pathToKey);
	}

	/**
	 * Tells git to use the anonymous SSH key specified by the gitquest
	 * repository.
	 * 
	 * @author NaOH
	 * @version 0.0.1
	 * @param use
	 *            True if the default SSH key should be used, false if the SSH
	 *            key specified in setSSHKey() should be used.
	 */
	public void useDefaultAnonymousSSHKey(boolean use) {
		if (use)
			ssh.setID(defaultAnonymousSSHKeyPath);
		else
			// TODO: this should throw an exception if userSpecifiedSSHKeyPath
			// is null.
			setSSHKey(userSpecifiedSSHKeyPath);
	}

	public void setCredentials() {
		// TODO implement username and password authentication.
	}

	/**
	 * Returns a String containing the path to the remote repository, either the
	 * SSH version or the HTTPS version, depending on if useSSH is true.
	 * 
	 * @author NaOH
	 * @version 0.0.1
	 */
	private String getRemotePath() {
		if (useSSH)
			return remoteSSHPath;
		else
			return remoteHTTPSPath;
	}
}