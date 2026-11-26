# Smart Campus Management System

> A collaborative Java project by Manish Chahal and Moulik

A Java console-based campus management project with a separate static web demo interface.

## Requirements
- JDK 17 or newer (JDK 21 recommended)
- Visual Studio Code
- VS Code extension: **Extension Pack for Java**
- MySQL is optional. Without MySQL, the Java application uses its local CSV persistence mode where supported.

## Open in VS Code
1. Extract this ZIP file.
2. In VS Code, choose **File → Open Folder** and select the extracted `SmartCampusManagementSystem` folder (the folder containing this README).
3. Install the recommended **Extension Pack for Java** if VS Code asks.
4. Open the integrated terminal (**Terminal → New Terminal**).
5. Run the command for your operating system:

### macOS / Linux
```bash
chmod +x run-java.sh
./run-java.sh
```

### Windows
```bat
run-java.bat
```

The script compiles all Java source files into `out/` and starts the console application through `Main.java`. You can also use the VS Code build task with **Terminal → Run Build Task**.

## Run the web interface
The `web` folder contains a separate browser-based demo interface. It is not connected to the Java console application or MySQL database.

The website page, styles, and JavaScript are together in `web/index.html`. To start the local server from the project folder after compiling, run:

```bash
java -cp out com.smartcampus.main.Main
```

Then open `http://localhost:8080/` in a browser. You can also open `web/index.html` directly or use Live Server.

## Optional MySQL setup
The Java application checks for MySQL at `localhost:3306` using the settings in:
`src/com/smartcampus/util/DatabaseConnection.java`

Current defaults:
- Database: `smart_campus_db`
- Username: `root`
- Password: `password`

If your MySQL username/password differ, update those constants before running. Execute `database/smart_campus.sql` in MySQL Workbench first. The MySQL Connector/J driver must also be added to the Java classpath if you want JDBC mode. If MySQL or its driver is unavailable, the app reports CSV local file mode.

## Project structure
- `src/` — Java source code
- `database/` — MySQL schema and seed SQL
- `data/` — sample CSV data
- `web/` — standalone HTML/CSS/JavaScript demo
- `.vscode/` — VS Code task and recommended extensions
- `run-java.sh`, `run-java.bat` — compile and run scripts

## Notes
`Main.java` is the single Java entry point. With no argument, it starts the website server. The run scripts pass `--console` to open the console application. The browser demo remains separate from the Java services and MySQL database.
