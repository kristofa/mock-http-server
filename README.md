# MockHttpServer

MockHttpServer is used to facilitate integration testing of Java applications
that rely on external http services (eg REST services).  MockHttpServer acts as a 
replacement for the external services and is configured to return specific responses 
for given requests.  

The advantages of using MockHttpServer are:

+   Software that is being integration tested does not need to change. The 'System Under
Test' (*) does not know it is accessing a mock service.
+   MockHttpServer is configured and started in the JVM that runs the tests so you 
don't have to set up complex systems and external services.
+   Integration tests typically run faster as MockHttpServer logic is very simple and no
network traffic is needed (MockHttpServer runs on localhost)

(*) I got the term System Under Test from [following post](http://delicious.com/redirect?url=http%3A//feedproxy.google.com/%7Er/blogspot/RLXA/%7E3/J9QTHN7BtEw/hermetic-servers.html).

![MockHttpServer class diagram](https://raw.github.com/wiki/kristofa/mock-http-server/mockhttpserver_classdiagram.png)


## Dealing with simple requests/responses

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
      public void testShouldHandleGetRequests() throws ClientProtocolException, IOException {
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
      }


When creating an instance of MockHttpServer you have to provide a port and a `HttpResponseProvider`
instance. The `HttpResponseProvider` is the one being configured with request/responses.
 
When you want to configure rather simple requests/responses you can use `SimpleHttpResponseProvider`
as shown in above piece of code. 

MockHttpServer is started by calling `start()` and is stopped by calling `stop()`. 



## Complex request/responses: use LoggingHttpProxy

![LoggingHttpProxy class diagram](https://raw.github.com/wiki/kristofa/mock-http-server/logginghttpproxy_classdiagram.png)

We have software that interacts with multiple external services and several of these services
return complex entities as part of their responses. Building those responses by hand in the source files
might not be the best solution. Also in some cases binary entities are returned.

LoggingHttpProxy is a proxy server that can be configured to sit in between our 'system under test' and
the external services. LoggingHttpProxy is configured to know how to forward requests it receives from
the 'system under test' to the external services. When it received the answer from the external services it
will return it to the 'system under test'.

What is special is that the LoggingHttpProxy can log and persist all the requests/responses.
These persisted requests/responses can be replayed by MockHttpServer. 

![LoggingHttpProxy](https://raw.github.com/wiki/kristofa/mock-http-server/logginghttpproxy.png)

When you configure LoggingHttpProxy to use `HttpRequestResponseFileLoggerFactory` the
requests/responses will be persisted to file. These requests/responses can be replayed
by MockHttpServer by using `FileHttpResponseProvider`.

![MockHttpServer](https://raw.github.com/wiki/kristofa/mock-http-server/mockhttpserver.png)

### Reworking existing integration tests to use MockHttpServer

We assume that you start from an integration test in which case the software you want to test 
actually communicates with an external service and the test runs green.

First thing to do is configure `LoggingHttpProxy` to sit in between the software you test
and the external services. See following code example:

    @Test
    public void testValidRequest() {

        final ForwardHttpRequestBuilder forwardHttpRequestBuilder = new ForwardHttpRequestBuilder() {

            @Override
            public FullHttpRequest getForwardRequest(final FullHttpRequest request) {
                final FullHttpRequestImpl forwardRequest = new FullHttpRequestImpl(request);
                if (request.getPath().contains("service1")) {
                    // Sets host and port for service 1.
                    forwardRequest.domain("host1"); 
                    forwardRequest.port(8080);
                } else {
                    // service 2, service 3
                    forwardRequest.domain("host2");
                    forwardRequest.port(8080);
                }

                return forwardRequest;
            }
        };

        proxy =
            new LoggingHttpProxy(PROXY_PORT, Arrays.asList(forwardHttpRequestBuilder),
                new HttpRequestResponseFileLoggerFactory("src/test/resources/",
                    "ITService_testValidRequest"));
        proxy.start();        
        try
        {
        	// Execute test specific code.
        	// The test code should be configured to have http requests submitted to our 
        	// proxy implementation.
        	// Request / responses will be persisted into src/test/resources/
        }
        finally
        {
          proxy.stop();
        }
        
    } 

    
The code above shows that `LoggingHttpProxy` is configured and started before the actual
test code is executed. What is not visible in the code is that the test code should get
configured to submit http requests to our proxy instead of to the original services.

`LoggingHttpProxy` will forward the requests to the different services (see the creation 
of the `ForwardHttpRequestBuilder`) and log the request/responses in src/test/resources in
files that include the name of the test (see the creation of `HttpResponseFileLoggerFactory`).

When you run the test it is expected to still run green but the different request/responses
should be persisted in src/test/resources.

Next you can update the code to look like this:

    @Test
    public void testValidRequest() {

        mockServer =
            new MockHttpServer(PROXY_PORT, new FileHttpResponseProvider("src/test/resources/",
                "ITService_testValidRequest"));
        mockServer.start();
        try
        {
        	// Execute test specific code.
        	// The test code should be configured to have http requests submitted to our 
        	// mock implementation. Requests/Responses previously persisted 
        	// using LoggingHttpProxy in src/test/resources will be served up. 
        	
        }
        finally
        {
          mockServer.stop();
        }
        
    } 

By configuring `MockHttpServer` to use `FileHttpResponseProvider` we can replay the
requests/responses previously persisted using `LoggingHttpProxy`. Notice that we configure
`FileHttpResponseProvider` in the same way as `HttpRequestResponseFileLoggerFactory` 
before (same directory, same file name). Both use the same naming convention and format
for reading/writing http requests/responses.  You can check in your test code like this
and from that moment on you don't rely on external services anymore.

Advantages of this approach:

+   By having the requests/responses of external services persisted and versioned
with the code our tests keep on functioning also if the external services change over 
time.
+   Decoupling our code from externally deployed services.
+   The tests typically should run faster as the logic of MockHttpServer to serve up 
responses is easy and typically faster then the real services.


