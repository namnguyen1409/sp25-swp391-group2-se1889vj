package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.InvoiceItemDTO;
import com.group2.sp25swp391group2se1889vj.repository.InvoiceItemRepository;
import com.group2.sp25swp391group2se1889vj.repository.InvoiceRepository;
import com.group2.sp25swp391group2se1889vj.service.InvoiceItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceItemServiceImpl implements InvoiceItemService {


    private final InvoiceItemRepository invoiceItemRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<InvoiceItemDTO> findByInvoiceId(Long id) {
        return invoiceItemRepository.findByInvoiceId(id).stream().map((element) -> modelMapper.map(element, InvoiceItemDTO.class)).collect(Collectors.toList());
    }
}
