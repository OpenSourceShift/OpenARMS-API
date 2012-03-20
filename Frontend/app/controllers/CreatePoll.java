package controllers;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import api.entities.ChoiceJSON;
import api.entities.PollJSON;
import api.requests.CreatePollRequest;
import api.responses.CreatePollResponse;

import models.Poll;
import play.mvc.Controller;

public class CreatePoll extends Controller {
	public static void index(String email, String question, String[] choices) {
		if (choices == null) {
			choices = new String[] { "", "" };
		}
		render(email, question, choices);
	}

	public static void success(String token) {
		render(token);
	}

	public static void submit(String email, String question, String[] choices, String type) {
		// Remove empty lines from the answers
		List<ChoiceJSON> choicesJson = new LinkedList<ChoiceJSON>();
		if (choices != null) {
			for (String s: choices) {
				if (s != null && !s.isEmpty()) {
					ChoiceJSON choice = new ChoiceJSON();
					choice.text = s;
					choicesJson.add(choice);
				}
			}
		}

		// Validate that the question and answers are there.
		validation.required(email);
		validation.email(email);
		validation.required(question);
		validation.required(type);

		// Validate that we have at least 2 answer options.
		if (choicesJson.size() < 2) {
			validation.addError("choices", "validation.required.atLeastTwo", choices);
		}

		// If we have an error, go to newpollform.
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			index(email, question, choices);
			return;
		}
		
		// TODO: Check if the email provided belongs to an existing user
		// If yes: Redirect the user to the login page - save the poll somewhere temporarily.
		// If no: Create a new user and create the poll, and bind these two together.

		// Finally, we're ready to send it to the server, and see if it likes it or not.
		PollJSON p = new PollJSON();
		p.question = question;
		p.choices = choicesJson;
		if (type.equals("multiple")) {
			p.multipleAllowed = true;
		}

		try {
			CreatePollResponse response = (CreatePollResponse) APIClient.send(new CreatePollRequest(p));
			PollJSON poll = response.poll;
			if(poll != null) {
				success(poll.token);
			} else {
				throw new Exception("Something went wrong, creating the poll.");
			}
	
			// Redirect to success
		} catch (Exception e) {
			// It failed!
			// TODO: Tell the user!
			//e.printStackTrace();
			params.flash();
			validation.addError(null, e.getMessage());
			validation.keep();
			index(email, question, choices);
		}
	}
}
