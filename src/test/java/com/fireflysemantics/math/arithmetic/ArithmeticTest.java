/**
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *  http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  */
package com.fireflysemantics.math.arithmetic;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.fireflysemantics.math.exception.ExceptionTypes;
import com.fireflysemantics.math.exception.MathException;

/**
 * Test cases for the {@link Arithmetic} class.
 *
 */
public class ArithmeticTest {

	@Test
	public void testAddAndCheck() {
		int big = Integer.MAX_VALUE;
		int bigNeg = Integer.MIN_VALUE;
		Assert.assertEquals(big, Arithmetic.addAndCheck(big, 0));
		try {
			Arithmetic.addAndCheck(big, 1);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
		}
		try {
			Arithmetic.addAndCheck(bigNeg, -1);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
		}
	}

	@Test
	public void testAddAndCheckLong() {
		long max = Long.MAX_VALUE;
		long min = Long.MIN_VALUE;
		Assert.assertEquals(max, Arithmetic.addAndCheck(max, 0L));
		Assert.assertEquals(min, Arithmetic.addAndCheck(min, 0L));
		Assert.assertEquals(max, Arithmetic.addAndCheck(0L, max));
		Assert.assertEquals(min, Arithmetic.addAndCheck(0L, min));
		Assert.assertEquals(1, Arithmetic.addAndCheck(-1L, 2L));
		Assert.assertEquals(1, Arithmetic.addAndCheck(2L, -1L));
		Assert.assertEquals(-3, Arithmetic.addAndCheck(-2L, -1L));
		Assert.assertEquals(min, Arithmetic.addAndCheck(min
				+ 1, -1L));
		Assert.assertEquals(-1, Arithmetic.addAndCheck(min, max));
		testAddAndCheckLongFailure(max, 1L);
		testAddAndCheckLongFailure(min, -1L);
		testAddAndCheckLongFailure(1L, max);
		testAddAndCheckLongFailure(-1L, min);
		testAddAndCheckLongFailure(max, max);
		testAddAndCheckLongFailure(min, min);
	}

	@Test
	public void testGcd() {
		int a = 30;
		int b = 50;
		int c = 77;

		Assert.assertEquals(0, Arithmetic.gcd(0, 0));

		Assert.assertEquals(b, Arithmetic.gcd(0, b));
		Assert.assertEquals(a, Arithmetic.gcd(a, 0));
		Assert.assertEquals(b, Arithmetic.gcd(0, -b));
		Assert.assertEquals(a, Arithmetic.gcd(-a, 0));

		Assert.assertEquals(10, Arithmetic.gcd(a, b));
		Assert.assertEquals(10, Arithmetic.gcd(-a, b));
		Assert.assertEquals(10, Arithmetic.gcd(a, -b));
		Assert.assertEquals(10, Arithmetic.gcd(-a, -b));

		Assert.assertEquals(1, Arithmetic.gcd(a, c));
		Assert.assertEquals(1, Arithmetic.gcd(-a, c));
		Assert.assertEquals(1, Arithmetic.gcd(a, -c));
		Assert.assertEquals(1, Arithmetic.gcd(-a, -c));

		Assert.assertEquals(3
				* (1 << 15),
				Arithmetic.gcd(3
						* (1 << 20),
						9
								* (1 << 15)));

		Assert.assertEquals(Integer.MAX_VALUE, Arithmetic.gcd(Integer.MAX_VALUE, 0));
		Assert.assertEquals(Integer.MAX_VALUE, Arithmetic.gcd(-Integer.MAX_VALUE, 0));
		Assert.assertEquals(1 << 30, Arithmetic.gcd(1 << 30, -Integer.MIN_VALUE));
		try {
			// gcd(Integer.MIN_VALUE, 0) > Integer.MAX_VALUE
			Arithmetic.gcd(Integer.MIN_VALUE, 0);
			Assert.fail("expecting MathException");
		} catch (MathException expected) {
			// expected
		}
		try {
			// gcd(0, Integer.MIN_VALUE) > Integer.MAX_VALUE
			Arithmetic.gcd(0, Integer.MIN_VALUE);
			Assert.fail("expecting MathException");
		} catch (MathException expected) {
			// expected
		}
		try {
			// gcd(Integer.MIN_VALUE, Integer.MIN_VALUE) > Integer.MAX_VALUE
			Arithmetic.gcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
			Assert.fail("expecting MathException");
		} catch (MathException expected) {
			// expected
		}
	}

