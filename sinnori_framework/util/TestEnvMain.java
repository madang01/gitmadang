
public class TestEnvMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sinnori_config_file = System.getenv("SINNORI_PROJECT_CONFIG_FILE");
		
		String sinnori_log_path = System.getenv("SINNORI_PROJECT_LOG_PATH");
		System.out.printf("sinnori_project_config_file[%s], sinnori_project_log_path[%s]\n",sinnori_config_file, sinnori_log_path);
	}
}
