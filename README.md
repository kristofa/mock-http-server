# MockHttpServer

MockHttpServer is used to facilitate integration testing of Java applications
that rely on external http services (eg REST services).  MockHttpServer acts as a 
replacement for the external services and is configured to return specific responses 
for given requests.  

The advantages of using MockHttpServer are:

+   Software that is being integration tested does not need to change. The 'System Under
Test' does not know it is accessing a mock service.
+   MockHttpServer is configured and started in the JVM that runs the tests so you 
don't have to set up complex systems and external services.
+   Integration tests typically run faster as MockHttpServer logic is very simple and no
network traffic is needed (MockHttpServer runs on localhost)

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

The test code below shows how we use `LoggingHttpProxy` to sit between the client and
`MockHttpServer`. The client accesses LoggingHttpProxy which forwards the request to 
MockHttpServer and returns the response to the client. In this example code the request/responses
are not persisted to disk. Instead it uses a mock `HttpRequestResponseLogger` which is used to
check that requests/responses are submitted to HttpRequestResponseLogger.

    public class LoggingHttpProxyTest {

      private final static int PROXY_PORT = 51234;
      private final String PROXY_URL = "http://localhost:" + PROXY_PORT;
      private final static int PORT = 51233;

      private LoggingHttpProxy proxy;
      private MockHttpServer server;
      private HttpClient client;
      private HttpRequestResponseLoggerFactory mockLoggerFactory;
      private HttpRequestResponseLogger mockLogger;
      private SimpleHttpResponseProvider responseProvider;

      @Before
      public void setup() throws Exception {

        final ForwardHttpRequestBuilder forwardHttpRequestBuilder = new ForwardHttpRequestBuilder() {

            @Override
            public FullHttpRequest getForwardRequest(final FullHttpRequest request) {
                final FullHttpRequestImpl forwardRequest = new FullHttpRequestImpl(request);
                forwardRequest.port(PORT);
                forwardRequest.domain("localhost");
                return forwardRequest;
            }
        };

        mockLoggerFactory = mock(HttpRequestResponseLoggerFactory.class);
        mockLogger = mock(HttpRequestResponseLogger.class);
        when(mockLoggerFactory.getHttpRequestResponseLogger()).thenReturn(mockLogger);

        proxy = new LoggingHttpProxy(PROXY_PORT, Arrays.asList(forwardHttpRequestBuilder), mockLoggerFactory);
        proxy.start();

        responseProvider = new SimpleHttpResponseProvider();
        server = new MockHttpServer(PORT, responseProvider);
        server.start();

        client = new DefaultHttpClient();
      }

      @After
      public void tearDown() throws Exception {
        proxy.stop();
        server.stop();
        client.getConnectionManager().shutdown();
      }
     
      @Test
      public void successfulForwardRequestTest() throws ClientProtocolException, IOException {

        // Given a mock server configured to respond to a GET / with "OK"
        responseProvider.expect(Method.GET, "/").respondWith(200, "text/plain", "OK");

        final HttpGet req = new HttpGet(PROXY_URL + "/");
        final HttpResponse response = client.execute(req);
        final String responseBody = IOUtils.toString(response.getEntity().getContent());
        final int statusCode = response.getStatusLine().getStatusCode();

        // Then the response is "OK"
        assertEquals("OK", responseBody);
        // And the status code is 200
        assertEquals(200, statusCode);

        final FullHttpRequestImpl expectedRequest = new FullHttpRequestImpl();
        expectedRequest.method(Method.GET);
        expectedRequest.path("/");

        final HttpResponseImpl expectedResponse = new HttpResponseImpl(200, "text/plain", "OK".getBytes());

        final InOrder inOrder = inOrder(mockLoggerFactory, mockLogger);
        inOrder.verify(mockLoggerFactory).getHttpRequestResponseLogger();
        inOrder.verify(mockLogger).log(expectedRequest);
        inOrder.verify(mockLogger).log(expectedResponse);
        verifyNoMoreInteractions(mockLogger, mockLoggerFactory, mockLogger);

      }

    }






