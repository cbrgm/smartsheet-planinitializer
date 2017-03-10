package util;

import java.util.Scanner;

public class InputValidator {

	private final String MESSAGE_PREFIX = "[Warte auf Eingabe...] ";

	// Field hält Referenz auf einzigartige Instanz
	private static InputValidator instance = new InputValidator();

	// Privater Konstruktur verhindert Instanziierung durch Client
	private InputValidator() {
	}

	// Stellt Einzigartigkeit sicher. Liefert Exemplar an Client.
	// Hier: Synchronisierte Eager-Loading-Variante
	public static InputValidator getInstance() {
		return instance;
	}

	/**
	 * Erwartet vom Benutzer eine Eingabe als Boolean. Der Benutzer ist
	 * aufgefordert True/False anzugeben.
	 * 
	 * @return
	 */
	public boolean handleBooleanInput() {
		boolean result;

		while (true) {

			Scanner s = new Scanner(System.in);
			System.out.println(MESSAGE_PREFIX + "True/False!");

			if (s.hasNextBoolean()) {
				result = s.nextBoolean();
				System.out.println();
				break;
			}

		}

		return result;

	}

	/**
	 * Erwartet vom Benutzer eine Eingabe als Integer. Der Integerwert darf
	 * dabei nur negativ sein, also int > 0.
	 * 
	 * @return
	 */
	public int handlePositiveIntegerInput() {
		Scanner s = new Scanner(System.in);
		int selectedOption = -1;

		// Nur Zahlen akzeptieren im Bereich der Eintraege
		while (!(selectedOption >= 0)) {
			System.out.println(MESSAGE_PREFIX + "Nur positive Ganzzahlen erlaubt!");
			while (!s.hasNextInt())
				s.next();
			selectedOption = s.nextInt();
		}

		return selectedOption;

	}

	/**
	 * Erwartet vom Benutzer eine Eingabe als Integer. Der Integerwert darf
	 * dabei nur negativ sein, also int < 0.
	 * 
	 * @return
	 */
	public int handleNegativeIntegerInput() {
		Scanner s = new Scanner(System.in);
		int selectedOption = 1;

		// Nur Zahlen akzeptieren im Bereich der Eintraege
		while (!(selectedOption < 0)) {
			System.out.println(MESSAGE_PREFIX + "Nur negative Ganzzahlen erlaubt!");
			while (!s.hasNextInt())
				s.next();
			selectedOption = s.nextInt();
		}

		return selectedOption;

	}

	/**
	 * Erwartet vom Benutzer eine Eingabe als Integer. Der Integer muss dabei
	 * innerhalb einer bestimmten Zahlenspanne sein.
	 * 
	 * @param rangeStart
	 * @param rangeEnd
	 * @return
	 */
	public int handleIntegerInputRange(int rangeStart, int rangeEnd) {
		Scanner s = new Scanner(System.in);
		int selectedOption = -1;

		// Nur Zahlen akzeptieren im Bereich der Eintraege
		while (!(selectedOption > rangeStart && selectedOption <= rangeEnd)) {
			System.out.println(MESSAGE_PREFIX + "Zahl im Bereich von " + rangeStart + "-" + rangeEnd + " erlaubt!");
			while (!s.hasNextInt())
				s.next();
			selectedOption = s.nextInt();
		}

		return selectedOption;

	}

	/**
	 * Erzwingt vom Benutzer eine Eingabe als String.
	 * 
	 * @return
	 */
	public String handleStringInput() {
		Scanner s = new Scanner(System.in);
		String result = "";

		// Nur Zeichenketten erlauben
		while (result.equals("")) {
			System.out.println(MESSAGE_PREFIX + "Erwarte Text!");
			result = s.nextLine();
		}

		return result;
	}

	/**
	 * Erzwingt vom Benutzer eine Eingabe als String. Der String muss dabei
	 * einem bestimmten Muster, anhand eines
	 * 
	 * @param regex
	 * @return
	 */
	public String handleStringInputRegex(String regex) {
		Scanner s = new Scanner(System.in);
		String result = "";

		// Nur Zeichenketten in bestimmten Muster erlauben
		while (!(result.matches(regex))) {
			System.out.println(MESSAGE_PREFIX + "Erwarte Text mit Ausdruck " + regex);
			result = s.next();
		}

		return result;

	}

	/**
	 * Erzwingt vom Benutzer eine Eingabe als Datum. Das Datum muss im Format
	 * yyyy-mm-dd angegeben werden!
	 * 
	 * @param regex
	 * @return
	 */
	public String handleDateInputRegex() {
		Scanner s = new Scanner(System.in);
		String result = "";

		// Nur Zeichenketten in bestimmten Muster erlauben
		while (!(result.matches("((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])"))) {
			System.out.println(MESSAGE_PREFIX + "Erwarte Datum mit Ausdruck JJJJ-MM-TT");
			result = s.next();
		}

		return result;

	}

}
