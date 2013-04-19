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

import java.util.Random;

import javax.swing.JDialog;

import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

/**
 * Implementation of the macro-averaged AUC measure.
 *
 * @author Grigorios Tsoumakas
 * @version 2010.12.10
 */
public class MacroAUC extends LabelBasedAUC implements MacroAverageMeasure
{

	private boolean weighted;

	/**
	 * Creates a new instance of this class
	 *
	 * @param numOfLabels the number of labels
	 */
	public MacroAUC(int numOfLabels)
	{
		this(numOfLabels, false);
	}

	public MacroAUC(int numOfLabels, boolean weighted)
	{
		super(numOfLabels);
		this.weighted = weighted;
	}

	public String getName()
	{
		return "Macro-averaged AUC weighted:" + weighted;
	}

	public double getValue()
	{
		double count = 0;
		double sum = 0;
		for (int i = 0; i < numOfLabels; i++)
		{
			double auc = getAUC(i);
			if (!Double.isNaN(auc))
			{
				double w = 1;
				if (weighted)
					w = m_Predictions[i].size();
				sum += auc * w;
				count += w;
			}
		}
		if (sum == 0)
			throw new IllegalStateException("Make sure to handle MacroAUC NaNs properly");
		return sum / count;
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
			//			if (tP == 0 && tN == 0) // no correct predictions
			//				return 0.0;
			//			else if (fP == 0 && fP == 0) // no incorrect predictions
			//				return 1.0;
			//			else 
			if (tP == 0 && fN == 0) // no positive instances
				return Double.NaN;
			else if (tN == 0 && fP == 0) // no negative instances
				return Double.NaN;
			else
				throw new IllegalStateException("WTF TP:" + tP + " FP:" + fP + " TN:" + tN + " FN:" + fN);
		}
	}

	private static NominalPrediction createPrediction(double actual, double predicted, double prob)
	{
		prob *= 0.5;
		double[] dist;
		if (actual == 1.0)
		{
			if (predicted == 1.0)
				dist = new double[] { 0.5 - prob, 0.5 + prob };
			else
				dist = new double[] { 0.5 + prob, 0.5 - prob };
		}
		else
		{
			if (predicted == 0.0)
				dist = new double[] { 0.5 + prob, 0.5 - prob };
			else
				dist = new double[] { 0.5 - prob, 0.5 + prob };
		}
		//		System.out.println(actual + " " + predicted + " " + Arrays.toString(dist));
		return new NominalPrediction(actual, dist);
	}

	@SuppressWarnings("deprecation")
	public static double computeAUC(FastVector<NominalPrediction> preds, boolean show) throws Exception
	{
		int tP = 0, tN = 0, fP = 0, fN = 0;
		for (int i = 0; i < preds.size(); i++)
		{
			NominalPrediction p = (NominalPrediction) preds.get(i);
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
		String s = "TP:" + tP + " FP:" + fP + " TN:" + tN + " FN:" + fN;
		//		System.out.println(s);
		ThresholdCurve tc = new ThresholdCurve();
		Instances result = tc.getCurve(preds, 1);
		double auc = ThresholdCurve.getROCArea(result);
		//		System.out.println(auc);
		if (show)
		{
			ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
			vmc.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(result), 4) + ")");
			vmc.setName(result.relationName());
			PlotData2D tempd = new PlotData2D(result);
			tempd.setPlotName(result.relationName());
			tempd.addInstanceNumberAttribute();
			// specify which points are connected
			boolean[] cp = new boolean[result.numInstances()];
			for (int n = 1; n < cp.length; n++)
				cp[n] = true;
			tempd.setConnectPoints(cp);
			// add plot
			vmc.addPlot(tempd);
			JDialog d = new JDialog();
			d.setTitle("auc: " + auc + " - " + s);
			d.getContentPane().add(vmc);
			d.pack();
			d.setVisible(true);
		}
		//		System.out.println();
		return auc;
	}

	@SuppressWarnings("deprecation")
	public static void main(String args[]) throws Exception
	{
		Random r = new Random();
		long seed = r.nextLong();
		//		seed = 8069103409826870036L;
		System.out.println("seed " + seed);
		r = new Random(seed);

		for (int x = 0; x < 1000; x++)
		{
			FastVector<NominalPrediction> preds = new FastVector<NominalPrediction>();

			double d[] = new double[2];
			for (int i = 0; i < 2; i++)
			{
				double activeIs1;
				if (i == 0)
					activeIs1 = 0.8;
				else
					activeIs1 = 0.2;

				FastVector<NominalPrediction> preds_i = new FastVector<NominalPrediction>();

				for (int j = 0; j < 100; j++)
				{
					double actual = r.nextDouble() < activeIs1 ? 1 : 0;
					double predicted = r.nextDouble() < activeIs1 ? 1 : 0;
					double conf = actual == predicted ? Math.min(1.0, r.nextDouble() * 1.1) : r.nextDouble() * 0.9;

					preds.add(createPrediction(actual, predicted, conf));
					preds_i.add(createPrediction(actual, predicted, conf));
				}
				d[i] = computeAUC(preds_i, false);
				System.out.println("auc " + d[i]);
			}
			double cummulative = computeAUC(preds, false);
			System.out.println("cumm=micro auc " + cummulative);

			double mean = Utils.mean(d);
			System.out.println("mean=macro auc " + mean);

			//		System.out.println(Math.abs(cummulative - mean));

			if (cummulative > d[0] && cummulative > d[1])
				break;
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
