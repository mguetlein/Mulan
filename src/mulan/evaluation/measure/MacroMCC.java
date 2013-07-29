package mulan.evaluation.measure;

public class MacroMCC extends LabelBasedMCC implements MacroAverageMeasure
{
	boolean weighted;

	/**
	 * Constructs a new object with given number of labels
	 * 
	 * @param numOfLabels the number of labels
	 */
	public MacroMCC(int numOfLabels)
	{
		this(ConfidenceLevelProvider.CONFIDENCE_LEVEL_ALL, numOfLabels, false);
	}

	public MacroMCC(ConfidenceLevel confLevel, int numOfLabels, boolean weighted)
	{
		super(confLevel, numOfLabels);
		this.weighted = weighted;
	}

	public double getValue()
	{
		double sum = 0;
		double count = 0;
		for (int labelIndex = 0; labelIndex < numOfLabels; labelIndex++)
		{
			double acc = mcc(labelIndex);
			if (!Double.isNaN(acc))
			{
				double w = 1.0;
				if (weighted)
					w = numNotMissing(labelIndex);
				sum += mcc(labelIndex) * w;
				count += w;
			}
		}
		return sum / count;
	}

	public String getName()
	{
		return "Macro-averaged MCC weighted:" + weighted + confLevel.getName();
	}

	/**
	 * Returns the precision for a label
	 *
	 * @param labelIndex the index of a label (starting from 0)
	 * @return the precision for the given label
	 */
	public double getValue(int labelIndex)
	{
		return mcc(labelIndex);
	}

	public void updateBipartition(boolean[] bipartition, boolean[] truth)
	{
		super.updateBipartition(bipartition, truth);
	}

}