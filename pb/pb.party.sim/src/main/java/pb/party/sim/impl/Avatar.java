/**
 * Copyright (C) 2013 Oliver Schünemann
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, 
 * Boston, MA 02110, USA 
 * 
 * @since 12.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.party.sim.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This Class is able to create an Avatar image for a simulated Party Member.
 * Hair, Skin and Dress Color must be configured by user.
 * 
 * @author oliver
 */
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class Avatar {

	private final static int width = 160;
	private final static int middle = width / 2;
	private final static int kragen = (int) (width * 0.35 + 0.5);
	private final static int aBody = width * 2 / 3;
	private final static int bBody = width / 4;
	private final static int moveY = width / 16;
	private final static int headYRadius = (int) (width / 3.9);
	private final static int headXRadius = (int) (width * 0.1875);
	private final Color hairColor;
	private final Color shirtColor;
	private final Color skinColor;

	protected BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);

	/**
	 * Default constructor for an Image with white hair, skin and dress
	 */
	public Avatar() {
		super();
		hairColor = Color.white;
		shirtColor = Color.white;
		skinColor = Color.white;
	}

	/**
	 * Constructor configuring the image generator to the given colors
	 * 
	 * @param hairColor
	 *            Hair Color
	 * @param shirtColor
	 *            Dress/Shirt Color
	 * @param skinColor
	 *            Skin Color
	 */
	public Avatar(final Color hairColor, final Color shirtColor, final Color skinColor) {
		super();
		this.hairColor = hairColor;
		this.shirtColor = shirtColor;
		this.skinColor = skinColor;
	}

	/**
	 * generates the points of one side of an ellipse, from start to end point.
	 * The list of Points is always centered to (0,0)
	 * 
	 * @param a
	 *            the one radius of the ellipse
	 * @param b
	 *            the other radius of the ellipse
	 * @param startX
	 *            start point
	 * @param endX
	 *            end point of the ellipse slice
	 * @return the list of points between start and end
	 */
	protected List<Point> getEllipsePoints(final int a, final int b, final int startX,
			final int endX) {
		final List<Point> ret = new ArrayList<>();
		if (startX <= endX) {
			for (int i = startX; i <= endX; ++i) {
				final int y = (int) (Math.sqrt((1.0 - (double) (i * i) / (double) (a * a)) * b * b) + 0.5);
				ret.add(new Point(i, y));
			}
		} else {
			for (int i = startX; i >= endX; --i) {
				final int y = -(int) (Math
						.sqrt((1.0 - (double) (i * i) / (double) (a * a)) * b * b) + 0.5);
				ret.add(new Point(i, y));
			}
		}
		return ret;
	}

	/**
	 * @return the Polygon of the left shoulder
	 */
	protected List<Point> getLeftShoulder() {
		return getEllipsePoints(aBody, bBody, -middle, -middle + kragen);
	}

	/**
	 * @return the polygon of the chin
	 */
	protected List<Point> getChin() {
		return getEllipsePoints(headXRadius, headYRadius, headXRadius, -headXRadius);
	}

	/**
	 * @param male
	 * @return the Polygon of the TShirt
	 */
	protected Polygon getShirtPolygon(final boolean male) {
		final Polygon ret = new Polygon();
		ret.addPoint(0, width + 4);
		int lastShoulder = 0;
		final List<Point> leftShoulder = getLeftShoulder();
		for (final Point point : leftShoulder) {
			if (middle + point.x > moveY) {
				ret.addPoint(middle + point.x, width - point.y - moveY);
			}
			lastShoulder = point.y;
		}
		if (male) {
			final int rKragen = middle - kragen;
			final List<Point> kragenPoly = getEllipsePoints(rKragen, rKragen / 2, -rKragen, rKragen);
			for (final Point point : kragenPoly) {
				ret.addPoint(middle + point.x, width - lastShoulder + point.y - moveY);
			}
		} else {
			ret.addPoint(middle, width - moveY);
		}

		final List<Point> rightShoulder = getEllipsePoints(aBody, bBody, middle - kragen,
				middle - 1);
		for (final Point point : rightShoulder) {
			if (middle + point.x < width - moveY) {
				ret.addPoint(middle + point.x, width - point.y - moveY);
			}
		}
		ret.addPoint(width - 1, width + 4);
		ret.addPoint(0, width + 4);
		return ret;
	}

	/**
	 * @param male
	 * @return the polygon of the neck
	 */
	protected Polygon getNeckPolygon(final boolean male) {
		final Polygon ret = new Polygon();
		int lastShoulder = 0;
		final List<Point> leftShoulder = getLeftShoulder();
		for (final Point point : leftShoulder) {
			lastShoulder = point.y;
		}

		final int rKragen = middle - kragen;
		final List<Point> kragenPoly = getEllipsePoints(rKragen, rKragen / 2, -rKragen, rKragen);
		final Point firstPoint = kragenPoly.get(0);
		final Point lastPoint = kragenPoly.get(kragenPoly.size() - 1);
		if (male) {
			for (final Point point : kragenPoly) {
				ret.addPoint(middle + point.x, width - lastShoulder + point.y - moveY);
			}
		} else {
			ret.addPoint(middle + firstPoint.x, width - lastShoulder + firstPoint.y - moveY);
			ret.addPoint(middle, width - moveY);
			ret.addPoint(middle + lastPoint.x, width - lastShoulder + lastPoint.y - moveY);
		}
		final int rXNeck = headYRadius - rKragen;
		final int rYNeck = rXNeck / 4;
		int lastNeckX = 0;

		final List<Point> leftNeck = getEllipsePoints(rXNeck, rYNeck, -rXNeck, 0);
		for (final Point point : leftNeck) {
			lastNeckX = middle + lastPoint.x - point.y;
			ret.addPoint(lastNeckX, width - lastShoulder + lastPoint.y - rXNeck - point.x - moveY);
		}

		final List<Point> kinn = getChin();
		for (final Point point : kinn) {
			final int x = middle + point.x;
			final int y = headYRadius + moveY * 2 - point.y;
			if (x >= 2 * middle - lastNeckX && x <= lastNeckX) {
				ret.addPoint(x, y);
			}
		}

		final List<Point> rightNeck = getEllipsePoints(rXNeck, rYNeck, 0, rXNeck);
		for (final Point point : rightNeck) {
			lastNeckX = middle - lastPoint.x + point.y;
			ret.addPoint(lastNeckX, width - lastShoulder + lastPoint.y - rXNeck + point.x - moveY);
		}
		return ret;
	}

	/**
	 * @return the head polygon
	 */
	public Polygon getHead() {
		final Polygon ret = new Polygon();
		final List<Point> kinn = getChin();
		for (final Point point : kinn) {
			ret.addPoint(middle + point.x, headYRadius + moveY * 2 - point.y);
		}
		for (final Point point : kinn) {
			if (point.y > -headYRadius / 3) {
				ret.addPoint(middle - point.x, headYRadius + moveY * 2 + point.y);
			}
		}
		return ret;
	}

	/**
	 * @param male
	 * @return the hair polygon
	 */
	public Polygon getHair(final boolean male) {
		final Polygon ret = new Polygon();
		final int hairRadius = (int) (width * 0.225);
		final List<Point> hair = getEllipsePoints(hairRadius, hairRadius, hairRadius, -hairRadius);
		for (final Point point : hair) {
			ret.addPoint(middle + point.x, (int) (headYRadius + moveY * 1.5 + point.y));
		}
		if (male) {
			for (final Point point : hair) {
				ret.addPoint(middle - point.x, (int) (headYRadius + moveY * 1.5 - point.y));
			}
		} else {
			ret.addPoint(middle - hairRadius - moveY, width);
			ret.addPoint(middle + hairRadius + moveY, width);
		}
		return ret;
	}

	/**
	 * draws the complete avatar image
	 */
	public void drawAvatar(final boolean male) {
		final Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, width);
		g.setStroke(new BasicStroke(3F));
		g.setColor(hairColor);
		g.fillPolygon(getHair(male));
		g.setColor(Color.GRAY);
		g.drawPolygon(getHair(male));
		g.setColor(shirtColor);
		g.fillPolygon(getShirtPolygon(male));
		g.setColor(Color.GRAY);
		g.drawPolygon(getShirtPolygon(male));
		g.setColor(skinColor);
		g.fillPolygon(getNeckPolygon(male));
		g.setColor(Color.GRAY);
		g.drawPolygon(getNeckPolygon(male));
		g.setColor(skinColor);
		g.fillPolygon(getHead());
		g.setColor(Color.GRAY);
		g.drawPolygon(getHead());
	}

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}
}
