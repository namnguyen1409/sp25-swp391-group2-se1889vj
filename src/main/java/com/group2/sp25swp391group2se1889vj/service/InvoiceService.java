package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.InvoiceDTO;
import com.group2.sp25swp391group2se1889vj.dto.InvoiceDataDTO;
import com.group2.sp25swp391group2se1889vj.dto.InvoiceFilterDTO;
import com.group2.sp25swp391group2se1889vj.dto.InvoiceDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceService {
    Long createInvoice(InvoiceDTO invoiceDTO);

    Page<InvoiceDetailDTO> searchInvoices(Long warehouseId, InvoiceFilterDTO invoiceFilterDTO, Pageable pageable);

    InvoiceDetailDTO findInvoiceBywarehouseIdAndId(Long warehouseId, Long invoiceId);

    InvoiceDataDTO findInvoiceDataBywarehouseIdAndId(Long warehouseId, Long id);

    List<Long> findInvoiceIdsByIsProcessedIsFalse();

}
