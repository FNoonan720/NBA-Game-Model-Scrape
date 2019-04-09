package nba.scrape;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Main {

	public static void main(String[] args) throws InterruptedException, IOException, RowsExceededException, WriteException  {
		
		// Opens chromedriver.exe as a web driver
    	String exePath = "C:\\Users\\Frankie\\Documents\\Other\\Programming\\Java\\Chrome Driver\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", exePath);
		
		// Initializes Scanner object for input
		Scanner input = new Scanner(System.in);
		
		System.out.print("Cleaning The Glass Log-in:\n\nEmail:\t\t");
		String email = input.nextLine();
		
		System.out.print("Password:\t");
		String password = input.nextLine();
		
		// Runs method for scraping model data
		scrapeModelData(email, password);
		
		// Closes input Scanner
		input.close();

	}

	public static void scrapeModelData (String email, String password) throws InterruptedException, IOException, RowsExceededException, WriteException {
		
		// Creates a Chrome driver instance
		WebDriver driver = new ChromeDriver();
				
		// Sets implicit wait times for 10 seconds
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		// Maximizes driver window
		driver.manage().window().maximize();
		
		// Gets the login page
		driver.get("https://cleaningtheglass.com?memberful_endpoint=auth");
		
		// Fills login/password fields with Email/Password & clicks 'Sign in' button
		driver.findElement(By.id("login")).sendKeys(email);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/form/button")).click();
		
		// Waits for log-in to be processed, jumps to CTG's Four Factors page for 'Home' performance, then waits for page load
		Thread.sleep(1000);
		driver.get("https://www.cleaningtheglass.com/stats/league/fourfactors?season=2018&seasontype=regseason&start=10/15/2018&end=07/1/2019&venue=home");
		Thread.sleep(1000);
		
		// Gets page's source code and saves all table data ("td") elements
		Document doc = Jsoup.parse(driver.getPageSource());
		Elements data = doc.getElementsByTag("td");
		
		// Saves td element's texts as strings in a 2D ArrayList of Strings
		ArrayList<ArrayList<String>> homeSeasonDataTableText = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < 30; i++) {
			ArrayList<String> temp = new ArrayList<String>();
			for(int j = 0; j < data.size()/30; j++) {
				temp.add(data.get((i*(data.size()/30))+j).text());
			}
			homeSeasonDataTableText.add(temp);
		}
		
		// Sorts table data by team name alphabetically
		Collections.sort(homeSeasonDataTableText, teamNameComparator);
		
		// Removes team names so the table can be processed as all Double values
		ArrayList<String> homeTeamNames = new ArrayList<String>();
		for(int i = 0; i < homeSeasonDataTableText.size(); i++) {
			homeSeasonDataTableText.get(i).remove(9);
			homeSeasonDataTableText.get(i).remove(19);
			homeTeamNames.add(homeSeasonDataTableText.get(i).get(0));
			homeSeasonDataTableText.get(i).remove(0);
		}
	
		// Saves table data as Double values in a 2D ArrayList of Doubles
		ArrayList<ArrayList<Double>> homeSeasonDataTable = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < homeSeasonDataTableText.size(); i++) {
			ArrayList<Double> temp = new ArrayList<Double>();
			for(int j = 0; j < homeSeasonDataTableText.get(0).size(); j++) {
				if(j == 1 || j == 5) {
					if(homeSeasonDataTableText.get(i).get(j).substring(0,1).equals("+")) {
						temp.add(Double.parseDouble(homeSeasonDataTableText.get(i).get(j).substring(1)));
					}
					else {
						temp.add(Double.parseDouble(homeSeasonDataTableText.get(i).get(j)));
					}
				}
				else if(j == 11 || j == 13 || j == 15 || j == 21 || j == 23 || j == 25) {
					temp.add(Double.parseDouble(homeSeasonDataTableText.get(i).get(j).substring(0,homeSeasonDataTableText.get(i).get(j).length()-1)));
				}
				else {
					temp.add(Double.parseDouble(homeSeasonDataTableText.get(i).get(j)));
				}
			}
			homeSeasonDataTable.add(temp);
		}
		
		// Jumps to CTG's Four Factors page for 'Away' performance, then waits for page load
		driver.get("https://www.cleaningtheglass.com/stats/league/fourfactors?season=2018&seasontype=regseason&start=10/15/2018&end=07/1/2019&venue=away");
		Thread.sleep(1000);
		
		// Gets page's source code and saves all table data ("td") elements
		doc = Jsoup.parse(driver.getPageSource());
		data = doc.getElementsByTag("td");
		
		// Saves td element's texts as strings in a 2D ArrayList of Strings
		ArrayList<ArrayList<String>> awaySeasonDataTableText = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < 30; i++) {
			ArrayList<String> temp = new ArrayList<String>();
			for(int j = 0; j < data.size()/30; j++) {
				temp.add(data.get((i*(data.size()/30))+j).text());
			}
			awaySeasonDataTableText.add(temp);
		}
		
		// Sorts table data by team name alphabetically
		Collections.sort(awaySeasonDataTableText, teamNameComparator);
		
		// Removes team names so the table can be processed as all Double values
		ArrayList<String> awayTeamNames = new ArrayList<String>();
		for(int i = 0; i < awaySeasonDataTableText.size(); i++) {
			awaySeasonDataTableText.get(i).remove(9);
			awaySeasonDataTableText.get(i).remove(19);
			awayTeamNames.add(awaySeasonDataTableText.get(i).get(0));
			awaySeasonDataTableText.get(i).remove(0);
		}
	
		// Saves table data as Double values in a 2D ArrayList of Doubles
		ArrayList<ArrayList<Double>> awaySeasonDataTable = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < awaySeasonDataTableText.size(); i++) {
			ArrayList<Double> temp = new ArrayList<Double>();
			for(int j = 0; j < awaySeasonDataTableText.get(0).size(); j++) {
				if(j == 1 || j == 5) {
					if(awaySeasonDataTableText.get(i).get(j).substring(0,1).equals("+")) {
						temp.add(Double.parseDouble(awaySeasonDataTableText.get(i).get(j).substring(1)));
					}
					else {
						temp.add(Double.parseDouble(awaySeasonDataTableText.get(i).get(j)));
					}
				}
				else if(j == 11 || j == 13 || j == 15 || j == 21 || j == 23 || j == 25) {
					temp.add(Double.parseDouble(awaySeasonDataTableText.get(i).get(j).substring(0,awaySeasonDataTableText.get(i).get(j).length()-1)));
				}
				else {
					temp.add(Double.parseDouble(awaySeasonDataTableText.get(i).get(j)));
				}
			}
			awaySeasonDataTable.add(temp);
		}
		
		// Gets current date and the date 2 weeks earlier
		int currDay = LocalDateTime.now().getDayOfYear();
		int begDay = currDay-14;
		
		// Sets today's date
		Calendar calendar = Calendar.getInstance();		
		calendar.set(Calendar.DAY_OF_YEAR, begDay);
		
		// Parses calendar data to get a month name (abbreviated), then saves the code as an integer in a HashMap to represent said month
		String monthName = calendar.getTime().toString().substring(4, 7);
		Map<String, Integer> monthCodes = new HashMap<String, Integer>();
		monthCodes.put("Jan", 1);
		monthCodes.put("Feb", 2);
		monthCodes.put("Mar", 3);
		monthCodes.put("Apr", 4);
		monthCodes.put("May", 5);
		monthCodes.put("Jun", 6);
		monthCodes.put("Jul", 7);
		monthCodes.put("Aug", 8);
		monthCodes.put("Sep", 9);
		monthCodes.put("Oct", 10);
		monthCodes.put("Nov", 11);
		monthCodes.put("Dec", 12);
		
		// Parses calendar data to get current day of month and year
		int dayOfMonth = Integer.parseInt(calendar.getTime().toString().substring(8, 10));
		int year = Integer.parseInt(calendar.getTime().toString().substring(24,28));
		
		// Jumps to CTG's Four Factors page for 'Last 2 Weeks' performance, then waits for page load
		driver.get("https://www.cleaningtheglass.com/stats/league/fourfactors?season=2018&seasontype=regseason&start=" + monthCodes.get(monthName) + "/" + dayOfMonth + "/" + year + "&end=07/1/2019");
		Thread.sleep(1000);
		
		// Gets page's source code and saves all table data ("td") elements
		doc = Jsoup.parse(driver.getPageSource());
		data = doc.getElementsByTag("td");
		
		// Saves td element's texts as strings in a 2D ArrayList of Strings
		ArrayList<ArrayList<String>> recentDataTableText = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < 30; i++) {
			ArrayList<String> temp = new ArrayList<String>();
			for(int j = 0; j < data.size()/30; j++) {
				temp.add(data.get((i*(data.size()/30))+j).text());
			}
			recentDataTableText.add(temp);
		}
		
		// Sorts table data by team name alphabetically
		Collections.sort(recentDataTableText, teamNameComparator);
		
		// Removes team names so the table can be processed as all Double values
		ArrayList<String> recentTeamNames = new ArrayList<String>();
		for(int i = 0; i < recentDataTableText.size(); i++) {
			recentDataTableText.get(i).remove(9);
			recentDataTableText.get(i).remove(19);
			recentTeamNames.add(recentDataTableText.get(i).get(0));
			recentDataTableText.get(i).remove(0);
		}
		
		// Saves table data as Double values in a 2D ArrayList of Doubles
		ArrayList<ArrayList<Double>> recentDataTable = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < recentDataTableText.size(); i++) {
			ArrayList<Double> temp = new ArrayList<Double>();
			for(int j = 0; j < recentDataTableText.get(0).size(); j++) {
				if(j == 1 || j == 5) {
					if(recentDataTableText.get(i).get(j).substring(0,1).equals("+")) {
						temp.add(Double.parseDouble(recentDataTableText.get(i).get(j).substring(1)));
					}
					else {
						temp.add(Double.parseDouble(recentDataTableText.get(i).get(j)));
					}
				}
				else if(j == 11 || j == 13 || j == 15 || j == 21 || j == 23 || j == 25) {
					temp.add(Double.parseDouble(recentDataTableText.get(i).get(j).substring(0,recentDataTableText.get(i).get(j).length()-1)));
				}
				else {
					temp.add(Double.parseDouble(recentDataTableText.get(i).get(j)));
				}
			}
			recentDataTable.add(temp);
		}
		
		// Uses current date & time to get the night's upcoming game data
		calendar.set(Calendar.DAY_OF_YEAR, currDay);
		monthName = calendar.getTime().toString().substring(4, 7);
		dayOfMonth = Integer.parseInt(calendar.getTime().toString().substring(8, 10));
		
		// Jumps to the night's upcoming game data, then waits for page load
		driver.get("https://www.cleaningtheglass.com/stats/games?date=2019-" + monthCodes.get(monthName) + "-" + dayOfMonth);
		Thread.sleep(1500);
		
		// Gets page's source code and saves all elements with the class "team_name" or "stat"
		doc = Jsoup.parse(driver.getPageSource());
		Elements teamNameElements = doc.getElementsByClass("team_name");
		Elements daysRestElements = doc.getElementsByClass("stat");
		
		// Initializes lists to store team names and their respective days rest heading into the night's games
		ArrayList<String> awayTeams = new ArrayList<String>();
		ArrayList<String> homeTeams = new ArrayList<String>();
		ArrayList<Integer> awayRest = new ArrayList<Integer>();
		ArrayList<Integer> homeRest = new ArrayList<Integer>();
		
		// Saves appropriate team names
		for(int i = 0; i < teamNameElements.size(); i++) {
			if(i%2==0) {
				awayTeams.add(teamNameElements.get(i).text());
			}
			else {
				homeTeams.add(teamNameElements.get(i).text());
			}
		}
		
		// Saves appropriate days rest
		for(int i = 0; i < daysRestElements.size(); i+=7) { 
			if(i%2==0) {
				awayRest.add(Integer.parseInt(daysRestElements.get(i).text()));
			}
			else {
				homeRest.add(Integer.parseInt(daysRestElements.get(i).text()));
			}
		}
		
		// Creates a HashMap to store team names and their respective days rest and stores appropriate values
		Map<String, Integer> daysRestMap = new HashMap<String, Integer>();
		for(int i = 0; i < awayTeams.size(); i++) {
			daysRestMap.put(awayTeams.get(i), awayRest.get(i));
			daysRestMap.put(homeTeams.get(i), homeRest.get(i));
		}    	
		
		// Jumps to the night's game lines/spreads on Bovada, waits for page load, clicks element that filters for games being played within the next 24 hours, and waits for page load again
		driver.get("https://www.bovada.lv/sports/basketball/nba");
		Thread.sleep(2000);
		driver.findElement(By.xpath("/html/body/bx-site/ng-component/div/sp-main/div/main/div/section/main/sp-path-event/div/header/sp-filter/section/div[1]/sp-tabbed-filter/div/ul/sp-tabbed-filter-element[1]")).click();
		Thread.sleep(2000);
		
		// Gets page's source code and saves all elements with the class "name" or "market-line bet-handicap"
		doc = Jsoup.parse(driver.getPageSource());
		Elements teamNames = doc.getElementsByClass("name");
		Elements spreadElements = doc.getElementsByClass("market-line bet-handicap");
	
		// (Change variable if the number of games on Bovada that occur the following day is greater than 0
		int numEarlyGames = 0;
		for(int i = 0; i < numEarlyGames; i++) { 
			teamNames.remove(teamNames.size()-1);
			teamNames.remove(teamNames.size()-1);
			spreadElements.remove(spreadElements.size()-1);
			spreadElements.remove(spreadElements.size()-1);
		}
		
		// Instantiates a HashMap that abbreviates each team name
		Map<String, String> teamNameCodes = new HashMap<String, String>();
		teamNameCodes.put("Atlanta Hawks", "ATL");
		teamNameCodes.put("Boston Celtics", "BOS");
		teamNameCodes.put("Brooklyn Nets", "BKN");
		teamNameCodes.put("Charlotte Hornets", "CHA");
		teamNameCodes.put("Chicago Bulls", "CHI");
		teamNameCodes.put("Cleveland Cavaliers", "CLE");
		teamNameCodes.put("Dallas Mavericks", "DAL");
		teamNameCodes.put("Denver Nuggets", "DEN");
		teamNameCodes.put("Detroit Pistons", "DET");
		teamNameCodes.put("Golden State Warriors", "GSW");
		teamNameCodes.put("Houston Rockets", "HOU");
		teamNameCodes.put("Indiana Pacers", "IND");
		teamNameCodes.put("Los Angeles Clippers", "LAC");
		teamNameCodes.put("Los Angeles Lakers", "LAL");
		teamNameCodes.put("Memphis Grizzlies", "MEM");
		teamNameCodes.put("Miami Heat", "MIA");
		teamNameCodes.put("Milwaukee Bucks", "MIL");
		teamNameCodes.put("Minnesota Timberwolves", "MIN");
		teamNameCodes.put("New Orleans Pelicans", "NOP");
		teamNameCodes.put("New York Knicks", "NYK");
		teamNameCodes.put("Oklahoma City Thunder", "OKC");
		teamNameCodes.put("Orlando Magic", "ORL");
		teamNameCodes.put("Philadelphia 76ers", "PHI");
		teamNameCodes.put("Phoenix Suns", "PHX");
		teamNameCodes.put("Portland Trail Blazers", "POR");
		teamNameCodes.put("Sacramento Kings", "SAC");
		teamNameCodes.put("San Antonio Spurs", "SAS");
		teamNameCodes.put("Toronto Raptors", "TOR");
		teamNameCodes.put("Utah Jazz", "UTA");
		teamNameCodes.put("Washington Wizards", "WAS");
		
		// Saves team names as their respective abbreviation
		ArrayList<String> teamAbvs = new ArrayList<String>();
		for(int i = 0; i < teamNames.size(); i++) {
			teamAbvs.add(teamNameCodes.get(teamNames.get(i).text()));
		}
		
		// Saves spreads into an ArrayList of Doubles
		ArrayList<Double> spreads = new ArrayList<Double>();
		for(int i = 0; i < spreadElements.size(); i+=2) {
			spreads.add(Double.parseDouble(spreadElements.get(i).text()));
			spreads.add(Double.parseDouble(spreadElements.get(i+1).text()));		
		}
		
		// Gets today's date, then calculates the previous date, which is used to get the previous night's scores from NBA.com
		currDay = LocalDateTime.now().getDayOfYear();
		begDay = currDay-1;
		calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, begDay);
		monthName = calendar.getTime().toString().substring(4, 7);    	
		dayOfMonth = Integer.parseInt(calendar.getTime().toString().substring(8, 10));
		DecimalFormat formatter = new DecimalFormat("00");
		
		// Jumps to NBA.com's previous night's scores, then waits for page load
		driver.get("https://stats.nba.com/scores/" + formatter.format(monthCodes.get(monthName)) + "/" + formatter.format(dayOfMonth) + "/2019");
		Thread.sleep(2000);
		
		// Gets page's source code and saves all elements with the class "team_name" or "final"
		doc = Jsoup.parse(driver.getPageSource());
		teamNames = doc.getElementsByClass("team-name");
		Elements scores = doc.getElementsByClass("final");
		
		// Closes WebDriver
		driver.close();
		
		// Saves "team-name" elements to an ArrayList of Strings
		ArrayList<String> teamStrings = new ArrayList<String>();
		for(int i = 0; i < teamNames.size(); i++) {
			teamStrings.add(teamNames.get(i).text());
		}
		
		// Saves "final" elements to an ArrayList of Doubles
		ArrayList<String> scoreNums = new ArrayList<String>();
		for(int i = 0; i < scores.size(); i+=3) {
			scoreNums.add(scores.get(i+1).text());
			scoreNums.add(scores.get(i+2).text());
		}
		
		// Creates an excel file with four sheets for hosting output data and names it "Game-Model-Scrape-Output.xls"
		WritableWorkbook workbook;
		workbook = Workbook.createWorkbook(new File("Game-Model-Scrape-Output.xls"));
		WritableSheet sheet1 = workbook.createSheet("Tonight's Games", 0);
		WritableSheet sheet2 = workbook.createSheet("Entire Season - Home", 1);
		WritableSheet sheet3 = workbook.createSheet("Entire Season - Away", 2);
		WritableSheet sheet4 = workbook.createSheet("Last 2 Weeks", 3);
		
		// Instantiates a list of Strings that constitute CTG's Four Factors table headers
		ArrayList<String> headers = new ArrayList<String>(Arrays.asList("Team", "Diff Rank", "Diff", "Exp W/82", "Exp W", "Win Diff Rank", "Win Diff", "W", "L", "ORtg Rank", "ORtg", "Off. eFG% Rank", "Off. eFG%", "Off. TOV% Rank", "Off. TOV%", "Off. ORB% Rank", "Off. ORB%", "Off. FT Rate Rank", "Off. FT Rate", "DRtg Rank", "DRtg", "Def. eFG% Rank", "Def. eFG%", "Def. TOV% Rank", "Def. TOV%", "Def. ORB% Rank", "Def. ORB%", "Def. FT Rate Rank", "Def. FT Rate"));
		
		// Writes "Away" & "Home" team labels to "Tonight's Games" sheet
		for(int i = 0; i < awayRest.size(); i++) {
			Label away = new Label(0, i*3, "Away");
			sheet1.addCell(away);
			Label home = new Label(0, i*3+1, "Home");
			sheet1.addCell(home);
		}
		
		// Writes the night's games' team abbreviations and days rest to "Tonight's Games" sheet
		for(int i = 0; i < teamAbvs.size()+(teamAbvs.size()/2)-1; i+=3) {
			Label awayTeam = new Label(1, i, teamAbvs.get(i-(i/3)));
			sheet1.addCell(awayTeam);
			Label homeTeam = new Label(1, i+1, teamAbvs.get(i-(i/3)+1));
			sheet1.addCell(homeTeam);
			Number awayRestNum = new Number(2, i, daysRestMap.get(teamAbvs.get(i-(i/3))));
			sheet1.addCell(awayRestNum);
			Number homeRestNum = new Number(2, i+1, daysRestMap.get(teamAbvs.get(i-(i/3)+1)));
			sheet1.addCell(homeRestNum);
		}
		
		// Writes the night's games' spreads to "Tonight's Games" sheet
		for(int i = 0; i < spreads.size()+(spreads.size()/2)-1; i+=3) {
			sheet1.addCell(new Number(3, i, spreads.get(i-(i/3))));
			sheet1.addCell(new Number(3, i+1, spreads.get(i-(i/3)+1)));
		}
		
		// Writes previous night's games' scores to "Tonight's Games" sheet
		for(int i = 0; i < scoreNums.size()+(scoreNums.size()/2)-1; i+=3) {
			Label awayName = new Label(5, i, teamStrings.get(i-(i/3)));
			sheet1.addCell(awayName);
			Label homeName = new Label(5, i+1, teamStrings.get(i-(i/3)+1));
			sheet1.addCell(homeName);
			Label awayScore = new Label(6, i, scoreNums.get(i-(i/3)));
			sheet1.addCell(awayScore);
			Label homeScore = new Label(6, i+1, scoreNums.get(i-(i/3)+1));
			sheet1.addCell(homeScore);
		}
		
		// Writes CTG's Four Factor table headers to "Entire Season - Home", "Entire Season - Away", and "Last 2 Weeks" sheets
		for(int i = 0; i < headers.size(); i++) {
			Label name = new Label(i, 0, headers.get(i));
			sheet2.addCell(name);
			Label name2 = new Label(i, 0, headers.get(i));
			sheet3.addCell(name2);
			Label name3 = new Label(i, 0, headers.get(i));
			sheet4.addCell(name3);
		}
		
		// Writes team abbreviations to "Entire Season - Home", "Entire Season - Away", and "Last 2 Weeks" sheets
		for(int j = 0; j < homeTeamNames.size(); j++) {
			Label name = new Label(0, j+1, homeTeamNames.get(j));
			sheet2.addCell(name);
			Label name2 = new Label(0, j+1, awayTeamNames.get(j));
			sheet3.addCell(name2);
			Label name3 = new Label(0, j+1, recentTeamNames.get(j));
			sheet4.addCell(name3);
		}
		
		// Writes CTG's Four Factor 'Home' data to "Entire Season - Home" sheet
		for(int i = 0; i < homeSeasonDataTable.get(0).size(); i++) {
			for(int j = 0; j < homeSeasonDataTable.size(); j++) {
				Number num = new Number(i+1, j+1, homeSeasonDataTable.get(j).get(i));
				sheet2.addCell(num);
			}
		}
		
		// Writes CTG's Four Factor 'Away' data to "Entire Season - Away" sheet
		for(int i = 0; i < awaySeasonDataTable.get(0).size(); i++) {
			for(int j = 0; j < awaySeasonDataTable.size(); j++) {
				Number num = new Number(i+1, j+1, awaySeasonDataTable.get(j).get(i));
				sheet3.addCell(num);
			}
		}
		
		// Writes CTG's Four Factor 'Last 2 Weeks' data to "Last 2 Weeks" sheet
		for(int i = 0; i < recentDataTable.get(0).size(); i++) {
			for(int j = 0; j < recentDataTable.size(); j++) {
				Number num = new Number(i+1, j+1, recentDataTable.get(j).get(i));
				sheet4.addCell(num);
			}
		}
		
		// Writes values to workbook and closes workbook
		workbook.write();
		workbook.close();
		
		// Notifies user to where their data has been output
		System.out.println("\nYour data can be found in 'Game-Model-Scrape-Output.xls'.\n");
		
	}

	//Custom comparator that compares ArrayList<String>'s by the first element (in this case: team name)
	public static Comparator<ArrayList<String>> teamNameComparator = new Comparator<ArrayList<String>>() {
	
		public int compare(ArrayList<String> s1, ArrayList<String> s2) {
		   String teamName1 = s1.get(0).toUpperCase();
		   String teamName2 = s2.get(0).toUpperCase();
	
		   //ascending order
		   return teamName1.compareTo(teamName2);
	
	    }
		
	};
	
}
