/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    Evaluator.java
 *    Copyright (C) 2009-2012 Aristotle University of Thessaloniki, Greece
 */
package mulan.evaluation;

import imputation.Imputation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.plaf.ListUI;

import mulan.classifier.InvalidDataException;
import mulan.classifier.MultiLabelLearner;
import mulan.classifier.MultiLabelOutput;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.measure.AveragePrecision;
import mulan.evaluation.measure.Coverage;
import mulan.evaluation.measure.ErrorSetSize;
import mulan.evaluation.measure.ExampleBasedAccuracy;
import mulan.evaluation.measure.ExampleBasedFMeasure;
import mulan.evaluation.measure.ExampleBasedPrecision;
import mulan.evaluation.measure.ExampleBasedRecall;
import mulan.evaluation.measure.ExampleBasedSpecificity;
import mulan.evaluation.measure.GeometricMeanAverageInterpolatedPrecision;
import mulan.evaluation.measure.GeometricMeanAveragePrecision;
import mulan.evaluation.measure.HammingLoss;
import mulan.evaluation.measure.HierarchicalLoss;
import mulan.evaluation.measure.IsError;
import mulan.evaluation.measure.MacroAUC;
import mulan.evaluation.measure.MacroFMeasure;
import mulan.evaluation.measure.MacroPrecision;
import mulan.evaluation.measure.MacroRecall;
import mulan.evaluation.measure.MacroSpecificity;
import mulan.evaluation.measure.MeanAverageInterpolatedPrecision;
import mulan.evaluation.measure.MeanAveragePrecision;
import mulan.evaluation.measure.Measure;
import mulan.evaluation.measure.MicroAUC;
import mulan.evaluation.measure.MicroFMeasure;
import mulan.evaluation.measure.MicroPrecision;
import mulan.evaluation.measure.MicroRecall;
import mulan.evaluation.measure.MicroSpecificity;
import mulan.evaluation.measure.OneError;
import mulan.evaluation.measure.RankingLoss;
import mulan.evaluation.measure.SubsetAccuracy;
import weka.classifiers.bayes.net.search.fixed.FromFile;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import appDomain.MLCApplicabilityDomain;

/**
 * Evaluator - responsible for generating evaluation data
 * 
 * @author rofr
 * @author Grigorios Tsoumakas
 * @version 2011.09.06
 */
public class Evaluator
{

	public static boolean VERBOSE = false;
	
	// seed for reproduction of cross-validation results
	private int seed = 1;

	/**
	 * Sets the seed for reproduction of cross-validation results
	 * 
	 * @param aSeed seed for reproduction of cross-validation results
	 */
	public void setSeed(int aSeed)
	{
		seed = aSeed;
	}

	/**
	 * Evaluates a {@link MultiLabelLearner} on given test data set using specified evaluation measures
	 * 
	 * @param learner the learner to be evaluated via cross-validation
	 * @param data the data set for cross-validation
	 * @param measures the evaluation measures to compute
	 * @return an Evaluation object
	 * @throws IllegalArgumentException if an input parameter is null
	 * @throws Exception
	 */
	public Evaluation evaluate(MultiLabelLearner learner, MultiLabelInstances data, List<Measure> measures)
			throws IllegalArgumentException, Exception
	{
		checkLearner(learner);
		checkData(data);
		checkMeasures(measures);

		// reset measures
		for (Measure m : measures)
		{
			m.reset();
		}

		int numLabels = data.getNumLabels();
		int[] labelIndices = data.getLabelIndices();
		boolean[] trueLabels;
		Set<Measure> failed = new HashSet<Measure>();
		Instances testData = data.getDataSet();
		int numInstances = testData.numInstances();
		for (int instanceIndex = 0; instanceIndex < numInstances; instanceIndex++)
		{
			Instance instance = testData.instance(instanceIndex);
			if (data.hasMissingLabels(instance))
			{
				continue;
			}
			Instance labelsMissing = (Instance) instance.copy();
			labelsMissing.setDataset(instance.dataset());
			for (int i = 0; i < data.getNumLabels(); i++)
			{
				labelsMissing.setMissing(data.getLabelIndices()[i]);
			}
			MultiLabelOutput output = learner.makePrediction(labelsMissing);
			trueLabels = getTrueLabels(instance, numLabels, labelIndices);
			Iterator<Measure> it = measures.iterator();
			while (it.hasNext())
			{
				Measure m = it.next();
				if (!failed.contains(m))
				{
					try
					{
						m.update(output, trueLabels);
					}
					catch (Exception ex)
					{
						failed.add(m);
					}
				}
			}
		}

		return new Evaluation(measures, data);
	}

