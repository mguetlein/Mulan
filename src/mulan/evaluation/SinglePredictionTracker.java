package mulan.evaluation;

import java.util.HashMap;
import java.util.HashSet;

import mulan.data.MultiLabelInstances;

public class SinglePredictionTracker
{
	public class Predictions
	{
		private Boolean trueValue = null;
		protected int numTotal = 0;
		protected int numCorrect = 0;

		public Predictions(Boolean trueValue)
		{
			this.trueValue = trueValue;
		}

		public void update(boolean correct)
		{
			if (correct)
				numCorrect++;
			numTotal++;
		}

		public Double getClassified()
		{
			if (numTotal != numRepetitions)
				throw new IllegalStateException();
			if (trueValue == true)
				return (numCorrect / (double) numTotal);
			else
				return (1 - (numCorrect / (double) numTotal));
		}

		public Double getMissclassified()
		{
			if (numTotal != numRepetitions)
				throw new IllegalStateException();
			return (1 - (numCorrect / (double) numTotal));
		}

		public Double getMissclassifiedAsTrue()
		{
			if (!trueValue)
				return getMissclassified();
			else
				return null;
		}

		public Double getMissclassifiedAsFalse()
		{
			if (trueValue)
				return getMissclassified();
			else
				return null;
		}
	}

	public class AllLabelPredictions extends Predictions
	{
		HashSet<Integer> labels = new HashSet<Integer>();

		public AllLabelPredictions()
		{
			super(null);
		}

		public void update(int label, boolean correct)
		{
			labels.add(label);
			super.update(correct);
		}

		public Double getMissclassified()
		{
			if (numTotal != (numRepetitions * labels.size()))
				throw new IllegalStateException();
			return (1 - (numCorrect / (double) numTotal));
		}

		public String getNumLabels()
		{
			return labels.size() + "";
		}

		public String getMissclassifiedTotal()
		{
			if (numTotal != (numRepetitions * labels.size()))
				throw new IllegalStateException();
			double totalMiss = numTotal - numCorrect;
			totalMiss /= (double) numRepetitions;
			return totalMiss + "";
		}

	}

	HashMap<String, Predictions> labelPredictions = new HashMap<String, Predictions>();
	HashMap<Integer, AllLabelPredictions> allLabelPredictions = new HashMap<Integer, AllLabelPredictions>();

	String datasetName;
	MultiLabelInstances data;
	int numRepetitions;
	String experimentName;

	public SinglePredictionTracker(String datasetName, String experimentName, MultiLabelInstances data,
			int numRepetitions)
	{
		this.datasetName = datasetName;
		this.experimentName = experimentName;
		this.data = data;
		this.numRepetitions = numRepetitions;
	}

	public synchronized void update(int globalInstanceIndex, int labelIndex, boolean trueValue, boolean correct,
			double origConf, double appDomain, double confInclAppDomain)
	{
		if (!labelPredictions.containsKey(globalInstanceIndex + "_" + labelIndex))
			labelPredictions.put(globalInstanceIndex + "_" + labelIndex, new Predictions(trueValue));
		labelPredictions.get(globalInstanceIndex + "_" + labelIndex).update(correct);

		if (!allLabelPredictions.containsKey(globalInstanceIndex))
			allLabelPredictions.put(globalInstanceIndex, new AllLabelPredictions());
		allLabelPredictions.get(globalInstanceIndex).update(labelIndex, correct);
	}

	public MultiLabelInstances getData()
	{
		return data;
	}

}
