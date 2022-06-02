/*
 * MIT License
 *
 * Copyright (c) 2020 - 2022 vini2003
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.vini2003.hammer.core.api.common.math.size;

import java.util.Objects;

public class Size implements SizeHolder {
	private final float width;
	private final float height;
	private final float length;
	
	/**
	 * Constructs a position.
	 *
	 * @param width  the width size component.
	 * @param height the height size component.
	 * @param length the length size component.
	 *
	 * @return the position.
	 */
	public Size(float width, float height, float length) {
		this.width = width;
		this.height = height;
		this.length = length;
	}
	
	/**
	 * Constructs a size.
	 *
	 * @param width  the width size component
	 * @param height the height size component.
	 *
	 * @return the size.
	 */
	public Size(float width, float height) {
		this.width = width;
		this.height = height;
		this.length = 0.0F;
	}
	
	/**
	 * Constructs an anchored size.
	 *
	 * @param anchor the anchor.
	 * @param width  the relative with size component.
	 * @param height the relative height size component.
	 * @param length the relative length size component.
	 *
	 * @return the size.
	 */
	public Size(SizeHolder anchor, float width, float height, float length) {
		this(anchor.getWidth() + width, anchor.getHeight() + height, anchor.getLength() + length);
	}
	
	/**
	 * Constructs an anchored size.
	 *
	 * @param anchor the anchor.
	 * @param width  the relative with size component.
	 * @param height the relative height size component.
	 *
	 * @return the size.
	 */
	public Size(SizeHolder anchor, float width, float height) {
		this(anchor.getWidth() + width, anchor.getHeight() + height, anchor.getLength());
	}
	
	/**
	 * Constructs an anchor's size.
	 *
	 * @param anchor the anchor.
	 *
	 * @return the size.
	 */
	public Size(SizeHolder holder) {
		this(holder.getWidth(), holder.getHeight(), holder.getLength());
	}
	
	/**
	 * Returns this size, adding another size to it.
	 *
	 * @param size the size.
	 *
	 * @return the resulting size.
	 */
	public Size plus(Size size) {
		return new Size(width + size.width, height + size.height, length + size.length);
	}
	
	/**
	 * Returns this size, subtracting another size from it.
	 *
	 * @param size the size.
	 *
	 * @return the resulting size.
	 */
	public Size minus(Size size) {
		return new Size(width + size.width, height + size.height, length + size.length);
	}
	
	/**
	 * Returns this size, multiplying its components by a given number.
	 *
	 * @param number the number.
	 *
	 * @return the resulting size.
	 */
	public Size times(float number) {
		return new Size(width * number, height * number, length * number);
	}
	
	/**
	 * Returns this size, dividing its components by a given number.
	 *
	 * @param number the number.
	 *
	 * @return the resulting size.
	 */
	public Size div(float number) {
		return new Size(width / number, height / number, length / number);
	}
	
	@Override
	public float getWidth() {
		return width;
	}
	
	@Override
	public float getHeight() {
		return height;
	}
	
	@Override
	public float getLength() {
		return length;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof Size)) {
			return false;
		}
		
		var size = (Size) o;
		
		return Float.compare(size.width, width) == 0 && Float.compare(size.height, height) == 0 && Float.compare(size.length, length) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(width, height, length);
	}
}
