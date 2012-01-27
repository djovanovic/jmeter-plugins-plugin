package kg.apc.jmeter.aws;

import kg.apc.jmeter.aws.*;

/**
 *
 * @author undera
 */
public interface AWSMonSampleGenerator {

    public void generate2Samples(long[] netIO, String string, String string0, double d);

    public void generate2Samples(long[] disksIO, String string, String string0);

    public void generateSample(double d, String string);

    public void generateErrorSample(String label, String errorMsg);
}
