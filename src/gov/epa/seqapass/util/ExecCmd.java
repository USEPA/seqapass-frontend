package gov.epa.seqapass.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecCmd {
	private String cmd;
	private StringBuilder standardOut = new StringBuilder("");
	private StringBuilder standardErr = new StringBuilder("");

	public ExecCmd(String cmd) {
		this.cmd = cmd;
		execute();
	}

	private void execute() {
		String stdOut;
		String stdErr;
		try {
			System.out.println("Runtime.getRuntime().exec("+cmd+")");
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((stdOut = stdInput.readLine()) != null) {
				standardOut.append(stdOut + "\n");
			}

			while ((stdErr = stdError.readLine()) != null) {
				standardErr.append(stdErr + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStandardOut() {
		return standardOut.toString();
	}

	public String getStandardErr() {
		return standardErr.toString();
	}

}
