package appDomain;

import weka.core.Instance;

public interface DistanceBasedApplicabilityDomain extends ApplicabilityDomain
{
	DistanceBasedApplicabilityDomain copy();

	double[] getTrainingDistances();

	double getMedianDistance();

	double getApplicabilityDomainDistance();

	String getDistanceDescription();

	double getContinousFullApplicabilityDomainDistance();

	double getDistance(Instance i);

	double getApplicabilityDomainPropability(Double distance);
}
