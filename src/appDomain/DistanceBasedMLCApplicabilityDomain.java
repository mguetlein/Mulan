package appDomain;

import weka.core.Instance;

public interface DistanceBasedMLCApplicabilityDomain extends MLCApplicabilityDomain
{
	DistanceBasedApplicabilityDomain getApplicabilityDomainCompleteDataset();

	DistanceBasedApplicabilityDomain getApplicabilityDomain(int labelIndex);

	double getDistance(Instance i, int labelIndex);

	double getDistanceCompleteDataset(Instance i);
}
