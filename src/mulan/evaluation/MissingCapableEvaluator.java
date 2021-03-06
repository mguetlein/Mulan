package mulan.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import mulan.classifier.MultiLabelLearner;
import mulan.classifier.MultiLabelOutput;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.measure.ConfidenceLevel;
import mulan.evaluation.measure.ConfidenceLevelProvider;
import mulan.evaluation.measure.HammingLoss;
import mulan.evaluation.measure.MacroAUC;
import mulan.evaluation.measure.MacroAccuracy;
import mulan.evaluation.measure.MacroFMeasure;
import mulan.evaluation.measure.MacroMCC;
import mulan.evaluation.measure.MacroNegativePredictiveValue;
import mulan.evaluation.measure.MacroPrecision;
import mulan.evaluation.measure.MacroRecall;
import mulan.evaluation.measure.MacroSpecificity;
import mulan.evaluation.measure.Measure;
import mulan.evaluation.measure.MicroAccuracy;
import mulan.evaluation.measure.MicroFMeasure;
import mulan.evaluation.measure.MicroMCC;
import mulan.evaluation.measure.MicroRecall;
import mulan.evaluation.measure.MicroSpecificity;
import mulan.evaluation.measure.SubsetAccuracy;
import weka.core.Instance;
import weka.core.Instances;
import appDomain.ApplicabilityDomainUtil;

@SuppressWarnings("javadoc")
public class MissingCapableEvaluator extends Evaluator
{

	private static void checkData(final MultiLabelInstances data)
	{
		if (data == null)
			throw new IllegalArgumentException("Evaluation data object is null.");
	}

	private static void checkLearner(final MultiLabelLearner learner)
	{
		if (learner == null)
			throw new IllegalArgumentException("Learner to be evaluated is null.");
	}

	private static void checkMeasures(final List<Measure> measures)
	{
		if (measures == null)
			throw new IllegalArgumentException("List of evaluation measures to compute is null.");
	}

	//	private static boolean[] getCutBoolean(final boolean[] bool, final boolean[] isMissing)
	//	{
	//		final boolean[] bipartition = new boolean[MissingCapableEvaluator.known(isMissing)];
	//		int n = 0;
	//		for (int i = 0; i < isMissing.length; i++)
	//		{
	//			if (!isMissing[i])
	//			{
	//				bipartition[n++] = bool[i];
	//			}
	//		}
	//		return bipartition;
	//	}

	// private int[] getCutInt(int[] ranks, boolean[] isMissing){
	// int[] bipartition = new int[known(isMissing)];
	// int n = 0;
	// for(int i = 0; i < isMissing.length; i++){
	// if(!isMissing[i]){
	// bipartition[n++] = ranks[i];
	// }
	// }
	// return bipartition;
	// }

	//	private static double[] getCutDouble(final double[] doub, final boolean[] isMissing)
	//	{
	//		final double[] bipartition = new double[MissingCapableEvaluator.known(isMissing)];
	//		int n = 0;
	//		for (int i = 0; i < isMissing.length; i++)
	//		{
	//			if (!isMissing[i])
	//			{
	//				bipartition[n++] = doub[i];
	//			}
	//		}
	//		return bipartition;
	//	}

	private static boolean[] getMissingLabels(final Instance instance, final int numLabels, final int[] labelIndices)
	{
		final boolean[] missingLabels = new boolean[numLabels];
		for (int counter = 0; counter < numLabels; counter++)
		{
			final int classIdx = labelIndices[counter];
			missingLabels[counter] = instance.isMissing(classIdx);
		}

		return missingLabels;
	}

	//	private static MultiLabelOutput getOutputForKnown(final MultiLabelOutput out, final boolean[] isMissing)
	//	{
	//		if (out.hasBipartition() && out.hasConfidences())
	//		{
	//			final boolean[] bipartition = MissingCapableEvaluator.getCutBoolean(out.getBipartition(), isMissing);
	//			final double[] confidences = MissingCapableEvaluator.getCutDouble(out.getConfidences(), isMissing);
	//			return new MultiLabelOutput(bipartition, confidences);
	//		}
	//		else if (out.hasBipartition())
	//		{
	//			final boolean[] bipartition = MissingCapableEvaluator.getCutBoolean(out.getBipartition(), isMissing);
	//			return new MultiLabelOutput(bipartition);
	//		}
	//		else if (out.hasConfidences())
	//		{
	//			final double[] confidences = MissingCapableEvaluator.getCutDouble(out.getConfidences(), isMissing);
	//			return new MultiLabelOutput(confidences);
	//		}
	//		throw new RuntimeException("Ranks not implemented");
	//	}

	private static boolean[] getTrueLabels(final Instance instance, final int numLabels, final int[] labelIndices)
	{
		final boolean[] trueLabels = new boolean[numLabels];
		for (int counter = 0; counter < numLabels; counter++)
		{
			final int classIdx = labelIndices[counter];
			final String classValue = instance.attribute(classIdx).value((int) instance.value(classIdx));
			trueLabels[counter] = classValue.equals("1");
		}

		return trueLabels;
	}

