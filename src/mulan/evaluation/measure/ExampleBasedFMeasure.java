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
 *    ExampleBasedFMeasure.java
 *    Copyright (C) 2009-2012 Aristotle University of Thessaloniki, Greece
 */
package mulan.evaluation.measure;

/**
 * Implementation of the example-based F measure.
 *
 * @author Grigorios Tsoumakas
 * @version 2012.05.29
 */
public class ExampleBasedFMeasure extends ExampleBasedBipartitionMeasureBase
{

	private final double beta;

	/**
	 * Creates a new object
	 *
	 */
	public ExampleBasedFMeasure()
	{
		this(1.0);
	}

	/**
	 * Creates a new object
	 *
	 * @param beta the beta parameter for precision and recall combination
	 */
	public ExampleBasedFMeasure(double beta)
	{
		this(ConfidenceLevelProvider.CONFIDENCE_LEVEL_ALL, beta);
	}

	public ExampleBasedFMeasure(ConfidenceLevel confLevel)
	{
		this(confLevel, 1.0);
	}

	public ExampleBasedFMeasure(ConfidenceLevel confLevel, double beta)
	{
		super(confLevel);
		this.beta = beta;
	}

	public String getName()
	{
		return "Example-Based F Measure" + confLevel.getName();
	}

	public double getIdealValue()
	{
		return 1;
	}

	@Override
	protected void updateBipartition(boolean[] bipartition, boolean[] truth)
	{
		double tp = 0;
		double fp = 0;
		double fn = 0;
		for (int i = 0; i < truth.length; i++)
		{
			if (bipartition[i] && truth[i])
			{
				tp++;
			}
			if (bipartition[i] && !truth[i])
			{
				fp++;
			}
			if (!bipartition[i] && truth[i])
			{
				fn++;
			}
		}

		sum += InformationRetrievalMeasures.fMeasure(tp, fp, fn, beta);
		count++;
	}
}