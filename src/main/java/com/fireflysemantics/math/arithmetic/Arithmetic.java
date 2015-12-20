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

import static com.fireflysemantics.math.arithmetic.exception.ArithmeticExceptionKeys.X;
import static com.fireflysemantics.math.arithmetic.exception.ArithmeticExceptionKeys.Y;
import static com.fireflysemantics.math.exception.ExceptionTypes.MAE__GCD_OVERFLOW_32_BITS;
import static com.fireflysemantics.math.exception.ExceptionTypes.MAE__GCD_OVERFLOW_64_BITS;
import static com.fireflysemantics.math.exception.ExceptionTypes.MAE__INTEGER_OVERFLOW;
import static com.fireflysemantics.math.exception.ExceptionTypes.MAE__LCM_OVERFLOW_32_BITS;
import static com.fireflysemantics.math.exception.ExceptionTypes.MAE__LCM_OVERFLOW_64_BITS;
import static com.fireflysemantics.math.exception.ExceptionTypes.MAE__LONG_OVERFLOW;
import static com.fireflysemantics.math.exception.ExceptionTypes.MAE__OVERFLOW_IN_ADDITION;
import static com.fireflysemantics.math.exception.ExceptionTypes.MAE__OVERFLOW_IN_SUBTRACTION;
import static com.fireflysemantics.math.exception.ExceptionTypes.NOT_POSITIVE;

import java.math.BigInteger;

import com.fireflysemantics.math.exception.MathException;

/**
 * Arithmetic utility functions complementing {@link Math}.
 */

public final class Arithmetic {

	/** Private constructor. */
	private Arithmetic() {
		super();
	}

	/**
	 * Add two integers, checking for overflow.
	 *
	 * @param x
	 *            an addend
	 * @param y
	 *            an addend
	 * @return the sum {@code x+y}
	 * @throws MathException
	 *             Of type {@code MAE__OVERFLOW_IN_ADDITION} if the result can
	 *             not be represented as an {@code int}.
	 */
	public static int addAndCheck(int x, int y) throws MathException {
		long s = (long) x
				+ (long) y;
		if (s < Integer.MIN_VALUE
				|| s > Integer.MAX_VALUE) {
			throw new MathException(MAE__OVERFLOW_IN_ADDITION).put(X, x).put(Y, y);
		}
		return (int) s;
	}

	/**
	 * Add two long integers, checking for overflow.
	 *
	 * @param x
	 *            an addend
	 * @param y
	 *            an addend
	 * @return the sum {@code x+y}
	 * @throws MathException
	 *             Of type {@code MAE__OVERFLOW_IN_ADDITION} if the result can
	 *             not be represented as an {@code long}.
	 */
	public static long addAndCheck(long x, long y) throws MathException {
		final long result = x
				+ y;
		if (!((x
				^ y) < 0
				| (x
						^ result) >= 0)) {
			throw new MathException(MAE__OVERFLOW_IN_ADDITION).put(X, x).put(Y, y);
		}
		return result;
	}