	private static int known(final boolean[] isMissing)
	{
		int notMissing = 0;
		for (int i = 0; i < isMissing.length; i++)
		{
			if (!isMissing[i])
			{
				notMissing++;
			}
		}
		return notMissing;
	}

	private static List<Measure> prepareMeasures(final MultiLabelLearner learner, final MultiLabelInstances data)
	{
		final List<Measure> measures = new ArrayList<Measure>();
		//		final boolean strict = false;

		MultiLabelOutput prediction;
		try
		{
			final MultiLabelLearner copyOfLearner = learner.makeCopy();
			prediction = copyOfLearner.makePrediction(data.getDataSet().instance(0));
			// add bipartition-based measures if applicable
			if (prediction.hasBipartition())
			{
				for (ConfidenceLevel confLevel : ConfidenceLevelProvider.LEVELS)
				{
					// add example-based measures
					measures.add(new HammingLoss(confLevel));
					measures.add(new SubsetAccuracy(confLevel));
					//				measures.add(new ExampleBasedPrecision());
					//				measures.add(new ExampleBasedRecall());
					//				measures.add(new ExampleBasedFMeasure());
					//				measures.add(new ExampleBasedAccuracy());

					int numOfLabels = data.getNumLabels();
					measures.add(new MicroAccuracy(confLevel, numOfLabels));
					measures.add(new MacroAccuracy(confLevel, numOfLabels, false));
					measures.add(new MacroAccuracy(confLevel, numOfLabels, true));

					measures.add(new MicroFMeasure(confLevel, numOfLabels));
					measures.add(new MacroFMeasure(confLevel, numOfLabels, false));
					measures.add(new MacroFMeasure(confLevel, numOfLabels, true));

					//measures.add(new MicroAUC(confLevel, numOfLabels));
					measures.add(new MacroAUC(confLevel, numOfLabels, false));
					measures.add(new MacroAUC(confLevel, numOfLabels, true));

					measures.add(new MicroMCC(confLevel, numOfLabels));
					measures.add(new MacroMCC(confLevel, numOfLabels, false));
					measures.add(new MacroMCC(confLevel, numOfLabels, true));

					measures.add(new MicroRecall(confLevel, numOfLabels));
					measures.add(new MacroRecall(confLevel, numOfLabels));

					measures.add(new MicroSpecificity(confLevel, numOfLabels));
					measures.add(new MacroSpecificity(confLevel, numOfLabels));

					measures.add(new MacroPrecision(confLevel, numOfLabels));
					measures.add(new MacroNegativePredictiveValue(confLevel, numOfLabels));
				}
				//				measures.add(new MacroPercentInsideAD(numOfLabels));
			}
			// add ranking-based measures if applicable
			if (prediction.hasRanking())
			{
				// add ranking based measures
				//				measures.add(new Coverage());
			}
		}
		catch (final Exception ex)
		{
			Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
		}

		return measures;
	}

	private SinglePredictionTracker tracker;

	@Override
	public Evaluation evaluate(final MultiLabelLearner learner, final MultiLabelInstances data)
			throws IllegalArgumentException, Exception
	{
		MissingCapableEvaluator.checkLearner(learner);
		MissingCapableEvaluator.checkData(data);

		final List<Measure> measures = MissingCapableEvaluator.prepareMeasures(learner, data);

		return this.evaluate(learner, data, measures);
	}

