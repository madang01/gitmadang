package main;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.ProjectBuilder;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BuildSystemException;
import kr.pe.codda.gui.helper.main.screen.HelperMainWindow;

public class HelperMain {
	
	public void startGUI() {
		ToolTipManager.sharedInstance().setDismissDelay(1000);
		
		HelperMainWindow mainWindow = new HelperMainWindow();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
	}
	
	public void applyInstalledPath(String installedPathString) {
		InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);
		
		log.info("1.installedPathString=[{}]", installedPathString);
		
		File installedPath = new File(installedPathString);
		if (! installedPath.exists()) {
			log.error("the installed path[{}] doesn't exist", installedPathString);
			System.exit(1);
		}
		
		if (! installedPath.isDirectory()) {
			log.error("the installed path[{}] is not a directory", installedPathString);
			System.exit(1);
		}
		
		try {
			installedPathString = installedPath.getCanonicalPath();
		} catch (IOException e) {
			log.error("fail to get a canonical path[{}] ", installedPathString);
			System.exit(1);
		}
		
		log.info("2.installedPathString=[{}]", installedPathString);
		
		String projectBasePathString = ProjectBuildSytemPathSupporter.getProjectBasePathString(installedPathString);
		
		File projectBasePath = new File(projectBasePathString);
		if (! projectBasePath.exists()) {
			log.error("the project base path[{}] doesn't exist", projectBasePathString);
			System.exit(1);
		}
		
		if (! projectBasePath.isDirectory()) {
			log.error("the project base path[{}] is not a directory", projectBasePathString);
			System.exit(1);
		}
		
		
		for (File childFile : projectBasePath.listFiles()) {
			if (childFile.isDirectory()) {
				String mainProjectName = childFile.getName();
				
				ProjectBuilder projectBuilder = null;
				
				try {
					projectBuilder = new ProjectBuilder(installedPathString, mainProjectName);
				} catch (BuildSystemException e) {
					log.warn("fail to create a instance of ProjectBuilder class", e);
					continue;
				} 
				
				try {
					projectBuilder.applyInstalledPath();
				} catch (BuildSystemException e) {
					String errorMessage = new StringBuilder()
							.append("fail to apply the installed path to the main proejct[")
							.append(mainProjectName)
							.append("]").toString();
					log.warn(errorMessage, e);
					continue;
				}
			}
		}
	}
	
	public void createProject(String mainProjectName, boolean force) {
		InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);
		File installedPath = new File(".");
		
		String installedPathString = null;
		try {
			installedPathString = installedPath.getCanonicalPath();
		} catch (IOException e) {
			log.error("fail to get a canonical path[{}] ", installedPath.getAbsolutePath());
			System.exit(1);
		}
		
		log.info("installedPathString={}", installedPathString);
		
		ProjectBuilder projectBuilder = null;
		
		try {
			projectBuilder = new ProjectBuilder(installedPathString, mainProjectName);
		} catch (BuildSystemException e) {
			log.error("fail to create a instance of ProjectBuilder class", e);
			System.exit(1);
		} 
		
		if (projectBuilder.whetherOnlyProjectPathExists()) {
			if (force) {
				try {
					projectBuilder.dropProject();
				} catch (BuildSystemException e) {
					log.error("fail to drop the project["+mainProjectName+"]", e);
					System.exit(1);
				}
			} else {
				log.error("can't create the main project[{}] becase it exists", mainProjectName);
				System.exit(1);
			}
		}
		
		boolean isServer=true;
		boolean isAppClient=true;
		boolean isWebClient=false; 
		String servletSystemLibraryPathString="";
		
		try {
			projectBuilder.createProject(isServer, isAppClient, isWebClient, servletSystemLibraryPathString);
		} catch (BuildSystemException e) {
			log.error("fail to create the project["+mainProjectName+"]", e);
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);
		
		Options options = new Options();
		options.addOption("h", "help", false, "help");
		options.addOption("n", null, true, "no gui mode, this option's the command[applyInstalledPath, createProject] argument defines a command running in no gui mode."
				+ "\nthe command 'applyInstalledPath' reflects the installation path specified for all projects."
				+ "\nthe command 'createProject' creates a project with the specified project name.");
		options.getOption("n").setArgName("command");
		
		
		options.addOption(null, "installedPath", true, "installed path");
		options.getOption("installedPath").setArgName("installedPath");
		
		options.addOption(null, "projectName", true, "the project name");
		options.getOption("projectName").setArgName("projectName");
		
		options.addOption(null, "force", false, "forced execution, ex) Forcibly remove an existing project when creating a project.");
		
		HelperMain helperMain = new HelperMain();
		
		 // create the parser
	    CommandLineParser parser = new DefaultParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        
	        if( line.hasOption( "h" ) ) {
	        	 HelpFormatter formatter = new HelpFormatter();
	        	 String header = "Codda Helper(=codda-helper.jar) is a GUI program that helps development\n\n";
	        	 String footer = "\nPlease report issues at k9200544@hanmail.net";
	        	 
	        	 formatter.setOptionComparator(null);
	        	 
	        	 formatter.printHelp("java -jar codda-helper.jar", header, options, footer, true);
	        	 
	        } else if( line.hasOption( "n" ) ) {
	            // print the value of block-size
	            log.info("no gui mode");
	            
	            String command = line.getOptionValue("n");
	            
	            log.info("command=[{}]", command);
	            
	            if (command.equals("applyInstalledPath")) {
	            	
	            	if (! line.hasOption( "installedPath" )) {
		            	log.error("no installed path argument");
		            	System.exit(1);
		            }
	            	
	            	String installedPathString = line.getOptionValue("installedPath");
	            	
	            	helperMain.applyInstalledPath(installedPathString);
	            } else if (command.equals("createProject")) {
	            	if (! line.hasOption( "projectName" )) {
		            	log.error("no installed path argument");
		            	System.exit(1);
		            }
	            	
	            	String mainProjectName = line.getOptionValue("projectName");
	            	boolean force = line.hasOption( "force");
	            	helperMain.createProject(mainProjectName, force);
	            	
	            } else {
	            	log.error("unknown command[{}]", command);
	            	System.exit(1);
	            }
	            
	        } else {
	        	helperMain.startGUI();
	        }

	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	    	log.error("Parsing failed.  Reason: {}", exp.getMessage());
	    	System.exit(1);
	    }	
	}
}
