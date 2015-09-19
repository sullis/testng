package test.serviceloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestNGListener;
import org.testng.TestNG;
import org.testng.annotations.Test;

import test.SimpleBaseTest;

public class ServiceLoaderTest extends SimpleBaseTest {

  @Test
  public void serviceLoaderShouldWork() throws MalformedURLException {
    TestNG tng = create(ServiceLoaderSampleTest.class);
    URL url = getClass().getClassLoader().getResource("serviceloader.jar");
    URLClassLoader ucl = URLClassLoader.newInstance(new URL[] { url });
    tng.setServiceLoaderClassLoader(ucl);
    tng.run();

    assertListenerType(tng.getServiceLoaderListeners(), TmpSuiteListener.class);
  }

  @Test
  public void serviceLoaderWithNoClassLoader() {
    //Here ServiceLoader is expected to rely on the current context class loader to load the service loader file
    //Since serviceloader.jar doesn't seem to be visible to the current thread's contextual class loader
    //resorting to pushing in a class loader into the current thread that can load the resource
    URL url = getClass().getClassLoader().getResource("serviceloader.jar");
    URLClassLoader ucl = URLClassLoader.newInstance(new URL[] { url });
    Thread.currentThread().setContextClassLoader(ucl);
    TestNG tng = create(ServiceLoaderSampleTest.class);
    tng.run();

    assertListenerType(tng.getServiceLoaderListeners(), TmpSuiteListener.class);
  }

  @Test(description = "GITHUB-491")
  public void serviceLoaderShouldWorkWithConfigurationListener() {
    TestNG tng = create(ServiceLoaderSampleTest.class);
    tng.run();

    Assert.assertEquals(1, tng.getServiceLoaderListeners().size());
    assertListenerType(tng.getServiceLoaderListeners(), MyConfigurationListener.class);
  }

  private static void assertListenerType(List<ITestNGListener> listeners, Class<? extends ITestNGListener> clazz) {
    for (ITestNGListener listener : listeners) {
      if (clazz.isInstance(listener)) {
        return;
      }
    }
    Assert.fail();
  }
}
