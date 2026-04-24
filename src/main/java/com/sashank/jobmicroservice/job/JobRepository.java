package com.sashank.jobmicroservice.job;

import org.springframework.data.jpa.repository.JpaRepository;
//object type and primary key type
public interface JobRepository extends JpaRepository<Job,Long> {

}
