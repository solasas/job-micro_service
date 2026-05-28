package com.sashank.jobmicroservice.job.clients;

import com.sashank.jobmicroservice.job.dto.ReviewDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * FeignClient for communicating with Review Microservice
 * Uses Eureka service discovery to find the REVIEW-MICROSERVICE
 */
@FeignClient(name="REVIEW-MICROSERVICE")
public interface ReviewClient {
    
    /**
     * Get reviews by company ID
     * 
     * @param companyId the company ID
     * @return List of ReviewDTO for the company
     */
    @GetMapping("/reviews")
    List<ReviewDTO> getReviewsByCompanyId(@RequestParam("companyId") Long companyId);
    
    /**
     * Get single review by ID
     * 
     * @param id the review ID
     * @return ReviewDTO for the review
     */
    @GetMapping("/reviews/{id}")
    ReviewDTO getReviewById(@PathVariable("id") Long id);
    
    /**
     * Get all reviews
     * 
     * @return List of all ReviewDTO
     */
    @GetMapping("/reviews/all")
    List<ReviewDTO> getAllReviews();
}
