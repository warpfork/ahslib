package ahs.test;

// java -server -Xmx3000m -cp ./bin/:./lib/* org.junit.runner.JUnitCore ahs.test.Interrupt

public class Interrupt extends TestCase {
	public void testKillable() {
		Object $x = new Object();
		for (int $i = 0; $i < 100000; $i++) {
			synchronized ($x) {
				try {
					$x.wait(10);
				} catch (InterruptedException $e) {
					/* eat it */
				}
			}
		}
		fail("you never interrupted it.");
	}
}
