package mulan.evaluation.measure;

public abstract class LabelBasedMCC extends LabelBasedBipartitionMeasureBase
{

	/**
	 * Constructs a new object with given number of labels
	 *
	 * @param numOfLabels the number of labels
	 */
	public LabelBasedMCC(int numOfLabels)
	{
		super(numOfLabels);
	}

	public double getIdealValue()
	{
		return 1;
	}

	public double mcc(int labelIndex)
	{
		double tp = truePositives[labelIndex];
		double tn = trueNegatives[labelIndex];
		double fp = falsePositives[labelIndex];
		double fn = falseNegatives[labelIndex];
		return mcc(tp, tn, fp, fn);
	}

	protected static double mcc(double tp, double tn, double fp, double fn)
	{
		double denominator = Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));
		if (denominator == 0)
			return Double.NaN;
		return (tp * tn - fp * fn) / denominator;
	}

}
