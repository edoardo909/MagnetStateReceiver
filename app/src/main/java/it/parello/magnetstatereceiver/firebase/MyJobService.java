package it.parello.magnetstatereceiver.firebase;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Parello on 13/04/2017.
 */

public class MyJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        // Do some work here

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }
}