	/**
	 * Computes the greatest common divisor of the absolute value of two
	 * numbers, using a modified version of the "binary gcd" method. See Knuth
	 * 4.5.2 algorithm B. The algorithm is due to Josef Stein (1961). <br/>
	 * Special cases:
	 * <ul>
	 * <li>The invocations {@code gcd(Integer.MIN_VALUE, Integer.MIN_VALUE)},
	 * {@code gcd(Integer.MIN_VALUE, 0)} and {@code gcd(0, Integer.MIN_VALUE)}
	 * throw an {@code MathException[MAE__GCD_OVERFLOW_32_BITS]}, because the
	 * result would be 2^31, which is too large for an int value.</li>
	 * <li>The result of {@code gcd(x, x)}, {@code gcd(0, x)} and
	 * {@code gcd(x, 0)} is the absolute value of {@code x}, except for the
	 * special cases above.</li>
	 * <li>The invocation {@code gcd(0, 0)} is the only one which returns
	 * {@code 0}.</li>
	 * </ul>
	 *
	 * @param x
	 *            Number.
	 * @param y
	 *            Number.
	 * @return the greatest common divisor (never negative).
	 * @throws MathException
	 *             Of type {@code MAE__GCD_OVERFLOW_32_BITS} if the result
	 *             cannot be represented as a non-negative {@code int} value.
	 */
	public static int gcd(int x, int y) throws MathException {
		int a = x;
		int b = y;
		if (a == 0
				|| b == 0) {
			if (a == Integer.MIN_VALUE
					|| b == Integer.MIN_VALUE) {
				throw new MathException(MAE__GCD_OVERFLOW_32_BITS).put(X, x).put(Y, y);
			}
			return Math.abs(a
					+ b);
		}

		long al = a;
		long bl = b;
		boolean useLong = false;
		if (a < 0) {
			if (Integer.MIN_VALUE == a) {
				useLong = true;
			} else {
				a = -a;
			}
			al = -al;
		}
		if (b < 0) {
			if (Integer.MIN_VALUE == b) {
				useLong = true;
			} else {
				b = -b;
			}
			bl = -bl;
		}
		if (useLong) {
			if (al == bl) {
				throw new MathException(MAE__GCD_OVERFLOW_32_BITS).put(X, x).put(Y, y);
			}
			long blbu = bl;
			bl = al;
			al = blbu
					% al;
			if (al == 0) {
				if (bl > Integer.MAX_VALUE) {
					throw new MathException(MAE__GCD_OVERFLOW_32_BITS).put(X, x).put(Y, y);
				}
				return (int) bl;
			}
			blbu = bl;

			// Now "al" and "bl" fit in an "int".
			b = (int) al;
			a = (int) (blbu
					% al);
		}

		return gcdPositive(a, b);
	}

	/**
	 * Computes the greatest common divisor of two <em>positive</em> numbers
	 * (this precondition is <em>not</em> checked and the result is undefined if
	 * not fulfilled) using the "binary gcd" method which avoids division and
	 * modulo operations. See Knuth 4.5.2 algorithm B. The algorithm is due to
	 * Josef Stein (1961). <br/>
	 * Special cases:
	 * <ul>
	 * <li>The result of {@code gcd(x, x)}, {@code gcd(0, x)} and
	 * {@code gcd(x, 0)} is the value of {@code x}.</li>
	 * <li>The invocation {@code gcd(0, 0)} is the only one which returns
	 * {@code 0}.</li>
	 * </ul>
	 *
	 * @param x
	 *            Positive number.
	 * @param y
	 *            Positive number.
	 * @return the greatest common divisor.
	 */
	private static int gcdPositive(int x, int y) {
		if (x == 0) {
			return y;
		} else if (y == 0) {
			return x;
		}

		// Make "a" and "b" odd, keeping track of common power of 2.
		final int aTwos = Integer.numberOfTrailingZeros(x);
		x >>= aTwos;
		final int bTwos = Integer.numberOfTrailingZeros(y);
		y >>= bTwos;
		final int shift = Math.min(aTwos, bTwos);

		// "a" and "b" are positive.
		// If a > b then "gdc(a, b)" is equal to "gcd(a - b, b)".
		// If a < b then "gcd(a, b)" is equal to "gcd(b - a, a)".
		// Hence, in the successive iterations:
		// "a" becomes the absolute difference of the current values,
		// "b" becomes the minimum of the current values.
		while (x != y) {
			final int delta = x
					- y;
			y = Math.min(x, y);
			x = Math.abs(delta);

			// Remove any power of 2 in "a" ("b" is guaranteed to be odd).
			x >>= Integer.numberOfTrailingZeros(x);
		}

		// Recover the common power of 2.
		return x << shift;
	}

