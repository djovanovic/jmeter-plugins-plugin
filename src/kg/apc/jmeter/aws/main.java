package kg.apc.jmeter.aws;

// CONNECT AND SET UP EC2
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;

import com.amazonaws.services.ec2.model.AttachVolumeRequest;

public class main {

	public static void main(String[] args) throws IOException {
		// connect to amazon
		InputStream credentialsAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties");
		AWSCredentials credentials = new PropertiesCredentials(credentialsAsStream);
		AmazonEC2 ec2 = new AmazonEC2Client(credentials);
		ec2.setEndpoint("https://ec2.us-east-1.amazonaws.com");	
	//	String snapshot = "snap-8c074ee4";	
		// create instance
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
		    .withInstanceType("t1.micro")
		    .withImageId("ami-31814f58") //ID of free instance
		    .withMinCount(1) // min number of instances to be created
		    .withMaxCount(1)
		    .withSecurityGroupIds("default") //name of secruity group
		    .withKeyName("myhosts-1") // name of key pair value, private key must be in the root of the project
		;
		RunInstancesResult runInstances = ec2.runInstances(runInstancesRequest);

		// name instances
		List<Instance> instances = runInstances.getReservation().getInstances();
		int i = 0;
		
		for (Instance instance : instances) {
		//  AttachVolumeRequest volReq = new AttachVolumeRequest(snapshot, instance.getInstanceId(), "");
		  CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		  createTagsRequest.withResources(instance.getInstanceId()) //
		      .withTags(new Tag("Name", "Instance 1" + i));
		  ec2.createTags(createTagsRequest);
		  i++;
		  System.out.println("\n ID: " + instance.getInstanceId());
		  System.out.println("\n New instance number " + i + " has been created\n");
		
		}
	}
}
