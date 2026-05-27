package com.sashank.jobmicroservice.job;

import com.sashank.jobmicroservice.job.dto.JobWithCompanyDTO;

import java.util.List;

public interface JobService {

List<JobWithCompanyDTO> findAll();
void createJob(Job job);

    Job getJobById(Long id);

    boolean deleteJobById(Long id);

    boolean updateJob(Long id,Job updatedJob);
}
