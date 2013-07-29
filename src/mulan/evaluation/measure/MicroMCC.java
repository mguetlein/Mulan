package mulan.evaluation.measure;

import weka.core.Utils;

public class MicroMCC extends LabelBasedMCC
{

	/**
	 * Constructs a new object with given number of labels
	 *
	 * @param numOfLabels the number of labels
	 */
	public MicroMCC(ConfidenceLevel confLevel, int numOfLabels)
	{
		super(confLevel, numOfLabels);
	}

	public double getValue()
	{
		double tp = Utils.sum(truePositives);
		double tn = Utils.sum(trueNegatives);
		double fp = Utils.sum(falsePositives);
		double fn = Utils.sum(falseNegatives);
		return mcc(tp, tn, fp, fn);
	}

	public String getName()
	{
		return "Micro-averaged MCC" + confLevel.getName();
	}
}
