package Startup;

import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetBuilder;

import commands.updateDatesCmd;
import util.SheetWorker;

public class Startup {

	public static void main(String[] args) {
		try {
			Smartsheet smartsheet = new SmartsheetBuilder().setAccessToken(args[0]).build();
			SheetWorker worker = new SheetWorker(smartsheet);

			CommandInvoker handler = new CommandInvoker();
			handler.setUpdateDatesCmd(new updateDatesCmd(worker));
			handler.updateDates();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
