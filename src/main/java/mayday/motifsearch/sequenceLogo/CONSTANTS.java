package mayday.motifsearch.sequenceLogo;

public abstract class CONSTANTS {
	public enum EXITCODE {
		/**
		 * The program has exited successfully.
		 */
		EXIT_SUCCESSFUL(0),
		/**
		 * One or more commandline-arguments are either invalid or missing.
		 */
		INVALID_ARGUMENT(1),
		/**
		 * The environment is invalid (e.g. no gui-support but gui called).
		 */
		INVALID_ENVIRONMENT(2),
		/**
		 * The data-matrix specified is invalid or the corresponding infile
		 * could not be read.
		 */
		INVALID_DATA(3),
		/**
		 * An irrecoverable runtime-error occurred during the calculation.
		 */
		RUNTIME_ERROR(4),
		/**
		 * Some kind of unspecified and unexpected error occurred.
		 */
		UNKNOWN_ERROR(5);

		public final int exitvalue;

		EXITCODE(int val) {
			exitvalue = val;
		}
	}

	/**
	 * Alphabet of all nucleotides used. Must contain exactly those nucleotids
	 * found in the fasta-files used.
	 */
	public final static char[] ALPHABET = { 'A', 'C', 'G', 'T' };
	/**
	 * Small value that is added to avoid DIV-BY-ZERO-related problems.
	 * 
	 * Default: <var>1E-12</var>
	 */
	public static final double EPSILON = 1E-12d;

	/**
	 * log_10(2). log_2(x) = log10(x) / log10(2) = log10(x) / LOG2.
	 */
	public final static double LOG2 = Math.log10(2);
}
