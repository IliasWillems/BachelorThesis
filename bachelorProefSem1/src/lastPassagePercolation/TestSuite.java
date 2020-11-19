package lastPassagePercolation;

import org.junit.jupiter.api.Test;

class TestSuite {

	@Test
	void test() {
		YoungTableau yt = new YoungTableau(5, true);
		yt.displayTableau();
		
		System.out.println();
		
		yt.getLastPassageTableau().displayTableau();
	}
}