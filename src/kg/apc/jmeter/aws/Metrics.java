package kg.apc.jmeter.aws;

// CONNECT AND SET UP EC2
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.cloudwatch.*;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.cloudwatch.model.*;



public class Metrics extends Thread {
private List<String> statistics2 = new ArrayList();
	
	
public TreeMap metricValues;
public InputStream credentialsAsStream;
public AWSCredentials credentials;
public AmazonEC2 ec2;
public AmazonCloudWatchClient cloudWatch;
public GetMetricStatisticsRequest statsRequest;
public GetMetricStatisticsResult statsResult;

@SuppressWarnings({ "rawtypes", "unchecked" })
public Metrics(List<String> instancesID) throws IOException{
	metricValues = new TreeMap<Long, Double>();
	credentialsAsStream = Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("AwsCredentials.properties");
	credentials = new PropertiesCredentials(credentialsAsStream);
	
	//set client to work with the cloud
	ec2 = new AmazonEC2Client(credentials);
	cloudWatch = new AmazonCloudWatchClient(credentials);
	
	// SHOW DETAILS of all currently owned instances
	System.out.println(ec2.describeInstances());
	
	//CLOUDWATCH metrics
	System.out.println("cloud watch listMetrics: " + cloudWatch.listMetrics());
	
	//START an existing instance
	/*StartInstancesRequest startInstancesRequest = new StartInstancesRequest(instancesID);
			ec2.startInstances(startInstancesRequest); 
			
	//ASSIGN snapshot - crate instance object with existing id
	/* Instance existingInstance = new Instance();
	existingInstance.setInstanceId(id); */
	
	statsRequest = GetRequest();
	statsResult = cloudWatch.getMetricStatistics(statsRequest);	
	
	System.out.println("metrics result: " + statsResult.toString());
	System.out.println("metrics label = " + statsResult.getLabel() + " result datapoints size " + statsResult.getDatapoints().size());

	for (Datapoint dp : statsResult.getDatapoints()) {
			metricValues.put(dp.getTimestamp().getTime(), dp.getAverage());
	}
	
	Set set = metricValues.entrySet();
	Iterator i = set.iterator();
	while(i.hasNext()) {
		Map.Entry me = (Map.Entry)i.next();
		System.out.print(me.getKey() + ": ");
		System.out.println(me.getValue());
	}
}


//sending the request
private GetMetricStatisticsRequest GetRequest(){
	return new GetMetricStatisticsRequest().withPeriod(60).withDimensions(new Dimension().withName("InstanceId")
			.withValue("i-38517d5a")).withNamespace("AWS/EC2").withMetricName("CPUUtilization")
			.withStartTime(new Date(new Date().getTime() - 1000 * 60 * 60 * 24)).withEndTime(new Date())
			.withStatistics("Average").withUnit(StandardUnit.Percent)
	;
}


	@SuppressWarnings("null")
	public static void main(String[] args) throws IOException {
		
		//id of an instance previously created
		//String id = "i-92ec80db";
		List<String> instancesID = Arrays.asList("i-92ec80db");
		Metrics m = new Metrics(instancesID);
		
		//STOP an existing instance
		/* StopInstancesRequest stopInstancesRequest = new StopInstancesRequest(instancesID);
		ec2.stopInstances(stopInstancesRequest); */
		
		//START an existing instance
		/*StartInstancesRequest startInstancesRequest = new StartInstancesRequest(instancesID);
		ec2.startInstances(startInstancesRequest); */
		
		//ASSIGN snapshot - crate instance object with existing id
		/* instance existingInstance = new Instance();
		existingInstance.setInstanceId(id); */

	}
		
}

