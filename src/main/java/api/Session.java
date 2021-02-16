package api;

import jobs.IJob;

public class Session {
	void runJob(IJob job) {
		job.runJob();
	}
}