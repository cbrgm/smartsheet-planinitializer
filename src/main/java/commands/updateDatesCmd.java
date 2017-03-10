package commands;

import com.smartsheet.api.models.Cell;
import com.smartsheet.api.models.Sheet;

import util.DateCalculator;
import util.InputValidator;
import util.SheetWorker;

public class updateDatesCmd implements Command {

	private SheetWorker _sheetWorker;
	private DateCalculator _dateCalculator;

	private int vgTurnus;
	private String vgName;
	private String vgAktVG;
	private String vgVorVG;
	private String vgStTag1;
	private String vgStTag2;

	/**
	 * Konstruktor fuer neue Exemplare der Klasse ConcreteCommandA.
	 */
	public updateDatesCmd(SheetWorker sheetWorker) {
		this._sheetWorker = sheetWorker;
		this._dateCalculator = new DateCalculator();
	}

	/**
	 * Ueberschreiben der Methode execute in der Klasse ConcreteCommandA. Fuer
	 * Details zur Implementierung siehe:
	 * 
	 * @see patterns.behavior.command.Command#execute()
	 */
	public void execute() {

		try {
			this.clearParameters();
			System.out.println("HMC-SMARTSHEET, PMT-PLANINITIALISIERER 1.0");
			System.out.println("Autor: Christian Bargmann, 2017-03-08 http://github.com/cbrgm/");
			System.out.println("");
			System.out.println("Benutze die API-Schnittstelle von Account: "
					+ _sheetWorker.getSmartsheet().userResources().getCurrentUser().getEmail());
			System.out.println(
					"Das gestartete Programm legt einen neuen Veranstaltungs-Projektplan für eine Veranstaltung an!");
			System.out.println(
					"Sie werden Schritt fuer Schritt durch den Anlageprozess gefuehrt. Moechten Sie fortfahren?");

			if (!InputValidator.getInstance().handleBooleanInput())
				return;

			System.out.println(
					"Der Hauptplan muss fuer den angemeldeten Benutzer freigegeben sein, \n oder im Blaetter-Ordner auf oberster Ebene des angemeldeten Benutzers liegen.");
			System.out.println("Bitte geben Sie den Namen der Hauptplan-Vorlage an!");

			_sheetWorker.loadSheetFromHome(InputValidator.getInstance().handleStringInput());

			System.out.println("Pruefe ob im Hauptplan " + _sheetWorker.getActiveSheetName()
					+ " die benoetigten Spalten existieren...");

			// Pruefen ob Spalten vorhanden sind
			if (!_sheetWorker.columnExists("Start Datum")) {
				System.out.println("Spalte Start Datum existiert nicht! Abbruch");
				return;
			}

			if (!_sheetWorker.columnExists("Ende Datum")) {
				System.out.println("Spalte Ende Datum existiert nicht! Abbruch");
				return;
			}

			if (!_sheetWorker.columnExists("Bezug 1J")) {
				System.out.println("Spalte Bezug 1J existiert nicht! Abbruch");
				return;
			}

			if (!_sheetWorker.columnExists("Delta 1J")) {
				System.out.println("Spalte Delta 1J existiert nicht! Abbruch");
				return;
			}

			if (!_sheetWorker.columnExists("Bezug 2J")) {
				System.out.println("Spalte Bezug 2J existiert nicht! Abbruch");
				return;
			}

			if (!_sheetWorker.columnExists("Delta 2J")) {
				System.out.println("Spalte Delta 2J existiert nicht! Abbruch");
				return;
			}

			System.out.println("Erledigt! Alle benoetigten Spalten sind vorhanden!");

			System.out.println("Bitte geben Sie den Namen bzw. Kuerzel der Veranstaltung an!");
			vgName = InputValidator.getInstance().handleStringInput();

			System.out.println("Bitte geben Sie den Veranstaltungs-Turnus (1 Jahres-Rythmus, 2-Jahres Rythmus an)");
			vgTurnus = InputValidator.getInstance().handleIntegerInputRange(0, 2);

			System.out.println("Bitte geben Sie den Beginn von " + vgName + " an (Kuerzel AktVG)");
			vgAktVG = InputValidator.getInstance().handleDateInputRegex();

			System.out.println("Bitte geben sie das Ende von  " + vgName + " an (Kuerzel StTag1)");
			vgStTag1 = InputValidator.getInstance().handleDateInputRegex();

			System.out.println("Gab es eine Vorveranstaltung? (Kuerzel VorVG)");
			if (InputValidator.getInstance().handleBooleanInput()) {
				System.out.println("Bitte geben Sie den LETZTEN Veranstaltungstag der Vorveranstaltung an!");
				vgVorVG = InputValidator.getInstance().handleDateInputRegex();
			}

			System.out.println("Bitte geben sie das Budgetjahr von  " + vgName
					+ " an! (In der Regel selbes Jahr wie Veranstaltung beginnend am 01.01.xx) (Kuerzel StTag2)");
			vgStTag2 = InputValidator.getInstance().handleDateInputRegex();

			System.out.println("ACHTUNG: Es wird eine Kopie von " + _sheetWorker.getActiveSheetName()
					+ " angelegt und im Anschluss die Ereigniszeitraeume aktualisiert! \n Der automatisch erstelle Hauptplan wird im Blaetter-Ordner des Benutzers "
					+ _sheetWorker.getSmartsheet().userResources().getCurrentUser().getEmail() + " gesichert!");

			System.out.println("");
			System.out.println("Folgende Parameter wurden eingetragen! Bitte kontrollieren Sie!");
			System.out.println("Veranstaltungs-Turnus : " + vgTurnus);
			System.out.println("Veranstaltungs-Name : " + vgName);
			System.out.println("Erster Veranstaltungstag (AktVG) " + vgName + " : " + vgAktVG);
			System.out.println("Letzter Veranstaltungstag (StTag1)  " + vgName + " : " + vgStTag1);
			System.out.println("Vorveranstaltung (Leer wenn nicht vorhanden, VorVg) : " + vgVorVG);
			System.out.println("Budgetjahr " + vgName + " : " + vgStTag2);
			System.out.println("");
			System.out.println("Moechten Sie fortfahren?");

			if (!InputValidator.getInstance().handleBooleanInput())
				return;

			// Turnus + Stichtage abfragen

			this.performSheetUpdate(vgTurnus, vgName, vgAktVG, vgVorVG, vgStTag1, vgStTag2);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void performSheetUpdate(int vgTurnus, String vgName, String vgAktVG, String vgVorVG, String vgStTag1,
			String vgStTag2) {

		String bezug = "";
		String sheetName = vgName + " Hauptplan (Generiert)";

		// Kopiere Plan
		System.out.println("Erstelle Kopie von " + _sheetWorker.getActiveSheetName() + "...");
		Sheet planToCopy = _sheetWorker.getActiveSheet();
		_sheetWorker.copySheetToHome(planToCopy, sheetName);
		System.out.println("Erledigt! Plan wurde unter [" + sheetName + " gespeichert!");

		_sheetWorker.loadSheetFromHome(sheetName);

		// Fuer alle Zeilen des Blattes
		for (int i = 0; i < _sheetWorker.getRowSize(); i++) {

			if (_sheetWorker.getCell(i, _sheetWorker.getColumn("Bezug " + vgTurnus + "J").getIndex())
					.getValue() != null) {
				// Nimm den Wert aus der Bezugstabelle abhaengig vom gewaehlten
				// Turnus
				bezug = _sheetWorker.getCell(i, _sheetWorker.getColumn("Bezug " + vgTurnus + "J").getIndex()).getValue()
						.toString();
				// Kalkuliere Anfangsdatum
				if (bezug.equals("VorVg")) {

					if (!vgVorVG.equals(""))
						updateVgDate(i, vgVorVG);

				} else if (bezug.equals("AktVg")) {

					if (!vgAktVG.equals(""))
						updateVgDate(i, vgAktVG);

				} else if (bezug.equals("StTag1")) {

					if (!vgStTag1.equals(""))
						updateVgDate(i, vgStTag1);

				} else if (bezug.equals("StTag2")) {

					if (!vgStTag2.equals(""))
						updateVgDate(i, vgStTag2);

				}
			} else {
				System.out.println("HINWEIS: Zeile " + i + " enthaelt keine Turnusangabe! Ueberspringe...");
				System.out.println("");
			}
		}

		System.out.println("Uebertrage Aenderungen in den Plan... Kann einen Moment dauern!");
		_sheetWorker.exexuteUpdate();

		System.out.println("");
		System.out.println("Daten wurden erfolgreich aktualisiert!");
		System.out.println("Programm wird nun beendet...");
		System.out.println("Tschuess!");
	}

	/**
	 * Setzt die Defaultwerte fuer Parameterwerte
	 */
	private void clearParameters() {
		vgTurnus = -1;
		vgName = "";
		vgAktVG = "";
		vgVorVG = "";
		vgStTag1 = "";
		vgStTag2 = "";
	}

	private void updateVgDate(int row, String vgDate) {
		// Suche die Zelle, in die das kalkulierte Ergebnis geschrieben werden
		// soll.
		Cell cellToUpdate = _sheetWorker.getCell(row, _sheetWorker.getColumn("Start Datum").getIndex());

		// Suche das Delta aus der Tabelle
		Double delta = (Double) _sheetWorker.getCell(row, _sheetWorker.getColumn("Delta " + vgTurnus + "J").getIndex())
				.getValue();

		// Berechne neues Datum
		String dateToSet = _dateCalculator.calculateDate(vgDate, delta);

		// Fuege Aenderung in Update-Queue ein.
		_sheetWorker.updateCell(cellToUpdate, dateToSet);
	}
}
