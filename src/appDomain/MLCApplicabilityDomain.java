package appDomain;

import java.io.Serializable;

import mulan.data.MultiLabelInstances;
import weka.core.Instance;

public interface MLCApplicabilityDomain extends Serializable
{
	void init(MultiLabelInstances data) throws Exception;

	boolean isContinous();

	boolean isInsideCompleteDataset(Instance i);

	double getApplicabilityDomainPropabilityCompleteDataset(Instance i);

	ApplicabilityDomain getApplicabilityDomainCompleteDataset();

	ApplicabilityDomain getApplicabilityDomain(int labelIndex);

	/**
	 * uses only instances that have nun-null values for label <labelIndex>
	 */
	boolean isInside(Instance i, int labelIndex);

	double getApplicabilityDomainPropability(Instance i, int labelIndex);

	double[] getApplicabilityDomainPropability(Instance i);

}
