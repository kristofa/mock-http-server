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







