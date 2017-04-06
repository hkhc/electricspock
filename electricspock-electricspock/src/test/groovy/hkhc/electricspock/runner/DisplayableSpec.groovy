package hkhc.electricspock.runner

import hkhc.electricspock.runner.testdata.JunitTestClass
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import spock.lang.Specification


/**
 * Created by hermanc on 6/4/2017.
 */
class DisplayableSpec extends Specification {

    class TestRunListener extends RunListener {
        @Override
        void testRunStarted(Description description) throws Exception {
            println "testRunStarted $description"
        }

        @Override
        void testRunFinished(Result result) throws Exception {
            println "testRunFinished $result"
        }

        @Override
        void testStarted(Description description) throws Exception {
            println "testStarted $description"
        }

        @Override
        void testFinished(Description description) throws Exception {
            println "testFinished $description"
        }

        @Override
        void testFailure(Failure failure) throws Exception {
            println "testFailure $failure"
        }

        @Override
        void testAssumptionFailure(Failure failure) {
            println "testAssumptionFailure $failure"
        }

        @Override
        void testIgnored(Description description) throws Exception {
            println "testIgnored $description"
        }
    }

    void dumpDescription(Description d) {
        println "class $d.className - $d.displayName"
        d.children.forEach {
            dumpDescription(it)
        }
    }

    def "description with class"() {

        when:
            dumpDescription description
        then:
            description.getClassName() == resolvedClass.getName()

        where:


            description |
                    resolvedClass
            Description.createTestDescription(this.class, "testOne") |
                DisplayableSpec.class
            Description.createTestDescription("hkhc.electricspock.runner.DisplayableSpec", "testOne") |
                DisplayableSpec.class
            Description.createTestDescription(
                    Class.forName(
                            Description.createTestDescription("hkhc.electricspock.runner.DisplayableSpec", "testOne").getClassName()
                    ),
            "testOne") |
                DisplayableSpec.class



    }

    def "test one"() {

        when:

        RunNotifier notifier = new RunNotifier()

        BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(JunitTestClass.class)
        Description desc = runner.description
        if (desc==null)
            println "desc is null"
        if (desc==null) {
            desc = new Description()
        }

        notifier.addListener(new TestRunListener())



        if (desc)
            println "root description is null"
        else
            dumpDescription desc

        runner.run(notifier)





        then:

        1==1

    }

}