	@Override
	public Evaluation evaluate(final MultiLabelLearner learner, final MultiLabelInstances data,
			final List<Measure> measures) throws IllegalArgumentException, Exception
	{
		MissingCapableEvaluator.checkLearner(learner);
		MissingCapableEvaluator.checkData(data);
		MissingCapableEvaluator.checkMeasures(measures);

		//		System.err.println("start evaluate");
		//		int correct = 0;
		//		int total = 0;

		// reset measures
		for (final Measure m : measures)
		{
			m.reset();
		}

		final int numLabels = data.getNumLabels();
		final int[] labelIndices = data.getLabelIndices();
		boolean[] trueLabels = new boolean[numLabels];
		final Set<Measure> failed = new HashSet<Measure>();
		final Instances testData = data.getDataSet();
		final int numInstances = testData.numInstances();
		
		HashMap<ConfidenceLevel, int[]> numNotMissing = new HashMap<ConfidenceLevel, int[]>();
		HashMap<ConfidenceLevel, int[]> numInsideAD = new HashMap<ConfidenceLevel, int[]>();
		for (ConfidenceLevel conf : ConfidenceLevelProvider.LEVELS)
		{
			numNotMissing.put(conf, new int[trueLabels.length]);
			numInsideAD.put(conf, new int[trueLabels.length]);
		}
		
//		int numNotMissing[] = new int[trueLabels.length];
//		int numInsideAD[] = new int[trueLabels.length];
		
		for (int instanceIndex = 0; instanceIndex < numInstances; instanceIndex++)
		{
			final Instance instance = testData.instance(instanceIndex);

			int globalInstanceIndex = (int) instance.weight();
			instance.setWeight(1.0);

			final boolean[] isMissing = MissingCapableEvaluator.getMissingLabels(instance, numLabels, labelIndices);
			if (MissingCapableEvaluator.known(isMissing) == 0)
			{
				continue;
			}

			final MultiLabelOutput output = learner.makePrediction(instance);
			trueLabels = MissingCapableEvaluator.getTrueLabels(instance, numLabels, labelIndices);

			double[] appDomainProb = new double[output.getBipartition().length];
			if (appDomain != null)
				appDomainProb = appDomain.getApplicabilityDomainPropability(instance);
			else
				Arrays.fill(appDomainProb, 1.0);

			boolean[] insideAppDomain = new boolean[output.getBipartition().length];
			double[] confidenceADAdjusted = new double[output.getBipartition().length];
			for (int l = 0; l < trueLabels.length; l++)
			{
				insideAppDomain[l] = appDomainProb[l] > 0;
				confidenceADAdjusted[l] = ApplicabilityDomainUtil.adjustConfidence(output.getBipartition()[l],
						output.getConfidences()[l], appDomainProb[l]);
			}

			if (tracker != null)
				for (int l = 0; l < trueLabels.length; l++)
					if (!isMissing[l])
						tracker.update(globalInstanceIndex, l, trueLabels[l],
								output.getBipartition()[l] == trueLabels[l], output.getConfidences()[l],
								appDomainProb[l], confidenceADAdjusted[l]);

			if (appDomain != null && appDomain.isAdjustConfidence())
				for (int l = 0; l < trueLabels.length; l++)
					output.getConfidences()[l] = confidenceADAdjusted[l];

			for (ConfidenceLevel conf : ConfidenceLevelProvider.LEVELS)
			{
//				numNotMissing.put(conf, new int[trueLabels.length]);
//				numInsideAD.put(conf, new int[trueLabels.length]);
				for (int l = 0; l < trueLabels.length; l++)
					if (conf.isInside(output.getConfidences()[l]))
						if (!isMissing[l])
						{
							numNotMissing.get(conf)[l]++;
							if (insideAppDomain[l])
								numInsideAD.get(conf)[l]++;
						}
			}
			
			HashMap<ConfidenceLevel, Boolean[]> bipartition = new HashMap<ConfidenceLevel, Boolean[]>();
			HashMap<ConfidenceLevel, Boolean[]> truth = new HashMap<ConfidenceLevel, Boolean[]>();
			HashMap<ConfidenceLevel, Double[]> confidence = new HashMap<ConfidenceLevel, Double[]>();
			for (ConfidenceLevel conf : ConfidenceLevelProvider.LEVELS)
			{
				Boolean[] b = new Boolean[trueLabels.length];
				Boolean[] t = new Boolean[trueLabels.length];
				Double[] c = new Double[trueLabels.length];
				for (int i = 0; i < trueLabels.length; i++)
				{
					if (!isMissing[i] && insideAppDomain[i] && conf.isInside(output.getConfidences()[i]))
					{
						b[i] = output.getBipartition()[i];
						t[i] = trueLabels[i];
						c[i] = output.getConfidences()[i];
					}
				}
				bipartition.put(conf, b);
				truth.put(conf, t);
				confidence.put(conf, c);
			}

			final Iterator<Measure> it = measures.iterator();

			while (it.hasNext())
			{
				final Measure m = it.next();
				if (!failed.contains(m))
				{
					try
					{
						//						m.update(MissingCapableEvaluator.getOutputForKnown(output, isMissing),
						//								MissingCapableEvaluator.getCutBoolean(trueLabels, isMissing));

						m.update(bipartition.get(m.getConfidenceLevel()), truth.get(m.getConfidenceLevel()),
								confidence.get(m.getConfidenceLevel()));
					}
					catch (final Exception ex)
					{
						failed.add(m);
					}
				}
			}
		}

		//		System.err.println(total + " predictions of non-nil values");
		//		System.err.println("Correct: " + correct + " " + StringUtil.formatDouble(correct / (double) total));

		HashMap<ConfidenceLevel, double[]> pctInsideADHash = new HashMap<ConfidenceLevel, double[]>();
		for (ConfidenceLevel c : ConfidenceLevelProvider.LEVELS)
		{
			pctInsideADHash.put(c, new double[trueLabels.length]);
			double[] pctInsideAD = new double[trueLabels.length];
			for (int i = 0; i < pctInsideAD.length; i++)
				if (numNotMissing.get(c)[i] == 0)
					pctInsideAD[i] = Double.NaN;
				else 
					pctInsideAD[i] = numInsideAD.get(c)[i] / (double) numNotMissing.get(c)[i];
			pctInsideADHash.put(c, pctInsideAD);
		}

		return new Evaluation(measures, data, pctInsideADHash);
	}

	public void setSinglePredictionTracker(SinglePredictionTracker tracker)
	{
		this.tracker = tracker;
	}

}