	@Test
	public void testGcdLong() {
		long a = 30;
		long b = 50;
		long c = 77;

		Assert.assertEquals(0, Arithmetic.gcd(0L, 0));

		Assert.assertEquals(b, Arithmetic.gcd(0, b));
		Assert.assertEquals(a, Arithmetic.gcd(a, 0));
		Assert.assertEquals(b, Arithmetic.gcd(0, -b));
		Assert.assertEquals(a, Arithmetic.gcd(-a, 0));

		Assert.assertEquals(10, Arithmetic.gcd(a, b));
		Assert.assertEquals(10, Arithmetic.gcd(-a, b));
		Assert.assertEquals(10, Arithmetic.gcd(a, -b));
		Assert.assertEquals(10, Arithmetic.gcd(-a, -b));

		Assert.assertEquals(1, Arithmetic.gcd(a, c));
		Assert.assertEquals(1, Arithmetic.gcd(-a, c));
		Assert.assertEquals(1, Arithmetic.gcd(a, -c));
		Assert.assertEquals(1, Arithmetic.gcd(-a, -c));

		Assert.assertEquals(3L
				* (1L << 45),
				Arithmetic.gcd(3L
						* (1L << 50),
						9L
								* (1L << 45)));

		Assert.assertEquals(1L << 45, Arithmetic.gcd(1L << 45, Long.MIN_VALUE));

		Assert.assertEquals(Long.MAX_VALUE, Arithmetic.gcd(Long.MAX_VALUE, 0L));
		Assert.assertEquals(Long.MAX_VALUE, Arithmetic.gcd(-Long.MAX_VALUE, 0L));
		Assert.assertEquals(1, Arithmetic.gcd(60247241209L, 153092023L));
		try {
			// gcd(Long.MIN_VALUE, 0) > Long.MAX_VALUE
			Arithmetic.gcd(Long.MIN_VALUE, 0);
			Assert.fail("expecting MathException");
		} catch (MathException expected) {
			// expected
			assertEquals(expected.getType(), ExceptionTypes.MAE__GCD_OVERFLOW_64_BITS);
		}
		try {
			// gcd(0, Long.MIN_VALUE) > Long.MAX_VALUE
			Arithmetic.gcd(0, Long.MIN_VALUE);
			Assert.fail("expecting MathException");
		} catch (MathException expected) {
			// expected
			assertEquals(expected.getType(), ExceptionTypes.MAE__GCD_OVERFLOW_64_BITS);
		}
		try {
			// gcd(Long.MIN_VALUE, Long.MIN_VALUE) > Long.MAX_VALUE
			Arithmetic.gcd(Long.MIN_VALUE, Long.MIN_VALUE);
			Assert.fail("expecting MathException");
		} catch (MathException expected) {
			// expected
			assertEquals(expected.getType(), ExceptionTypes.MAE__GCD_OVERFLOW_64_BITS);
		}
	}

	@Test
	public void testLcm() {
		int a = 30;
		int b = 50;
		int c = 77;

		Assert.assertEquals(0, Arithmetic.lcm(0, b));
		Assert.assertEquals(0, Arithmetic.lcm(a, 0));
		Assert.assertEquals(b, Arithmetic.lcm(1, b));
		Assert.assertEquals(a, Arithmetic.lcm(a, 1));
		Assert.assertEquals(150, Arithmetic.lcm(a, b));
		Assert.assertEquals(150, Arithmetic.lcm(-a, b));
		Assert.assertEquals(150, Arithmetic.lcm(a, -b));
		Assert.assertEquals(150, Arithmetic.lcm(-a, -b));
		Assert.assertEquals(2310, Arithmetic.lcm(a, c));

		// Assert that no intermediate value overflows:
		// The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
		Assert.assertEquals((1 << 20)
				* 15,
				Arithmetic.lcm((1 << 20)
						* 3,
						(1 << 20)
								* 5));

		// Special case
		Assert.assertEquals(0, Arithmetic.lcm(0, 0));

		try {
			// lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
			Arithmetic.lcm(Integer.MIN_VALUE, 1);
			Assert.fail("Expecting MathException");
		} catch (MathException expected) {
			// expected
			assertEquals(expected.getType(), ExceptionTypes.MAE__LCM_OVERFLOW_32_BITS);
		}

		try {
			// lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
			Arithmetic.lcm(Integer.MIN_VALUE, 1 << 20);
			Assert.fail("Expecting MathException");
		} catch (MathException expected) {
			// expected
			assertEquals(expected.getType(), ExceptionTypes.MAE__LCM_OVERFLOW_32_BITS);
		}

		try {
			Arithmetic.lcm(Integer.MAX_VALUE, Integer.MAX_VALUE
					- 1);
			Assert.fail("Expecting MathException");
		} catch (MathException expected) {
			// expected
			assertEquals(expected.getType(), ExceptionTypes.MAE__INTEGER_OVERFLOW);
		}
	}

