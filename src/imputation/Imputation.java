package imputation;

import java.util.Random;

import mulan.classifier.MultiLabelLearner;
import mulan.classifier.MultiLabelOutput;
import mulan.classifier.transformation.EnsembleOfClassifierChains;
import mulan.data.MultiLabelInstances;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;

public class Imputation
{
	public static enum Mode
	{
		enabled, disabled, random
	}
	
	public static void apply(Mode mode, MultiLabelInstances mlTrain)
			throws Exception
	{
		if (mode==Mode.disabled)
			throw new IllegalArgumentException();
		if (mode == Mode.enabled)
			apply(new EnsembleOfClassifierChains(new RandomForest(), 15, true, false), null, mlTrain);
		if (mode==Mode.random)
			apply(null, new Random(), mlTrain);
	}
	
	public static void apply(MultiLabelLearner imputationLearner, Random randomImputation, MultiLabelInstances mlTrain)
			throws Exception
	{
		if ((imputationLearner == null && randomImputation == null)
				|| (imputationLearner != null && randomImputation != null))
			throw new IllegalStateException("plz provide either imputationLearner or randomImputation");

		int fillCounter = 0;
		int nonMissing = 0;

		MultiLabelLearner imputationLearnerClone = null;
		if (imputationLearner != null)
		{
			imputationLearnerClone = imputationLearner.makeCopy();
			imputationLearnerClone.build(mlTrain);
		}
		//					for (int j = 0; j < mlTrain.getNumLabels(); j++)
		//					{
		//						Attribute label = mlTrain.getDataSet().attribute(mlTrain.getLabelIndices()[j]);
		//						if (mlTrain.getDataSet().numDistinctValues(label) != 2)
		//							throw new Error("WTF");
		//					}
		for (int instanceIndex = 0; instanceIndex < mlTrain.getNumInstances(); instanceIndex++)
		{
			boolean hasMissing = false;
			for (int j = 0; j < mlTrain.getNumLabels(); j++)
			{
				Attribute label = mlTrain.getDataSet().attribute(mlTrain.getLabelIndices()[j]);
				//							System.err.println(i + " " + j + " " + label.index());

				if (mlTrain.getDataSet().get(instanceIndex).isMissing(label))
				{
					hasMissing = true;
					break;
				}
				else
				{
					//just checking
					String val = mlTrain.getDataSet().get(j).stringValue(label);
					if (!val.equals("1") && !val.equals("0"))
						throw new Error("WTF : " + val);
				}
			}
			if (hasMissing)
			{
				MultiLabelOutput prediction = null;
				if (imputationLearner != null)
				{
					prediction = imputationLearnerClone.makePrediction(mlTrain.getDataSet().get(instanceIndex));
					if (prediction.getBipartition().length != mlTrain.getNumLabels())
						throw new Error("WTF");
				}
				for (int labelIndex = 0; labelIndex < mlTrain.getNumLabels(); labelIndex++)
				{
					Attribute label = mlTrain.getDataSet().attribute(mlTrain.getLabelIndices()[labelIndex]);
					if (mlTrain.getDataSet().get(instanceIndex).isMissing(label))
					{
						fillCounter++;
						if (imputationLearner != null)
							mlTrain.getDataSet().get(instanceIndex)
									.setValue(label, prediction.getBipartition()[labelIndex] ? "1" : "0");
						else if (randomImputation != null)
							mlTrain.getDataSet().get(instanceIndex)
									.setValue(label, randomImputation.nextBoolean() ? "1" : "0");
						else
							throw new Error("WTF");
					}
					else
						nonMissing++;
				}
			}
			else
				nonMissing += mlTrain.getNumLabels();
		}
		if (fillCounter + nonMissing != mlTrain.getNumInstances() * mlTrain.getNumLabels())
			throw new Error("WTF");
		//					System.out.println("fill a total of " + fillCounter + " missing values in training fold with "
		//							+ mlTrain.getNumInstances() + " instances and " + mlTrain.getNumLabels()
		//							+ " labels (non-missing: " + nonMissing + ")");
	}
}
