package mulan.evaluation.measure;

import weka.core.Utils;

public class MicroAccuracy extends LabelBasedAccuracy
{

	/**
	 * Constructs a new object with given number of labels
	 *
	 * @param numOfLabels the number of labels
	 */
	public MicroAccuracy(int numOfLabels)
	{
		super(numOfLabels);
	}

	public double getValue()
	{
		double tp = Utils.sum(truePositives);
		double tn = Utils.sum(trueNegatives);
		double fp = Utils.sum(falsePositives);
		double fn = Utils.sum(falseNegatives);
		return accuracy(tp, tn, fp, fn);
	}

	public String getName()
	{
		return "Micro-averaged Accuracy";
	}
}
