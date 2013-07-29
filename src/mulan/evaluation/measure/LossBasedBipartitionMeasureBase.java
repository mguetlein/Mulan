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
 *    LossBasedBipartitionMeasureBase.java
 *    Copyright (C) 2009-2012 Aristotle University of Thessaloniki, Greece
 */
package mulan.evaluation.measure;

import mulan.evaluation.loss.BipartitionLossFunction;

/**
 *
 * @author Grigorios Tsoumakas
 * @version 2010.11.10
 */
public abstract class LossBasedBipartitionMeasureBase extends ExampleBasedBipartitionMeasureBase
{

	// a bipartition loss function
	private final BipartitionLossFunction loss;

	public LossBasedBipartitionMeasureBase(BipartitionLossFunction loss)
	{
		this(ConfidenceLevelProvider.CONFIDENCE_LEVEL_ALL, loss);
	}

	/**
	 * Creates a loss-based bipartition measure
	 *
	 * @param aLoss a bipartition loss function
	 */
	public LossBasedBipartitionMeasureBase(ConfidenceLevel confLevel, BipartitionLossFunction aLoss)
	{
		super(confLevel);
		this.loss = aLoss;
	}

	@Override
	public void updateBipartition(boolean[] bipartition, boolean[] truth)
	{
		sum += loss.computeLoss(bipartition, truth);
		count++;
	}

	@Override
	public void updateBipartition(Boolean[] bipartition, Boolean[] truth)
	{
		sum += loss.computeLoss(bipartition, truth);
		count++;
	}

	public String getName()
	{
		return loss.getName() + confLevel.getName();
	}

	public double getIdealValue()
	{
		return 0;
	}

}