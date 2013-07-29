package mulan.evaluation.measure;

public class ConfidenceLevelProvider
{
	private static class AbstractConfidenceLevel implements ConfidenceLevel
	{
		private String niceName;
		private String name;
		private String shortName;
		private double min;
		private double max;

		public AbstractConfidenceLevel(String niceName, String name, String shortName, double min, double max)
		{
			this.niceName = niceName;
			this.name = name;
			this.shortName = shortName;
			this.min = min;
			this.max = max;
		}

		@Override
		public boolean isInside(double confidence)
		{
			if (confidence < -0.00001 || confidence > 1.00001)
				throw new IllegalArgumentException("confidence value: " + confidence);
			double absConf;
			if (confidence >= 0.5)
				absConf = (confidence - 0.5) * 2;
			else
				absConf = (0.5 - confidence) * 2;
			absConf = Math.min(1.0, absConf); // BR returns confidence of 1.00000000XYZ
			return absConf >= min && absConf <= max;
		}

		@Override
		public String getNiceName()
		{
			return niceName;
		}

		@Override
		public String getName()
		{
			return " " + name;
		}

		@Override
		public String getShortName()
		{
			return "-" + shortName;
		}
	}

	public final static ConfidenceLevel CONFIDENCE_LEVEL_LOW = new AbstractConfidenceLevel("low confidence (<33%)",
			"low confidence", "cl", 0, 1 / 3.0);
	public final static ConfidenceLevel CONFIDENCE_LEVEL_MEDIUM = new AbstractConfidenceLevel(
			"medium confidence (>33%)", "medium confidence", "cm", 1 / 3.0, 2 / 3.0);
	public final static ConfidenceLevel CONFIDENCE_LEVEL_HIGH = new AbstractConfidenceLevel("high confidence (>66%)",
			"high confidence", "ch", 2 / 3.0, 1);
	public final static ConfidenceLevel CONFIDENCE_LEVEL_ALL = new ConfidenceLevel()
	{
		@Override
		public boolean isInside(double confidence)
		{
			return true;
		}

		@Override
		public String getNiceName()
		{
			return "ignoring confidence";
		}

		@Override
		public String getName()
		{
			return "";
		}

		@Override
		public String getShortName()
		{
			return "";
		}
	};

	public final static ConfidenceLevel[] LEVELS = { CONFIDENCE_LEVEL_ALL, CONFIDENCE_LEVEL_HIGH,
			CONFIDENCE_LEVEL_MEDIUM, CONFIDENCE_LEVEL_LOW };

	public static ConfidenceLevel getConfidence(double confidence)
	{
		if (CONFIDENCE_LEVEL_HIGH.isInside(confidence))
			return CONFIDENCE_LEVEL_HIGH;
		if (CONFIDENCE_LEVEL_MEDIUM.isInside(confidence))
			return CONFIDENCE_LEVEL_MEDIUM;
		if (CONFIDENCE_LEVEL_LOW.isInside(confidence))
			return CONFIDENCE_LEVEL_LOW;
		throw new Error("WTF");
	}
}
