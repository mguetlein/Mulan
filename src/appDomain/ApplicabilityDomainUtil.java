package appDomain;

public class ApplicabilityDomainUtil
{
	public static double adjustConfidence(boolean prediction, double conf, double appDomain)
	{
		if (appDomain == 0.0)
			return 0.5;
		if (appDomain == 1.0)
			return conf;
		double confInclAppDomain;
		if (prediction)
		{
			double confN = Math.max(0, conf - 0.5) * 2.0;
			double confInclAppDomainN = confN * appDomain;
			confInclAppDomain = confInclAppDomainN * 0.5 + 0.5;
		}
		else
		{
			double confN = conf * 2.0;
			double confInclAppDomainN = confN * appDomain;
			confInclAppDomain = confInclAppDomainN * 0.5;
		}
		return confInclAppDomain;
	}
}
