package com.meeting.identity.repository

import com.meeting.identity.entity.Organisation
import org.springframework.data.repository.CrudRepository

interface OrganisationRepository : CrudRepository<Organisation, Int>