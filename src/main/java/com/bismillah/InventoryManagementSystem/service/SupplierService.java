package com.bismillah.InventoryManagementSystem.service;

import com.bismillah.InventoryManagementSystem.dto.CategoryDTO;
import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.dto.SupplierDTO;

public interface SupplierService {

    Response addSupplier(SupplierDTO supplierDTO);

    Response updateSuppliers(Long id, SupplierDTO supplierDTO);

    Response getAllSuppliers();

    Response getSuppliersById(Long id);

    Response deleteSuppliers(Long id);
}
