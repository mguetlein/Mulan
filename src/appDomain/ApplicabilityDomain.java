package appDomain;

import java.io.Serializable;

import weka.core.Instance;
import weka.core.Instances;

public interface ApplicabilityDomain extends Serializable
{
	void init(Instances inst) throws Exception;

	boolean isInside(Instance i);

	double getApplicabilityDomainPropability(Instance i);

	boolean isAdjustConfidence();

	Instances getData();

	ApplicabilityDomain copy();

}
