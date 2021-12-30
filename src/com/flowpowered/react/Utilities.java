/*
 * This file is part of React, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 Flow Powered <https://flowpowered.com/>
 * Original ReactPhysics3D C++ library by Daniel Chappuis <http://danielchappuis.ch>
 * React is re-licensed with permission from ReactPhysics3D author.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.flowpowered.react;

/**
 * This class contains static utilities. It was added for the port to implement some C++ code in Java.
 */
public class Utilities {
    /**
     * Returns the index of an object in an array, or -1 if it can't be found.
     *
     * @param array The array to search
     * @param object The object to look for
     * @return The index, or -1 if the object wasn't found
     */
    public static int indexOf(Object[] array, Object object) {
        for (int i = 0; i < array.length; i++) {
            if (object.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Represents a pair of 32 bit integers.
     */
    public static class IntPair {
        private int first;
        private int second;

        /**
         * Constructs a new int pair with both integers being 0.
         */
        public IntPair() {
            this(0, 0);
        }

        /**
         * Constructs a new int pair with the desired value for each member.
         *
         * @param first The value of the first member
         * @param second The value of the second member
         */
        public IntPair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        /**
         * Gets the value of the first member.
         *
         * @return The first member's value
         */
        public int getFirst() {
            return first;
        }

        /**
         * Sets the first member's value.
         *
         * @param first The value for the first member
         */
        public void setFirst(int first) {
            this.first = first;
        }

        /**
         * Gets the value of the second member.
         *
         * @return The second member's value
         */
        public int getSecond() {
            return second;
        }

        /**
         * Sets the second member's value.
         *
         * @param second The value for the second member
         */
        public void setSecond(int second) {
            this.second = second;
        }

        /**
         * Swaps both members. First becomes second, second becomes first.
         */
        public void swap() {
            final int temp = first;
            first = second;
            second = temp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IntPair)) {
                return false;
            }
            final IntPair intPair = (IntPair) o;
            if (first != intPair.getFirst()) {
                return false;
            }
            return second == intPair.getSecond();
        }

        @Override
        public int hashCode() {
            int result = first;
            result = 31 * result + second;
            return result;
        }
    }
}
