package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.dto.InvoiceItemDTO;
import com.group2.sp25swp391group2se1889vj.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    List<Invoice> findInvoiceByIsProcessedIsFalse();

    Optional<Invoice> findByIdAndWarehouseId(Long invoiceId, Long warehouseId);

    List<Long> findIdsByIsProcessedIsFalse();
}
