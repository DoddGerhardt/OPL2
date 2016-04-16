
public class Launch {
	public static void main(String args[]) throws Exception {
		spoon.Launcher.main(new String[] {"-i","input/jsoup/src/main/java","-p","metamutator.UnaryOperatorMetaMutator"});
	}
}