	/**
	 * <p>
	 * Gets the greatest common divisor of the absolute value of two numbers,
	 * using the "binary gcd" method which avoids division and modulo
	 * operations. See Knuth 4.5.2 algorithm B. This algorithm is due to Josef
	 * Stein (1961).
	 * </p>
	 * Special cases:
	 * <ul>
	 * <li>The invocations {@code gcd(Long.MIN_VALUE, Long.MIN_VALUE)},
	 * {@code gcd(Long.MIN_VALUE, 0L)} and {@code gcd(0L, Long.MIN_VALUE)} throw
	 * an {@code ArithmeticException}, because the result would be 2^63, which
	 * is too large for a long value.</li>
	 * <li>The result of {@code gcd(x, x)}, {@code gcd(0L, x)} and
	 * {@code gcd(x, 0L)} is the absolute value of {@code x}, except for the
	 * special cases above.
	 * <li>The invocation {@code gcd(0L, 0L)} is the only one which returns
	 * {@code 0L}.</li>
	 * </ul>
	 *
	 * @param x
	 *            Number.
	 * @param y
	 *            Number.
	 * @return the greatest common divisor, never negative.
	 * @throws MathException
	 *             Of type {@code MAE__GCD_OVERFLOW_64_BITS} if the result
	 *             cannot be represented as a non-negative {@code long} value.
	 */
	public static long gcd(final long x, final long y) throws MathException {
		long u = x;
		long v = y;
		if ((u == 0)
				|| (v == 0)) {
			if ((u == Long.MIN_VALUE)
					|| (v == Long.MIN_VALUE)) {
				throw new MathException(MAE__GCD_OVERFLOW_64_BITS).put(X, x).put(Y, y);
			}
			return Math.abs(u)
					+ Math.abs(v);
		}
		// keep u and v negative, as negative integers range down to
		// -2^63, while positive numbers can only be as large as 2^63-1
		// (i.e. we can't necessarily negate a negative number without
		// overflow)
		/* assert u!=0 && v!=0; */
		if (u > 0) {
			u = -u;
		} // make u negative
		if (v > 0) {
			v = -v;
		} // make v negative
			// B1. [Find power of 2]
		int k = 0;
		while ((u
				& 1) == 0
				&& (v
						& 1) == 0
				&& k < 63) { // while u and v are
								// both even...
			u /= 2;
			v /= 2;
			k++; // cast out twos.
		}
		if (k == 63) {
			throw new MathException(MAE__GCD_OVERFLOW_64_BITS).put(X, x).put(Y, y);
		}
		// B2. Initialize: u and v have been divided by 2^k and at least
		// one is odd.
		long t = ((u
				& 1) == 1) ? v
						: -(u
								/ 2)/* B3 */;
		// t negative: u was odd, v may be even (t replaces v)
		// t positive: u was even, v is odd (t replaces u)
		do {
			/* assert u<0 && v<0; */
			// B4/B3: cast out twos from t.
			while ((t
					& 1) == 0) { // while t is even..
				t /= 2; // cast out twos
			}
			// B5 [reset max(u,v)]
			if (t > 0) {
				u = -t;
			} else {
				v = t;
			}
			// B6/B3. at this point both u and v should be odd.
			t = (v
					- u)
					/ 2;
			// |u| larger: t positive (replace u)
			// |v| larger: t negative (replace v)
		} while (t != 0);
		return -u
				* (1L << k); // gcd is u*2^k
	}