	@Test
	public void testLcmLong() {
		long a = 30;
		long b = 50;
		long c = 77;

		Assert.assertEquals(0, Arithmetic.lcm(0, b));
		Assert.assertEquals(0, Arithmetic.lcm(a, 0));
		Assert.assertEquals(b, Arithmetic.lcm(1, b));
		Assert.assertEquals(a, Arithmetic.lcm(a, 1));
		Assert.assertEquals(150, Arithmetic.lcm(a, b));
		Assert.assertEquals(150, Arithmetic.lcm(-a, b));
		Assert.assertEquals(150, Arithmetic.lcm(a, -b));
		Assert.assertEquals(150, Arithmetic.lcm(-a, -b));
		Assert.assertEquals(2310, Arithmetic.lcm(a, c));

		Assert.assertEquals(Long.MAX_VALUE, Arithmetic.lcm(60247241209L, 153092023L));

		// Assert that no intermediate value overflows:
		// The naive implementation of lcm(a,b) would be (a*b)/gcd(a,b)
		Assert.assertEquals((1L << 50)
				* 15,
				Arithmetic.lcm((1L << 45)
						* 3,
						(1L << 50)
								* 5));

		// Special case
		Assert.assertEquals(0L, Arithmetic.lcm(0L, 0L));

		try {
			// lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
			Arithmetic.lcm(Long.MIN_VALUE, 1);
			Assert.fail("Expecting MathException");
		} catch (MathException expected) {
			// expected
		}

		try {
			// lcm == abs(MIN_VALUE) cannot be represented as a nonnegative int
			Arithmetic.lcm(Long.MIN_VALUE, 1 << 20);
			Assert.fail("Expecting MathException");
		} catch (MathException expected) {
			// expected
		}

		Assert.assertEquals((long) Integer.MAX_VALUE
				* (Integer.MAX_VALUE
						- 1),
				Arithmetic.lcm((long) Integer.MAX_VALUE, Integer.MAX_VALUE
						- 1));
		try {
			Arithmetic.lcm(Long.MAX_VALUE, Long.MAX_VALUE
					- 1);
			Assert.fail("Expecting MathException");
		} catch (MathException expected) {
			// expected
		}
	}

	@Test
	public void testMulAndCheck() {
		int big = Integer.MAX_VALUE;
		int bigNeg = Integer.MIN_VALUE;
		Assert.assertEquals(big, Arithmetic.mulAndCheck(big, 1));
		try {
			Arithmetic.mulAndCheck(big, 2);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
		}
		try {
			Arithmetic.mulAndCheck(bigNeg, 2);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
		}
	}

