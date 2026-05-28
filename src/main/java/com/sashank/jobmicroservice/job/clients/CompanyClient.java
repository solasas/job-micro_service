package com.sashank.jobmicroservice.job.clients;

import com.sashank.jobmicroservice.job.external.Company;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="COMPANY-SERVICE")
public interface CompanyClient {
    @GetMapping("/companies/{Id}")
     Company getCompanyById(@PathVariable("id") Long companyId);
}
