package controllers;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import play.mvc.Controller;
import play.mvc.Http.StatusCode;
import api.entities.ChoiceJSON;
import api.entities.PollInstanceJSON;
import api.entities.PollJSON;
import api.requests.CreatePollInstanceRequest;
import api.requests.ReadPollByTokenRequest;
import api.requests.ReadPollRequest;
import api.requests.UpdatePollRequest;
import api.responses.AuthenticateUserResponse;
import api.responses.CreatePollInstanceResponse;
import api.responses.ReadPollResponse;
import api.responses.Response;
import api.responses.UpdatePollResponse;

public class ManagePoll extends Controller {
	public static void index(Long id) {
		try {
			ReadPollResponse response = (ReadPollResponse) APIClient.send(new ReadPollRequest(id));
			String pollToken = response.poll.token;
			String pollReference = response.poll.reference;
			Boolean pollMultipleAllowed = response.poll.multipleAllowed;
			String pollQuestion = response.poll.question;
			List<ChoiceJSON> pollChoices = response.poll.choices;
			
			validation.required(pollToken);
			validation.required(pollReference);
			validation.required(pollMultipleAllowed);
			validation.required(pollQuestion);
			validation.required(pollChoices);
			
			if (!validation.hasErrors()) {
				render(pollToken, pollReference, pollMultipleAllowed, pollQuestion, pollChoices);
			}
			else {
				// TODO: error handling
			}
		} catch (Exception e) {
			// TODO: tell the user it failed
		}
	}
	
	public static void update(String token, String reference, Boolean multipleAllowed, String question, List<ChoiceJSON> choices) {
		validation.required(reference);
		validation.required(multipleAllowed);
		validation.required(question);
		validation.required(choices);
		
		if (!validation.hasErrors()) {
			// TODO: error handling
			}
		
		PollJSON pollJson = new PollJSON();
		pollJson.choices = choices;
		pollJson.multipleAllowed = multipleAllowed;
		pollJson.question = question;
		pollJson.reference = reference;
		
		try {
			UpdatePollResponse response = (UpdatePollResponse) APIClient.send(new UpdatePollRequest(pollJson));
		} catch (Exception e) {
			// TODO: tell the user it failed
		}
	}
	
	public static void listfun() {
		render();
	}

	public static void activate(Date start, Date end) {
		// TODO: this! (activate/instantiate pollinstance with start and end time
		PollInstanceJSON pollInstance = new PollInstanceJSON();
		pollInstance.endDateTime = end;
		pollInstance.startDateTime = start;
		
		validation.required(end);
		validation.required(start);
		
		if (!validation.hasErrors()) {
			try {
				CreatePollInstanceResponse response = (CreatePollInstanceResponse) APIClient.send(new CreatePollInstanceRequest(pollInstance));
			} catch (Exception e) {
				// TODO Exception handling
			}
			render();
		}
		else {
			// TODO error handling
		}
	}

	public static void clonepoll() {
		// TODO: this! (clone existing poll)
	}

	public static void statistics() {
		try {
			// Load service data.
			Response res0 = APIClient.loadServiceData("data.yml");
			if(!StatusCode.success(res0.statusCode)) {
				throw new Exception("Couldn't load test data.");
			}
			boolean authenticated = APIClient.authenticateSimple("spam@creen.dk", "openarms");
			if(!authenticated) {
				throw new Exception("Could not authenticate");
			}

			ReadPollResponse res1 = (ReadPollResponse)APIClient.send(new ReadPollByTokenRequest("123456"));
			if(!StatusCode.success(res1.statusCode)) {
				throw new Exception("Couldn't read poll with token 123456.");
			}

			PollInstanceJSON pi = new PollInstanceJSON();
			pi.poll_id = res1.poll.id;
			pi.startDateTime = Calendar.getInstance().getTime();
			Calendar endDateTimeCalendar = Calendar.getInstance();
			endDateTimeCalendar.set(Calendar.YEAR, 2020);
			pi.endDateTime = endDateTimeCalendar.getTime();
			CreatePollInstanceResponse res2 = (CreatePollInstanceResponse)APIClient.send(new CreatePollInstanceRequest(pi));
			if(!StatusCode.success(res2.statusCode)) {
				throw new Exception("Couldn't create poll instance with token time now.");
			}
			
			Long pollinstance_id = res2.pollinstance.id;
			render(pollinstance_id);
		} catch(Exception e) {
			e.printStackTrace();
			// TODO: Use an error template.
		}
	}

	public static void testStatistics() {
		renderText("{\"pollID\":518196,\"questionID\":127,\"question\":\"Question?\",\"answers\":[\"Answer 1\",\"Answer 2\", \"Answer 3\"],\"votes\":[50, 10, 40]}");
	}
}
