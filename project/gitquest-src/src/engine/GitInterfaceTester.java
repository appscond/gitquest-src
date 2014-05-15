package engine;

import gitio.GitInterface;
import gitio.SSHInterface;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GitInterfaceTester {
	public static void main(String[] args){
		try {
			GitInterface git = new GitInterface();
			boolean exists = git.getExistsRepository();
			System.out.println(exists);
			if (!exists){
				git.cloneFromRemote();
			}
			File local = git.getLocalRepositoryPath();
			Random r = new Random();
			File rf = new File(local.getAbsolutePath()+"/test/file"+r.nextInt(10000));
			rf.getParentFile().mkdir();
			rf.createNewFile();
			git.stageAndCommit("testClient", "nstbayless@gmail.com", "Added random test file (" + rf.getName()+")");
			git.pushToRemote();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}