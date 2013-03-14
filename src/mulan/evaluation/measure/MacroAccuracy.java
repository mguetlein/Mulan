package mulan.evaluation.measure;

public class MacroAccuracy extends LabelBasedAccuracy implements MacroAverageMeasure
{

	/**
	 * Constructs a new object with given number of labels
	 * 
	 * @param numOfLabels the number of labels
	 */
	public MacroAccuracy(int numOfLabels)
	{
		super(numOfLabels);
	}

	public double getValue()
	{
		double sum = 0;
		int count = 0;
		for (int labelIndex = 0; labelIndex < numOfLabels; labelIndex++)
		{
			sum += accuracy(labelIndex);
			count++;
		}
		return sum / count;
	}

	public String getName()
	{
		return "Macro-averaged Accuracy";
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