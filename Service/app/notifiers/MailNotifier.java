/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package notifiers;

import play.mvc.*;
import models.*;

/**
 *
 * @author OpenARS Server API team
 */
public class MailNotifier extends Mailer {

    public static void sendAdminLink(Poll question) {
        setSubject("Your admin link");
        // TODO: Add the email for the user that owns the poll.
        //addRecipient(question.email);
        setFrom("OpenARMS.dk <no-reply@openarms.dk>");
        System.out.println("question: " + question);
        if (question != null) {
            send(question);
        }
    }

    public static void sendPollIDLink(Poll question) {
        setSubject("Your poll link");
        // TODO: Add the email for the user that owns the poll.
        //addRecipient(question.email);
        setFrom("OpenARMS.dk <no-reply@openarms.dk>");
        System.out.println(question);
        if (question != null) {
            send(question);
        }
    }

}
