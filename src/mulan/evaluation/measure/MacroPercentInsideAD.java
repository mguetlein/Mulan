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
 *    MacroPrecision.java
 *    Copyright (C) 2009-2012 Aristotle University of Thessaloniki, Greece
 */
package mulan.evaluation.measure;


public class MacroPercentInsideAD //extends MeasureBase implements MacroAverageMeasure
{
	//	int numOfLabels;
	//	double numNotMissing[];
	//	double numInsideAD[];
	//
	//	/**
	//	 * Constructs a new object with given number of labels
	//	 * 
	//	 * @param numOfLabels the number of labels
	//	 */
	//	public MacroPercentInsideAD(int numOfLabels)
	//	{
	//		this.numOfLabels = numOfLabels;
	//		reset();
	//	}
	//
	//	@Override
	//	public double getIdealValue()
	//	{
	//		return numOfLabels;
	//	}
	//
	//	public String getName()
	//	{
	//		return "Macro-averaged Percent Inside AD";
	//	}
	//
	//	@Override
	//	public double getValue()
	//	{
	//		double sum = 0;
	//		double num = 0;
	//		for (int i = 0; i < numOfLabels; i++)
	//			if (!Double.isNaN(getValue(i)))
	//			{
	//				sum += getValue(i);
	//				num++;
	//			}
	//		sum /= num;
	//		return sum;
	//	}
	//
	//	@Override
	//	public double getValue(int labelIndex)
	//	{
	//		if (numNotMissing[labelIndex] == 0)
	//			return Double.NaN;
	//		return numInsideAD[labelIndex] / numNotMissing[labelIndex];
	//	}
	//
	//	@Override
	//	public void reset()
	//	{
	//		numNotMissing = new double[numOfLabels];
	//		numInsideAD = new double[numOfLabels];
	//	}
	//
	//	@Override
	//	protected void updateInternal(MultiLabelOutput prediction, boolean[] truth)
	//	{
	//		throw new Error("not yet implemetend");
	//	}
	//
	//	@Override
	//	protected void updateInternal(MultiLabelOutput prediction, boolean[] truth, boolean[] missingTruth,
	//			boolean[] insideAD)
	//	{
	//		for (int i = 0; i < numOfLabels; i++)
	//		{
	//			if (!missingTruth[i])
	//			{
	//				numNotMissing[i]++;
	//				if (insideAD[i])
	//					numInsideAD[i]++;
	//			}
	//		}
	//	}
}