	private void checkLearner(MultiLabelLearner learner)
	{
		if (learner == null)
		{
			throw new IllegalArgumentException("Learner to be evaluated is null.");
		}
	}

	private void checkData(MultiLabelInstances data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("Evaluation data object is null.");
		}
	}

	private void checkMeasures(List<Measure> measures)
	{
		if (measures == null)
		{
			throw new IllegalArgumentException("List of evaluation measures to compute is null.");
		}
	}

	private void checkFolds(int someFolds)
	{
		if (someFolds < 2)
		{
			throw new IllegalArgumentException("Number of folds must be at least two or higher.");
		}
	}

	/**
	 * Evaluates a {@link MultiLabelLearner} on given test data set.
	 * 
	 * @param learner the learner to be evaluated
	 * @param data the data set for evaluation
	 * @return the evaluation result
	 * @throws IllegalArgumentException if either of input parameters is null.
	 * @throws Exception
	 */
	public Evaluation evaluate(MultiLabelLearner learner, MultiLabelInstances data) throws IllegalArgumentException,
			Exception
	{
		checkLearner(learner);
		checkData(data);

		List<Measure> measures = prepareMeasures(learner, data);

		return evaluate(learner, data, measures);
	}

	private List<Measure> prepareMeasures(MultiLabelLearner learner, MultiLabelInstances data)
	{
		List<Measure> measures = new ArrayList<Measure>();

		MultiLabelOutput prediction;
		try
		{
			MultiLabelLearner copyOfLearner = learner.makeCopy();
			prediction = copyOfLearner.makePrediction(data.getDataSet().instance(0));
			// add bipartition-based measures if applicable
			if (prediction.hasBipartition())
			{
				// add example-based measures
				measures.add(new HammingLoss());
				measures.add(new SubsetAccuracy());
				measures.add(new ExampleBasedPrecision());
				measures.add(new ExampleBasedRecall());
				measures.add(new ExampleBasedFMeasure());
				measures.add(new ExampleBasedAccuracy());
				measures.add(new ExampleBasedSpecificity());
				// add label-based measures
				int numOfLabels = data.getNumLabels();
				measures.add(new MicroPrecision(numOfLabels));
				measures.add(new MicroRecall(numOfLabels));
				measures.add(new MicroFMeasure(numOfLabels));
				measures.add(new MicroSpecificity(numOfLabels));
				measures.add(new MacroPrecision(numOfLabels));
				measures.add(new MacroRecall(numOfLabels));
				measures.add(new MacroFMeasure(numOfLabels));
				measures.add(new MacroSpecificity(numOfLabels));
			}
			// add ranking-based measures if applicable
			if (prediction.hasRanking())
			{
				// add ranking based measures
				measures.add(new AveragePrecision());
				measures.add(new Coverage());
				measures.add(new OneError());
				measures.add(new IsError());
				measures.add(new ErrorSetSize());
				measures.add(new RankingLoss());
			}
			// add confidence measures if applicable
			if (prediction.hasConfidences())
			{
				int numOfLabels = data.getNumLabels();
				measures.add(new MeanAveragePrecision(numOfLabels));
				measures.add(new GeometricMeanAveragePrecision(numOfLabels));
				measures.add(new MeanAverageInterpolatedPrecision(numOfLabels, 10));
				measures.add(new GeometricMeanAverageInterpolatedPrecision(numOfLabels, 10));
				measures.add(new MicroAUC(numOfLabels));
				measures.add(new MacroAUC(numOfLabels));
			}
			// add hierarchical measures if applicable
			if (data.getLabelsMetaData().isHierarchy())
			{
				measures.add(new HierarchicalLoss(data));
			}
		}
		catch (Exception ex)
		{
			Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
		}

		return measures;
	}

	private boolean[] getTrueLabels(Instance instance, int numLabels, int[] labelIndices)
	{

		boolean[] trueLabels = new boolean[numLabels];
		for (int counter = 0; counter < numLabels; counter++)
		{
			int classIdx = labelIndices[counter];
			String classValue = instance.attribute(classIdx).value((int) instance.value(classIdx));
			trueLabels[counter] = classValue.equals("1");
		}

		return trueLabels;
	}

	/**
	 * Evaluates a {@link MultiLabelLearner} via cross-validation on given data set with defined number of folds and seed.
	 * 
	 * @param learner the learner to be evaluated via cross-validation
	 * @param data the multi-label data set for cross-validation
	 * @param someFolds
	 * @return a {@link MultipleEvaluation} object holding the results
	 */
	public MultipleEvaluation crossValidate(MultiLabelLearner learner, MultiLabelInstances data, int someFolds)
	{
		checkLearner(learner);
		checkData(data);
		checkFolds(someFolds);

		return innerCrossValidate(learner, data, false, null, someFolds);
	}

	/**
	 * Evaluates a {@link MultiLabelLearner} via cross-validation on given data set using given evaluation measures with defined number of folds and seed.
	 * 
	 * @param learner the learner to be evaluated via cross-validation
	 * @param data the multi-label data set for cross-validation
	 * @param measures the evaluation measures to compute
	 * @param someFolds
	 * @return a {@link MultipleEvaluation} object holding the results
	 */
	public MultipleEvaluation crossValidate(MultiLabelLearner learner, MultiLabelInstances data,
			List<Measure> measures, int someFolds)
	{
		checkLearner(learner);
		checkData(data);
		checkMeasures(measures);

		return innerCrossValidate(learner, data, true, measures, someFolds);
	}

	MultiLabelLearner imputationLearner = null;
	Random randomImputation = null;

	public void setImputationLearner(MultiLabelLearner mlcAlgorithm)
	{
		if (randomImputation != null)
			throw new IllegalArgumentException("please do either set an imputation learner or enable random imputation");
		this.imputationLearner = mlcAlgorithm;
	}

	public void setImputationAtRandom(Random random)
	{
		if (imputationLearner != null)
			throw new IllegalArgumentException("please do either set an imputation learner or enable random imputation");
		this.randomImputation = random;
	}

	MLCApplicabilityDomain appDomain;

	public void setApplicabilityDomain(MLCApplicabilityDomain appDomain)
	{
		this.appDomain = appDomain;
	}

	boolean stillRunning;
	int fold;
	long start;
	long lastMsg;
	int msgInterval = 30;
	static Integer STATIC_ID = 0;
	int id;
	
	String info;
	
	public void setInfo(String info)
	{
		this.info = info;
	}
	
	
	/**
	 * returns identical object as key that can be used to synchronize
	 */
	private static String key(MultiLabelInstances data, int seed, Imputation.Mode imputation)
	{
		String k = data.hashCode()+"_seed:"+seed+"_imp:"+imputation;
		if (!keyObjects.containsKey(k))
			keyObjects.put(k,k);
		return keyObjects.get(k);
	}
	
	private static class CVDataCache
	{
		MultiLabelInstances train[];
		MultiLabelInstances test[];
	}
	
	private static HashMap<String, String> keyObjects = new HashMap<String, String>();
	private static HashMap<String, CVDataCache> cache = new HashMap<String, CVDataCache>();
	
	private static MultiLabelInstances getSet(int f, boolean training, MultiLabelInstances data, int seed, int numFolds, Imputation.Mode imputation)
	{
		CVDataCache cvCache;
		String k = key(data,seed, imputation); 
		synchronized (k)
		{
			if (!cache.containsKey(k))
			{
				System.out.println("Create cross-validation data for "+data+", seed: "+seed+", imputation:"+imputation);
				
				cvCache = new CVDataCache();
				cvCache.train = new MultiLabelInstances[numFolds];
				cvCache.test = new MultiLabelInstances[numFolds];
				
				Instances workingSet = new Instances(data.getDataSet());
				int c = 0;
				for (Instance instance : workingSet)
					instance.setWeight(c++);
				workingSet.randomize(new Random(seed));
				for (int fold = 0; fold < numFolds; fold++)
				{
					try
					{
						Instances train = workingSet.trainCV(numFolds, fold);
						for (Instance instance : train)
							instance.setWeight(1.0);
						Instances test = workingSet.testCV(numFolds, fold);
						MultiLabelInstances mlTrain = new MultiLabelInstances(train, data.getLabelsMetaData());
						if (imputation != Imputation.Mode.disabled)
							Imputation.apply(imputation, mlTrain);
						MultiLabelInstances mlTest = new MultiLabelInstances(test, data.getLabelsMetaData());
						
						cvCache.train[fold] = mlTrain;
						cvCache.test[fold] = mlTest;
					}
					catch (Exception ex)
					{
						Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
						throw new Error(ex);
					}
				}
				cache.put(k, cvCache);
			}
			cvCache = cache.get(k);
		}
		if (training)
			return cvCache.train[f];
		else
			return cvCache.test[f];
	}
	
	private MultipleEvaluation innerCrossValidate(MultiLabelLearner learner, MultiLabelInstances data,
			boolean hasMeasures, List<Measure> measures, int someFolds)
	{
		synchronized (STATIC_ID)
		{
			STATIC_ID++;
			id = STATIC_ID;
		}
		stillRunning = true;
		fold = -1;
		start = System.currentTimeMillis();
		lastMsg = start;
		if (info != null)
		{
			Thread th = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					while(stillRunning)
					{
						try
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						long running = (System.currentTimeMillis() - lastMsg)/1000;
						if (running > msgInterval)
						{
							int minutes = (int)((System.currentTimeMillis() - start)/60000); 
							System.out.println(id+" "+minutes+"min f:"+fold+" - "+info);
							lastMsg = System.currentTimeMillis();
						}
					}
				}
			});
			th.start();
		}
		
		Evaluation[] evaluation = new Evaluation[someFolds];
		
