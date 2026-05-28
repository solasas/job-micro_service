package com.sashank.jobmicroservice.job.impl;
import com.sashank.jobmicroservice.job.Job;
import com.sashank.jobmicroservice.job.JobRepository;
import com.sashank.jobmicroservice.job.JobService;
import com.sashank.jobmicroservice.job.dto.JobWithCompanyDTO;
import com.sashank.jobmicroservice.job.dto.JobWithCompanyAndReviewsDTO;
import com.sashank.jobmicroservice.job.dto.ReviewDTO;
import com.sashank.jobmicroservice.job.external.Company;
import com.sashank.jobmicroservice.job.clients.CompanyClient;
import com.sashank.jobmicroservice.job.clients.ReviewClient;
import com.sashank.jobmicroservice.job.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@Service
public class JobServiceImpl implements JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
    
    JobRepository jobrepository;
    
    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    JobMapper jobMapper;
    
    @Autowired
    CompanyClient companyClient;
    
    @Autowired
    ReviewClient reviewClient;

    public JobServiceImpl(JobRepository jobrepository,CompanyClient companyClient, ReviewClient reviewClient) {
        this.companyClient = companyClient;
        this.reviewClient = reviewClient;
        this.jobrepository = jobrepository;
    }

    @Override
    public List<JobWithCompanyDTO> findAll() {
        logger.info("Finding all jobs with company and reviews");
        try {
            List<Job> jobs = jobrepository.findAll();
            logger.info("Found {} jobs in database", jobs.size());
            List<JobWithCompanyDTO> jobWithCompanyDTOS = new ArrayList<>();
            
            for(Job job: jobs){
                try {
                    JobWithCompanyDTO dto = convertToDto(job);
                    if (dto != null) {
                        // Fetch and attach reviews
                        if (dto.getCompanyId() != null) {
                            List<ReviewDTO> reviews = fetchReviewsForCompany(dto.getCompanyId());
                            dto.setReviews(reviews);
                            logger.info("Attached {} reviews to job {}", reviews.size(), job.getId());
                        }
                        jobWithCompanyDTOS.add(dto);
                    }
                } catch (Exception e) {
                    logger.error("Error converting job {} to DTO, skipping", job.getId(), e);
                }
            }
            logger.info("Successfully converted {} jobs to DTOs with reviews", jobWithCompanyDTOS.size());
            return jobWithCompanyDTOS;
        } catch (Exception e) {
            logger.error("Error retrieving all jobs", e);
            throw new RuntimeException("Failed to retrieve jobs: " + e.getMessage(), e);
        }
    }

    private JobWithCompanyDTO convertToDto(Job job) {
        if (job == null) {
            logger.warn("Job object is null");
            return null;
        }
        
        logger.info("Converting Job {} to JobWithCompanyDTO", job.getId());
        
        Company company = null;
        if (job.getCompanyId() != null) {
            try {
                logger.info("Fetching company {} using FeignClient", job.getCompanyId());
                company = companyClient.getCompanyById(job.getCompanyId());
                if (company != null) {
                    logger.info("✓ Successfully fetched company: {} - {}", company.getId(), company.getName());
                }
            } catch (Exception e) {
                logger.error("Error fetching company {} using FeignClient: {}", job.getCompanyId(), e.getMessage());
                company = null;
            }
        } else {
            logger.warn("Job {} has no companyId", job.getId());
        }
        
        try {
            // Use JobMapper to convert Job and Company to JobWithCompanyDTO
            JobWithCompanyDTO dto = jobMapper.jobToJobWithCompanyDTO(job, company);
            logger.info("Successfully converted Job {} to DTO", job.getId());
            return dto;
        } catch (Exception e) {
            logger.error("Error during mapping Job {} to DTO", job.getId(), e);
            throw new RuntimeException("Failed to convert Job to DTO: " + e.getMessage(), e);
        }
    }

    @Override
    public void createJob(Job job) {
        //giving the control to set id only to company
//        job.setId(nextId++);
//        jobs.add(job);
         jobrepository.save(job);
    }

    @Override
    public JobWithCompanyDTO getJobById(Long id) {
        logger.info("Getting job by id: {} with company and reviews", id);
        try {
            Optional<Job> jobOptional = jobrepository.findById(id);
            if(jobOptional.isPresent()) {
                logger.info("Job found with id: {}", id);
                JobWithCompanyDTO dto = convertToDto(jobOptional.get());
                if (dto != null && dto.getCompanyId() != null) {
                    // Fetch and attach reviews
                    List<ReviewDTO> reviews = fetchReviewsForCompany(dto.getCompanyId());
                    dto.setReviews(reviews);
                    logger.info("Attached {} reviews to job {}", reviews.size(), id);
                }
                return dto;
            } else {
                logger.warn("Job not found with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error retrieving job with id: {}", id, e);
            throw new RuntimeException("Failed to retrieve job: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteJobById(Long id) {
//        Iterator<Job> iterator=jobs.iterator();
//        while(iterator.hasNext()){
//            Job job=iterator.next();
//            if(job.getId().equals(id)){
//                iterator.remove();
//                return true;
//            }
//        }
//        return false;
        try{
        jobrepository.deleteById(id);
        return true;}
        catch (Exception e){
            return false;

        }


    }

    @Override
    public boolean updateJob(Long id,Job updatedJob) {
//        for(Job job: jobs){
//            if(job.getId().equals(id)){
//                job.setTitle(updatedJob.getTitle());
//                job.setDescription(updatedJob.getDescription());
//                job.setMinSalary(updatedJob.getMinSalary());
//                job.setMaxSalary(updatedJob.getMaxSalary());
//                job.setLocation(updatedJob.getLocation());
//                return true;
//            }
//        }
        Optional<Job> joboptional=jobrepository.findById(id);
        if(joboptional.isPresent()){
            Job job=joboptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setLocation(updatedJob.getLocation());
             job.setCompanyId(updatedJob.getCompanyId());
            jobrepository.save(job);
            return true;

        }

        return false;
    }
    
    public List<ReviewDTO> fetchReviewsForCompany(Long companyId) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                logger.info("[Review Attempt {}/3] Fetching reviews for company {} using FeignClient", attempt, companyId);
                
                List<ReviewDTO> reviews = reviewClient.getReviewsByCompanyId(companyId);
                
                if (reviews != null && !reviews.isEmpty()) {
                    logger.info("✓ Successfully fetched {} reviews for company: {} using FeignClient", reviews.size(), companyId);
                    return reviews;
                } else {
                    logger.warn("[Review Attempt {}/3] No reviews returned for company: {}", attempt, companyId);
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                logger.error("[Review Attempt {}/3] Exception while fetching reviews for companyId {}: {}", 
                    attempt, companyId, e.getMessage());
                
                if (attempt < 3) {
                    try {
                        logger.info("Waiting 1 second before retry...");
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        logger.warn("✗ Failed to fetch reviews for company {} after 3 attempts using FeignClient, returning empty list", companyId);
        return new ArrayList<>();
    }

}
