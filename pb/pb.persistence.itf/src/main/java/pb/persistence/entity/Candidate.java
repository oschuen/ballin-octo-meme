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
 * @since 11.10.2013
 * @version 1.0
 * @author oliver
 */
package pb.persistence.entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import pb.persistence.avatar.Images;

/**
 * Entity storing all required informations about a candidate
 * 
 * @author oliver
 */
@Entity
@Table(name = Candidate.TABLE_NAME)
public class Candidate {

	public static final int optImageSize = 160;
	public static final String TABLE_NAME = "CANDIDATES";
	public static final String COLUMN_ID = "ID";
	public static final String COLUMN_IMAGE = "IMAGE";
	public static final String COLUMN_IMAGE_COUNT = "IMAGE_COUNT";
	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_SD = "SHORT_DESCRIPTION";
	public static final String COLUMN_GENDER = "GENDER";
	public static final String COLUMN_AGE = "AGE";
	public static final String COLUMN_AVAILABLE = "AVAILABLE";

	/**
	 * Enum used to describe the gender of the candidate
	 * 
	 * @author oliver
	 */
	public static enum Gender {
		MALE, FEMALE
	}

	@Id
	@Column(name = COLUMN_ID)
	protected int id;

	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Column(name = COLUMN_IMAGE)
	protected byte[] image;

	@Basic
	@Column(name = COLUMN_IMAGE_COUNT)
	protected int imageCount;

	@Basic
	@Column(name = COLUMN_NAME, length = 100)
	protected String name;

	@Basic
	@Column(name = COLUMN_SD, length = 500)
	protected String shortDescription;

	@Basic
	@Column(name = COLUMN_GENDER)
	@Enumerated(EnumType.STRING)
	protected Gender gender;

	@Basic
	@Column(name = COLUMN_AGE)
	protected int age;

	@Basic
	@Column(name = COLUMN_AVAILABLE)
	protected boolean available;

	public Candidate() {
		super();
	}

	/**
	 * Constructor setting all values at once
	 * 
	 * @param id
	 *            Primary key
	 * @param image
	 *            Portrait of the candidate
	 * @param name
	 *            nick name of the candidate
	 * @param age
	 *            age of the candidate
	 * @param shortDescription
	 *            a short Description containing hobbies etc.
	 * @param gender
	 *            the gender of the candidate. (I know there is something
	 *            in-between)
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public Candidate(final int id, final BufferedImage image, final String name, final int age,
			final String shortDescription, final Gender gender) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.shortDescription = shortDescription;
		this.gender = gender;
		this.available = true;
		this.setImage(image);
	}

	/**
	 * Constructor setting all values at once
	 * 
	 * @param id
	 *            Primary key
	 * @param image
	 *            Portrait of the candidate
	 * @param name
	 *            nick name of the candidate
	 * @param age
	 *            age of the candidate
	 * @param shortDescription
	 *            a short Description containing hobbies etc.
	 * @param gender
	 *            the gender of the candidate. (I know there is something
	 *            in-between)
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public Candidate(final int id, final byte[] image, final String name, final int age,
			final String shortDescription, final Gender gender) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.shortDescription = shortDescription;
		this.gender = gender;
		this.available = true;
		this.setImage(image);
		this.imageCount = 0;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * @return the image
	 */
	public byte[] getImage() {
		byte ret[] = Images.getDefaultAvatar();
		if (image != null) {
			ret = image;
		}
		return ret;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(final byte[] image) {
		if (image.length > 0) {
			try {
				final BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(image));
				setImage(bImage);
			} catch (final Throwable e) {
				if (gender == Gender.MALE) {
					this.image = Images.getMaleAvatar();
				} else {
					this.image = Images.getFemaleAvatar();
				}
			}
		}
	}

	/**
	 * @return the imageCount
	 */
	public int getImageCount() {
		return imageCount;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(final BufferedImage image) {
		final ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try {
			BufferedImage renderImage = image;
			// First crop image
			final int width = image.getWidth();
			final int height = image.getHeight();
			int xOff = 0;
			int yOff = 0;
			final int opt = Math.min(width, height);
			if (width > opt) {
				xOff = (width - opt) / 2;
			}
			if (height > opt) {
				yOff = (height - opt) / 2;
			}
			if ((xOff > 0) || (yOff > 0)) {
				renderImage = image.getSubimage(xOff, yOff, opt, opt);
			}
			if (opt != optImageSize) {
				final BufferedImage tempImage = new BufferedImage(optImageSize, optImageSize,
						BufferedImage.TYPE_INT_RGB);
				final Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
				final Image scaledImage = renderImage.getScaledInstance(optImageSize, optImageSize,
						Image.SCALE_AREA_AVERAGING);
				g2d.drawImage(scaledImage, 0, 0, null);
				scaledImage.flush();
				g2d.dispose();
				renderImage = tempImage;
			}
			ImageIO.write(renderImage, "PNG", bao);
			this.image = bao.toByteArray();
			renderImage.flush();

		} catch (final Throwable e) {
			if (gender == Gender.MALE) {
				this.image = Images.getMaleAvatar();
			} else {
				this.image = Images.getFemaleAvatar();
			}
		}
		imageCount++;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription
	 *            the shortDescription to set
	 */
	public void setShortDescription(final String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the gender
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(final Gender gender) {
		this.gender = gender;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(final int age) {
		this.age = age;
	}

	/**
	 * @return the available
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * @param available
	 *            the available to set
	 */
	public void setAvailable(final boolean available) {
		this.available = available;
	}
}