	/**
	 * <p>
	 * Returns the least common multiple of the absolute value of two numbers,
	 * using the formula {@code lcm(a,b) = (a / gcd(a,b)) * b}.
	 * </p>
	 * Special cases:
	 * <ul>
	 * <li>The invocations {@code lcm(Integer.MIN_VALUE, n)} and
	 * {@code lcm(n, Integer.MIN_VALUE)}, where {@code abs(n)} is a power of 2,
	 * throw an {@code ArithmeticException}, because the result would be 2^31,
	 * which is too large for an int value.</li>
	 * <li>The result of {@code lcm(0, x)} and {@code lcm(x, 0)} is {@code 0}
	 * for any {@code x}.
	 * </ul>
	 *
	 * @param x
	 *            Number.
	 * @param y
	 *            Number.
	 * @return the least common multiple, never negative.
	 * @throws MathException
	 *             Of type {@code MAE__LCM_OVERFLOW_32_BITS} if the result
	 *             cannot be represented as a non-negative {@code int} value.
	 */
	public static int lcm(int x, int y) throws MathException {
		if (x == 0
				|| y == 0) {
			return 0;
		}
		int lcm = Math.abs(Arithmetic.mulAndCheck(x
				/ gcd(x, y), y));
		if (lcm == Integer.MIN_VALUE) {
			throw new MathException(MAE__LCM_OVERFLOW_32_BITS).put(X, x).put(Y, y);
		}
		return lcm;
	}

	/**
	 * <p>
	 * Returns the least common multiple of the absolute value of two numbers,
	 * using the formula {@code lcm(a,b) = (a / gcd(a,b)) * b}.
	 * </p>
	 * Special cases:
	 * <ul>
	 * <li>The invocations {@code lcm(Long.MIN_VALUE, n)} and
	 * {@code lcm(n, Long.MIN_VALUE)}, where {@code abs(n)} is a power of 2,
	 * throw an {@code ArithmeticException}, because the result would be 2^63,
	 * which is too large for an int value.</li>
	 * <li>The result of {@code lcm(0L, x)} and {@code lcm(x, 0L)} is {@code 0L}
	 * for any {@code x}.
	 * </ul>
	 *
	 * @param x
	 *            Number.
	 * @param y
	 *            Number.
	 * @return the least common multiple, never negative.
	 * @throws MathException
	 *             Of type {@code MAE__LCM_OVERFLOW_64_BITS} if the result
	 *             cannot be represented as a non-negative {@code long} value.
	 */
	public static long lcm(long x, long y) throws MathException {
		if (x == 0
				|| y == 0) {
			return 0;
		}
		long lcm = Math.abs(Arithmetic.mulAndCheck(x
				/ gcd(x, y), y));
		if (lcm == Long.MIN_VALUE) {
			throw new MathException(MAE__LCM_OVERFLOW_64_BITS).put(X, x).put(Y, y);
		}
		return lcm;
	}

	/**
	 * Multiply two integers, checking for overflow.
	 *
	 * @param x
	 *            Factor.
	 * @param y
	 *            Factor.
	 * @return the product {@code x * y}.
	 * @throws MathException[MAE__INTEGER_OVERFLOW]
	 *             if the result can not be represented as an {@code int}.
	 */
	public static int mulAndCheck(int x, int y) throws MathException {
		long m = ((long) x)
				* ((long) y);
		if (m < Integer.MIN_VALUE
				|| m > Integer.MAX_VALUE) {
			throw new MathException(MAE__INTEGER_OVERFLOW);

		}
		return (int) m;
	}

	/**
	 * Multiply two long integers, checking for overflow.
	 *
	 * @param x
	 *            Factor.
	 * @param y
	 *            Factor.
	 * @return the product {@code a * b}.
	 * @throws MathException
	 *             Of type {@code MAE__LONG_OVERFLOW} if the result can not be
	 *             represented as a {@code long}.
	 */
	public static long mulAndCheck(long x, long y) throws MathException {
		long ret;
		if (x > y) {
			// use symmetry to reduce boundary cases
			ret = mulAndCheck(y, x);
		} else {
			if (x < 0) {
				if (y < 0) {
					// check for positive overflow with negative a, negative b
					if (x >= Long.MAX_VALUE
							/ y) {
						ret = x
								* y;
					} else {
						throw new MathException(MAE__LONG_OVERFLOW);
					}
				} else if (y > 0) {
					// check for negative overflow with negative a, positive b
					if (Long.MIN_VALUE
							/ y <= x) {
						ret = x
								* y;
					} else {
						throw new MathException(MAE__LONG_OVERFLOW);
					}
				} else {
					// assert b == 0
					ret = 0;
				}
			} else if (x > 0) {
				// assert a > 0
				// assert b > 0

				// check for positive overflow with positive a, positive b
				if (x <= Long.MAX_VALUE
						/ y) {
					ret = x
							* y;
				} else {
					throw new MathException(MAE__LONG_OVERFLOW);
				}
			} else {
				// assert a == 0
				ret = 0;
			}
		}
		return ret;
	}

