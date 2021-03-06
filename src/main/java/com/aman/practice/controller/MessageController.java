package com.aman.practice.controller;

import com.aman.practice.dto.Employee;
import com.aman.practice.pojo.EmployeePojo;
import com.aman.practice.producres.JmsProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@RestController
@Slf4j
public class MessageController {

    @Value("${active-mq.queue}")
    private String queue;

    @Autowired
    JmsTemplate jmsQueueTemplate;

    @Autowired
    JmsProducer jmsProducer;

    @Autowired
    ConnectionFactory  activeMQConnectionFactory;

    @PostMapping(value="/employee")
    public EmployeePojo sendMessage(@RequestBody EmployeePojo employee){
        System.out.println("here"+employee);
        jmsProducer.sendMessage(employee);
        return employee;
    }

    @GetMapping(value="/getPendingMessages" )
    public void getAllMessages()  {

        List<Message>  mlist =jmsQueueTemplate.browse(queue, new BrowserCallback<List<Message>>() {

            @Override
            public List<Message> doInJms(Session session, QueueBrowser browser) throws JMSException {

                List<Message> list = new ArrayList<>();
                Enumeration messages = browser.getEnumeration();
                int total = 0;
                while (messages.hasMoreElements()) {
                    ObjectMessage m = (ObjectMessage)messages.nextElement();
                    System.out.println((EmployeePojo)m.getObject());
                    list.add(m);
                }
                return list;
            }
        });
        System.out.println(mlist.size());
    }

    @GetMapping(value="/getSelectedPendingMessages" )
    public void getSelectedMessages(@RequestParam String id)  {
        String messageSelector="JMSCorrelationID='"+id+"'";

        List<Message>  mlist =jmsQueueTemplate.browseSelected(queue,messageSelector, new BrowserCallback<List<Message>>() {

            @Override
            public List<Message> doInJms(Session session, QueueBrowser browser) throws JMSException {

                List<Message> list = new ArrayList<>();
                Enumeration messages = browser.getEnumeration();
                int total = 0;
                while (messages.hasMoreElements()) {
                    ObjectMessage m = (ObjectMessage)messages.nextElement();
                    System.out.println((Employee)m.getObject());
                    list.add(m);
                }
                return list;
            }
        });
        System.out.println(mlist.size());
    }
}