import metamutator.MutantSearchSpaceExplorator;


public class Launch {
	public static void main(String args[]) throws Exception {
		spoon.Launcher.main(new String[] {"-i","input/jsoup/src/main/java","-o","spooned","-p","metamutator.UnaryOperatorMetaMutator"});
	}
}
