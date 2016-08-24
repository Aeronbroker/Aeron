package eu.neclab.iotplatform.mocks.utils;

public enum Mode {
	RANDOM("random"), FROMFILE("fromfile");

	private String mode;

	Mode(String mode) {
		this.mode = mode;
	}

	public String getMode() {
		return this.mode;
	}

	public static Mode fromString(String mode) {
		if (mode != null) {
			for (Mode m : Mode.values()) {
				if (mode.equalsIgnoreCase(m.mode)) {
					return m;
				}
			}
		}
		return null;
	}
	
	public static Mode fromString(String mode, Mode defaultMode) {
		if (mode != null) {
			for (Mode m : Mode.values()) {
				if (mode.equalsIgnoreCase(m.mode)) {
					return m;
				}
			}
		}
		return defaultMode;
	}
	
	

}
