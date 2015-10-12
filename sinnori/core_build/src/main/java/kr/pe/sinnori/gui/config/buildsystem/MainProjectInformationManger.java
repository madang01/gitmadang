package kr.pe.sinnori.gui.config.buildsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.common.exception.MessageInfoSAXParserException;
import kr.pe.sinnori.common.message.info.MessageInfoSAXParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainProjectInformationManger {
	private Logger log = LoggerFactory.getLogger(MainProjectInformationManger.class);
	
	private String sinnoriInstalledPathString = null;
	private List<MainProjectInformation> mainProjectInformationList 
	= new ArrayList<>();
	private HashMap<String, MainProjectInformation> mainProjectInformationHash =
			new HashMap<>();
	private MessageInfoSAXParser messageInfoSAXParser = null;		
	
	private static MainProjectInformationManger instance = null;
		
	private MainProjectInformationManger(String sinnoriInstalledPathString) 
			throws IllegalArgumentException, ConfigErrorException {
		if (null == sinnoriInstalledPathString) {
			String errorMessage = "parameter sinnoriInstalledPathString is null"; 
			log.info(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.sinnoriInstalledPathString = sinnoriInstalledPathString;
		
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		if (!sinnoriInstalledPath.exists()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s]) doesn't exist", sinnoriInstalledPathString);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!sinnoriInstalledPath.isDirectory()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s]) is not a directory", sinnoriInstalledPathString);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!sinnoriInstalledPath.canRead()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s]) doesn't hava permission to read", sinnoriInstalledPathString);
			throw new IllegalArgumentException(errorMessage);
		}
		
		String projectBasePathString = BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathString);
		
		File projectBasePath = new File(projectBasePathString);
		if (!projectBasePath.exists()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't exist", 
					sinnoriInstalledPathString, projectBasePathString);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!projectBasePath.isDirectory()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] is not a direcotry", 
					sinnoriInstalledPathString, projectBasePathString);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (!projectBasePath.canRead()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't hava permission to read", 
					sinnoriInstalledPathString, projectBasePathString);
			throw new IllegalArgumentException(errorMessage);
		}
		
		List<String> tempProjectNameList = new ArrayList<String>();
		// projectNameList = new ArrayList<String>();		
		
		for (File fileOfList : projectBasePath.listFiles()) {
			if (fileOfList.isDirectory()) {
				if (!fileOfList.canRead()) {
					String errorMessage = String.format("the sinnori project base path[%s] doesn't hava permission to read", fileOfList.getAbsolutePath());
					throw new IllegalArgumentException(errorMessage);
				}
				
				if (!fileOfList.canWrite()) {
					String errorMessage = String.format("the sinnori project base path[%s] doesn't hava permission to write", fileOfList.getAbsolutePath());
					throw new IllegalArgumentException(errorMessage);
				}
				
				tempProjectNameList.add(fileOfList.getName());
			}
		}
		
		boolean isCreation = false;
		for (String mainProjectName : tempProjectNameList) {
			MainProjectInformation mainProjectInformation = new MainProjectInformation(isCreation, mainProjectName, sinnoriInstalledPathString, messageInfoSAXParser);
			mainProjectInformationList.add(mainProjectInformation);
			mainProjectInformationHash.put(mainProjectName, mainProjectInformation);
		}
		
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (MessageInfoSAXParserException e) {
			String errorMessage = "fail to create instance of MessageInfoSAXParser class";
			log.warn(errorMessage, e);
			throw new ConfigErrorException(new StringBuilder(errorMessage)
			.append(", errormessage=").append(e.getMessage()).toString());
		}
	}
	
	/*public void renew() throws ConfigErrorException {
		mainProjectInformationList.clear();
		mainProjectInformationHash.clear();
		
		for (MainProjectInformation oldMainProjectInformation : mainProjectInformationList) {
			String mainProjectName = oldMainProjectInformation.getMainProjectName();
			MainProjectInformation newMainProjectInformation = BuildSystemSupporter.loadMainProject(mainProjectName, sinnoriInstalledPathString);
			mainProjectInformationList.add(newMainProjectInformation);
			mainProjectInformationHash.put(mainProjectName, newMainProjectInformation);
		}
	}*/

	public static synchronized MainProjectInformationManger getInstance(String sinnoriInstalledPathString) 
			throws IllegalArgumentException, ConfigErrorException {
		if (null == instance) {
			instance =  new MainProjectInformationManger(sinnoriInstalledPathString);
		}
		return instance;
	}
	
	// FIXME!
	public void addNewMainProject(String newMainProjectName) 
			throws IllegalArgumentException, ConfigErrorException {
		boolean isCreation = true;
		MainProjectInformation newMainProjectInformation = new MainProjectInformation(isCreation, newMainProjectName, newMainProjectName, messageInfoSAXParser);
		mainProjectInformationList.add(newMainProjectInformation);
		mainProjectInformationHash.put(newMainProjectName, newMainProjectInformation);
	}
	
	public void deleteMainProject(String mainProjectName) 
			throws IllegalArgumentException, ConfigErrorException {
		
		MainProjectInformation mainProjectInformation = mainProjectInformationHash.get(mainProjectName);
		
		if (null == mainProjectInformation) {
			// FIXME!
			String errorMessage = String.format("the main project[%s] is not registered", mainProjectName);
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
				
		BuildSystemSupporter.removeProjectDirectory(mainProjectName, sinnoriInstalledPathString);
		
		mainProjectInformationHash.remove(mainProjectName);
	}
	
	public List<MainProjectInformation> getMainProjectInformationList() {
		return mainProjectInformationList;
	}
	
	public MainProjectInformation getMainProjectInformation(String mainProjectName) {
		return mainProjectInformationHash.get(mainProjectName);
	}
	
}
