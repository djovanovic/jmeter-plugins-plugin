package kg.apc.jmeter.aws;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * This class is used to connect to the AWS CloudWatch and get the metrics
 * @author Nick & Andrei
 * 
 */
public class AWSConnector extends Thread {
    public final static long AGENT_ERROR = -2L;
    public static final List<String> metrics_list = Arrays.asList(new String[]{"CPUUtilization", "MEMORY", "SWAP", "DISKIO", "NETWORK"});
    //must be sync with the metrics array indexes
    public final static int CPU = 0;
    public final static int MEMORY = 1;
    public final static int SWAP = 2;
    public final static int DISKIO = 3;
    public final static int NETWORK = 4;
    private int metricType;
    private static final Logger log = LoggingManager.getLoggerForClass();
    private String instanceID; 
    private String credentials_path;
    private int awsInterval;
    private long startTime;
    private String statisticType;
    private StandardUnit unit;
    
    private AWSCredentials credentials;
    private InputStream credentialsAsStream;
    
    private AmazonCloudWatchClient cloudWatch;
     
    private GetMetricStatisticsRequest statsRequest;
    private GetMetricStatisticsResult statsResult;
    
 
   
  

   
    private Map<String, String> metrics = new HashMap<String, String>();
    private String[] metricLabels;

    
    //instance connection
    public AWSConnector(String instanceID, String credentials_path) {
        this.instanceID = instanceID;
        this.credentials_path = credentials_path;
    }
    
   

 
    public void connect() throws UnknownHostException, IOException, AWSMonException {
        log.warn("credentials_path: "+credentials_path);
        credentialsAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(credentials_path);
         log.warn("credentials as stream: "+credentialsAsStream.toString());
        credentials = new PropertiesCredentials(credentialsAsStream);
        cloudWatch = new AmazonCloudWatchClient(credentials);
    }

    /**
     * Disconnect from the CloudWatch. 
     */

    public void disconnect() {
        try {
           cloudWatch.shutdown();
        } catch (Exception e) {
            log.error("Exception disconnecting client:", e);
        }
    }

    /**
     * Generic method to query CloudWatch.
     * @param data the element to retrieve, eg "mem" or "cpu".
     * @return a String containing the numbered value
     * @throws Exception if a communication problem occurred
     */
    private String getData(String metricName) throws AWSMonException {
        if (cloudWatch == null) {
            throw new AWSMonException("Not yet connected");
        } 
        log.warn("getdata() metricName: "+metricName);
        statsResult = cloudWatch.getMetricStatistics(getRequest(metricName));
       // log.debug("RESULT: "+statsResult.toString());
        List<Datapoint> dp = statsResult.getDatapoints();
 
         log.warn("RESULT dp: "+dp.get(CPU).getAverage().toString());
            Datapoint point = dp.get(0);
        String ret = null;
        if (statisticType.equalsIgnoreCase("AVERAGE")){
            ret = point.getAverage().toString();
            log.debug("Read " + metricName + "=" + ret);
            return ret;
        }else
            if (statisticType.equalsIgnoreCase("MAXIMUM")){
                ret = point.getMaximum().toString();
                log.debug("Read " + metricName + "=" + ret);
                return ret;
        }else 
            if (statisticType.equalsIgnoreCase("MINIMUM")){
                ret = point.getMinimum().toString();
                log.debug("Read " + metricName + "=" + ret);
                return ret;
            }else{
                log.error("Error receiving data");
                throw new AWSMonException("Connection lost with '" + instanceID + "'!");
            }
         
    }

    private void throwNotSupportedMetricException(String metric) throws AWSMonException {
        throw new AWSMonException("Getting " + metric + " metrics is not supported by CloudWatch API...");
    }

    /**
     * Get the current total memory used on the server 1min/5min
     * @return the total memory in bytes or -1 if any error occurred
     */
    public long getMem() throws AWSMonException {
        long ret = -1;
        this.metricType = MEMORY;
        String value = getData("MEMORY");
        if (value != null) {
            ret = Long.parseLong(value);
        }
        if (ret <= 0) {
            throwNotSupportedMetricException("memory");
        }

        return ret;
    }

    /**
     * Get the current cpu load on the server
     * @return the current cpu load % on the server or -1 if a problem occurred.
     */
    public double getCpu() throws AWSMonException {
        double ret = -1;
         this.metricType = CPU;
        String value = getData("CPUUtilization");
        if (value != null) {
            ret = Double.parseDouble(value);
        }
        if (ret < 0) {
            throwNotSupportedMetricException("cpu");
        }

        return ret;
    }

    public long[] getSwap() throws AWSMonException {
        long[] ret = {-1L, -1L};
         this.metricType = SWAP;
        String value = getData("SWAPUtilistion");
        if (value != null) {
            ret[0] = Long.parseLong(value.substring(0, value.indexOf(':')));
            ret[1] = Long.parseLong(value.substring(value.indexOf(':') + 1));
        }
        if (ret[0] < 0 || ret[1] < 0) {
            throwNotSupportedMetricException("swap");
        }

        return ret;
    }

    public long[] getDisksIO() throws AWSMonException {
        long[] ret = {-1L, -1L};
         this.metricType = DISKIO;
        String value = getData("DISKUtilisation");
        if (value != null) {
            ret[0] = Long.parseLong(value.substring(0, value.indexOf(':')));
            ret[1] = Long.parseLong(value.substring(value.indexOf(':') + 1));
        }
        if (ret[0] < 0 || ret[1] < 0) {
            throwNotSupportedMetricException("disks I/O");
        }
        return ret;
    }

    public long[] getNetIO() throws AWSMonException {
        long[] ret = {-1L, -1L};
         this.metricType = NETWORK;
        String value = getData("NETWORK");
        if (value != null) {
            ret[0] = Long.parseLong(value.substring(0, value.indexOf(':')));
            ret[1] = Long.parseLong(value.substring(value.indexOf(':') + 1));
        }
        if (ret[0] < 0 || ret[1] < 0) {
            throwNotSupportedMetricException("network I/O");
        }

        return ret;
    }

    /**
     * Get the remote server name.
     * @return the name of the remote server.
     */
    public String getRemoteServerName() {
       
        
        return "";
    }

    public String getInstanceID() {
        return instanceID;
    }

    public AWSCredentials getCredentials() {
        return credentials;
    }

    public void setMetricType(String metric) {
        metricType = metrics_list.indexOf(metric);
        log.warn("AGENT CONNECTOR - setMetricType(): "+"metric: "+metric+"  metrictype: "+metricType);
    }

    public int getMetricType() {
       return metricType;
    }
   
  
  
    
    
    //sending the metric request
    private GetMetricStatisticsRequest getRequest(String metricName){
	return new GetMetricStatisticsRequest().withPeriod(awsInterval).withDimensions(new Dimension().withName("InstanceId")
			.withValue(instanceID)).withNamespace("AWS/EC2").withMetricName(metricName)
			.withStartTime(new Date(new Date().getTime() - startTime)).withEndTime(new Date())
			.withStatistics(statisticType).withUnit(unit);
    }

    public void setStandardUnit(StandardUnit unit){
        this.unit = unit;
    }
    
    public void setStartTime(Long time){
        this.startTime = time;
    }
    
    
    public void setStatisticsType(String statisticsType){
        this.statisticType = statisticsType;
    }
    
    
    public void setPeriod(int awsInterval){
        this.awsInterval = awsInterval;
    }
  
   
}
