package edu.ucsb.cs156.team02.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;

// Empty interface.
// https://ucsb-cs156-w22.slack.com/archives/C030KT692A3/p1643766836800789

@Repository
public interface UCSBRequirementRepository extends CrudRepository<UCSBRequirement, Long> { }
