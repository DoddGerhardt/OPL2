import java.io.File;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import metamutator.MutantSearchSpaceExplorator;


public class Launch {
	public static void main(String args[]) throws Exception {
		spoon.Launcher.main(new String[] {"-i","input/jsoup/src/main/java","-o","spooned/jsoup/src/main/java","-p","metamutator.UnaryOperatorMetaMutator"});
		
		Launcher spoon = new Launcher();
		SpoonCompiler comp = spoon.createCompiler();
		comp.addInputSource(new File("input"));
		comp.setBinaryOutputDirectory(new File("spooned"));
		comp.compile();
		
		MutantSearchSpaceExplorator.runMetaProgramIn("input/jsoup/target/test-classes","resources/search_replay_test");
	}
}
