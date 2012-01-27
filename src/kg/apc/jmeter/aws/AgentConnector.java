package kg.apc.jmeter.aws;

import com.amazonaws.services.cloudwatch.model.StandardUnit;
import kg.apc.jmeter.aws.*;
import java.io.IOException;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 *
 * @author Andrei & Nick
 * 
 */
public class AgentConnector implements AWSMonAgentConnector {

    private static final Logger log = LoggingManager.getLoggerForClass();
   // private static boolean isTranslate = false;
    AWSConnector connector;
    private boolean metricWasSet = false;

    

    public AgentConnector(String instanceID, String credentialsPath) {
        connector = new AWSConnector(instanceID, credentialsPath);
    }

    public void connect() throws IOException {
        connector.connect();
    }

    public void disconnect() {
        connector.disconnect();
    }

  
    // TODO: cache it to be efficient
    private String getLabel() {
        String instanceID;
            instanceID = connector.getInstanceID();
             log.warn("AGENT CONNECTOR: "+instanceID+connector.getMetricType());
        return instanceID + " - ";// + AWSConnector.metrics_list.get(connector.getMetricType());
    }

    public void generateSamples(AWSMonSampleGenerator collector) throws IOException {
        String label = getLabel();   
        switch (connector.getMetricType()) {
            case AWSConnector.CPU:
                collector.generateSample(100 * connector.getCpu(), label + ", %");
                log.warn("AGENT CONNECTOR - generateSample() CPU:  "+connector.getCpu());
                break;
            case AWSConnector.MEMORY:
                collector.generateSample((double) connector.getMem() / AWSCollector.MEGABYTE, label + ", MB");
                break;
            case AWSConnector.SWAP:
                collector.generate2Samples(connector.getSwap(), label + " page in", label + " page out");
                break;
            case AWSConnector.DISKIO:
                collector.generate2Samples(connector.getDisksIO(), label + " reads", label + " writes");
                break;
            case AWSConnector.NETWORK:
                collector.generate2Samples(connector.getNetIO(), label + " recv, KB", label + " sent, KB", 1024d);
                break;
            default:
                throw new IOException("Unknown metric index: " + connector.getMetricType());
        }
    }

    // TODO: label currently ignored - maybe use it instead of calculated?
    public void addMetric(String metric, String statisticType, String label, StandardUnit unit, Long startTime, int awsInterval) {
        log.warn("AGENT CONNECTOR - addMetric(): metric: "+metric+"  statisticType: "+statisticType+"   label: " +label);
        if (metricWasSet) {
            throw new RuntimeException("Old connector don't support multiple metrics");
        }
        metricWasSet = true;
        connector.setMetricType(metric);
        connector.setStandardUnit(unit);
        connector.setStartTime(startTime);
        connector.setStatisticsType(statisticType);
        connector.setPeriod(awsInterval);
    }
}
