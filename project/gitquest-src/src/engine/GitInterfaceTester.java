package engine;

import gitio.GitInterface;

import java.io.IOException;

public class GitInterfaceTester {
	public static void main(String[] args){
		try {
			GitInterface git = new GitInterface();
			boolean exists = git.getExistsRepository();
			System.out.println(exists);
			if (!exists){
				git.cloneFromRemote();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
