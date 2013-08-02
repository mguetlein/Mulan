package appDomain;

import weka.core.Instance;

public interface DistanceBasedApplicabilityDomain extends ApplicabilityDomain
{
	public static enum Method
	{
		median, mean
	}

	DistanceBasedApplicabilityDomain copy();

	double[] getTrainingDistances();

	double getAverageTrainingDistance();

	double getMaxTrainingDistance();

	double getApplicabilityDomainDistance();

	String getDistanceDescription();

	double getContinousFullApplicabilityDomainDistance();

	double getDistance(Instance i);

	double getApplicabilityDomainPropability(Double distance);

	public void setMethod(Method method);

	public void setDistanceMultiplier(double distance);

	public void setAdjustConfidence(boolean adjustConfidence);

	public void setFullDistanceMultiplier(double fullDistanceMultiplier);
}
