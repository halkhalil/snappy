/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.joshworks.snappy.loader;

import io.joshworks.snappy.loader.archive.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link PropertiesLauncher}.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 */
public class PropertiesLauncherTests {

    @Rule
    public OutputCapture output = new OutputCapture();
    @Mock
    private JavaAgentDetector javaAgentDetector;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        System.setProperty("loader.home",
                new File("src/test/resources").getAbsolutePath());
    }

    @After
    public void close() {
        System.clearProperty("loader.home");
        System.clearProperty("loader.path");
        System.clearProperty("loader.main");
        System.clearProperty("loader.config.name");
        System.clearProperty("loader.config.location");
        System.clearProperty("loader.system");
    }

    @Test
    public void testDefaultHome() {
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals(new File(System.getProperty("loader.home")),
                launcher.getHomeDirectory());
    }

    @Test
    public void testUserSpecifiedMain() throws Exception {
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("demo.Application", launcher.getMainClass());
        assertNull(System.getProperty("loader.main"));
    }

    @Test
    public void testUserSpecifiedConfigName() throws Exception {
        System.setProperty("loader.config.name", "foo");
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("my.Application", launcher.getMainClass());
        assertEquals("[etc/]",
                ReflectionTestUtils.getField(launcher, "paths").toString());
    }

    @Test
    public void testUserSpecifiedDotPath() throws Exception {
        System.setProperty("loader.path", ".");
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("[.]", ReflectionTestUtils.getField(launcher, "paths").toString());
    }

    @Test
    public void testUserSpecifiedWildcardPath() throws Exception {
        System.setProperty("loader.path", "jars/*");
        System.setProperty("loader.main", "demo.Application");
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("[jars/]",
                ReflectionTestUtils.getField(launcher, "paths").toString());
        launcher.launch(new String[0]);
        waitFor("Hello World");
    }

    @Test
    public void testUserSpecifiedJarPath() throws Exception {
        System.setProperty("loader.path", "jars/app.jar");
        System.setProperty("loader.main", "demo.Application");
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("[jars/app.jar]",
                ReflectionTestUtils.getField(launcher, "paths").toString());
        launcher.launch(new String[0]);
        waitFor("Hello World");
    }

    @Test
    public void testUserSpecifiedJarPathWithDot() throws Exception {
        System.setProperty("loader.path", "./jars/app.jar");
        System.setProperty("loader.main", "demo.Application");
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("[jars/app.jar]",
                ReflectionTestUtils.getField(launcher, "paths").toString());
        launcher.launch(new String[0]);
        waitFor("Hello World");
    }

    @Test
    public void testUserSpecifiedClassLoader() throws Exception {
        System.setProperty("loader.path", "jars/app.jar");
        System.setProperty("loader.classLoader", URLClassLoader.class.getName());
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("[jars/app.jar]",
                ReflectionTestUtils.getField(launcher, "paths").toString());
        launcher.launch(new String[0]);
        waitFor("Hello World");
    }

    @Test
    public void testUserSpecifiedClassPathOrder() throws Exception {
        System.setProperty("loader.path", "more-jars/app.jar,jars/app.jar");
        System.setProperty("loader.classLoader", URLClassLoader.class.getName());
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("[more-jars/app.jar, jars/app.jar]",
                ReflectionTestUtils.getField(launcher, "paths").toString());
        launcher.launch(new String[0]);
        waitFor("Hello Other World");
    }

    @Test
    public void testCustomClassLoaderCreation() throws Exception {
        System.setProperty("loader.classLoader", TestLoader.class.getName());
        PropertiesLauncher launcher = new PropertiesLauncher();
        ClassLoader loader = launcher.createClassLoader(Collections.<Archive>emptyList());
        assertNotNull(loader);
        assertEquals(TestLoader.class.getName(), loader.getClass().getName());
    }

    @Test
    public void testUserSpecifiedConfigPathWins() throws Exception {

        System.setProperty("loader.config.name", "foo");
        System.setProperty("loader.config.location", "classpath:bar.properties");
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("my.BarApplication", launcher.getMainClass());
    }

    @Test
    public void testSystemPropertySpecifiedMain() throws Exception {
        System.setProperty("loader.main", "foo.Bar");
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("foo.Bar", launcher.getMainClass());
    }

    @Test
    public void testSystemPropertiesSet() throws Exception {
        System.setProperty("loader.system", "true");
        new PropertiesLauncher();
        assertEquals("demo.Application", System.getProperty("loader.main"));
    }

    @Test
    public void testArgsEnhanced() throws Exception {
        System.setProperty("loader.args", "foo");
        PropertiesLauncher launcher = new PropertiesLauncher();
        assertEquals("[foo, bar]", Arrays.asList(launcher.getArgs("bar")).toString());
    }

    @Test
    public void testJavaAgentJarsAreExcludedFromClasspath() throws Exception {
        List<Archive> allArchives = new PropertiesLauncher().getClassPathArchives();
        URL[] parentUrls = ((URLClassLoader) getClass().getClassLoader()).getURLs();
        for (URL url : parentUrls) {
            given(this.javaAgentDetector.isJavaAgentJar(url)).willReturn(true);
        }
        List<Archive> nonAgentArchives = new PropertiesLauncher(this.javaAgentDetector)
                .getClassPathArchives();
        assertThat(nonAgentArchives.size(),
                is(equalTo(allArchives.size() - parentUrls.length)));
        for (URL url : parentUrls) {
            verify(this.javaAgentDetector).isJavaAgentJar(url);
        }
    }

    private void waitFor(String value) throws Exception {
        int count = 0;
        boolean timeout = false;
        while (!timeout && count < 100) {
            count++;
            Thread.sleep(50L);
            timeout = this.output.toString().contains(value);
        }
        assertTrue("Timed out waiting for (" + value + ")", timeout);
    }

    public static class TestLoader extends URLClassLoader {

        public TestLoader(ClassLoader parent) {
            super(new URL[0], parent);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }
    }

}