//		Imputation.Mode imp = Imputation.Mode.disabled;
//		if (imputationLearner != null)
//			imp = Imputation.Mode.enabled;
//		else if (randomImputation!=null)
//			imp = Imputation.Mode.random;
//		
//		for (fold = 0; fold < someFolds; fold++)
//		{
//			if (VERBOSE)
//			  System.out.println("Fold " + (fold + 1) + "/" + someFolds);
//			
//			try
//			{
//				MultiLabelInstances mlTrain = getSet(fold, true, data, seed, someFolds, imp);				
//				MultiLabelInstances mlTest = getSet(fold, false, data, seed, someFolds, imp);
//				MultiLabelLearner clone = learner.makeCopy();
//				clone.build(mlTrain);
//				if (appDomain != null)
//					appDomain.init(mlTrain);
//				if (hasMeasures)
//					evaluation[fold] = evaluate(clone, mlTest, measures);
//				else
//					evaluation[fold] = evaluate(clone, mlTest);
//			}
//			catch (Exception e)
//			{
//				Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, e);
//				throw new Error(e);
//			}
//		}

		Instances workingSet = new Instances(data.getDataSet());
		int c = 0;
		for (Instance instance : workingSet)
			instance.setWeight(c++);

		workingSet.randomize(new Random(seed));
		for (fold = 0; fold < someFolds; fold++)
		{
			if (VERBOSE)
			  System.out.println("Fold " + (fold + 1) + "/" + someFolds);
			try
			{
				Instances train = workingSet.trainCV(someFolds, fold);
				for (Instance instance : train)
					instance.setWeight(1.0);

				Instances test = workingSet.testCV(someFolds, fold);
				test = new Instances(test);
				MultiLabelInstances mlTrain = new MultiLabelInstances(train, data.getLabelsMetaData());

				if (appDomain != null)
					appDomain.init(mlTrain);

				if (imputationLearner != null || randomImputation != null)
				{
					if (VERBOSE)
						System.out.println("Start imputation");
					Imputation.apply(imputationLearner, randomImputation, mlTrain);
					if (VERBOSE)
						System.out.println("Imputation done");
				}
				
				MultiLabelInstances mlTest = new MultiLabelInstances(test, data.getLabelsMetaData());
				MultiLabelLearner clone = learner.makeCopy();
				clone.build(mlTrain);

				if (hasMeasures)
					evaluation[fold] = evaluate(clone, mlTest, measures);
				else
					evaluation[fold] = evaluate(clone, mlTest);
			}
			catch (Exception ex)
			{
				Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		MultipleEvaluation me = new MultipleEvaluation(evaluation, data);
		me.calculateStatistics();
		stillRunning = false;
		return me;
	}
}