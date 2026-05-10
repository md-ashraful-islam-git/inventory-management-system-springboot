package com.bismillah.InventoryManagementSystem.service.impl;

import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.dto.SupplierDTO;
import com.bismillah.InventoryManagementSystem.entity.Supplier;
import com.bismillah.InventoryManagementSystem.exception.DeleteConstraintException;
import com.bismillah.InventoryManagementSystem.exception.NotFoundException;
import com.bismillah.InventoryManagementSystem.repository.SupplierRepository;
import com.bismillah.InventoryManagementSystem.repository.TransactionRepository;
import com.bismillah.InventoryManagementSystem.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;
    private final TransactionRepository transactionRepository;

    @Override
    public Response addSupplier(SupplierDTO supplierDTO) {
        Supplier supplierToSave = modelMapper.map(supplierDTO, Supplier.class);
        supplierRepository.save(supplierToSave);

        return Response.builder()
                .status(200)
                .message("Supplier Added Successfully")
                .build();
    }

    @Override
    public Response updateSuppliers(Long id, SupplierDTO supplierDTO) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Supplier not found"));

        if (supplierDTO.getName() != null) existingSupplier.setName(supplierDTO.getName());
        if (supplierDTO.getAddress() != null) existingSupplier.setAddress(supplierDTO.getAddress());

        supplierRepository.save(existingSupplier);

        return Response.builder()
                .status(200)
                .message("Supplier successfully updated")
                .build();
    }

    @Override
    public Response getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<SupplierDTO> supplierDTOS = modelMapper.map(suppliers, new TypeToken<List<SupplierDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .suppliers(supplierDTOS)
                .build();
    }

    @Override
    public Response getSuppliersById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier Not Found"));
        SupplierDTO supplierDTO = modelMapper.map(supplier, SupplierDTO.class);
        return Response.builder()
                .status(200)
                .message("success")
                .supplier(supplierDTO)
                .build();
    }

    @Override
    public Response deleteSuppliers(Long id) {
        log.info("Attempting to delete supplier with ID: {}", id);
        supplierRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Supplier not found"));

        if (transactionRepository.countBySupplierId(id) > 0) {
            log.warn("Deletion blocked: Supplier ID {} is linked to transactions", id);
            throw new DeleteConstraintException("Cannot delete supplier. It is linked to existing transactions.");
        }

        supplierRepository.deleteById(id);
        log.info("Supplier ID {} successfully deleted", id);

        return Response.builder()
                .status(200)
                .message("Supplier successfully deleted")
                .build();
    }
}
