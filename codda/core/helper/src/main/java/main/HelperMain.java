package main;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
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
	
	public static void main(String[] args) {
		InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticFinalVars.BASE_PACKAGE_NAME);
		
		int i=0;
		for (String arg : args) {
			log.info("{} 번째 arg=[{}]", i++, arg);
		}
		
		Options options = new Options();
		options.addOption("n", "no gui");
		
		Option commandTypeOption = new Option( "x", true, "command type" );
		commandTypeOption.setArgName("command type");
		options.addOption(commandTypeOption);
		
		
		Option installedPathOption = new Option( null, "installedPath", true, "installed path" );
		installedPathOption.setArgName("installed path");
		options.addOption(installedPathOption);
		
		HelperMain helperMain = new HelperMain();
		
		 // create the parser
	    CommandLineParser parser = new DefaultParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        
	        if( line.hasOption( "n" ) ) {
	            // print the value of block-size
	            log.info("no gui mode");
	            
	            if (! line.hasOption( "x" )) {
	            	log.error("no command type argument");
	            	System.exit(1);
	            }
	            
	            String commandType = line.getOptionValue("x");
	            
	            log.info("command type=[{}]", commandType);
	            
	            if (commandType.equals("installedPath")) {
	            	
	            	if (! line.hasOption( "installedPath" )) {
		            	log.error("no installed path argument");
		            	System.exit(1);
		            }
	            	
	            	String installedPathString = line.getOptionValue("installedPath");
	            	
	            	helperMain.applyInstalledPath(installedPathString);
	            	
	            } else {
	            	log.error("unknown command type[{}]", commandType);
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
