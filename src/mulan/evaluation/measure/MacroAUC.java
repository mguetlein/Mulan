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
 *    MacroAUC.java
 *    Copyright (C) 2009-2012 Aristotle University of Thessaloniki, Greece
 */
package mulan.evaluation.measure;

import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Implementation of the macro-averaged AUC measure.
 *
 * @author Grigorios Tsoumakas
 * @version 2010.12.10
 */
public class MacroAUC extends LabelBasedAUC implements MacroAverageMeasure
{

	/**
	 * Creates a new instance of this class
	 *
	 * @param numOfLabels the number of labels
	 */
	public MacroAUC(int numOfLabels)
	{
		super(numOfLabels);
	}

	public String getName()
	{
		return "Macro-averaged AUC";
	}

	public double getValue()
	{
		double[] labelAUC = new double[numOfLabels];
		int nonNan = 0;
		for (int i = 0; i < numOfLabels; i++)
		{
			labelAUC[i] = getAUC(i);
			if (!Double.isNaN(labelAUC[i]))
				nonNan++;
		}
		if (nonNan == 0)
			throw new IllegalStateException("Make sure to handle MacroAUC NaNs properly");
		double nonNanVals[] = new double[nonNan];
		nonNan = 0;
		for (int i = 0; i < numOfLabels; i++)
			if (!Double.isNaN(labelAUC[i]))
				nonNanVals[nonNan++] = labelAUC[i];
		return Utils.mean(nonNanVals);
	}

	private double getAUC(int labelIndex)
	{
		if (m_Predictions[labelIndex].size() == 0)
			return Double.NaN;
		ThresholdCurve tc = new ThresholdCurve();
		Instances result = tc.getCurve(m_Predictions[labelIndex], 1);
		double d = ThresholdCurve.getROCArea(result);
		if (!Double.isNaN(d))
			return d;
		else
		{
			int tP = 0, tN = 0, fP = 0, fN = 0;
			//			System.err.println("\nauc problem solving");
			for (int i = 0; i < m_Predictions[labelIndex].size(); i++)
			{
				NominalPrediction p = (NominalPrediction) m_Predictions[labelIndex].get(i);
				//				System.err.println(p.actual() + " " + p.predicted());
				if (p.predicted() == 1.0)
				{
					if (p.actual() == 1.0)
						tP++;
					else
						fP++;
				}
				else
				{
					if (p.actual() == 0.0)
						tN++;
					else
						fN++;
				}
			}
			if (tP == 0 && fP > 0)
				return 0.0;
			else if (fP == 0 && tP > 0)
				return 1.0;
			else if (tP == 0 && fP == 0)
				return Double.NaN;
			else
				throw new IllegalStateException("WTF " + tP + " " + fP + " " + tN + " " + fN);
		}
	}

	/**
	 * Returns the AUC for a particular label
	 * 
	 * @param labelIndex the index of the label 
	 * @return the AUC for that label
	 */
	public double getValue(int labelIndex)
	{
		return getAUC(labelIndex);
	}

}