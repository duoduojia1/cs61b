package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/** Performs some basic array deque tests. */
public class ArrayDequeTest {

    @Test
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();

        assertTrue("A newly initialized ArrayDeque should be empty", ad.isEmpty());
        ad.addFirst("front");

        assertEquals(1, ad.size());
        assertFalse("ArrayDeque should now contain 1 item", ad.isEmpty());

        ad.addLast("middle");
        assertEquals(2, ad.size());

        ad.addLast("back");
        assertEquals(3, ad.size());

        System.out.println("Printing out deque: ");
        ad.printDeque();
    }

    @Test
    public void addRemoveTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        assertTrue("ArrayDeque should be empty upon initialization", ad.isEmpty());

        ad.addFirst(10);
        assertFalse("ArrayDeque should contain 1 item", ad.isEmpty());

        ad.removeFirst();
        assertTrue("ArrayDeque should be empty after removal", ad.isEmpty());
    }

    @Test
    public void removeEmptyTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(3);

        ad.removeLast();
        ad.removeFirst();
        ad.removeLast();
        ad.removeFirst();

        int size = ad.size();
        String errorMsg = "Bad size returned when removing from empty deque.\n" +
                "student size() returned " + size + "\n" +
                "actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    public void multipleParamTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ArrayDeque<Double> ad2 = new ArrayDeque<>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    @Test
    public void emptyNullReturnTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        assertEquals("Should return null when removeFirst is called on empty deque", null, ad.removeFirst());
        assertEquals("Should return null when removeLast is called on empty deque", null, ad.removeLast());
    }

    @Test
    public void bigArrayDequeTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        for (int i = 0; i < 1000000; i++) {
            ad.addLast(i);
        }
        ad.printDeque();
        for (int i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (int) ad.removeFirst());
        }

        for (int i = 999999; i >= 500000; i--) {
            assertEquals("Should have the same value", i, (int) ad.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        int N = 1000000;
        for (int i = 0; i < N; i++) {
            int op = StdRandom.uniform(0, 6);
            if (op == 0) {
                int val = StdRandom.uniform(0, 100);
                ad.addFirst(val);
            } else if (op == 1) {
                int val = StdRandom.uniform(0, 100);
                ad.addLast(val);
            } else if (ad.size() == 0) {
                assertTrue(ad.isEmpty());
            } else if (op == 2) {
                assertTrue(ad.size() > 0);
            } else if (op == 3) {
                ad.removeFirst();
            } else if (op == 4) {
                ad.removeLast();
            } else if (op == 5) {
                int idx = StdRandom.uniform(0, ad.size());
                ad.get(idx);
            }
        }
    }
}
