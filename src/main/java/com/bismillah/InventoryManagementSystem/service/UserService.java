package com.bismillah.InventoryManagementSystem.service;

import com.bismillah.InventoryManagementSystem.dto.LoginRequest;
import com.bismillah.InventoryManagementSystem.dto.RegisterRequest;
import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.dto.UserDTO;
import com.bismillah.InventoryManagementSystem.entity.User;

public interface UserService {
    Response registerUser(RegisterRequest registerRequest);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    User getCurrentLoggedInUser();

    Response updateUser(Long id, UserDTO userDTO);

    Response deleteUser(Long id);

    Response getUserTransactions(Long id);


}
