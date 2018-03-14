package kr.pe.sinnori.client.connection;

public class ConnectionFixedParameter {
	private String projectName = null;
	private long socketTimeOut;
	private String host = null;
	private int port;
	private ClientMessageUtilityIF clientMessageUtility = null;	
	
	public ConnectionFixedParameter(String projectName, String host, int port, long socketTimeOut,			
			ClientMessageUtilityIF clientMessageUtility) {
		
		if (null == projectName) {
			throw new IllegalArgumentException("the parameter projectName is null");
		}
		
		if (null == host) {
			String errorMessage = "the parameter host is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (port <= 0) {
			String errorMessage = String.format("the parameter port[%d] is less than or equal to zero", port); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (socketTimeOut < 0) {
			String errorMessage = String.format("the parameter socketTimeOut[%d] is less than zero", socketTimeOut); 
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == clientMessageUtility) {
			String errorMessage = "the parameter clientMessageUtility is null"; 
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.projectName = projectName;
		this.host = host;
		this.port = port;
		this.socketTimeOut = socketTimeOut;
		this.clientMessageUtility = clientMessageUtility;
	}

	public String getProjectName() {
		return projectName;
	}

	public long getSocketTimeOut() {
		return socketTimeOut;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public ClientMessageUtilityIF getClientMessageUtility() {
		return clientMessageUtility;
	}
}