	/**
	 * Subtract two integers, checking for overflow.
	 *
	 * @param x
	 *            Minuend.
	 * @param y
	 *            Subtrahend.
	 * @return the difference {@code x - y}.
	 * @throws MathException
	 *             Of type {@code MAE__OVERFLOW_IN_SUBTRACTION} if the result
	 *             can not be represented as an {@code int}.
	 */
	public static int subAndCheck(int x, int y) throws MathException {
		long s = (long) x
				- (long) y;
		if (s < Integer.MIN_VALUE
				|| s > Integer.MAX_VALUE) {
			throw new MathException(MAE__OVERFLOW_IN_SUBTRACTION).put(X, x).put(Y, y);
		}
		return (int) s;
	}

	/**
	 * Subtract two long integers, checking for overflow.
	 *
	 * @param x
	 *            Value.
	 * @param y
	 *            Value.
	 * @return the difference {@code x - y}.
	 * @throws MathException
	 *             Of type {@code MAE__OVERFLOW_IN_SUBTRACTION} if the result
	 *             can not be represented as a {@code long}.
	 */
	public static long subAndCheck(long x, long y) throws MathException {
		long ret;
		if (y == Long.MIN_VALUE) {
			if (x < 0) {
				ret = x
						- y;
			} else {
				throw new MathException(MAE__OVERFLOW_IN_SUBTRACTION).put(X, x).put(Y, y);
			}
		} else {
			// use additive inverse
			try {
				ret = addAndCheck(x, -y);
			} catch (MathException e) {
				MathException me = new MathException(MAE__OVERFLOW_IN_SUBTRACTION).put(X, x).put(Y, y);
				me.initCause(e);
				throw me;
			}
		}
		return ret;
	}

	/**
	 * Raise an int to an int power.
	 *
	 * @param x
	 *            Number to raise.
	 * @param y
	 *            Exponent (must be positive or zero).
	 * @return \( x^y \)
	 * @throws MathException
	 *             if {@code y < 0}.
	 * @throws MathException
	 *             Of type {@code NOT_POSITIVE_EXCEPTION} if the result would
	 *             overflow.
	 */
	public static int pow(final int x, final int y) throws MathException {
		if (y < 0) {
			throw new MathException(NOT_POSITIVE).put(Y, y);
		}

		try {
			int exp = y;
			int result = 1;
			int x2y = x;
			while (true) {
				if ((exp
						& 0x1) != 0) {
					result = mulAndCheck(result, x2y);
				}

				exp >>= 1;
				if (exp == 0) {
					break;
				}

				x2y = mulAndCheck(x2y, x2y);
			}
			return result;
		} catch (MathException e) {
			MathException me = new MathException(MAE__INTEGER_OVERFLOW).put(X, x).put(Y, y);
			me.initCause(e);
			throw me;
		}
	}

	/**
	 * Raise a long to an int power.
	 *
	 * @param x
	 *            Number to raise.
	 * @param y
	 *            Exponent (must be positive or zero).
	 * @return \( x^y \)
	 * @throws MathException
	 *             Of type {@code NOT_POSITIVE} if {@code y < 0}.
	 * @throws MathException
	 *             Of type {@code MAE__LONG_OVERFLOW} if the result would
	 *             overflow.
	 */
	public static long pow(final long x, final int y) throws MathException {
		if (y < 0) {
			throw new MathException(NOT_POSITIVE).put(Y, y);
		}

		try {
			int exp = y;
			long result = 1;
			long x2y = x;
			while (true) {
				if ((exp
						& 0x1) != 0) {
					result = mulAndCheck(result, x2y);
				}

				exp >>= 1;
				if (exp == 0) {
					break;
				}

				x2y = mulAndCheck(x2y, x2y);
			}

			return result;
		} catch (MathException e) {
			MathException me = new MathException(MAE__LONG_OVERFLOW).put(X, x).put(Y, y);
			me.initCause(e);
			throw me;
		}
	}