	@Test
	public void testMulAndCheckLong() {
		long max = Long.MAX_VALUE;
		long min = Long.MIN_VALUE;
		Assert.assertEquals(max, Arithmetic.mulAndCheck(max, 1L));
		Assert.assertEquals(min, Arithmetic.mulAndCheck(min, 1L));
		Assert.assertEquals(0L, Arithmetic.mulAndCheck(max, 0L));
		Assert.assertEquals(0L, Arithmetic.mulAndCheck(min, 0L));
		Assert.assertEquals(max, Arithmetic.mulAndCheck(1L, max));
		Assert.assertEquals(min, Arithmetic.mulAndCheck(1L, min));
		Assert.assertEquals(0L, Arithmetic.mulAndCheck(0L, max));
		Assert.assertEquals(0L, Arithmetic.mulAndCheck(0L, min));
		Assert.assertEquals(1L, Arithmetic.mulAndCheck(-1L, -1L));
		Assert.assertEquals(min, Arithmetic.mulAndCheck(min
				/ 2, 2));
		testMulAndCheckLongFailure(max, 2L);
		testMulAndCheckLongFailure(2L, max);
		testMulAndCheckLongFailure(min, 2L);
		testMulAndCheckLongFailure(2L, min);
		testMulAndCheckLongFailure(min, -1L);
		testMulAndCheckLongFailure(-1L, min);
	}

	@Test
	public void testSubAndCheck() {
		int big = Integer.MAX_VALUE;
		int bigNeg = Integer.MIN_VALUE;
		Assert.assertEquals(big, Arithmetic.subAndCheck(big, 0));
		Assert.assertEquals(bigNeg
				+ 1, Arithmetic.subAndCheck(bigNeg, -1));
		Assert.assertEquals(-1, Arithmetic.subAndCheck(bigNeg, -big));
		try {
			Arithmetic.subAndCheck(big, -1);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
		}
		try {
			Arithmetic.subAndCheck(bigNeg, 1);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
		}
	}

	@Test
	public void testSubAndCheckErrorMessage() {
		int big = Integer.MAX_VALUE;
		try {
			Arithmetic.subAndCheck(big, -1);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
			Assert.assertTrue(ex.getMessage().length() > 1);
		}
	}

	@Test
	public void testSubAndCheckLong() {
		long max = Long.MAX_VALUE;
		long min = Long.MIN_VALUE;
		Assert.assertEquals(max, Arithmetic.subAndCheck(max, 0));
		Assert.assertEquals(min, Arithmetic.subAndCheck(min, 0));
		Assert.assertEquals(-max, Arithmetic.subAndCheck(0, max));
		Assert.assertEquals(min
				+ 1, Arithmetic.subAndCheck(min, -1));
		// min == -1-max
		Assert.assertEquals(-1, Arithmetic.subAndCheck(-max
				- 1, -max));
		Assert.assertEquals(max, Arithmetic.subAndCheck(-1, -1
				- max));
		testSubAndCheckLongFailure(0L, min);
		testSubAndCheckLongFailure(max, -1L);
		testSubAndCheckLongFailure(min, 1L);
	}

	@Test
	public void testPow() {

		Assert.assertEquals(1801088541, Arithmetic.pow(21, 7));
		Assert.assertEquals(1, Arithmetic.pow(21, 0));
		try {
			Arithmetic.pow(21, -7);
			Assert.fail("Expecting MathException");
		} catch (MathException e) {
			// expected behavior
		}

		Assert.assertEquals(1801088541, Arithmetic.pow(21, 7));
		Assert.assertEquals(1, Arithmetic.pow(21, 0));
		try {
			Arithmetic.pow(21, -7);
			Assert.fail("Expecting MathException");
		} catch (MathException e) {
			// expected behavior
		}

		Assert.assertEquals(1801088541l, Arithmetic.pow(21l, 7));
		Assert.assertEquals(1l, Arithmetic.pow(21l, 0));
		try {
			Arithmetic.pow(21l, -7);
			Assert.fail("Expecting MathException");
		} catch (MathException e) {
			// expected behavior
		}

		BigInteger twentyOne = BigInteger.valueOf(21l);
		Assert.assertEquals(BigInteger.valueOf(1801088541l), Arithmetic.pow(twentyOne, 7));
		Assert.assertEquals(BigInteger.ONE, Arithmetic.pow(twentyOne, 0));
		try {
			Arithmetic.pow(twentyOne, -7);
			Assert.fail("Expecting MathException");
		} catch (MathException e) {
			// expected behavior
		}

		Assert.assertEquals(BigInteger.valueOf(1801088541l), Arithmetic.pow(twentyOne, 7l));
		Assert.assertEquals(BigInteger.ONE, Arithmetic.pow(twentyOne, 0l));
		try {
			Arithmetic.pow(twentyOne, -7l);
			Assert.fail("Expecting MathException");
		} catch (MathException e) {
			// expected behavior
		}

		Assert.assertEquals(BigInteger.valueOf(1801088541l),
				Arithmetic.pow(twentyOne, BigInteger.valueOf(7l)));
		Assert.assertEquals(BigInteger.ONE, Arithmetic.pow(twentyOne, BigInteger.ZERO));
		try {
			Arithmetic.pow(twentyOne, BigInteger.valueOf(-7l));
			Assert.fail("Expecting MathException");
		} catch (MathException e) {
			// expected behavior
		}

		BigInteger bigOne = new BigInteger("1543786922199448028351389769265814882661837148"
				+ "4763915343722775611762713982220306372888519211"
				+ "560905579993523402015636025177602059044911261");
		Assert.assertEquals(bigOne, Arithmetic.pow(twentyOne, 103));
		Assert.assertEquals(bigOne, Arithmetic.pow(twentyOne, 103l));
		Assert.assertEquals(bigOne, Arithmetic.pow(twentyOne, BigInteger.valueOf(103l)));

	}

