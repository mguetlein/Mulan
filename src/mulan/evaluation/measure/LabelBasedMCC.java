package mulan.evaluation.measure;

public abstract class LabelBasedMCC extends LabelBasedBipartitionMeasureBase
{

	/**
	 * Constructs a new object with given number of labels
	 *
	 * @param numOfLabels the number of labels
	 */
	public LabelBasedMCC(ConfidenceLevel confLevel, int numOfLabels)
	{
		super(confLevel, numOfLabels);
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

	private static double tp = 0;
	private static double tn = 0;
	private static double fp = 0;
	private static double fn = 0;

	public static void test_mcc(double tp, double tn, double fp, double fn)
	{
		LabelBasedMCC.tp += tp;
		LabelBasedMCC.tn += tn;
		LabelBasedMCC.fp += fp;
		LabelBasedMCC.fn += fn;
		System.out.println(mcc(tp, tn, fp, fn));
	}

	public static void test_all()
	{
		System.out.println(mcc(tp, tn, fp, fn));
	}

	public static void main(String args[])
	{
		System.out.println("single");
		test_mcc(19, 1, 1, 1);
		test_mcc(1, 19, 1, 1);
		System.out.println("cumm");
		test_all();
	}

}
