package com.sashank.jobmicroservice.job.mapper;

import com.sashank.jobmicroservice.job.Job;
import com.sashank.jobmicroservice.job.dto.JobWithCompanyDTO;
import com.sashank.jobmicroservice.job.external.Company;

public interface JobMapper {
    JobWithCompanyDTO jobToJobWithCompanyDTO(Job job, Company company);
    Job jobWithCompanyDTOtoJob(JobWithCompanyDTO jobWithCompanyDTO);
}

