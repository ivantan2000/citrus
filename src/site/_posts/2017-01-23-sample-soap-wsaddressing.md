---
layout: sample
title: SOAP WS Addressing sample
sample: sample-soap-wsaddressing
description: Configure SOAP web service client and server with WSSecurity
categories: [samples]
permalink: /samples/soap-wsaddressing/
---

This sample uses SOAP web services with WSAddressing SOAP header elements. Clients must use proper addressing header elements. 
You can read more about the Citrus SOAP features in [reference guide](http://www.citrusframework.org/reference/html/#soap)

Objectives
---------

The sample project uses WSAddressing feature for requests sent to sample SOAP server. The Citrus SOAP web service
server endpoint validates incoming requests and expects WSAddressing headers to be present in all requests.

First of all we add the WSAddressing header conversion to the client component.

{% highlight xml %}
<citrus-ws:client id="todoListClient"
                  request-url="http://localhost:8080/services/ws/todolist"
                  message-converter="wsAddressingMessageConverter"/>

<bean id="wsAddressingMessageConverter" class="com.consol.citrus.ws.message.converter.WsAddressingMessageConverter">
  <constructor-arg>
    <bean id="wsAddressing200408" class="com.consol.citrus.ws.addressing.WsAddressingHeaders">
      <property name="version" value="VERSION200408"/>
      <property name="action" value="http://citrusframework.org/samples/todolist"/>
      <property name="to" value="http://citrusframework.org/samples/todolist"/>
      <property name="from">
        <bean class="org.springframework.ws.soap.addressing.core.EndpointReference">
          <constructor-arg value="http://citrusframework.org/samples/client"/>
        </bean>
      </property>
      <property name="replyTo">
        <bean class="org.springframework.ws.soap.addressing.core.EndpointReference">
          <constructor-arg value="http://citrusframework.org/samples/client"/>
        </bean>
      </property>
      <property name="faultTo">
        <bean class="org.springframework.ws.soap.addressing.core.EndpointReference">
          <constructor-arg value="http://citrusframework.org/samples/client/fault"/>
        </bean>
      </property>
    </bean>
  </constructor-arg>
</bean>
{% endhighlight %}
   
The client message converter automatically adds WSAddressing headers to the SOAP header. The resulting request look as follows.

{% highlight xml %}
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing">
        <wsa:To SOAP-ENV:mustUnderstand="1">http://citrusframework.org/samples/todolist</wsa:To>
        <wsa:From>
            <wsa:Address xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing">http://citrusframework.org/samples/client</wsa:Address>
        </wsa:From>
        <wsa:ReplyTo>
            <wsa:Address xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing">http://citrusframework.org/samples/client</wsa:Address>
        </wsa:ReplyTo>
        <wsa:FaultTo>
            <wsa:Address xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing">http://citrusframework.org/samples/client</wsa:Address>
        </wsa:FaultTo>
        <wsa:Action>http://citrusframework.org/samples/todolist</wsa:Action>
        <wsa:MessageID>urn:uuid:a3975ef6-68f2-4074-b157-db6c230120b6</wsa:MessageID>
    </SOAP-ENV:Header>
    <SOAP-ENV:Body>
        <todo:getTodoListRequest xmlns:todo="http://citrusframework.org/samples/todolist">
        </todo:getTodoListRequest>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
{% endhighlight %}

The WSAddressing information goes to the header section and contains several elements. One of the elements is marked as *SOAP-ENV:mustUnderstand*.

The server component has to add the *SOAP-ENV:mustUnderstand* handling explicitly in order to support the incoming WSAddressing headers:

{% highlight xml %}
<citrus-ws:server id="todoListServer"
                  port="8080"
                  auto-start="true"
                  interceptors="serverInterceptors"/>

<util:list id="serverInterceptors">
  <bean class="com.consol.citrus.ws.interceptor.SoapMustUnderstandEndpointInterceptor">
    <property name="acceptedHeaders">
      <list>
        <value>{http://schemas.xmlsoap.org/ws/2004/08/addressing}To</value>
      </list>
    </property>
  </bean>
  <bean class="com.consol.citrus.ws.interceptor.LoggingEndpointInterceptor"/>
</util:list>   
{% endhighlight %}
     
The server is now ready to receive the request and validate the WSAddressing header information. 

{% highlight java %}
soap()
    .server(todoServer)
    .receive()
    .payload(new ClassPathResource("templates/addTodoEntryRequest.xml"))
    .header(new ClassPathResource("templates/soapWsAddressingHeader.xml"));

soap()
    .server(todoServer)
    .send()
    .payload(new ClassPathResource("templates/addTodoEntryResponse.xml"));
{% endhighlight %}
        
We do this by adding the complete SOAP header as expected XML structure. The header information is loaded from external file resource.
       
{% highlight xml %}
<SOAP-ENV:Header xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing">
  <wsa:To SOAP-ENV:mustUnderstand="1">http://citrusframework.org/samples/todolist</wsa:To>
  <wsa:From>
    <wsa:Address>http://citrusframework.org/samples/client</wsa:Address>
  </wsa:From>
  <wsa:ReplyTo>
    <wsa:Address>http://citrusframework.org/samples/client</wsa:Address>
  </wsa:ReplyTo>
  <wsa:FaultTo>
    <wsa:Address>http://citrusframework.org/samples/client</wsa:Address>
  </wsa:FaultTo>
  <wsa:Action>http://citrusframework.org/samples/todolist</wsa:Action>
  <wsa:MessageID>@ignore@</wsa:MessageID>
</SOAP-ENV:Header>     
{% endhighlight %}

Citrus automatically performs XML comparison and validation on all header elements. As you can see we ignore the *MessageID* element with *@ignore@*. This is 
simply because this is a generated UUID value that is newly generated on the client side for each request.

In general we can overwrite WSAddressing header information in each send operation by setting the special WSAddressing message headers.

{% highlight java %}
soap()
    .client(todoClient)
    .send()
    .soapAction("addTodoEntry")
    .header(WsAddressingMessageHeaders.ACTION, "http://citrusframework.org/samples/todolist/addTodoEntry")
    .header(WsAddressingMessageHeaders.MESSAGE_ID, "urn:uuid:citrus:randomUUID()")
    .payload(new ClassPathResource("templates/addTodoEntryRequest.xml"));
{% endhighlight %}
        
Run
---------

The sample application uses Maven as build tool. So you can compile, package and test the
sample with Maven.
 
     mvn clean install
    
This executes the complete Maven build lifecycle. During the build you will see Citrus performing some integration tests.

Execute all Citrus tests by calling

     mvn integration-test

You can also pick a single test by calling

     mvn integration-test -Ptest=TodoListIT

You should see Citrus performing several tests with lots of debugging output. 
And of course green tests at the very end of the build.

Of course you can also start the Citrus tests from your favorite IDE.
Just start the Citrus test using the TestNG IDE integration in IntelliJ, Eclipse or Netbeans.