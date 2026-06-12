package com.company.system.repository;

import com.company.system.model.dto.FullUserCreateRequestDto;

import java.sql.SQLException;

public interface UserProvisioningRepository {

    boolean createFullUser(FullUserCreateRequestDto request, String passwordHash) throws SQLException;
}
