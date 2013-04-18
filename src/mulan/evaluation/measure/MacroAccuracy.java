package mulan.evaluation.measure;

public class MacroAccuracy extends LabelBasedAccuracy implements MacroAverageMeasure
{
	boolean weighted;

	/**
	 * Constructs a new object with given number of labels
	 * 
	 * @param numOfLabels the number of labels
	 */
	public MacroAccuracy(int numOfLabels)
	{
		this(numOfLabels, false);
	}

	public MacroAccuracy(int numOfLabels, boolean weighted)
	{
		super(numOfLabels);
		this.weighted = weighted;
	}

	public double getValue()
	{
		double sum = 0;
		double count = 0;
		for (int labelIndex = 0; labelIndex < numOfLabels; labelIndex++)
		{
			double acc = accuracy(labelIndex);
			if (!Double.isNaN(acc))
			{
				double w = 1.0;
				if (weighted)
					w = numNotMissing(labelIndex);
				sum += accuracy(labelIndex) * w;
				count += w;
			}
		}
		return sum / count;
	}

	public String getName()
	{
		return "Macro-averaged Accuracy weighted:" + weighted;
	}

	/**
	 * Returns the precision for a label
	 *
	 * @param labelIndex the index of a label (starting from 0)
	 * @return the precision for the given label
	 */
	public double getValue(int labelIndex)
	{
		return accuracy(labelIndex);
	}

	public void updateBipartition(boolean[] bipartition, boolean[] truth)
	{
		super.updateBipartition(bipartition, truth);
	}

}