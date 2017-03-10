package util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.models.Cell;
import com.smartsheet.api.models.Column;
import com.smartsheet.api.models.ContainerDestination;
import com.smartsheet.api.models.Home;
import com.smartsheet.api.models.PagedResult;
import com.smartsheet.api.models.PaginationParameters;
import com.smartsheet.api.models.Row;
import com.smartsheet.api.models.Sheet;
import com.smartsheet.api.models.enums.ColumnInclusion;
import com.smartsheet.api.models.enums.DestinationType;
import com.smartsheet.api.models.enums.SheetCopyInclusion;
import com.smartsheet.api.models.enums.SourceInclusion;

public class SheetWorker {

	Smartsheet _smartsheet;
	Home _home;

	// Aktuelle Blatteigenschaften.
	Sheet _activeSheet;
	Map<String, Column> _columns = new HashMap<String, Column>();
	Map<Integer, Column> _columnsIndex = new HashMap<Integer, Column>();
	Map<Integer, Row> _rows = new HashMap<Integer, Row>();

	Cell[][] _cellArray;
	List<Row> _updatedRows;

	/**
	 * Konstruktor für neue Exemplare der Klasse SheetWorker
	 * 
	 * @param smartsheet
	 */
	public SheetWorker(Smartsheet smartsheet) {
		try {
			_smartsheet = smartsheet;
			_home = _smartsheet.homeResources().getHome(EnumSet.of(SourceInclusion.SOURCE));
			_updatedRows = new ArrayList<Row>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prueft ob eine Spalte in dem aktuellen Sheet existiert
	 * 
	 * @param name
	 * @return
	 */
	public boolean columnExists(String name) {
		if (_columns.containsKey(name))
			return true;
		return false;
	}

	/**
	 * Kopiert ein Sheet in das eigene Homeverzeichnis
	 * 
	 * @param sheet
	 */
	public void copySheetToHome(Sheet sheet, String name) {
		try {
			// Specify destination.
			ContainerDestination destination = new ContainerDestination.AddContainerDestinationBuilder()
					.setDestinationType(DestinationType.HOME).setNewName(name).build();
			// Copy sheet (specify 'include' parameter with value of "all").
			_smartsheet.sheetResources().copySheet(sheet.getId(), destination, EnumSet.of(SheetCopyInclusion.ALL));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Private Hilfsmethode. Erstellt eine Matrix aller Zellen im Tabellenblatt
	 * für schnellen Indexzugriff auf die Zelleninhalte
	 */
	private void createCellMatrix() {
		System.out.println("Erstelle Zellenmatrix...");
		System.out.println("Zeilen: " + _rows.size());
		System.out.println("Spalten: " + _columns.size());

		_cellArray = new Cell[_rows.size()][_columns.size()];

		for (int x = 0; x < _rows.size(); x++) {
			for (int y = 0; y < _columns.size(); y++) {
				Cell cellToInsert = _rows.get(x + 1).getCells().get(y);
				cellToInsert.setRowId(_rows.get(x + 1).getId());
				cellToInsert.setColumnId(_columnsIndex.get(y).getId());
				_cellArray[x][y] = cellToInsert;
			}
		}

	}

	/**
	 * Uebertraegt alle Änderungen der Tabelle an das Original-Smartsheet-Sheet.
	 * Smartsheet.
	 * 
	 * @return
	 */
	public boolean exexuteUpdate() {
		// Update rows in sheet.
		try {
			System.out.println("Fuehre Aenderungen in " + _activeSheet.getName() + " durch... [Anzahl Updates: "
					+ _updatedRows.size() + "]");
			if (_updatedRows.size() != 0) {
				_smartsheet.sheetResources().rowResources().updateRows(_activeSheet.getId(), _updatedRows);
				_updatedRows.clear();
				System.out.println("Erledigt!");
				return true;
			} else {
				throw new IllegalStateException("Es befinden sich keine zu aktualisierenden Zellen in der Queue!");
			}
		} catch (SmartsheetException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Liefert die Referenz auf eine Zelle zurueck unter Angabe der
	 * Matrixkoordinaten x = Zeile, y = Spalte
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public Cell getCell(int row, int column) {
		return _cellArray[row][column];
	}

	/**
	 * Liefert die Referenz auf eine Spalte zurueck.
	 * 
	 * @param name
	 * @return
	 */
	public Column getColumn(String name) {
		return _columns.get(name);
	}

	/**
	 * Liefert die Referenz auf eine Spalte zurueck.
	 * 
	 * @param name
	 * @return
	 */
	public Column getColumn(int index) {
		return _columnsIndex.get(index);
	}

	/**
	 * Liefert die Gesamtanzahl aller Spalten zurueck.
	 * 
	 * @return
	 */
	public int getColumnSize() {
		return _columns.size();
	}

	/**
	 * Liefert die Referenz auf eine Zeile zurueck.
	 * 
	 * @param rowNumber
	 * @return
	 */
	public Row getRow(int rowNumber) {
		return _rows.get(rowNumber);
	}

	/**
	 * Liefert die Gesamtanzahl aller Zeilen zurueck.
	 * 
	 * @return
	 */
	public int getRowSize() {
		return _rows.size();
	}

	/**
	 * Liefert die Rerefenz auf das Smartsheetobjekt
	 * 
	 * @return
	 */
	public Smartsheet getSmartsheet() {
		return _smartsheet;
	}

	/**
	 * Liefert den Namen des aktuell geladenen Blattes zurueck.
	 * 
	 * @return
	 */
	public String getActiveSheetName() {
		return _activeSheet.getName();
	}

	/**
	 * 
	 * @return
	 */
	public Sheet getActiveSheet() {
		return _activeSheet;
	}

	/**
	 * Liefert ein Sheet anhand einer Namensangabe aus dem Homeverzeichnis
	 * zurueck.
	 * 
	 * @param name
	 * @return
	 */
	private Sheet getSheetByName(String name) {
		System.out.println("Suche Sheet mit Namen " + name + "...");
		try {
			List<Sheet> sheetData = _smartsheet.homeResources().getHome(null).getSheets();
			Sheet result = null;
			for (Sheet s : sheetData) {
				if (s.getName().equals(name)) {
					result = _smartsheet.sheetResources().getSheet(s.getId(), null, null, null, null, null, null, null);
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new NullPointerException("Es wurde kein Sheet mit dem Namen " + name + " gefunden!");
	}

	/**
	 * Laedt ein Sheet unter Angabe seines Namens aus dem Homeverzeichnis
	 * 
	 * @param name
	 */
	public void loadSheetFromHome(String name) {
		_activeSheet = this.getSheetByName(name);
		updateSheetDetails();
		createCellMatrix();
	}

	/**
	 * Aktualisiert den Zelleninhalt einer Zelle in dem aktuell geladenen Sheet.
	 * 
	 * @param cell
	 * @param value
	 */
	public void updateCell(Cell cell, Object value) {

		System.out.println("Cache Aktualisierung! [Zelle " + cell.toString() + "]");
		System.out.println("Alter Wert: " + cell.getValue());
		System.out.println("Neuer Wert: " + value.toString());
		System.out.println("");

		// Specify updated cell values for first row.
		List<Cell> updatedCell = new Cell.UpdateRowCellsBuilder().addCell(cell.getColumnId(), value.toString()).build();

		// Cache lokal alle Zellenaenderungen und warte auf executeUpdate()
		_updatedRows.add(new Row.UpdateRowBuilder().setCells(updatedCell).setRowId(cell.getRowId()).build());
	}

	/**
	 * Private Hilfsmethode, aktualisiert die Blatteingeschaften
	 */
	private void updateSheetDetails() {
		try {

			// Lade alle Daten
			PaginationParameters parameters = new PaginationParameters.PaginationParametersBuilder().setIncludeAll(true)
					.build();
			_smartsheet.sheetResources().columnResources().listColumns(_activeSheet.getId(),
					EnumSet.of(ColumnInclusion.FILTERS), parameters);
			PagedResult<Column> columnList = _smartsheet.sheetResources().columnResources()
					.listColumns(_activeSheet.getId(), null, null);

			// Spalten akutlaisieren
			System.out.println("Aktualisiere Spalteninformationen...");
			_columns.clear();
			for (Column c : columnList.getData()) {
				_columns.put(c.getTitle(), c);
			}

			_columnsIndex.clear();
			for (Column c : columnList.getData()) {
				_columnsIndex.put(c.getIndex(), c);
			}

			// Zeilen aktualisieren
			System.out.println("Aktualisiere Zeileninformationen...");
			_rows.clear();
			for (Row r : _activeSheet.getRows()) {
				_rows.put(r.getRowNumber(), r);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
