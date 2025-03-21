package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.InvoiceItemDTO;

import java.util.List;

public interface InvoiceItemService {
    List<InvoiceItemDTO> findByInvoiceId(Long id);
}
