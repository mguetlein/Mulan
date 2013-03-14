package mulan.evaluation.measure;

public abstract class LabelBasedAccuracy extends LabelBasedBipartitionMeasureBase
{

	/**
	 * Constructs a new object with given number of labels
	 *
	 * @param numOfLabels the number of labels
	 */
	public LabelBasedAccuracy(int numOfLabels)
	{
		super(numOfLabels);
	}

	public double getIdealValue()
	{
		return 1;
	}

	public double accuracy(int labelIndex)
	{
		double tp = truePositives[labelIndex];
		double tn = trueNegatives[labelIndex];
		double fp = falsePositives[labelIndex];
		double fn = falseNegatives[labelIndex];
		return accuracy(tp, tn, fp, fn);
	}

	protected static double accuracy(double tp, double tn, double fp, double fn)
	{
		if ((tp + tn) == 0)
			return 0;
		return (tp + tn) / (tp + tn + fp + fn);
	}

}
