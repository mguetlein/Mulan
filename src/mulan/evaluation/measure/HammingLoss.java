/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    HammingLoss.java
 *    Copyright (C) 2009-2012 Aristotle University of Thessaloniki, Greece
 */
package mulan.evaluation.measure;

/**
 * Implementation of the Hamming loss function.
 * 
 * @author Grigorios Tsoumakas
 * @version 2010.12.04
 */
public class HammingLoss extends LossBasedBipartitionMeasureBase
{

	/**
	 * Creates an instance of this object based on the corresponding loss
	 * function
	 */
	public HammingLoss(ConfidenceLevel confLevel)
	{
		super(confLevel, new mulan.evaluation.loss.HammingLoss());
	}

	public HammingLoss()
	{
		this(ConfidenceLevelProvider.CONFIDENCE_LEVEL_ALL);
	}

}