	@Test(expected = MathException.class)
	public void testPowIntOverflow() {
		Arithmetic.pow(21, 8);
	}

	@Test
	public void testPowInt() {
		final int base = 21;

		Assert.assertEquals(85766121L, Arithmetic.pow(base, 6));
		Assert.assertEquals(1801088541L, Arithmetic.pow(base, 7));
	}

	@Test(expected = MathException.class)
	public void testPowNegativeIntOverflow() {
		Arithmetic.pow(-21, 8);
	}

	@Test
	public void testPowNegativeInt() {
		final int base = -21;

		Assert.assertEquals(85766121, Arithmetic.pow(base, 6));
		Assert.assertEquals(-1801088541, Arithmetic.pow(base, 7));
	}

	@Test
	public void testPowMinusOneInt() {
		final int base = -1;
		for (int i = 0; i < 100; i++) {
			final int pow = Arithmetic.pow(base, i);
			Assert.assertEquals("i: "
					+ i,
					i
							% 2 == 0 ? 1 : -1,
					pow);
		}
	}

	@Test
	public void testPowOneInt() {
		final int base = 1;
		for (int i = 0; i < 100; i++) {
			final int pow = Arithmetic.pow(base, i);
			Assert.assertEquals("i: "
					+ i, 1, pow);
		}
	}

	@Test(expected = MathException.class)
	public void testPowLongOverflow() {
		Arithmetic.pow(21, 15);
	}

	@Test
	public void testPowLong() {
		final long base = 21;

		Assert.assertEquals(154472377739119461L, Arithmetic.pow(base, 13));
		Assert.assertEquals(3243919932521508681L, Arithmetic.pow(base, 14));
	}

	@Test(expected = MathException.class)
	public void testPowNegativeLongOverflow() {
		Arithmetic.pow(-21L, 15);
	}

	@Test
	public void testPowNegativeLong() {
		final long base = -21;

		Assert.assertEquals(-154472377739119461L, Arithmetic.pow(base, 13));
		Assert.assertEquals(3243919932521508681L, Arithmetic.pow(base, 14));
	}

	@Test
	public void testPowMinusOneLong() {
		final long base = -1;
		for (int i = 0; i < 100; i++) {
			final long pow = Arithmetic.pow(base, i);
			Assert.assertEquals("i: "
					+ i,
					i
							% 2 == 0 ? 1 : -1,
					pow);
		}
	}

	@Test
	public void testPowOneLong() {
		final long base = 1;
		for (int i = 0; i < 100; i++) {
			final long pow = Arithmetic.pow(base, i);
			Assert.assertEquals("i: "
					+ i, 1, pow);
		}
	}

	@Test
	public void testIsPowerOfTwo() {
		final int n = 1025;
		final boolean[] expected = new boolean[n];
		Arrays.fill(expected, false);
		for (int i = 1; i < expected.length; i *= 2) {
			expected[i] = true;
		}
		for (int i = 0; i < expected.length; i++) {
			final boolean actual = Arithmetic.isPowerOfTwo(i);
			Assert.assertTrue(Integer.toString(i), actual == expected[i]);
		}
	}

