package orientedSwapProcess;

import org.junit.jupiter.api.Test;

/**
 * This class contains some test used for devoloping this package
 */
class TestSuite {

	@Test
	void test() {
		Number n1 = new Number(1);
		Number n2 = new Number(2);
		Number n3 = new Number(3);
		Number n4 = new Number(4);
		
		NumberList list = new NumberList();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		
		assert list.getPositionOf(3) == 2;
		assert list.getValueAt(0) == 1;
		
		OrientedSwapProcess OSP = new OrientedSwapProcess(5);
		OSP.printExponentialClocks();
		System.out.println();
		OSP.doNextMoment();
		OSP.printExponentialClocks();
	}

}
