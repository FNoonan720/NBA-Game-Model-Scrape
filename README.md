# NBA-Game-Model-Scrape
Script used to scrape cleaningtheglass.com &amp; stats.nba.com, using a Selenium WebDriver & Jsoup source code parsing libraries, for statistics used in creating a predictive game model for NBA contests.


First, the user is prompted to sign-in to Cleaning The Glass's website, as most of their stat pages are only able to be viewed by subscribers.

Next, a Selenium WebDriver is opened and sent to CTG's log-in page, where the log-in information is passed to enable log-in sequence.

Then, the driver jumps to CTG's Four Factors page where the table data is grabbed by Jsoup's source code document parsing. The table data is then saved into an ArrayList for eventual writing to an excel spreadsheet. This process is then repeated for CTG's Four Factors pages filtered for home, away, and last two weeks results.

Next, the driver jumps to CTG's 'Games' page where the night's games and teams' days rest are scraped.

After all necessary statistics are collected, the driver jumps to Bovada's NBA Game Lines and grabs the night's games' moneyline values and spreads.

Now, an excel spreadsheet is generated using the jxl library and all collected data is written to it in a readable format before being passed to my proprietary predictive game model.


You will need a CTG Log-in and the ability to execute 'Main.java', which entails having the necessary jars (Selenium, Jsoup, jxl). An excel spreadsheet with every teams season-long performance metrics at home, on the road, during the last two weeks, and the night's game lines is generated.