	private void testAddAndCheckLongFailure(long a, long b) {
		try {
			Arithmetic.addAndCheck(a, b);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
			// success
		}
	}

	private void testMulAndCheckLongFailure(long a, long b) {
		try {
			Arithmetic.mulAndCheck(a, b);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
			// success
		}
	}

	private void testSubAndCheckLongFailure(long a, long b) {
		try {
			Arithmetic.subAndCheck(a, b);
			Assert.fail("Expecting MathException");
		} catch (MathException ex) {
			// success
		}
	}

	/**
	 * Testing helper method.
	 * 
	 * @return an array of int numbers containing corner cases:
	 *         <ul>
	 *         <li>values near the beginning of int range,</li>
	 *         <li>values near the end of int range,</li>
	 *         <li>values near zero</li>
	 *         <li>and some randomly distributed values.</li>
	 *         </ul>
	 */
	private static int[] getIntSpecialCases() {
		int ints[] = new int[100];
		int i = 0;
		ints[i++] = Integer.MAX_VALUE;
		ints[i++] = Integer.MAX_VALUE
				- 1;
		ints[i++] = 100;
		ints[i++] = 101;
		ints[i++] = 102;
		ints[i++] = 300;
		ints[i++] = 567;
		for (int j = 0; j < 20; j++) {
			ints[i++] = j;
		}
		for (int j = i
				- 1; j >= 0; j--) {
			ints[i++] = ints[j] > 0 ? -ints[j] : Integer.MIN_VALUE;
		}
		java.util.Random r = new java.util.Random(System.nanoTime());
		for (; i < ints.length;) {
			ints[i++] = r.nextInt();
		}
		return ints;
	}

	/**
	 * Testing helper method.
	 * 
	 * @return an array of long numbers containing corner cases:
	 *         <ul>
	 *         <li>values near the beginning of long range,</li>
	 *         <li>values near the end of long range,</li>
	 *         <li>values near the beginning of int range,</li>
	 *         <li>values near the end of int range,</li>
	 *         <li>values near zero</li>
	 *         <li>and some randomly distributed values.</li>
	 *         </ul>
	 */
	private static long[] getLongSpecialCases() {
		long longs[] = new long[100];
		int i = 0;
		longs[i++] = Long.MAX_VALUE;
		longs[i++] = Long.MAX_VALUE
				- 1L;
		longs[i++] = (long) Integer.MAX_VALUE
				+ 1L;
		longs[i++] = Integer.MAX_VALUE;
		longs[i++] = Integer.MAX_VALUE
				- 1;
		longs[i++] = 100L;
		longs[i++] = 101L;
		longs[i++] = 102L;
		longs[i++] = 300L;
		longs[i++] = 567L;
		for (int j = 0; j < 20; j++) {
			longs[i++] = j;
		}
		for (int j = i
				- 1; j >= 0; j--) {
			longs[i++] = longs[j] > 0L ? -longs[j] : Long.MIN_VALUE;
		}
		java.util.Random r = new java.util.Random(System.nanoTime());
		for (; i < longs.length;) {
			longs[i++] = r.nextLong();
		}
		return longs;
	}

	private static long toUnsignedLong(int number) {
		return number < 0 ? 0x100000000L
				+ (long) number : (long) number;
	}

	private static int remainderUnsignedExpected(int dividend, int divisor) {
		return (int) remainderUnsignedExpected(toUnsignedLong(dividend), toUnsignedLong(divisor));
	}

	private static int divideUnsignedExpected(int dividend, int divisor) {
		return (int) divideUnsignedExpected(toUnsignedLong(dividend), toUnsignedLong(divisor));
	}

	private static BigInteger toUnsignedBigInteger(long number) {
		return number < 0L ? BigInteger.ONE.shiftLeft(64).add(BigInteger.valueOf(number))
				: BigInteger.valueOf(number);
	}

	private static long remainderUnsignedExpected(long dividend, long divisor) {
		return toUnsignedBigInteger(dividend).remainder(toUnsignedBigInteger(divisor)).longValue();
	}

	private static long divideUnsignedExpected(long dividend, long divisor) {
		return toUnsignedBigInteger(dividend).divide(toUnsignedBigInteger(divisor)).longValue();
	}

