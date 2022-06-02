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

package dev.vini2003.hammer.core.api.client.color;

public class Color {
	public static final Color DARK_RED = new Color(0.67F, 0.0F, 0.0F, 1.0F);
	public static final Color RED = new Color(1.0F, 0.33F, 0.33F, 1.0F);
	public static final Color GOLD = new Color(1.0F, 0.67F, 0.0F, 1.0F);
	public static final Color YELLOW = new Color(1.0F, 1.0F, 0.33F, 1.0F);
	public static final Color DARK_GREEN = new Color(0.0F, 0.67F, 0.0F, 1.0F);
	public static final Color GREEN = new Color(0.33F, 1.0F, 0.33F, 1.0F);
	public static final Color AQUA = new Color(0.33F, 1.0F, 1.0F, 1.0F);
	public static final Color DARK_AQUA = new Color(0.0F, 0.67F, 0.67F, 1.0F);
	public static final Color DARK_BLUE = new Color(0.0F, 0.0F, 0.67F, 1.0F);
	public static final Color BLUE = new Color(0.33F, 0.33F, 1.0F, 1.0F);
	public static final Color LIGHT_PURPLE = new Color(1.0F, 0.33F, 1.0F, 1.0F);
	public static final Color DARK_PURPLE = new Color(0.67F, 0.0F, 0.67F, 1.0F);
	public static final Color WHITE = new Color(1.0F, 1.0F, 1.0F, 1.0F);
	public static final Color GRAY = new Color(0.67F, 0.67F, 0.67F, 1.0F);
	public static final Color DARK_GRAY = new Color(0.33F, 0.33F, 0.33F, 1.0F);
	public static final Color BLACK = new Color(0.0F, 0.0F, 0.0F, 1.0F);
	
	private final float r;
	private final float g;
	private final float b;
	private final float a;
	
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public Color(String rgbaHex) {
		this(Long.parseLong(rgbaHex, 16));
	}
	
	public Color(long rgba) {
		this.r = ((rgba >> 24) & 0xFF) / 255.0F;
		this.g = ((rgba >> 16) & 0xFF) / 255.0F;
		this.b = ((rgba >> 8) & 0xFF) / 255.0F;
		this.a = (rgba & 0xFF) / 255.0F;
	}
	
	public Color(int rgb) {
		this.r = ((rgb >> 16) & 0xFF) / 255.0F;
		this.g = ((rgb >> 8) & 0xFF) / 255.0F;
		this.b = (rgb & 0xFF) / 255.0F;
		this.a = 1.0F;
	}
	
	public float getR() {
		return r;
	}
	
	public float getG() {
		return g;
	}
	
	public float getB() {
		return b;
	}
	
	public float getA() {
		return a;
	}
	
	public long toRgba() {
		var r = (long) (this.r * 255.0F);
		var g = (long) (this.g * 255.0F);
		var b = (long) (this.b * 255.0F);
		var a = (long) (this.a * 255.0F);
		
		var rgba = r;
		rgba = (rgba << 8) + g;
		rgba = (rgba << 8) + b;
		rgba = (rgba << 8) + a;
		
		return rgba;
	}
	
	public int toRgb() {
		var r = (int) (this.r * 255.0F);
		var g = (int) (this.g * 255.0F);
		var b = (int) (this.b * 255.0F);
		
		var rgb = r;
		rgb = (rgb << 8) + g;
		rgb = (rgb << 8) + b;
		
		return rgb;
	}
}
