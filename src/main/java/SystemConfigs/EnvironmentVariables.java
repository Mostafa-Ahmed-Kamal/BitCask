package SystemConfigs;

public class EnvironmentVariables {
    private static EnvironmentVariables environmentVariables;
    private EnvironmentVariables(){
        // read env here files
    }
    public static EnvironmentVariables getEnvironmentVariablesInstance(){
        if (environmentVariables==null){
            environmentVariables = new EnvironmentVariables();
        }
        return environmentVariables;
    }
    public final long MAX_FILE_SIZE = 10000; //bytes
    public final String FILES_DIRECTORY = "DBFiles";
    public final long MERGE_INTERVAL = 1000; // seconds
}