	@Test(timeout = 5000L)
	public void testRemainderUnsignedInt() {
		Assert.assertEquals(36, Arithmetic.remainderUnsigned(-2147479015, 63));
		Assert.assertEquals(6, Arithmetic.remainderUnsigned(-2147479015, 25));
	}

	@Test(timeout = 5000L)
	public void testRemainderUnsignedIntSpecialCases() {
		int ints[] = getIntSpecialCases();
		for (int dividend : ints) {
			for (int divisor : ints) {
				if (divisor == 0) {
					try {
						Arithmetic.remainderUnsigned(dividend, divisor);
						Assert.fail("Should have failed with ArithmeticException: division by zero");
					} catch (ArithmeticException e) {
						// Success.
					}
				} else {
					Assert.assertEquals(remainderUnsignedExpected(dividend, divisor),
							Arithmetic.remainderUnsigned(dividend, divisor));
				}
			}
		}
	}

	@Test(timeout = 5000L)
	public void testRemainderUnsignedLong() {
		Assert.assertEquals(48L, Arithmetic.remainderUnsigned(-2147479015L, 63L));
	}

	@Test // (timeout=5000L)
	public void testRemainderUnsignedLongSpecialCases() {
		long longs[] = getLongSpecialCases();
		for (long dividend : longs) {
			for (long divisor : longs) {
				if (divisor == 0L) {
					try {
						Arithmetic.remainderUnsigned(dividend, divisor);
						Assert.fail("Should have failed with ArithmeticException: division by zero");
					} catch (ArithmeticException e) {
						// Success.
					}
				} else {
					Assert.assertEquals(remainderUnsignedExpected(dividend, divisor),
							Arithmetic.remainderUnsigned(dividend, divisor));
				}
			}
		}
	}

	@Test(timeout = 5000L)
	public void testDivideUnsignedInt() {
		Assert.assertEquals(34087115, Arithmetic.divideUnsigned(-2147479015, 63));
		Assert.assertEquals(85899531, Arithmetic.divideUnsigned(-2147479015, 25));
		Assert.assertEquals(2147483646, Arithmetic.divideUnsigned(-3, 2));
		Assert.assertEquals(330382098, Arithmetic.divideUnsigned(-16, 13));
		Assert.assertEquals(306783377, Arithmetic.divideUnsigned(-16, 14));
		Assert.assertEquals(2, Arithmetic.divideUnsigned(-1, 2147483647));
		Assert.assertEquals(2, Arithmetic.divideUnsigned(-2, 2147483647));
		Assert.assertEquals(1, Arithmetic.divideUnsigned(-3, 2147483647));
		Assert.assertEquals(1, Arithmetic.divideUnsigned(-16, 2147483647));
		Assert.assertEquals(1, Arithmetic.divideUnsigned(-16, 2147483646));
	}

	@Test(timeout = 5000L)
	public void testDivideUnsignedIntSpecialCases() {
		int ints[] = getIntSpecialCases();
		for (int dividend : ints) {
			for (int divisor : ints) {
				if (divisor == 0) {
					try {
						Arithmetic.divideUnsigned(dividend, divisor);
						Assert.fail("Should have failed with ArithmeticException: division by zero");
					} catch (ArithmeticException e) {
						// Success.
					}
				} else {
					Assert.assertEquals(divideUnsignedExpected(dividend, divisor),
							Arithmetic.divideUnsigned(dividend, divisor));
				}
			}
		}
	}

	@Test(timeout = 5000L)
	public void testDivideUnsignedLong() {
		Assert.assertEquals(292805461453366231L, Arithmetic.divideUnsigned(-2147479015L, 63L));
	}

	@Test(timeout = 5000L)
	public void testDivideUnsignedLongSpecialCases() {
		long longs[] = getLongSpecialCases();
		for (long dividend : longs) {
			for (long divisor : longs) {
				if (divisor == 0L) {
					try {
						Arithmetic.divideUnsigned(dividend, divisor);
						Assert.fail("Should have failed with ArithmeticException: division by zero");
					} catch (ArithmeticException e) {
						// Success.
					}
				} else {
					Assert.assertEquals(divideUnsignedExpected(dividend, divisor),
							Arithmetic.divideUnsigned(dividend, divisor));
				}
			}
		}
	}
}