	/**
	 * Raise a BigInteger to an int power.
	 *
	 * @param x
	 *            Number to raise.
	 * @param y
	 *            Exponent (must be positive or zero).
	 * @return x<sup>y</sup>
	 * @throws MathException
	 *             Of type {@code NOT_POSITIVE} if {@code y < 0}.
	 */
	public static BigInteger pow(final BigInteger x, int y) throws MathException {
		if (y < 0) {
			throw new MathException(NOT_POSITIVE).put(Y, y);
		}
		return x.pow(y);
	}

	/**
	 * Raise a BigInteger to a long power.
	 *
	 * @param x
	 *            Number to raise.
	 * @param y
	 *            Exponent (must be positive or zero).
	 * @return x<sup>y</sup>
	 * @throws MathException
	 *             Of type {@code NOT_POSITIVE} if {@code e < 0}.
	 */
	public static BigInteger pow(final BigInteger x, long y) throws MathException {
		if (y < 0) {
			throw new MathException(NOT_POSITIVE).put(Y, y);
		}

		BigInteger result = BigInteger.ONE;
		BigInteger k2p = x;
		while (y != 0) {
			if ((y
					& 0x1) != 0) {
				result = result.multiply(k2p);
			}
			k2p = k2p.multiply(k2p);
			y >>= 1;
		}

		return result;

	}

	/**
	 * Raise a BigInteger to a BigInteger power.
	 *
	 * @param x
	 *            Number to raise.
	 * @param y
	 *            Exponent (must be positive or zero).
	 * @return x<sup>y</sup>
	 * @throws MathException
	 *             Of type {@code NOT_POSITIVE} if {@code e < 0}.
	 */
	public static BigInteger pow(final BigInteger x, BigInteger y) throws MathException {
		if (y.compareTo(BigInteger.ZERO) < 0) {
			throw new MathException(NOT_POSITIVE).put(Y, y);
		}

		BigInteger result = BigInteger.ONE;
		BigInteger x2y = x;
		while (!BigInteger.ZERO.equals(y)) {
			if (y.testBit(0)) {
				result = result.multiply(x2y);
			}
			x2y = x2y.multiply(x2y);
			y = y.shiftRight(1);
		}

		return result;
	}

	/**
	 * Returns true if the argument is a power of two.
	 *
	 * @param n
	 *            the number to test
	 * @return true if the argument is a power of two
	 */
	public static boolean isPowerOfTwo(long n) {
		return (n > 0)
				&& ((n
						& (n
								- 1)) == 0);
	}

	/**
	 * Returns the unsigned remainder from dividing the first argument by the
	 * second where each argument and the result is interpreted as an unsigned
	 * value.
	 * <p>
	 * This method does not use the {@code long} datatype.
	 * </p>
	 *
	 * @param dividend
	 *            the value to be divided
	 * @param divisor
	 *            the value doing the dividing
	 * @return the unsigned remainder of the first argument divided by the
	 *         second argument.
	 */
	public static int remainderUnsigned(int dividend, int divisor) {
		if (divisor >= 0) {
			if (dividend >= 0) {
				return dividend
						% divisor;
			}
			// The implementation is a Java port of algorithm described in the
			// book
			// "Hacker's Delight" (section "Unsigned short division from signed
			// division").
			int q = ((dividend >>> 1)
					/ divisor) << 1;
			dividend -= q
					* divisor;
			if (dividend < 0
					|| dividend >= divisor) {
				dividend -= divisor;
			}
			return dividend;
		}
		return dividend >= 0
				|| dividend < divisor ? dividend
						: dividend
								- divisor;
	}

