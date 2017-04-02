package hkhc.electricspock.internal;

import org.robolectric.annotation.Config;
import org.robolectric.internal.SdkEnvironment;
import org.spockframework.runtime.model.SpecInfo;

/**
 * Created by hermanc on 31/3/2017.
 */

public class ElectricSpockInterceptorParam {

    public Object specinfo;
    public Object sdkEnvironment;
    public Object config;
    public Object containedRobolectricTestRunner;

    public ElectricSpockInterceptorParam() {

    }

    public ElectricSpockInterceptorParam specInfo(SpecInfo specInfo) {
        this.specinfo = specInfo;
        return this;
    }

    public ElectricSpockInterceptorParam sdkEnvironment(SdkEnvironment sdkEnvironment) {
        this.sdkEnvironment = sdkEnvironment;
        return this;
    }

    public ElectricSpockInterceptorParam config(Config config) {
        this.config = config;
        return this;
    }

    public ElectricSpockInterceptorParam containedRobolectricTestRunner(ContainedRobolectricTestRunner runner) {
        this.containedRobolectricTestRunner = runner;
        return this;
    }

}
