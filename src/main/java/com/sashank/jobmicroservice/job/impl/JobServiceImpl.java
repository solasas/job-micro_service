package com.sashank.jobmicroservice.job.impl;

import com.sashank.jobmicroservice.job.Job;
import com.sashank.jobmicroservice.job.JobRepository;
import com.sashank.jobmicroservice.job.JobService;
import com.sashank.jobmicroservice.job.dto.JobWithCompanyDTO;
import com.sashank.jobmicroservice.job.external.Company;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {
//    private List<Job> jobs=new ArrayList<>();
    JobRepository jobrepository;
    
    @Value("${company.service.url:http://localhost:8081}")
    private String companyServiceUrl;
//    private Long nextId=1L;

    public JobServiceImpl(JobRepository jobrepository) {
        this.jobrepository = jobrepository;
    }

    @Override
    public List<JobWithCompanyDTO> findAll() {
        List<Job> jobs = jobrepository.findAll();
        List<JobWithCompanyDTO> jobWithCompanyDTOS = new ArrayList<>();
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            for(Job job: jobs){
                Company company = restTemplate.getForObject(companyServiceUrl + "/companies/" + job.getCompanyId(), Company.class);
                jobWithCompanyDTOS.add(convertToDto(job, company));
            }
        } catch (RestClientException e) {
            System.err.println("Warning: Could not fetch company data from " + companyServiceUrl);
            System.err.println("Error: " + e.getMessage());
            // Continue execution even if company service is unavailable
            // Return jobs without company data
            for(Job job: jobs){
                jobWithCompanyDTOS.add(convertToDto(job, null));
            }
        }
        return jobWithCompanyDTOS;
    }

    private JobWithCompanyDTO convertToDto(Job job, Company company) {
        JobWithCompanyDTO jobWithCompanyDTO = new JobWithCompanyDTO();
        jobWithCompanyDTO.setJob(job);
        jobWithCompanyDTO.setCompany(company);
        return jobWithCompanyDTO;
    }

    @Override
    public void createJob(Job job) {
        //giving the control to set id only to company
//        job.setId(nextId++);
//        jobs.add(job);
         jobrepository.save(job);
    }

    @Override
    public Job getJobById(Long id) {
//        for(Job job: jobs){
//            if(job.getId().equals(id)){
//                return  job;
//            }
//        }
//        return null;

        return jobrepository.findById(id).orElse(null);
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

}