	/**
	 * Returns the unsigned remainder from dividing the first argument by the
	 * second where each argument and the result is interpreted as an unsigned
	 * value.
	 * <p>
	 * This method does not use the {@code BigInteger} datatype.
	 * </p>
	 *
	 * @param dividend
	 *            the value to be divided
	 * @param divisor
	 *            the value doing the dividing
	 * @return the unsigned remainder of the first argument divided by the
	 *         second argument.
	 */
	public static long remainderUnsigned(long dividend, long divisor) {
		if (divisor >= 0L) {
			if (dividend >= 0L) {
				return dividend
						% divisor;
			}
			// The implementation is a Java port of algorithm described in the
			// book
			// "Hacker's Delight" (section "Unsigned short division from signed
			// division").
			long q = ((dividend >>> 1)
					/ divisor) << 1;
			dividend -= q
					* divisor;
			if (dividend < 0L
					|| dividend >= divisor) {
				dividend -= divisor;
			}
			return dividend;
		}
		return dividend >= 0L
				|| dividend < divisor ? dividend
						: dividend
								- divisor;
	}

	/**
	 * Returns the unsigned quotient of dividing the first argument by the
	 * second where each argument and the result is interpreted as an unsigned
	 * value.
	 * <p>
	 * Note that in two's complement arithmetic, the three other basic
	 * arithmetic operations of add, subtract, and multiply are bit-wise
	 * identical if the two operands are regarded as both being signed or both
	 * being unsigned. Therefore separate {@code
	 * addUnsigned}, etc. methods are not provided.
	 * </p>
	 * <p>
	 * This method does not use the {@code long} datatype.
	 * </p>
	 *
	 * @param dividend
	 *            the value to be divided
	 * @param divisor
	 *            the value doing the dividing
	 * @return the unsigned quotient of the first argument divided by the second
	 *         argument
	 */
	public static int divideUnsigned(int dividend, int divisor) {
		if (divisor >= 0) {
			if (dividend >= 0) {
				return dividend
						/ divisor;
			}
			// The implementation is a Java port of algorithm described in the
			// book
			// "Hacker's Delight" (section "Unsigned short division from signed
			// division").
			int q = ((dividend >>> 1)
					/ divisor) << 1;
			dividend -= q
					* divisor;
			if (dividend < 0L
					|| dividend >= divisor) {
				q++;
			}
			return q;
		}
		return dividend >= 0
				|| dividend < divisor ? 0 : 1;
	}

	/**
	 * Returns the unsigned quotient of dividing the first argument by the
	 * second where each argument and the result is interpreted as an unsigned
	 * value.
	 * <p>
	 * Note that in two's complement arithmetic, the three other basic
	 * arithmetic operations of add, subtract, and multiply are bit-wise
	 * identical if the two operands are regarded as both being signed or both
	 * being unsigned. Therefore separate {@code
	 * addUnsigned}, etc. methods are not provided.
	 * </p>
	 * <p>
	 * This method does not use the {@code BigInteger} datatype.
	 * </p>
	 *
	 * @param dividend
	 *            the value to be divided
	 * @param divisor
	 *            the value doing the dividing
	 * @return the unsigned quotient of the first argument divided by the second
	 *         argument.
	 */
	public static long divideUnsigned(long dividend, long divisor) {
		if (divisor >= 0L) {
			if (dividend >= 0L) {
				return dividend
						/ divisor;
			}
			// The implementation is a Java port of algorithm described in the
			// book
			// "Hacker's Delight" (section "Unsigned short division from signed
			// division").
			long q = ((dividend >>> 1)
					/ divisor) << 1;
			dividend -= q
					* divisor;
			if (dividend < 0L
					|| dividend >= divisor) {
				q++;
			}
			return q;
		}
		return dividend >= 0L
				|| dividend < divisor ? 0L : 1L;
	}

}
