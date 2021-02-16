package api;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jobs.CheckPlacesJob;
import jobs.ReserveJob;

public class SessionsAPI extends HttpServlet {
	private static final long serialVersionUID = -2179514316093816186L;

	private static final Random RANDOM = new Random();
	
	private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Session>> sessions = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, Session>>();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getHeader("action");
		
		if (action.equals("requestKey")) {
			long key;
			
			do {
				key = Math.abs(RANDOM.nextLong());
			} while (sessions.containsKey(key));
			
			sessions.put(key, new ConcurrentHashMap<Long, Session>());
			response.addHeader("apiKey", String.valueOf(key));
			
			return;
		}
		
		long apiKey = Long.valueOf(request.getHeader("apiKey"));
		ConcurrentHashMap<Long, Session> keySessions = sessions.get(apiKey);
		
		if (action.equals("createSession")) {
			long id;
			
			do {
				id = RANDOM.nextLong();
			} while (sessions.containsKey(id));
			
			keySessions.put(id, new Session());
			response.addHeader("sessionId", String.valueOf(id));
			
			return;
		}
		
		long usedSessionId = Long.valueOf(request.getHeader("session"));
		Session usedSession = keySessions.get(usedSessionId);
		
		if (action.equals("checkPlace")) {
			usedSession.runJob(new CheckPlacesJob());
			
			return;
		}
		
		if (action.equals("reserve")) {
			usedSession.runJob(new ReserveJob());
			
			return;
		}
	}
}
