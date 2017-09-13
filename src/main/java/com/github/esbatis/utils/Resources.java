/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.esbatis.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * A class to simplify access to resources through the classloader.
 * @author jinzhong.zhang
 */
public class Resources {

    private static ClassLoader defaultClassLoader;
    private static ClassLoader systemClassLoader;

    /*
     * Charset to use when calling getResourceAsReader.
     * null means use the system default.
     */
    private static Charset charset;

    private Resources() {
    }

    static {
        try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException ignored) {
            // AccessControlException on Google App Engine
        }
    }

    /*
   * Returns the default classloader (may be null).
   *
   * @return The default classloader
   */
    public static ClassLoader getDefaultClassLoader() {
        return defaultClassLoader;
    }

    /*
     * Sets the default classloader
     *
     * @param defaultClassLoader - the new default ClassLoader
     */
    public static void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        defaultClassLoader = defaultClassLoader;
    }

    /*
     * Returns the URL of the resource on the classpath
     *
     * @param resource The resource to find
     * @return The resource
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static URL getResourceURL(String resource) throws IOException {
        return getResourceURL(resource, null);
    }

    /*
     * Returns the URL of the resource on the classpath
     *
     * @param loader   The classloader used to fetch the resource
     * @param resource The resource to find
     * @return The resource
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static URL getResourceURL(String resource, ClassLoader loader) throws IOException {
        URL url = getResourceAsURL(resource, getClassLoaders(loader));
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return url;
    }

    /*
     * Returns a resource on the classpath as a Stream object
     *
     * @param resource The resource to find
     * @return The resource
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(resource, (ClassLoader) null);
    }

    /*
     * Returns a resource on the classpath as a Stream object
     *
     * @param resource The resource to find
     * @param loader   The classloader used to fetch the resource
     * @return The resource
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static InputStream getResourceAsStream(String resource, ClassLoader loader) throws IOException {
        InputStream in = getResourceAsStream(resource, getClassLoaders(loader));
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    /*
     * Returns a resource on the classpath as a Properties object
     *
     * @param resource The resource to find
     * @return The resource
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static Properties getResourceAsProperties(String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(resource);
        props.load(in);
        in.close();
        return props;
    }

    /*
     * Returns a resource on the classpath as a Properties object
     *
     * @param resource The resource to find
     * @param loader   The classloader used to fetch the resource
     * @return The resource
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static Properties getResourceAsProperties(String resource, ClassLoader loader) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(resource, loader);
        props.load(in);
        in.close();
        return props;
    }

    /*
     * Returns a resource on the classpath as a Reader object
     *
     * @param resource The resource to find
     * @return The resource
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static Reader getResourceAsReader(String resource) throws IOException {
        Reader reader;
        if (charset == null) {
            reader = new InputStreamReader(getResourceAsStream(resource));
        } else {
            reader = new InputStreamReader(getResourceAsStream(resource), charset);
        }
        return reader;
    }

    /*
     * Returns a resource on the classpath as a File object
     *
     * @param resource The resource to find
     * @return The resource
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static File getResourceAsFile(String resource) throws IOException {
        return new File(getResourceURL(resource).getFile());
    }

    /*
     * Returns a resource on the classpath as a File object
     *
     * @param loader   - the classloader used to fetch the resource
     * @param resource - the resource to find
     * @return The resource
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static File getResourceAsFile(ClassLoader loader, String resource) throws IOException {
        return new File(getResourceURL(resource, loader).getFile());
    }

    /*
     * Gets a URL as an input stream
     *
     * @param urlString - the URL to get
     * @return An input stream with the data from the URL
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static InputStream getUrlAsStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getInputStream();
    }

    /*
     * Gets a URL as a Reader
     *
     * @param urlString - the URL to get
     * @return A Reader with the data from the URL
     * @throws java.io.IOException If the resource cannot be found or read
     */
    public static Reader getUrlAsReader(String urlString) throws IOException {
        Reader reader;
        if (charset == null) {
            reader = new InputStreamReader(getUrlAsStream(urlString));
        } else {
            reader = new InputStreamReader(getUrlAsStream(urlString), charset);
        }
        return reader;
    }

    /*
     * Find a class on the classpath (or die trying)
     *
     * @param name - the class to look for
     * @return - the class
     * @throws ClassNotFoundException Duh.
     */
    public static Class<?> classForName(String name) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(null));
    }

    /*
     * Find a class on the classpath, starting with a specific classloader (or die trying)
     *
     * @param name        - the class to look for
     * @param classLoader - the first classloader to try
     * @return - the class
     * @throws ClassNotFoundException Duh.
     */
    public static Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(classLoader));
    }

    /*
     * Try to get a resource from a group of classloaders
     *
     * @param resource    - the resource to get
     * @param classLoader - the classloaders to examine
     * @return the resource or null
     */
    private static InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
        for (ClassLoader cl : classLoader) {
            if (null != cl) {

                // try to find the resource as passed
                InputStream returnValue = cl.getResourceAsStream(resource);

                // now, some class loaders want this leading "/", so we'll add it and try again if we didn't find the resource
                if (null == returnValue) {
                    returnValue = cl.getResourceAsStream("/" + resource);
                }

                if (null != returnValue) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    /*
     * Get a resource as a URL using the current class path
     *
     * @param resource    - the resource to locate
     * @param classLoader - the class loaders to examine
     * @return the resource or null
     */
    private static URL getResourceAsURL(String resource, ClassLoader[] classLoader) {

        URL url;

        for (ClassLoader cl : classLoader) {

            if (null != cl) {

                // look for the resource as passed in...
                url = cl.getResource(resource);

                // ...but some class loaders want this leading "/", so we'll add it
                // and try again if we didn't find the resource
                if (null == url) {
                    url = cl.getResource("/" + resource);
                }

                // "It's always in the last place I look for it!"
                // ... because only an idiot would keep looking for it after finding it, so stop looking already.
                if (null != url) {
                    return url;
                }

            }

        }

        // didn't find it anywhere.
        return null;

    }

    /*
     * Attempt to load a class from a group of classloaders
     *
     * @param name        - the class to load
     * @param classLoader - the group of classloaders to examine
     * @return the class
     * @throws ClassNotFoundException - Remember the wisdom of Judge Smails: Well, the world needs ditch diggers, too.
     */
    private static Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {

        for (ClassLoader cl : classLoader) {

            if (null != cl) {

                try {

                    Class<?> c = Class.forName(name, true, cl);

                    if (null != c) {
                        return c;
                    }

                } catch (ClassNotFoundException e) {
                    // we'll ignore this until all classloaders fail to locate the class
                }

            }

        }

        throw new ClassNotFoundException("Cannot find class: " + name);

    }

    private static ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        return new ClassLoader[]{
                classLoader,
                defaultClassLoader,
                Thread.currentThread().getContextClassLoader(),
                Resources.class.getClassLoader(),
                systemClassLoader};
    }

}
