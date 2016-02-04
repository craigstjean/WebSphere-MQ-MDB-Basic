package com.craigstjean.mqtut.basic.servlet;

import java.io.IOException;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/servlet/hello")
public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = -8211165987639677828L;

	@Resource(name = "java:comp/env/jms/HelloConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "java:comp/env/jms/Request_HelloQueue")
	private Destination requestQueue;

	@Resource(name = "java:comp/env/jms/Response_HelloQueue")
	private Destination responseQueue;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");

		Connection jmsConnection = null;
		Session session = null;

		String response = null;
		try {
			jmsConnection = connectionFactory.createConnection();
			session = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			TextMessage requestMessage = session.createTextMessage();
			requestMessage.setText(name);

			MessageProducer mp = session.createProducer(null);
			// use 30 minute expiry
			mp.setTimeToLive(1000 * 60 * 30);
			mp.send(requestQueue, requestMessage);

			String messageSelector = "JMSCorrelationID='" + requestMessage.getJMSMessageID() + "'";
			MessageConsumer mc = session.createConsumer(responseQueue, messageSelector);
			jmsConnection.start();

			Message responseMessage = mc.receive(5 * 1000);
			if (responseMessage != null) {
				response = ((TextMessage) responseMessage).getText();
			} else {
				response = "No message found within 5 seconds...";
			}

			req.setAttribute("message", response);
			req.getRequestDispatcher("/index.jsp").forward(req, resp);
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (JMSException e) {

			}

			try {
				if (jmsConnection != null) {
					jmsConnection.close();
				}
			} catch (JMSException e) {

			}
		}

	}

}
