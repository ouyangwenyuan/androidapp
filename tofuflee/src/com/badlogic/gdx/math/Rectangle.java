/*
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com), Nathan Sweet (admin@esotericsoftware.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.badlogic.gdx.math;

/**
 * Encapsulates a 2D rectangle defined by it's bottom corner point and its extends in x (width) and y (height).
 * @author badlogicgames@gmail.com
 * 
 */
public final class Rectangle {
	private float x, y;
	private float width, height;

	/**
	 * Constructs a new rectangle with all values set to zero
	 */
	public Rectangle () {

	}

	/**
	 * Constructs a new rectangle with the given corner point in the bottom left and dimensions.
	 * @param x The corner point x-coordinate
	 * @param y The corner point y-coordinate
	 * @param width The width
	 * @param height The height
	 */
	public Rectangle (float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructs a rectangle based on the given rectangle
	 * @param rect The rectangle
	 */
	public Rectangle (Rectangle rect) {
		x = rect.x;
		y = rect.y;
		width = rect.width;
		height = rect.height;
	}

	/**
	 * @return the x-coordinate of the bottom left corner
	 */
	public float getX () {
		return x;
	}

	/**
	 * Sets the x-coordinate of the bottom left corner
	 * @param x The x-coordinate
	 */
	public void setX (float x) {
		this.x = x;
	}

	/**
	 * @return the y-coordinate of the bottom left corner
	 */
	public float getY () {
		return y;
	}

	/**
	 * Sets the y-coordinate of the bottom left corner
	 * @param y The y-coordinate
	 */
	public void setY (float y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public float getWidth () {
		return width;
	}

	/**
	 * Sets the width of this rectangle
	 * @param width The width
	 */
	public void setWidth (float width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public float getHeight () {
		return height;
	}

	/**
	 * Sets the height of this rectangle
	 * @param height The height
	 */
	public void setHeight (float height) {
		this.height = height;
	}
}
