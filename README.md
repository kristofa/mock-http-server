# MockHttpServer


**This project is archived and not maintained anymore. The repo remains read-only and can be forked. I suggest you to look at similar projects that are still maintained like [wiremock](http://wiremock.org/) or [mockwebserver](https://github.com/square/okhttp/tree/master/mockwebserver)**

[![Build Status](https://travis-ci.org/kristofa/mock-http-server.svg?branch=master)](https://travis-ci.org/kristofa/mock-http-server)

MockHttpServer is available through Maven Central so you can get it by including
following dependency in your pom.xml:

    <dependency>
        <groupId>com.github.kristofa</groupId>
        <artifactId>mock-http-server</artifactId>
        <version>4.1</version>
        <scope>test</scope>
    </dependency>


MockHttpServer is used to facilitate integration testing of Java applications
that rely on external http services (eg REST services).  MockHttpServer acts as a 
replacement for the external services and is configured to return specific responses 
for given requests.

Features:

+   Setting up expected http request/responses.
+   Support for logging existing traffic between 'System Under Test' (*) and external services and replaying the request/responses.
+   Support for matching http requests that don't completely match when re-executed.
+   Supports multiple times the same request with same or different responses.

Advantages:

+   Software that is being integration tested does not need to change. The 'System Under
Test' does not know it is accessing a mock service.
+   MockHttpServer is configured and started in the JVM that runs the tests so you 
don't have to set up complex systems and external services. You can test components in isolation.
+   Integration tests typically run faster as MockHttpServer logic is very simple and no
network traffic is needed (MockHttpServer runs on localhost)

(*) I got the term System Under Test from [following post](http://googletesting.blogspot.be/2012/10/hermetic-servers.html).

See also following posts by Martin Fowler: [Self Initializing Fake](http://martinfowler.com/bliki/SelfInitializingFake.html) and
[Integration Contract Test](http://martinfowler.com/bliki/IntegrationContractTest.html).

![MockHttpServer class diagram](https://raw.github.com/wiki/kristofa/mock-http-server/mockhttpserver_classdiagram.png)

There are also other solutions to this problem which might be a better fit to your needs or which might be more mature: [wiremock](http://wiremock.org/), [rest-driver](https://github.com/rest-driver/rest-driver),
[mockwebserver](https://github.com/square/okhttp/tree/master/mockwebserver).


## Simple requests/responses ##

    public class MockHttpServerTest {
      private static final int PORT = 51234;
      private static final String baseUrl = "http://localhost:" + PORT;
      private MockHttpServer server;
      private SimpleHttpResponseProvider responseProvider;
      private HttpClient client;

      @Before
      public void setUp() throws Exception {
          responseProvider = new SimpleHttpResponseProvider();
          server = new MockHttpServer(PORT, responseProvider);
          server.start();
          client = new DefaultHttpClient();
      }

      @After
      public void tearDown() throws Exception {
          client.getConnectionManager().shutdown();
          server.stop();
      }

      @Test
      public void testShouldHandleGetRequests() throws ClientProtocolException, IOException, UnsatisfiedExpectationException {
          // Given a mock server configured to respond to a GET / with "OK"
          responseProvider.expect(Method.GET, "/").respondWith(200, "text/plain", "OK");

          // When a request for GET / arrives
          final HttpGet req = new HttpGet(baseUrl + "/");
          final HttpResponse response = client.execute(req);
          final String responseBody = IOUtils.toString(response.getEntity().getContent());
          final int statusCode = response.getStatusLine().getStatusCode();

          // Then the response is "OK"
          assertEquals("OK", responseBody);

          // And the status code is 200
          assertEquals(200, statusCode);
          // Check there were no unexpected requests.
          server.verify();
      }


When creating an instance of MockHttpServer you have to provide a port and a `HttpResponseProvider`
instance. The `HttpResponseProvider` is the one being configured with request/responses.
 
When you want to configure rather simple requests/responses you can use `SimpleHttpResponseProvider`
as shown in above piece of code. 

MockHttpServer is started by calling `start()` and is stopped by calling `stop()`. 
You can execute `verify()` when your test completed to make sure you got all and only your 
expected requests. `verify()` will throw a `UnsatisfiedExpectationException` when this is not the case.


## Complex request/responses ##

![LoggingHttpProxy class diagram](https://raw.github.com/wiki/kristofa/mock-http-server/logginghttpproxy_classdiagram.png)

We have software that interacts with multiple external services and several of these services
return complex entities as part of their responses. Building those responses by hand in the source files
might not be the best solution. Also in some cases binary entities are returned.

`LoggingHttpProxy` is a proxy server that can be configured to sit in between our 'system under test' and
the external services. LoggingHttpProxy is configured to know how to forward requests it receives from
the 'system under test' to the external services. When it received the answer from the external services it
will return it to the 'system under test'.

What is special is that the LoggingHttpProxy can log and persist all the requests/responses.
These persisted requests/responses can be replayed by MockHttpServer. 

![LoggingHttpProxy](https://raw.github.com/wiki/kristofa/mock-http-server/logginghttpproxy.png)

When you configure LoggingHttpProxy to use `HttpRequestResponseFileLoggerFactory` the
requests/responses will be persisted to files. These requests/responses can be replayed
by MockHttpServer by using `FileHttpResponseProvider`.

![MockHttpServer](https://raw.github.com/wiki/kristofa/mock-http-server/mockhttpserver.png)

### Reworking existing integration tests to log and replay http requests ###

We assume that you start from an integration test in which case the software you want to test 
communicates with an external service and the test runs green. You want to mock the communication
with the external service as it might be unreliable or even disappear.

You can use `MockHttpServer` or `LoggingHttpProxy` by themselves but since 2.0-SNAPSHOT 
it is advised to use `MockAndProxyFacade` which will make it a lot easier. See following code
example:

    import static org.junit.Assert.assertEquals;

    import java.io.IOException;

	import org.apache.http.HttpResponse;
	import org.apache.http.client.HttpClient;
	import org.apache.http.client.methods.HttpGet;
	import org.apache.http.impl.client.DefaultHttpClient;
	import org.junit.Test;

	import com.github.kristofa.test.http.MockAndProxyFacade.Builder;
	import com.github.kristofa.test.http.MockAndProxyFacade.Mode;
	import com.github.kristofa.test.http.file.FileHttpResponseProvider;
	import com.github.kristofa.test.http.file.HttpRequestResponseFileLoggerFactory;

	public class MockHttpRequestTest {

		// Host of original service. Service which we in the end want to replace with our mock implementation.
		private final static String SERVICE_HOST = "host.domain";
		// Port for host.
		private final static int SERVICE_PORT = 8080;
		// The port at which our mock or proxy will be running.
		private final static int MOCK_AND_PROXY_PORT = 51235;
		private final static String MOCK_PROXY_URL = "http://localhost:" + MOCK_AND_PROXY_PORT;

		// Requests and responses will be logged to src/test/resources.
		// This is what you typically want to do and check them in with your source code.
		private final static String REQUEST_LOG_DIR = "src/test/resources/";
		// We make sure our persisted request/responses have a unique name. Name of test class
		// is probably a good choice.
		private final static String REQUEST_LOG_FILE_NAME = "MockHttpRequestTest";

		@Test
		public void test() throws IOException, UnsatisfiedExpectationException {
			// Initially you will want to log your existing requests. So you can put it to Mode.LOGGING.
			// Once they are logged you can switch to MOCKING to replay persisted requests/responses.
			// Changing this mode is the only thing you need to do. Your remaining test code stays the same.
			final MockAndProxyFacade facade = buildFacade(Mode.LOGGING);
			facade.start();
			try {
				// Execute your test code that will exercise mock or proxy depending on mode of operation for our facade.

				final HttpClient httpClient = new DefaultHttpClient();
				try {
					final HttpGet req1 = new HttpGet(MOCK_PROXY_URL + "/service/a");
					final HttpResponse response1 = httpClient.execute(req1);
					assertEquals(200, response1.getStatusLine().getStatusCode());
				} finally {
					httpClient.getConnectionManager().shutdown();
				}

				// Verify that we got all and only the requests we expected.
				facade.verify();
			} finally {
				facade.stop();
			}

		}

		private MockAndProxyFacade buildFacade(final Mode mode) {
			final Builder builder = new Builder();
			return builder
				.mode(mode)
				.addForwardHttpRequestBuilder(new PassthroughForwardHttpRequestBuilder(SERVICE_HOST, SERVICE_PORT))
				.httpRequestResponseLoggerFactory(
					new HttpRequestResponseFileLoggerFactory(REQUEST_LOG_DIR, REQUEST_LOG_FILE_NAME)).port(MOCK_AND_PROXY_PORT)
				.httpResponseProvider(new FileHttpResponseProvider(REQUEST_LOG_DIR, REQUEST_LOG_FILE_NAME)).build();
		}

	}

Important: You can't copy and run this code as is. It compiles and is valid but the http request will fail as there is no service at http://host.domain:8080. It is just an example
for you to reuse.

This code example shows the usage of `MockAndProxyFacade`. This is a facade around `MockHttpServer` and `LoggingHttpProxy` and has as
purpose to switch between both with as least impact on test code as possible.

As you can see in the `buildFade(Mode)` method the MockAndProxyFacade is created using a Builder. The Builder handles configuration for both Logging as Mocking mode.

Mandatory Builder parameters (required for both mocking and logging modes):

+   Mode: The mode needs to be defined and is either Mode.LOGGING or Mode.MOCKING
+   Port: The port at which the mock or proxy will listen.

Builder parameters mandatory for LOGGING mode:

+   ForwardHttpRequestBuilder: Is responsible for creating a forward request for incoming requests in LoggingHttpProxy. In our example we use `PassthroughForwardHttpRequestBuilder` which
simply adapts the host and port to the service we want to mock. This is what we want to typically do.
+   HttpRequestResponseLoggerFactory: This factory create `HttpRequestResponseLogger` instances that will be used to log requests/responses. In this 
example we use and `HttpRequestResponseFileLoggerFactory` which will persist them to disk so we can replay them with MockHttpServer.

Builder parameters mandatory for MOCKING mode:

+   HttpResponseProvider: Provides responses for expected requests. In our example we use `FileHttpResponseProvider` which will use the requests/responses 
persisted by `HttpRequestResponseFileLoggerFactory`. Notice `HttpRequestResponseFileLoggerFactory` and `FileHttpResponseProvider` are configured with same directory and filename
values.  This will make sure FileHttpResponseProvider will pick up requests/responses persisted during LOGGING mode.

So if we want to switch between mocking and logging mode, because we expect our depend service is changed, we can simply change the mode parameter we pass to the `buildFade(Mode)`
method. The remaining test code and logic stays the same.

Advantages of this approach:

+   By having the requests/responses of external services persisted and versioned
with the code our tests keep on functioning also if the external services become unavailable or change over 
time.
+   Decoupling our code from externally deployed services and having everything under version control.
+   The tests typically should run faster as the logic of MockHttpServer to serve up 
responses is easy and typically faster than the real services.
+   Persisted requests/responses are copies from the requests/responses with the real
services so no chance of mistakes by manually creating requests/responses.

## Custom http request matching logic ##

As of version 4.0-SNAPSHOT support is added for matching variable, 
non deterministic content in http requests.

An example of variable content can be a JSON entity.
JSON does not specify a fixed order of properties so when serializing JSON the order of
the properties can be different from 1 run to the other.

By default mock-http-server does an exact match of http requests, including entities. 
To change the matching logic you can implement a `HttpRequestMatchingFilter` that you
can set in any of the available `HttpResponseProvider` instances.

Also `HttpRequestMatchingFilter` instances can be chained so you can set use multiple.
If you would have json entities but also other variable content like access tokens you
can build 2 `HttpRequestMatchingFilter` instances, 1 for each purpose and chain them.

There are some examples of Http Request Matching Filters that you can inspect:

+   AllExceptContentTypeHeaderFilter
+   AllExceptOriginalHeadersFilter
+   JsonMatchingFilter ([in separate project](https://github.com/kristofa/mock-http-server-json-matcher))



## Contribution ##

If you want to contribute, this is a list of wanted functionality:

+   Maven plugin: Set up and start mock-http-server before executing tests and tear it down when tests are finished.
Preferably configured to replay requests/responses from file. 

## Changelog ##

### 4.1 - 27th of November 2014 ###

+   `MockHttpServer` selects a free port when 0 is passed as port number. This prevents conflicts as opposed to always taking a fixed port which can already be in used. Used port is returned by `start`method and there is also a new `getPort`method.
+   `DefaultHttpResponseProvider`has a `reset`method which allows reseting its state.

### 4.0 - 10th of March 2014 ###

Major version bump because custom matching of http requests, introduced in 3.0, has been reworked.
When using version 3.0 in real use cases it became clear that it was sub optimal.
4.0-SNAPSHOT has a better implementation of custom matching and major cleanup of existing
`HttpResponseProvider` instances.

+   Better and chainable custom http request matching support.
+   Clean-up of existing HttpResponseProvider implementations. 
+   `HttpRequestResponseFileLoggerFactory` has the option to delete previously logged requests/responses. 
This prevents potential test failures if your new log session has less request/responses than previous one or if the requests/responses come in different order.
In reality most people manually deleted previously logged files. Now it can be done automatically through optional constructor argument.

### 3.0 - 13th of February 2014 ###

Major version bump because `HttpResponseProvider` interface has a new method.
Code that worked with 2.0 will also still work with 3.0 but if you have written your own `HttpResponseProvider` instances they will need adaptation and will not compile anymore.

+   New: Introduce `HttpRequestMatcher` which allows you to match http requests on part of their content.  Can also be used to match http requests that have variable content. For example a
UUID that is generated and part of the request and which will be always different.
You can add custom `HttpRequestMatcher` instances to `HttpResponseProvider` instances which will than be used for matching.

### 2.0 - 2nd of January 2014 ###

Version bump because the changes explained in 1st, 2nd and 3rd main bullet points can lead to failing tests that worked with 1.3 or earlier.
The api is still the same but the behaviour is different.

+   Bugfix: Bring `MockHttpServer` and `LoggingHttpProxy` in line (by sharing code and removing some code duplication).
      +   Do not filter http headers anymore in LoggingHttpProxy.
      +   Support request entities for which content length is not provided in LoggingHttpProxy.
+   Change default response code of `MockHttpServer` that indicates no matching request is found from 500 to 598.
+   Introduce and use a custom http client in `LoggingHttpProxy` so that original requests/responses are not modified. Previously some http headers could be added when they were not provided for example User-Agent.
+   Bugfix: `HttpRequestFileReaderImpl` supports message headers / key value pairs with multiple 'equal signs'. We now support for example: ContentType=application/json; charset=UTF-8.
+   Bugfix: `HttpResponseFileWriterImpl` supports responses without Content-Type.
+   Improve robustness of `MockHttpServer` by catching any exception that occurs, log it, and return specific response code which is configurable (default=599).
+   Add some error and debug log messages in both MockHttpSever and LoggingHttpProxy. This should improve debugging of unexpected requests.
+   Update FileHttpResponseProvider to use lazy initialization. This is needed to get it working with MockAndProxyFacade.
+   New: Introduce `MockAndProxyFacade` which makes it a lot more easy to switch between 'logging request' mode and 'mocking' mode. There is an integration test `ITMockAndProxyFacade` that shows how it works.

### 1.3 - 14th of July 2013 ###

+   Adapt MockHttpServer to Support request entities for which content length is not provided.
+   Update SimpleHttpResponseProvider so it supports specifying query parameters as part of path.

### 1.2 - 8th of June 2013 ###

+   [Sam Starling](https://github.com/samstarling) : Make it possible to specify custom 
HTTP code when no matching response is found. Before 500 was returned but it you want
to build a test and you expect 500 to be returned you can change it now.
+   [Sam Starling](https://github.com/samstarling) : Support reseting 
SimpleHttpResponseProvider. This allows you to set up MockHttpServer only once for a set
of tests instead of setting it up for each test. This makes tests run faster.
+   When Content-Type is not set in response also don't add header with empty value 
in MockHttpServer.

### 1.1 - 4th of May 2013 ###

+   [Dominique Dierickx](https://github.com/ddierickx) : Introduce PassthroughLoggingHttpProxy.
+   In version 1.0 MockHttpServer filtered all http headers except Content-Type. This filter is removed now but SimpleHttpResponseProvider is adapted
so it only cares about Content-Type so behaviour is same as before. There is a new implementation, DefaultHttpResponseProvider, which matches all headers of your choice.

### 1.0 - 2nd of January 2013 ###

First release
