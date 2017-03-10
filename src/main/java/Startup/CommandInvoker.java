package Startup;

import commands.Command;

public class CommandInvoker {

	private Command _updateDatesCmd;

	/**
	 * Fuehrt den ersten Befehl aus.
	 */
	public void updateDates() {
		_updateDatesCmd.execute();
	}

	/**
	 * Setter-Methode. Ermöglicht es einem Clienten wie z.B. einem
	 * Kommandofenster das setzen von auszufuehrenden Befehlen.
	 * 
	 * @param befehl
	 */
	public void setUpdateDatesCmd(Command befehl) {
		_updateDatesCmd = befehl;
	}

}
