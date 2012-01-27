package kg.apc.jmeter.aws;

import com.amazonaws.services.cloudwatch.model.StandardUnit;
import kg.apc.jmeter.perfmon.*;
import java.io.IOException;

/**
 *
 * @author undera
 */
public interface AWSMonAgentConnector {

    public void connect() throws IOException;

    public void disconnect();

    public void generateSamples(AWSMonSampleGenerator collector) throws IOException;

    public void addMetric(String metric, String statisticType, String label, StandardUnit unit, Long startTime, int awsInterval);
}
