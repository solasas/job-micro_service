package com.sashank.jobmicroservice.job.mapper;

import com.sashank.jobmicroservice.job.Job;
import com.sashank.jobmicroservice.job.dto.JobWithCompanyDTO;
import com.sashank.jobmicroservice.job.external.Company;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JobMapperImpl implements JobMapper {
    private static final Logger logger = LoggerFactory.getLogger(JobMapperImpl.class);

    @Override
    public JobWithCompanyDTO jobToJobWithCompanyDTO(Job job, Company company) {
        if (job == null) {
            logger.warn("Job object is null in mapper");
            return null;
        }

        JobWithCompanyDTO jobWithCompanyDTO = new JobWithCompanyDTO();
        
        try {
            // Map job fields
            jobWithCompanyDTO.setId(job.getId());
            jobWithCompanyDTO.setTitle(job.getTitle());
            jobWithCompanyDTO.setDescription(job.getDescription());
            jobWithCompanyDTO.setMinSalary(job.getMinSalary());
            jobWithCompanyDTO.setMaxSalary(job.getMaxSalary());
            jobWithCompanyDTO.setLocation(job.getLocation());
            jobWithCompanyDTO.setCompanyId(job.getCompanyId());
            
            // Map company object - log if null
            if (company == null) {
                logger.warn("Company is NULL for job id: {}. Check if Company Microservice is running on port 8081", job.getId());
            } else {
                logger.info("Successfully mapped company: {} for job: {}", company.getId(), job.getId());
            }
            jobWithCompanyDTO.setCompany(company);
            
            return jobWithCompanyDTO;
        } catch (Exception e) {
            logger.error("Error mapping Job to DTO", e);
            throw new RuntimeException("Mapping error: " + e.getMessage(), e);
        }
    }

    @Override
    public Job jobWithCompanyDTOtoJob(JobWithCompanyDTO jobWithCompanyDTO) {
        if (jobWithCompanyDTO == null) {
            logger.warn("JobWithCompanyDTO object is null in mapper");
            return null;
        }

        try {
            Job job = new Job();
            
            job.setId(jobWithCompanyDTO.getId());
            job.setTitle(jobWithCompanyDTO.getTitle());
            job.setDescription(jobWithCompanyDTO.getDescription());
            job.setMinSalary(jobWithCompanyDTO.getMinSalary());
            job.setMaxSalary(jobWithCompanyDTO.getMaxSalary());
            job.setLocation(jobWithCompanyDTO.getLocation());
            job.setCompanyId(jobWithCompanyDTO.getCompanyId());
            
            logger.info("Successfully mapped DTO to Job for jobId: {}", jobWithCompanyDTO.getId());
            return job;
        } catch (Exception e) {
            logger.error("Error mapping DTO to Job", e);
            throw new RuntimeException("Reverse mapping error: " + e.getMessage(), e);
        }
    }
}

