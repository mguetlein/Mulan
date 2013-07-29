package mulan.evaluation.measure;

import java.io.Serializable;

public interface ConfidenceLevel extends Serializable
{
	public boolean isInside(double confidence);

	public String getName();

	public String getShortName();

	public String getNiceName();
}
